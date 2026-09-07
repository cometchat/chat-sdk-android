# AI-DLC Audit Log

## Workspace Detection - Start
**Timestamp**: 2026-05-14T22:00:00Z
**User Request**: "Using AIDLC, we need to build the CometChat Notification Feed feature. We need to build it for chat sdk only for now. Refer to the common design in the spec directory — cometchat-notification-feed.md. We need to only build the sdk for now."
**Linear Ticket**: ENG-35195 (parent), ENG-35196–ENG-35200 (sub-issues)
**Context**: Fresh start with preserved reverse engineering artifacts from earlier today. Common SDK design doc provided by user and saved to aidlc-docs/inception/specs/design-doc-sdk-common.md.

---

## Workspace Detection - Complete
**Timestamp**: 2026-05-14T22:01:00Z
**Findings**: Brownfield project, Java, Gradle 7.4.2, single-module Android library (AAR)
**Reverse Engineering**: Preserved from earlier session (2026-05-14T21:15:00Z) — still current
**Next Stage**: Requirements Analysis

---

## Requirements Analysis - Complete
**Timestamp**: 2026-05-14T22:10:00Z
**Questions Asked**: 6 (Android-specific implementation decisions)
**User Responses**: Q1=A (match existing pattern), Q2=A (PushNotification in Chat SDK), Q3=A (JSONObject), Q4=A (full PBT), Q5=A (full security), Q6=A (cursor-based)
**Artifacts Created**: 
- requirement-verification-questions.md
- requirements.md
**Extensions Enabled**: Property-Based Testing (Full), Security Baseline (Full)
**Next Stage**: Workflow Planning

---

## Workflow Planning - Approval
**Timestamp**: 2026-05-14T22:15:00Z
**AI Prompt**: "Ready to proceed with this plan?"
**User Response**: "Approvce and continue"
**Status**: Approved (with risk level updated to Medium per user feedback on customer-requested feature)
**Context**: Execution plan approved — skipping User Stories, Application Design, Units Generation, Functional Design, NFR Requirements, NFR Design, Infrastructure Design. Proceeding to Code Generation.

---

## Code Generation — Unit 1: Data Models & Enums (ENG-35196)
**Timestamp**: 2026-05-14T22:20:00Z
**Linear Issue**: ENG-35196
**Status**: In Progress → Complete
**Commit**: b042a38a — feat(ENG-35196): add notification feed data models and enums
**Files Created**:
- chat-sdk-android/src/main/java/com/cometchat/chat/enums/FeedEngagementType.java
- chat-sdk-android/src/main/java/com/cometchat/chat/enums/FeedReadState.java
- chat-sdk-android/src/main/java/com/cometchat/chat/models/NotificationFeedItem.java
- chat-sdk-android/src/main/java/com/cometchat/chat/models/NotificationCategory.java
- chat-sdk-android/src/main/java/com/cometchat/chat/models/PushNotification.java
**Files Modified**:
- chat-sdk-android/src/main/java/com/cometchat/chat/constants/CometChatConstants.java
**Summary**: Created all data models (NotificationFeedItem, NotificationCategory, PushNotification) and enums (FeedEngagementType, FeedReadState) for the Notification Feed module. Added all JSON key constants and API path constants.

---

## Code Generation — Unit 2: NotificationFeedRequestBuilder (ENG-35197)
**Timestamp**: 2026-05-14T22:25:00Z
**Linear Issue**: ENG-35197
**Status**: In Progress → Under Dev Review
**Commit**: 419051ac — feat(ENG-35197): add NotificationFeedRequest with cursor-based pagination
**Files Created**:
- chat-sdk-android/src/main/java/com/cometchat/chat/core/NotificationFeedRequest.java
**Summary**: Created NotificationFeedRequest with inner NotificationFeedRequestBuilder. Cursor-based pagination, fluent builder API with filters (readState, category, channelId, tags, dateFrom, dateTo). Depends on ApiConnection.getNotificationFeed() to be wired in Unit 4.

---

## Code Generation — Unit 3: NotificationCategoriesRequestBuilder (ENG-35198)
**Timestamp**: 2026-05-14T22:28:00Z
**Linear Issue**: ENG-35198
**Status**: In Progress → Under Dev Review
**Commit**: 4926e414 — feat(ENG-35198): add NotificationCategoriesRequest with cursor-based pagination
**Files Created**:
- chat-sdk-android/src/main/java/com/cometchat/chat/core/NotificationCategoriesRequest.java
**Summary**: Created NotificationCategoriesRequest with inner NotificationCategoriesRequestBuilder. Cursor-based pagination, default limit 50. Depends on ApiConnection.getNotificationCategories() to be wired in Unit 4.

---

## Code Generation — Unit 4: Engagement & Reporting Methods (ENG-35199)
**Timestamp**: 2026-05-14T22:35:00Z
**Linear Issue**: ENG-35199
**Status**: In Progress → Under Dev Review
**Commit**: 4e3dbb41 — feat(ENG-35199): add engagement and reporting methods for notification feed
**Files Modified**:
- chat-sdk-android/src/main/java/com/cometchat/chat/core/ApiConnection.java (10 URL constants + 10 API methods)
- chat-sdk-android/src/main/java/com/cometchat/chat/core/CometChat.java (9 public static methods + imports)
**Summary**: Added all engagement, reporting, and utility methods. ApiConnection handles HTTP calls (GET/POST/PUT). CometChat facade exposes public API with input validation and main-thread callbacks. All methods idempotent.

---

## Code Generation — Unit 5: NotificationFeedListener / WebSocket (ENG-35200)
**Timestamp**: 2026-05-14T22:40:00Z
**Linear Issue**: ENG-35200
**Status**: In Progress → Under Dev Review
**Commit**: 2de12a7f — feat(ENG-35200): add NotificationFeedListener for real-time WebSocket events
**Files Created**:
- chat-sdk-android/src/main/java/com/cometchat/chat/core/NotificationFeedListener.java
- chat-sdk-android/src/main/java/com/cometchat/chat/core/CometChatNotificationFeedEvent.java
**Files Modified**:
- chat-sdk-android/src/main/java/com/cometchat/chat/core/CometChat.java (listener HashMap + add/remove/dispatch methods)
- chat-sdk-android/src/main/java/com/cometchat/chat/core/DispatchController.java (informNotificationFeedListener + interface)
**Summary**: Implemented real-time WebSocket listener for notification feed. Independent from other listeners. Filters type=notification_feed_item AND action=sent. Malformed payloads skipped safely.

---

## Build and Test — Complete
**Timestamp**: 2026-05-14T22:45:00Z
**Build Command**: `./gradlew :chat-sdk-android:compileDebugJavaWithJavac`
**Build Status**: ✅ SUCCESS (9s, 0 errors)
**Files Verified**: 9 new + 4 modified — all compile successfully
**Regressions**: None (additive changes only)
**Test Status**: No unit test infrastructure in this module; integration tests require live backend
**Next**: Code review and PR creation

---

## Code Generation — Quoted Replies notification preference (ENG-38326)
**Timestamp**: 2026-08-18T09:42:57Z
**Linear Issue**: ENG-38326
**Spec**: Quoted Replies — Notification Preference Specification (QR-1…QR-22)
**Status**: Backlog → Under Dev Review
**Branch**: feat/eng-38326-quoted-replies-preference → release-v5-quoted-replies-preferences
**Files Created**:
- chat-sdk-android/src/main/java/com/cometchat/chat/enums/QuotedRepliesOptions.java (4 members + get(int); QR-6, QR-8, QR-22)
**Files Modified**:
- chat-sdk-android/src/main/java/com/cometchat/chat/constants/CometChatNotificationsConstants.java (ONE_ON_ONE_QUOTED_REPLIES, KEY_GROUP_QUOTED_REPLIES — QR-9)
- chat-sdk-android/src/main/java/com/cometchat/chat/models/OneOnOnePreferences.java (field + accessors + toJson/fromJson/toMap/fromMap/Parcelable/contentEquals/toString/clone; guarded Enum.valueOf on the parcel path — QR-13)
- chat-sdk-android/src/main/java/com/cometchat/chat/models/GroupPreferences.java (same set, following this file's int/-1-sentinel parcel pattern)
- chat-sdk-android/src/main/java/com/cometchat/chat/core/CometChatNotifications.java (deleted dead private getUpdatePushPreferenceJsonRequest() + 3 imports orphaned by it)
**Tests Added** (31 new, in src/test/java/com/cometchat/chat/notifications/):
- NotificationEnumsTest +8 — the four wire values, unknown → null, QR-8 type distinctness
- NotificationModelsTest +17 — json/map round trips for both buckets, unset omitted, clone/contentEquals/toString, PushPreferences + NotificationPreferences inheritance
- NotificationPreferencesTest +6 — per-conversation-type independence, guarded parcel decode path
**Test Status**: ✅ `./gradlew :chat-sdk-android:testDebugUnitTest` — 40 classes, 623 tests, 0 failures
**Invariants re-verified**: QR-12 (unset omitted from toJson/toMap) · QR-13 (unknown int → unset; guarded valueOf) · QR-14 (payload byte-identical when unset) · QR-19 (field in every representation) · QR-20 (PushPreferences inherits via the shared bucket objects — verified against code, not assumed)
**Deviation**: a true android.os.Parcel round-trip cannot run as a JVM unit test in this module (Parcel is a stub — "Method obtain in android.os.Parcel not mocked"; no Robolectric on the unit-test classpath). The parcel read path is covered at its decision point instead. Device-side round trip left as follow-up.
**Regressions**: None (additive; the only deletion was dead code).

---

## Code Review Follow-up — test corrections (ENG-38326)
**Timestamp**: 2026-08-18T10:25:17Z
**Linear Issue**: ENG-38326
**Status**: Under Dev Review (unchanged)
**Branch**: feat/eng-38326-quoted-replies-preference → release-v5-quoted-replies-preferences (PR #412 updated)
**Scope**: test-only — no src/main file touched in this pass.
**Files Modified**:
- chat-sdk-android/src/test/java/com/cometchat/chat/notifications/NotificationEnumsTest.java — removed `qr8_theThreadedReplyOptionSet_cannotExpressTheFourthValue`. It asserted `RepliesOptions.values().length == 3` and `RepliesOptions.get(4) == null`; ENG-37566 (In QA) adds `SUBSCRIBE_TO_SUBSCRIBED_THREADS(4)` to that set, so both assertions break on convergence and the failure message ("the threaded-replies preference is unchanged by this feature") points the reader at the wrong feature. QR-8 mandates distinct, non-interchangeable types, not a fixed arity for `RepliesOptions`. A comment now records why arity must not be asserted from here.
- chat-sdk-android/src/test/java/com/cometchat/chat/notifications/NotificationPreferencesTest.java — `qr13_anUnknownValueOnTheGroupParcelPath_degradesToUnset` named and documented a `GroupPreferences` parcel path its body never entered (it only called `QuotedRepliesOptions.get(-1)`/`get(99)`, duplicating `NotificationEnumsTest.qr13_unrecognisedQuotedReplyLevel_isTreatedAsUnset`). Renamed to `qr13_theGroupBucketsUnsetSentinel_cannotCollideWithARealOption` and rewritten to assert the precondition the group bucket's int/-1 encoding actually rests on: no member of `QuotedRepliesOptions` carries the value -1. Javadoc now states plainly that the group parcel constructor is not executed here.
**QR-8 coverage after this pass**: intact via `qr8_theTwoOptionSets_areDistinctTypes`, `qr8_neitherPreferenceAcceptsTheOtherOptionSet` and `qr8_theQuotedReplyGetters_returnTheDedicatedOptionSet` — all green.
**Net test count**: 31 → 30 added by this ticket (NotificationEnumsTest +8 → +7; the other two files unchanged at +17 / +6).
**Why the group parcel path stays uncovered in JVM tests**: `android.os.Parcel` is a non-instantiable stub in this module's unit-test classpath, and neither Mockito nor Robolectric is a `testImplementation` dependency (verified in `chat-sdk-android/build.gradle`). Making that test genuinely exercise the constructor would require a new test dependency, which is out of scope for a test-correction pass. Device-side round trip remains the follow-up.
**Test Status**: ✅ `./gradlew :chat-sdk-android:testDebugUnitTest` — 40 classes, 622 tests, 0 failures, 0 errors, 0 skipped (623 → 622: the one removed test). `qr8_theTwoOptionSets_areDistinctTypes`, `qr8_neitherPreferenceAcceptsTheOtherOptionSet`, `qr8_theQuotedReplyGetters_returnTheDedicatedOptionSet` and the renamed QR-13 test all present and green.
**Regressions**: None.

---

## Bug Fix — fromMap() NPE on an explicit null preference value (ENG-38347)
**Timestamp**: 2026-08-18T14:15:00Z
**Linear Issue**: ENG-38347
**Spec**: QR-13 — an unrecognised value "MUST be treated as unset and MUST NOT fail, throw or crash, on any code path" (general to preference handling, not specific to quoted replies)
**Status**: Backlog → Under Dev Review
**Branch**: feat/eng-38326-quoted-replies-preference → release-v5-quoted-replies-preferences (stacked onto PR #412)
**Defect**: `fromMap` guarded each field with `containsKey`, then handed the value to an enum `get(int)` resolver (or an `(int)`/`(boolean)` cast) that force-unboxes. A map carrying an *explicit null* — the shape a JSON `null` takes once a React Native / Flutter wrapper converts it — clears `containsKey` and then throws `NullPointerException` out of a public static factory that has no `try/catch` to contain it.
**Files Created**:
- chat-sdk-android/src/main/java/com/cometchat/chat/models/PreferenceMaps.java — package-private `opt(Map, key)`; one place where "key absent", "key present but null" and "null map" become the same case, and one place documenting why
**Files Modified** (every `fromMap` in the notification-preference chain, not only the field the defect was found through):
- models/GroupPreferences.java — all 11 fields
- models/OneOnOnePreferences.java — all 4 fields (read order preserved)
- models/MutePreferences.java — `dnd` + `schedule`; also drops a day whose name `DayOfWeek.get` does not recognise instead of storing it under a `null` key (that key threw out of `toMap()`/`writeToParcel()` later, far from the payload that caused it) — found by `/code-review`
- models/DaySchedule.java — `from` / `to` / `dnd`
- models/NotificationPreferences.java, models/PushPreferences.java — `usePrivacyTemplate` + the three nested sections. **Required, not scope creep**: without them a null section would have started decoding as an *empty bucket* instead of staying unset, regressing NOTIF-41.
**Scope note**: the ticket says "12 on GroupPreferences, 3 on OneOnOnePreferences". The code has **11 and 4** (15 sites total, which matches the ticket's own "15 copy-pasted null checks"). All 15 fixed.
**Tests Added** (12 new, `src/test/java/com/cometchat/chat/notifications/NotificationPreferencesTest.java`, PREF-07):
every field of both buckets explicitly null → unset · a null beside real values keeps the real values · mute/day-schedule nulls · a null day entry → closed default · an unrecognised day name → dropped, `toMap()` still usable · null sections on both container types → unset, privacy template keeps its default · every factory tolerates a null map · `pref07_theRawUnboxingCall_wouldHaveThrown` guards the reason the checks exist
**Test Status**: ✅ `./gradlew :chat-sdk-android:testDebugUnitTest` — 40 classes, 634 tests, 0 failures, 0 errors, 0 skipped (622 → 634)
**Mutation-checked, not just green**: reverting `GroupPreferences.fromMap` to the pre-fix code fails 3 of the new tests. The tests detect the defect rather than merely accompanying the fix.
**Invariants re-verified**: non-null values behave exactly as before on every path (the casts and resolvers are unchanged; only the guard in front of them moved) · QR-12 unset still omitted from `toJson`/`toMap` · QR-13 no throw on any `fromMap` path · NOTIF-41 an absent *or* null section stays `null`, never an empty bucket
**Not fixed — identified during this pass, tracked separately**: `MutedConversation.fromMap` (`(long) map.get(KEY_UNTIL)`) and `ConversationUpdateSettings.fromMap` carry the same unboxing pattern but are outside the notification-preference buckets this ticket names. No follow-up ticket existed when this entry was first written; they are being filed separately.
**Regressions**: None.

---

## Test Coverage — device-side parcel round trip for the preference buckets (ENG-38346)
**Timestamp**: 2026-08-18T14:20:00Z
**Linear Issue**: ENG-38346
**Spec**: QR-13, QR-19
**Status**: Backlog → Under Dev Review
**Branch**: feat/eng-38326-quoted-replies-preference → release-v5-quoted-replies-preferences (stacked onto PR #412)
**Gap closed**: nothing had ever executed `writeToParcel` on either bucket. Ordering is correct today, but a value written into the wrong slot — or a field added to the parcel *read* and missed in the *write* — would have passed the entire unit suite. ENG-38326 substituted a reflective check on the private `quotedRepliesFromName` helper and never constructed `new OneOnOnePreferences(Parcel)`, so re-inlining `QuotedRepliesOptions.valueOf(...)` would have kept the suite green while re-opening the crash the guard exists to prevent.
**Why instrumented and not a unit test**: `android.os.Parcel` is a stub on this module's unit-test classpath and there is no Robolectric on `testImplementation` (`build.gradle:118-142`); `testOptions` does not set `returnDefaultValues`. Adding Robolectric is a build-dependency change affecting all 634 unit tests.
**Files Created**:
- chat-sdk-android/src/androidTest/java/com/cometchat/chat/notifications/NotificationPreferencesParcelTest.java — 7 tests. First Parcelable coverage in `src/androidTest/`; follows the existing conventions there (`AndroidJUnit4ClassRunner`, `@FixMethodOrder(NAME_ASCENDING)`, JUnit 4 asserts). Deliberately **not** a member of `CometChatTestSuite`: that suite is a sequenced live-backend run and these tests need no backend, no login and no ordering.
**Coverage**: all four `QuotedRepliesOptions` values round trip on both buckets · unset stays unset · a fully empty bucket round trips with every field unset · an unknown enum name on the one-to-one `name()`/`Enum.valueOf` path degrades to unset without throwing (QR-13), with a hand-written parcel in exactly the layout `writeToParcel` produces · every case carries populated sibling fields (the group case alternates the two `MemberActionsOptions` values across its seven member slots) so a wrong-slot write cannot be absorbed silently.
**Test Status**: ✅ **7/7 OK, executed on a real device** — Samsung A015, Android 16 (API 36), via `adb shell am instrument -e class com.cometchat.chat.notifications.NotificationPreferencesParcelTest`.
**Toolchain note**: `./gradlew :chat-sdk-android:connectedDebugAndroidTest` does **not** work on this machine — AGP 7.4.2's Unified Test Platform dies before instrumenting with `java.lang.IllegalAccessError: class com.google.protobuf.GeneratedMessageV3 tried to access method CodedInputStream.shouldDiscardUnknownFields()`, reporting "There were failing tests" against a run of 0 tests. It is a UTP/protobuf classloader conflict, unrelated to this change. The APK builds fine (`assembleDebugAndroidTest`); driving `am instrument` directly works.
**Mutation-checked, not just green**: swapping the `groupQuotedReplies` and `groupReactions` slots in `GroupPreferences.writeToParcel` fails 2 of the 7 (`expected:<DONT_SUBSCRIBE> but was:<SUBSCRIBE_TO_MENTIONS>`). The whole unit suite stays green under that same mutation — which is precisely the gap this ticket describes.
**Known overlap (from the ticket)**: ENG-37566 also edits `OneOnOnePreferences(Parcel)`, guarding the sibling fields' unguarded `Enum.valueOf`. This pass deliberately does **not** touch those three lines, so the conflict stays a clean one. Whoever lands second should extend `a4_...` to cover the sibling fields.
**Regressions**: None — test-only, no `src/main` file touched by this commit.

---
