package com.cometchat.chat.core;

import androidx.annotation.NonNull;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.MessageThread;
import com.cometchat.chat.utils.ThreadParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A paginated request for the threads the logged-in user participates in.
 *
 * <p>Build one with {@link ThreadsRequestBuilder} and page through it with {@link #fetchNext}. Every
 * row returned is a thread the user is subscribed to (participation is subscription); an unsubscribe
 * removes the row.
 *
 * <p><b>Single-use and one-directional.</b> The request accumulates its cursor, its seen-id set and
 * its exhausted flag internally and has no {@code reset()} and no {@code fetchPrevious()} (matching
 * {@code ConversationsRequest} / {@code MessagesRequest}). To refresh a list, build a new request
 * and replace the old one.
 *
 * <p>Pagination deliberately does <b>not</b> trust {@code meta.next} (a confirmed backend bug): the
 * cursor is derived from the oldest row of each page and sent as {@code updatedAt} + {@code id} —
 * the same compound cursor {@code MessagesRequest} sends as {@code sentAt} + {@code id}. The id is
 * what makes a same-second block of threads safe to page through: without it the cursor could only
 * name a second, and rows tied on that second could be skipped. The boundary row that an inclusive
 * cursor repeats is de-duplicated against {@link #seenIds}, and the list is exhausted on a short raw
 * page — or, defensively, when a full page yields nothing fresh and the cursor cannot advance.
 */
public class ThreadsRequest {

    private static final int MIN_LIMIT = 1;
    private static final int MAX_LIMIT = 1000;
    private static final int DEFAULT_LIMIT = 30;

    private final int limit;
    private final boolean participatedByMe;
    private final String guid;
    private final String uid;

    private ThreadParser.Cursor cursor = ThreadParser.Cursor.NONE;
    private boolean exhausted = false;
    private boolean inProgress = false;
    private final Set<Long> seenIds = new HashSet<>();

    private ThreadsRequest(ThreadsRequestBuilder builder) {
        this.limit = builder.limit;
        this.participatedByMe = builder.participatedByMe;
        this.guid = builder.guid;
        this.uid = builder.uid;
    }

    /**
     * Fetches the next page of threads. Delivers an empty list once the list is exhausted, and an
     * error if a fetch is already in flight or the limit is out of range.
     */
    public void fetchNext(@NonNull final CometChat.CallbackListener<List<MessageThread>> listener) {
        if (limit < MIN_LIMIT) {
            postError(listener, new CometChatException(CometChatConstants.Errors.ERROR_NON_POSITIVE_LIMIT,
                    CometChatConstants.Errors.ERROR_NON_POSITIVE_LIMIT_MESSSAGE));
            return;
        }
        if (limit > MAX_LIMIT) {
            postError(listener, new CometChatException(CometChatConstants.Errors.ERROR_LIMIT_EXCEEDED,
                    String.format(CometChatConstants.Errors.ERROR_LIMIT_EXCEEDED_MESSAGE, MAX_LIMIT)));
            return;
        }
        if (exhausted) {
            CometChat.postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onSuccess(new ArrayList<MessageThread>());
                }
            });
            return;
        }
        if (inProgress) {
            postError(listener, new CometChatException(CometChatConstants.Errors.ERROR_REQUEST_IN_PROGRESS,
                    CometChatConstants.Errors.ERROR_REQUEST_IN_PROGRESS_MESSAGE));
            return;
        }
        inProgress = true;
        doFetch(cursor, listener);
    }

    private void doFetch(final ThreadParser.Cursor fetchCursor, final CometChat.CallbackListener<List<MessageThread>> listener) {
        ApiConnection.getInstance().getThreads(limit, participatedByMe, guid, uid,
                CometChatConstants.AFFIX_PREPEND, fetchCursor.updatedAt, fetchCursor.id,
                new ApiConnection.APIConnectionListener() {
                    @Override
                    public void onResponse(String response, final CometChatException ce) {
                        if (ce != null) {
                            CometChat.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    inProgress = false;
                                    listener.onError(ce);
                                }
                            });
                            return;
                        }
                        try {
                            List<MessageThread> page = ThreadParser.parseThreadList(response);
                            ThreadParser.Cursor boundary = ThreadParser.parseCursor(response);
                            PageOutcome outcome = reducePage(page, limit, seenIds, fetchCursor, boundary);
                            exhausted = outcome.exhausted;
                            cursor = outcome.nextCursor;

                            final List<MessageThread> result = outcome.fresh;
                            CometChat.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    inProgress = false;
                                    listener.onSuccess(result);
                                }
                            });
                        } catch (final Exception e) {
                            CometChat.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    inProgress = false;
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, e.getMessage()));
                                }
                            });
                        }
                    }
                });
    }

    /** The pagination decision for one page: what is fresh, where the cursor goes, and whether we are done. */
    static final class PageOutcome {
        final List<MessageThread> fresh;
        final ThreadParser.Cursor nextCursor;
        final boolean exhausted;

        PageOutcome(List<MessageThread> fresh, ThreadParser.Cursor nextCursor, boolean exhausted) {
            this.fresh = fresh;
            this.nextCursor = nextCursor;
            this.exhausted = exhausted;
        }
    }

    /**
     * Pure pagination decision (no I/O), extracted so the risky boundary logic is directly testable.
     *
     * <p>De-dupes the page against {@code seenIds} (an inclusive cursor repeats the boundary row) and
     * advances the cursor to the page's boundary row. Because the cursor carries the boundary id and
     * not just its second, a same-second block simply spills across pages instead of being skipped —
     * so there is no cursor step-back here and no row is ever dropped to make progress.
     *
     * <p>Exhaustion is a short raw page. The one extra guard: a full page that yields nothing fresh
     * <i>and</i> leaves the cursor exactly where it was cannot make progress on any further attempt
     * (only reachable with an inclusive cursor and {@code limit == 1}), so the list ends there rather
     * than looping forever. Mutates {@code seenIds} with the ids it accepts as fresh.
     */
    static PageOutcome reducePage(List<MessageThread> page, int limit, Set<Long> seenIds,
                                  ThreadParser.Cursor fetchCursor, ThreadParser.Cursor boundary) {
        List<MessageThread> fresh = new ArrayList<>();
        for (MessageThread thread : page) {
            long id = thread.getParentMessageId();
            if (id > 0 && !seenIds.contains(id)) {
                seenIds.add(id);
                fresh.add(thread);
            }
        }
        ThreadParser.Cursor nextCursor = boundary.isValid() ? boundary : fetchCursor;
        boolean shortPage = page.size() < limit;
        boolean stalled = !shortPage && fresh.isEmpty() && nextCursor.sameAs(fetchCursor);
        return new PageOutcome(fresh, nextCursor, shortPage || stalled);
    }

    private void postError(final CometChat.CallbackListener<List<MessageThread>> listener, final CometChatException e) {
        CometChat.postOnMainThread(new Runnable() {
            @Override
            public void run() {
                listener.onError(e);
            }
        });
    }

    public int getLimit() {
        return limit;
    }

    public boolean isParticipatedByMe() {
        return participatedByMe;
    }

    public String getGuid() {
        return guid;
    }

    public String getUid() {
        return uid;
    }

    /** @return {@code true} while more pages may remain; {@code false} once the list is exhausted. */
    public boolean hasMore() {
        return !exhausted;
    }

    public static class ThreadsRequestBuilder {
        int limit = DEFAULT_LIMIT;
        boolean participatedByMe = true;
        String guid;
        String uid;

        /**
         * Sets the page size. Validated against 1…{@value #MAX_LIMIT} at fetch time; a heavy thread row
         * (root message plus last reply) is why the default is a modest {@value #DEFAULT_LIMIT}.
         */
        public ThreadsRequestBuilder setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        /** Whether to fetch only the threads the logged-in user participates in. Defaults to {@code true}. */
        public ThreadsRequestBuilder setParticipatedByMe(boolean participatedByMe) {
            this.participatedByMe = participatedByMe;
            return this;
        }

        /** Scopes the list to a group's threads. Mutually exclusive with {@link #setUid(String)}. */
        public ThreadsRequestBuilder setGuid(String guid) {
            this.guid = guid;
            return this;
        }

        /** Scopes the list to a user's threads. Mutually exclusive with {@link #setGuid(String)}. */
        public ThreadsRequestBuilder setUid(String uid) {
            this.uid = uid;
            return this;
        }

        public ThreadsRequest build() {
            if (guid != null && uid != null) {
                throw new IllegalArgumentException("ThreadsRequest: setGuid and setUid are mutually exclusive");
            }
            return new ThreadsRequest(this);
        }
    }
}
