# Unit 2 Summary — NotificationFeedRequestBuilder (ENG-35197)

## Files Created
1. `chat-sdk-android/src/main/java/com/cometchat/chat/core/NotificationFeedRequest.java`
   - Outer class: `NotificationFeedRequest` with `fetchNext()` method
   - Inner class: `NotificationFeedRequestBuilder` with fluent builder methods

## Implementation Details
- **Pattern**: Matches `UsersRequest` / `UsersRequestBuilder` inner class pattern
- **Pagination**: Cursor-based (stores cursor from response meta, returns empty on exhaustion)
- **Default limit**: 20, max: 100
- **Filters**: readState, category, channelId, tags, dateFrom, dateTo
- **Thread safety**: `inProgress` flag prevents concurrent fetchNext() calls
- **Callbacks**: Uses `CometChat.CallbackListener<List<NotificationFeedItem>>`
- **Main thread dispatch**: All callbacks posted via `CometChat.postOnMainThread()`

## Dependencies
- Requires `ApiConnection.getNotificationFeed()` method (implemented in Unit 4)
- Uses `NotificationFeedItem.listFromJson()` from Unit 1
- Uses `FeedReadState` enum from Unit 1
- Uses `CometChatConstants.NotificationFeedKeys` from Unit 1

## API Endpoint
`GET /v3.0/campaigns/notification-feed?limit=X&cursor=Y&readState=Z&category=W&channelId=V&tags=U&dateFrom=T&dateTo=S`
