# Unit 1 Summary — Data Models & Enums (ENG-35196)

## Files Created
1. `chat-sdk-android/src/main/java/com/cometchat/chat/enums/FeedEngagementType.java` — Enum: VIEWED, CLICKED, INTERACTED
2. `chat-sdk-android/src/main/java/com/cometchat/chat/enums/FeedReadState.java` — Enum: READ, UNREAD, ALL
3. `chat-sdk-android/src/main/java/com/cometchat/chat/models/NotificationFeedItem.java` — Main feed item model (Parcelable, fromJson, listFromJson)
4. `chat-sdk-android/src/main/java/com/cometchat/chat/models/NotificationCategory.java` — Category model (Parcelable, fromJson, listFromJson)
5. `chat-sdk-android/src/main/java/com/cometchat/chat/models/PushNotification.java` — Push notification model (Parcelable, fromJson)

## Files Modified
6. `chat-sdk-android/src/main/java/com/cometchat/chat/constants/CometChatConstants.java` — Added NotificationFeedKeys, NotificationCategoryKeys, PushNotificationKeys, NotificationFeedPaths, WS constants

## Key Decisions
- All models implement Parcelable (Android IPC support)
- All models provide static `fromJson(JSONObject)` factory methods
- NotificationFeedItem maps backend `subCategory` → `category`
- NotificationFeedItem.content stored as JSONObject
- Enums follow existing pattern (String value, getValue(), static get())
- Constants organized in inner static final classes
- API paths use String.format() placeholders for IDs
