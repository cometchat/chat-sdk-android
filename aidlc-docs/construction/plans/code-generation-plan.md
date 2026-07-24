# Code Generation Plan — ENG-35195: Notification Feed Module

## Workspace Root
`/Users/admin/Ashfaq/Android/V5-Android/chat-sdk-android`

## Code Location
All source files go in:
`chat-sdk-android/src/main/java/com/cometchat/chat/`

## Unit Execution Order

---

## Unit 1: Data Models & Enums (ENG-35196)

### Step 1: Create FeedEngagementType enum
- [x] Create `chat-sdk-android/src/main/java/com/cometchat/chat/enums/FeedEngagementType.java`
- Pattern: Follow `ModerationStatus.java` — enum with String value, `getValue()`, static `get(String)`
- Values: VIEWED("viewed"), CLICKED("clicked"), INTERACTED("interacted")

### Step 2: Create FeedReadState enum
- [x] Create `chat-sdk-android/src/main/java/com/cometchat/chat/enums/FeedReadState.java`
- Pattern: Same as FeedEngagementType
- Values: READ("read"), UNREAD("unread"), ALL("all")

### Step 3: Create NotificationFeedItem model
- [x] Create `chat-sdk-android/src/main/java/com/cometchat/chat/models/NotificationFeedItem.java`
- Fields: id (String), category (String), content (JSONObject), readAt (Long nullable), deliveredAt (Long nullable), sentAt (long), metadata (HashMap<String, Object>), tags (List<String>), sender (String), receiver (String), receiverType (String)
- Implements: Parcelable
- Methods: getters/setters, `isRead()`, `fromJson(JSONObject)`, `listFromJson(JSONObject)`, `toString()`, `contentEquals()`
- JSON mapping: backend `subCategory` → `category`

### Step 4: Create NotificationCategory model
- [x] Create `chat-sdk-android/src/main/java/com/cometchat/chat/models/NotificationCategory.java`
- Fields: id (String), label (String)
- Implements: Parcelable
- Methods: getters/setters, `fromJson(JSONObject)`, `listFromJson(JSONObject)`, `toString()`

### Step 5: Create PushNotification model
- [x] Create `chat-sdk-android/src/main/java/com/cometchat/chat/models/PushNotification.java`
- Fields: id (String), announcementId (String), campaignId (String nullable), source (String)
- Implements: Parcelable
- Methods: getters/setters, `fromJson(JSONObject)`, `toString()`

### Step 6: Add constants to CometChatConstants
- [x] Modify `chat-sdk-android/src/main/java/com/cometchat/chat/constants/CometChatConstants.java`
- Add inner interface `NotificationFeedKeys` with JSON key constants (id, subCategory, data, readAt, deliveredAt, sentAt, metadata, tags, sender, receiver, receiverType)
- Add inner interface `NotificationCategoryKeys` with constants (id, label)
- Add inner interface `PushNotificationKeys` with constants (id, announcementId, campaignId, source)
- Add API path constants for all notification feed endpoints

### Step 7: Unit 1 Summary
- [x] Create `aidlc-docs/construction/notification-feed/code/unit-1-summary.md`

---

## Unit 2: NotificationFeedRequestBuilder (ENG-35197)

### Step 8: Create NotificationFeedRequest class
- [ ] Create `chat-sdk-android/src/main/java/com/cometchat/chat/core/NotificationFeedRequest.java`
- Outer class: `NotificationFeedRequest` with `fetchNext(CometChat.CallbackListener<List<NotificationFeedItem>>)`
- Inner class: `NotificationFeedRequestBuilder` with setLimit, setReadState, setCategory, setChannelId, setTags, setDateFrom, setDateTo, build()
- Pagination: cursor-based (store cursor from response, return empty on exhaustion)
- HTTP: GET `/v3.0/campaigns/notification-feed` with query params
- Pattern: Follow `UsersRequest.java` structure

### Step 9: Unit 2 Summary
- [ ] Create `aidlc-docs/construction/notification-feed/code/unit-2-summary.md`

---

## Unit 3: NotificationCategoriesRequestBuilder (ENG-35198)

### Step 10: Create NotificationCategoriesRequest class
- [ ] Create `chat-sdk-android/src/main/java/com/cometchat/chat/core/NotificationCategoriesRequest.java`
- Outer class: `NotificationCategoriesRequest` with `fetchNext(CometChat.CallbackListener<List<NotificationCategory>>)`
- Inner class: `NotificationCategoriesRequestBuilder` with setLimit, build()
- Pagination: cursor-based
- HTTP: GET `/v3.0/campaigns/templates/categories` with query params

### Step 11: Unit 3 Summary
- [ ] Create `aidlc-docs/construction/notification-feed/code/unit-3-summary.md`

---

## Unit 4: Engagement & Reporting Methods (ENG-35199)

### Step 12: Add static methods to CometChat.java
- [ ] Modify `chat-sdk-android/src/main/java/com/cometchat/chat/core/CometChat.java`
- Add methods:
  - `markFeedItemAsDelivered(NotificationFeedItem, CallbackListener<Void>)`
  - `markFeedItemsAsDelivered(List<NotificationFeedItem>, CallbackListener<Void>)`
  - `markFeedItemAsRead(NotificationFeedItem, CallbackListener<Void>)`
  - `markAllFeedItemsAsRead(CallbackListener<Void>)`
  - `reportFeedEngagement(NotificationFeedItem, FeedEngagementType, CallbackListener<Void>)`
  - `getNotificationFeedUnreadCount(CallbackListener<Integer>)`
  - `getNotificationFeedItem(String, CallbackListener<NotificationFeedItem>)`
  - `markPushNotificationDelivered(PushNotification, CallbackListener<Void>)`
  - `markPushNotificationClicked(PushNotification, CallbackListener<Void>)`
- Each method delegates to ApiConnection for HTTP calls
- All engagement methods are idempotent

### Step 13: Add API methods to ApiConnection.java
- [ ] Modify `chat-sdk-android/src/main/java/com/cometchat/chat/core/ApiConnection.java`
- Add internal methods for each endpoint:
  - POST `/v3.0/campaigns/notification-feed/{id}/delivered`
  - POST `/v3.0/campaigns/notification-feed/{id}/read`
  - POST `/v3/announcements/read`
  - POST `/v3.0/campaigns/notification-feed/{id}/engagement`
  - GET `/v3.0/campaigns/notification-feed/unread-count`
  - GET `/v3/announcements/{id}`
  - PUT `/v3.0/campaigns/push-notifications/{id}/delivered`
  - PUT `/v3.0/campaigns/push-notifications/{id}/clicked`

### Step 14: Unit 4 Summary
- [ ] Create `aidlc-docs/construction/notification-feed/code/unit-4-summary.md`

---

## Unit 5: NotificationFeedListener — WebSocket (ENG-35200)

### Step 15: Create NotificationFeedListener abstract class
- [ ] Create `chat-sdk-android/src/main/java/com/cometchat/chat/core/NotificationFeedListener.java`
- Abstract class with: `onFeedItemReceived(NotificationFeedItem feedItem)`

### Step 16: Create NotificationFeedEvent class
- [ ] Create `chat-sdk-android/src/main/java/com/cometchat/chat/core/CometChatNotificationFeedEvent.java`
- Extends CometChatEvent
- Parses WebSocket payload where `type == "notification_feed_item"` and `body.action == "sent"`
- Extracts `body.feedItem` into NotificationFeedItem

### Step 17: Add listener registration to CometChat.java
- [ ] Modify `chat-sdk-android/src/main/java/com/cometchat/chat/core/CometChat.java`
- Add `ConcurrentHashMap<String, NotificationFeedListener> notificationFeedListeners`
- Add `addNotificationFeedListener(String listenerId, NotificationFeedListener listener)`
- Add `removeNotificationFeedListener(String listenerId)`

### Step 18: Add dispatch logic to DispatchController.java
- [ ] Modify `chat-sdk-android/src/main/java/com/cometchat/chat/core/DispatchController.java`
- Add handling for `type == "notification_feed_item"` WebSocket messages
- Parse into CometChatNotificationFeedEvent
- Dispatch to all registered NotificationFeedListeners on main thread
- Skip malformed payloads with warning log

### Step 19: Unit 5 Summary
- [ ] Create `aidlc-docs/construction/notification-feed/code/unit-5-summary.md`

---

## Post-Generation

### Step 20: Final verification
- [ ] Verify all files compile (no syntax errors)
- [ ] Verify no duplicate files created
- [ ] Verify all constants referenced correctly
- [ ] Update aidlc-state.md

---

## File Summary

### New Files (11)
1. `chat-sdk-android/src/main/java/com/cometchat/chat/enums/FeedEngagementType.java`
2. `chat-sdk-android/src/main/java/com/cometchat/chat/enums/FeedReadState.java`
3. `chat-sdk-android/src/main/java/com/cometchat/chat/models/NotificationFeedItem.java`
4. `chat-sdk-android/src/main/java/com/cometchat/chat/models/NotificationCategory.java`
5. `chat-sdk-android/src/main/java/com/cometchat/chat/models/PushNotification.java`
6. `chat-sdk-android/src/main/java/com/cometchat/chat/core/NotificationFeedRequest.java`
7. `chat-sdk-android/src/main/java/com/cometchat/chat/core/NotificationCategoriesRequest.java`
8. `chat-sdk-android/src/main/java/com/cometchat/chat/core/NotificationFeedListener.java`
9. `chat-sdk-android/src/main/java/com/cometchat/chat/core/CometChatNotificationFeedEvent.java`

### Modified Files (4)
10. `chat-sdk-android/src/main/java/com/cometchat/chat/constants/CometChatConstants.java` — add key constants
11. `chat-sdk-android/src/main/java/com/cometchat/chat/core/CometChat.java` — add static methods + listener registration
12. `chat-sdk-android/src/main/java/com/cometchat/chat/core/ApiConnection.java` — add API endpoint methods
13. `chat-sdk-android/src/main/java/com/cometchat/chat/core/DispatchController.java` — add WebSocket dispatch

### Documentation (5)
14–18. Unit summary markdown files in `aidlc-docs/construction/notification-feed/code/`
