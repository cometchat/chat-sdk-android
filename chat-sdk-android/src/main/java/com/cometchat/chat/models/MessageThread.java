package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A thread the logged-in user participates in — a decorated view of the thread's root message.
 *
 * <p>Identity is {@link #parentMessageId}, the id of the root message; there is no separate thread
 * resource id. {@code threadId} is deliberately avoided as a symbol — it already denotes an
 * AI-assistant run thread across the SDKs.
 *
 * <p>This is a plain data holder. All JSON decoding lives in
 * {@code com.cometchat.chat.utils.ThreadParser} (the single chokepoint) so that no thread wire key
 * appears outside it; {@link #toMap()} / {@link #fromMap(Map)} carry this model's own field keys and
 * are consumed by the Flutter and React Native bridges.
 *
 * <p><b>Never add a bare {@code receiver} accessor.</b> On {@link BaseMessage}, {@code receiver} is
 * the hydrated entity; because {@link #parentMessage} and {@link #lastReply} are {@code BaseMessage}s,
 * a {@code String getReceiver()} here would sit one dot from an entity-typed one — a silent cast
 * hazard. The raw group/user id is exposed as {@link #receiverUid}; the untouched wire key stays in
 * {@link #rawData}.
 */
public class MessageThread implements Parcelable, Cloneable {

    /** Keys for {@link #toMap()} / {@link #fromMap(Map)} — the bridge contract, not wire keys. */
    public static final class MapKeys {
        public static final String PARENT_MESSAGE_ID = "parentMessageId";
        public static final String REPLY_COUNT = "replyCount";
        public static final String UPDATED_AT = "updatedAt";
        public static final String CONVERSATION_ID = "conversationId";
        public static final String RECEIVER_TYPE = "receiverType";
        public static final String RECEIVER_UID = "receiverUid";
        public static final String SUBSCRIBED = "subscribed";
        public static final String UNREAD_REPLY_COUNT = "unreadReplyCount";
        public static final String PARENT_MESSAGE = "parentMessage";
        public static final String LAST_REPLY = "lastReply";

        private MapKeys() {}
    }

    private long parentMessageId;
    private BaseMessage parentMessage;
    private int replyCount = 0;
    private BaseMessage lastReply;
    private long updatedAt = 0;
    private String conversationId;
    private String receiverType;
    private String receiverUid;
    /** Presence in the thread list is itself the subscription — unsubscribe hard-deletes the row. */
    private boolean subscribed;
    /** Nullable on purpose: {@code null} = unknown (not served yet), which is not the same as {@code 0}. */
    private Integer unreadReplyCount;
    private JSONObject rawData;

    public MessageThread() {}

    // ---- accessors ----------------------------------------------------------

    public long getParentMessageId() {
        return parentMessageId;
    }

    public void setParentMessageId(long parentMessageId) {
        this.parentMessageId = parentMessageId;
    }

    public BaseMessage getParentMessage() {
        return parentMessage;
    }

    public void setParentMessage(BaseMessage parentMessage) {
        this.parentMessage = parentMessage;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public BaseMessage getLastReply() {
        return lastReply;
    }

    public void setLastReply(BaseMessage lastReply) {
        this.lastReply = lastReply;
    }

    /**
     * The cursor timestamp (unix seconds). Second-granular with no tiebreak, and which events bump
     * it is still unspecified — treat it as an opaque cursor and do not sort a UI on it.
     */
    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(String receiverType) {
        this.receiverType = receiverType;
    }

    /** The raw group {@code guid} / user {@code uid} — no name or avatar. Hydrate lazily on render. */
    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    /**
     * Whether the logged-in user is subscribed to this thread. Every row served by
     * {@code ThreadsRequest} is subscribed, so on a fetched list this is always {@code true}.
     */
    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    /** @return the unread reply count, or {@code null} when unknown ({@code null} != {@code 0}). */
    public Integer getUnreadReplyCount() {
        return unreadReplyCount;
    }

    public void setUnreadReplyCount(Integer unreadReplyCount) {
        this.unreadReplyCount = unreadReplyCount;
    }

    /** The untouched raw thread object — the escape hatch for any field not yet modelled. */
    public JSONObject getRawData() {
        return rawData;
    }

    public void setRawData(JSONObject rawData) {
        this.rawData = rawData;
    }

    // ---- bridge map ---------------------------------------------------------

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(MapKeys.PARENT_MESSAGE_ID, parentMessageId);
        map.put(MapKeys.REPLY_COUNT, replyCount);
        map.put(MapKeys.UPDATED_AT, updatedAt);
        if (conversationId != null) {
            map.put(MapKeys.CONVERSATION_ID, conversationId);
        }
        if (receiverType != null) {
            map.put(MapKeys.RECEIVER_TYPE, receiverType);
        }
        if (receiverUid != null) {
            map.put(MapKeys.RECEIVER_UID, receiverUid);
        }
        map.put(MapKeys.SUBSCRIBED, subscribed);
        if (unreadReplyCount != null) {
            map.put(MapKeys.UNREAD_REPLY_COUNT, unreadReplyCount);
        }
        if (parentMessage != null) {
            map.put(MapKeys.PARENT_MESSAGE, parentMessage);
        }
        if (lastReply != null) {
            map.put(MapKeys.LAST_REPLY, lastReply);
        }
        return map;
    }

    public static MessageThread fromMap(Map<String, Object> map) {
        MessageThread thread = new MessageThread();
        if (map == null) {
            return thread;
        }
        if (map.get(MapKeys.PARENT_MESSAGE_ID) instanceof Number) {
            thread.parentMessageId = ((Number) map.get(MapKeys.PARENT_MESSAGE_ID)).longValue();
        }
        if (map.get(MapKeys.REPLY_COUNT) instanceof Number) {
            thread.replyCount = ((Number) map.get(MapKeys.REPLY_COUNT)).intValue();
        }
        if (map.get(MapKeys.UPDATED_AT) instanceof Number) {
            thread.updatedAt = ((Number) map.get(MapKeys.UPDATED_AT)).longValue();
        }
        if (map.get(MapKeys.CONVERSATION_ID) instanceof String) {
            thread.conversationId = (String) map.get(MapKeys.CONVERSATION_ID);
        }
        if (map.get(MapKeys.RECEIVER_TYPE) instanceof String) {
            thread.receiverType = (String) map.get(MapKeys.RECEIVER_TYPE);
        }
        if (map.get(MapKeys.RECEIVER_UID) instanceof String) {
            thread.receiverUid = (String) map.get(MapKeys.RECEIVER_UID);
        }
        if (map.get(MapKeys.SUBSCRIBED) instanceof Boolean) {
            thread.subscribed = (Boolean) map.get(MapKeys.SUBSCRIBED);
        }
        if (map.get(MapKeys.UNREAD_REPLY_COUNT) instanceof Number) {
            thread.unreadReplyCount = ((Number) map.get(MapKeys.UNREAD_REPLY_COUNT)).intValue();
        }
        if (map.get(MapKeys.PARENT_MESSAGE) instanceof BaseMessage) {
            thread.parentMessage = (BaseMessage) map.get(MapKeys.PARENT_MESSAGE);
        }
        if (map.get(MapKeys.LAST_REPLY) instanceof BaseMessage) {
            thread.lastReply = (BaseMessage) map.get(MapKeys.LAST_REPLY);
        }
        return thread;
    }

    // ---- Parcelable ---------------------------------------------------------

    protected MessageThread(Parcel in) {
        parentMessageId = in.readLong();
        parentMessage = in.readParcelable(BaseMessage.class.getClassLoader());
        replyCount = in.readInt();
        lastReply = in.readParcelable(BaseMessage.class.getClassLoader());
        updatedAt = in.readLong();
        conversationId = in.readString();
        receiverType = in.readString();
        receiverUid = in.readString();
        subscribed = in.readByte() != 0;
        int unread = in.readInt();
        unreadReplyCount = unread >= 0 ? unread : null;
        String rawDataStr = in.readString();
        if (rawDataStr != null) {
            try {
                rawData = new JSONObject(rawDataStr);
            } catch (Exception e) {
                rawData = null;
            }
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(parentMessageId);
        dest.writeParcelable(parentMessage, flags);
        dest.writeInt(replyCount);
        dest.writeParcelable(lastReply, flags);
        dest.writeLong(updatedAt);
        dest.writeString(conversationId);
        dest.writeString(receiverType);
        dest.writeString(receiverUid);
        dest.writeByte((byte) (subscribed ? 1 : 0));
        dest.writeInt(unreadReplyCount != null ? unreadReplyCount : -1);
        dest.writeString(rawData != null ? rawData.toString() : null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MessageThread> CREATOR = new Creator<MessageThread>() {
        @Override
        public MessageThread createFromParcel(Parcel in) {
            return new MessageThread(in);
        }

        @Override
        public MessageThread[] newArray(int size) {
            return new MessageThread[size];
        }
    };

    @Override
    public MessageThread clone() {
        try {
            MessageThread cloned = (MessageThread) super.clone();
            if (this.parentMessage != null) {
                cloned.parentMessage = this.parentMessage.clone();
            }
            if (this.lastReply != null) {
                cloned.lastReply = this.lastReply.clone();
            }
            if (this.rawData != null) {
                cloned.rawData = new JSONObject(this.rawData.toString());
            }
            return cloned;
        } catch (Exception e) {
            Logger.error(e.toString());
            return null;
        }
    }

    @Override
    public String toString() {
        return "MessageThread{" +
                "parentMessageId=" + parentMessageId +
                ", replyCount=" + replyCount +
                ", updatedAt=" + updatedAt +
                ", conversationId='" + conversationId + '\'' +
                ", receiverType='" + receiverType + '\'' +
                ", receiverUid='" + receiverUid + '\'' +
                ", subscribed=" + subscribed +
                ", unreadReplyCount=" + unreadReplyCount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }

    /**
     * Compares this object with another for content equality across all fields, including the
     * nested {@link BaseMessage}s (via their own {@code contentEquals}).
     */
    public boolean contentEquals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (!(other instanceof MessageThread)) return false;

        MessageThread that = (MessageThread) other;
        return parentMessageId == that.parentMessageId
                && replyCount == that.replyCount
                && updatedAt == that.updatedAt
                && subscribed == that.subscribed
                && ContentEqualsHelper.stringsEqual(conversationId, that.conversationId)
                && ContentEqualsHelper.stringsEqual(receiverType, that.receiverType)
                && ContentEqualsHelper.stringsEqual(receiverUid, that.receiverUid)
                && (unreadReplyCount == null ? that.unreadReplyCount == null
                        : unreadReplyCount.equals(that.unreadReplyCount))
                && ContentEqualsHelper.objectsContentEqual(parentMessage, that.parentMessage)
                && ContentEqualsHelper.objectsContentEqual(lastReply, that.lastReply)
                && ContentEqualsHelper.jsonObjectsEqual(rawData, that.rawData);
    }
}
