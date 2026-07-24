# Requirements — ENG-35195: Android Chat SDK Notification Feed Module

## Intent Analysis

- **User Request**: Build the CometChat Notification Feed feature for the Android Chat SDK
- **Request Type**: New Feature
- **Scope**: Single component (new module within existing SDK)
- **Complexity**: Moderate — well-defined API surface, follows existing SDK patterns, multiple sub-components

## Reference Document

All requirements derive from: `aidlc-docs/inception/specs/design-doc-sdk-common.md`

---

## Functional Requirements

### FR-1: Data Models & Enums (ENG-35196)

- **FR-1.1**: Define `NotificationFeedItem` model class with fields: `id` (String), `category` (String, mapped from backend `subCategory`), `content` (JSONObject — Card_Schema JSON), `readAt` (Long, nullable), `deliveredAt` (Long, nullable), `sentAt` (long), `metadata` (HashMap<String, Object>), `tags` (List<String>), `sender` (String), `receiver` (String), `receiverType` (String)
- **FR-1.2**: `NotificationFeedItem` must implement `Parcelable` and provide `fromJson(JSONObject)` static factory method (following existing SDK model patterns)
- **FR-1.3**: `NotificationFeedItem` must expose computed property `isRead()` returning `readAt != null`
- **FR-1.4**: Define `NotificationCategory` model class with fields: `id` (String), `label` (String). Must implement `Parcelable` and provide `fromJson(JSONObject)` factory.
- **FR-1.5**: Define `PushNotification` model class with fields: `id` (String), `announcementId` (String), `campaignId` (String, nullable), `source` (String). Must implement `Parcelable` and provide `fromJson(JSONObject)` factory.
- **FR-1.6**: Define `FeedEngagementType` enum with values: `VIEWED("viewed")`, `CLICKED("clicked")`, `INTERACTED("interacted")`. Must include `getValue()` method and static `get(String)` lookup.
- **FR-1.7**: Define `FeedReadState` enum with values: `READ("read")`, `UNREAD("unread")`, `ALL("all")`. Must include `getValue()` method and static `get(String)` lookup.

### FR-2: NotificationFeedRequestBuilder (ENG-35197)

- **FR-2.1**: Define `NotificationFeedRequest` class with static inner `NotificationFeedRequestBuilder` class (matching existing SDK pattern like `UsersRequest`/`UsersRequestBuilder`)
- **FR-2.2**: Builder methods: `setLimit(int)`, `setReadState(FeedReadState)`, `setCategory(String)`, `setChannelId(String)`, `setTags(List<String>)`, `setDateFrom(String)`, `setDateTo(String)`. All return `NotificationFeedRequestBuilder` for chaining.
- **FR-2.3**: `build()` returns `NotificationFeedRequest` instance
- **FR-2.4**: `fetchNext(CometChat.CallbackListener<List<NotificationFeedItem>>)` — fetches next page from `GET /v3.0/campaigns/notification-feed` with configured query parameters
- **FR-2.5**: Cursor-based pagination: store cursor from response internally; when server returns no cursor, subsequent `fetchNext()` calls return empty list via success callback
- **FR-2.6**: Default limit: 20. Max limit: 100.
- **FR-2.7**: Query parameters sent: `limit`, `cursor`, `readState`, `category`, `channelId`, `tags` (comma-separated), `dateFrom`, `dateTo`

### FR-3: NotificationCategoriesRequestBuilder (ENG-35198)

- **FR-3.1**: Define `NotificationCategoriesRequest` class with static inner `NotificationCategoriesRequestBuilder` class
- **FR-3.2**: Builder methods: `setLimit(int)`. Returns builder for chaining.
- **FR-3.3**: `build()` returns `NotificationCategoriesRequest` instance
- **FR-3.4**: `fetchNext(CometChat.CallbackListener<List<NotificationCategory>>)` — fetches from `GET /v3.0/campaigns/templates/categories`
- **FR-3.5**: Cursor-based pagination (same behavior as FR-2.5)
- **FR-3.6**: Default limit: 50.

### FR-4: Engagement & Reporting Methods (ENG-35199)

All methods are static on `CometChat` class:

- **FR-4.1**: `markFeedItemAsDelivered(NotificationFeedItem, CallbackListener<Void>)` — POST to `/v3.0/campaigns/notification-feed/{id}/delivered`
- **FR-4.2**: `markFeedItemsAsDelivered(List<NotificationFeedItem>, CallbackListener<Void>)` — batch delivery reporting
- **FR-4.3**: `markFeedItemAsRead(NotificationFeedItem, CallbackListener<Void>)` — POST to `/v3.0/campaigns/notification-feed/{id}/read`
- **FR-4.4**: `markAllFeedItemsAsRead(CallbackListener<Void>)` — POST to `/v3/announcements/read`
- **FR-4.5**: `reportFeedEngagement(NotificationFeedItem, FeedEngagementType, CallbackListener<Void>)` — POST to `/v3.0/campaigns/notification-feed/{id}/engagement` with body `{ "type": engagementType.getValue() }`
- **FR-4.6**: `getNotificationFeedUnreadCount(CallbackListener<Integer>)` — GET `/v3.0/campaigns/notification-feed/unread-count`, returns count from response
- **FR-4.7**: `getNotificationFeedItem(String id, CallbackListener<NotificationFeedItem>)` — GET `/v3/announcements/{id}`, returns single item
- **FR-4.8**: `markPushNotificationDelivered(PushNotification, CallbackListener<Void>)` — PUT `/v3.0/campaigns/push-notifications/{id}/delivered`
- **FR-4.9**: `markPushNotificationClicked(PushNotification, CallbackListener<Void>)` — PUT `/v3.0/campaigns/push-notifications/{id}/clicked`
- **FR-4.10**: All engagement methods are idempotent — safe to call multiple times

### FR-5: NotificationFeedListener — WebSocket (ENG-35200)

- **FR-5.1**: Define `NotificationFeedListener` abstract class with callback: `onFeedItemReceived(NotificationFeedItem feedItem)`
- **FR-5.2**: `CometChat.addNotificationFeedListener(String listenerId, NotificationFeedListener listener)` — registers listener in a dedicated `ConcurrentHashMap`
- **FR-5.3**: `CometChat.removeNotificationFeedListener(String listenerId)` — removes listener
- **FR-5.4**: Filter WebSocket messages: only process messages where `type == "notification_feed_item"` AND `body.action == "sent"`
- **FR-5.5**: Parse `body.feedItem` into `NotificationFeedItem` and dispatch to all registered listeners on main thread
- **FR-5.6**: Malformed WebSocket payloads are logged and skipped (no crash)
- **FR-5.7**: Listener is independent from `MessageListener`, `GroupListener`, `CallListener` — separate HashMap, separate dispatch path

---

## Non-Functional Requirements

### NFR-1: Consistency with Existing SDK Patterns
- Use Java (not Kotlin) — matches existing codebase
- Models implement `Parcelable`
- Models provide `fromJson(JSONObject)` static factory methods
- JSON parsing uses `org.json.JSONObject` (no third-party JSON library)
- Request builders use inner builder class pattern
- Callbacks use `CometChat.CallbackListener<T>`
- Listener dispatch on main thread via existing `postOnMainThread` mechanism
- Constants defined in `CometChatConstants` inner interfaces

### NFR-2: Error Handling
- Network errors → `CometChatException` with error code and message
- 404 → `NOT_FOUND` error code
- 429 → `RATE_LIMITED` error code
- Auth errors → `AUTH_ERR` error code
- `fetchNext()` after cursor exhaustion → empty list (no error)
- Builder used without `build()` → configuration error

### NFR-3: Thread Safety
- Listener HashMap must be `ConcurrentHashMap`
- WebSocket event dispatch must happen on main thread
- `fetchNext()` must prevent concurrent calls (inProgress flag)

### NFR-4: Performance
- No local caching (Phase 1 — network only)
- Cursor-based pagination for efficient page traversal

---

## Android-Specific Implementation Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Builder naming | `NotificationFeedRequest` + inner `NotificationFeedRequestBuilder` | Matches existing SDK pattern (`UsersRequest`) |
| PushNotification model | Defined in Chat SDK (minimal class) | Methods need typed parameter; Push SDK can extend later |
| Content field type | `JSONObject` | Matches Kotlin reference, consistent with SDK internals |
| Pagination | Cursor-based | Backend API uses cursors; design doc specifies this |
| PBT enforcement | Full | Project has serialization, data transformations, stateful cursor management |
| Security enforcement | Full | Production-grade SDK |

---

## Extension Configuration

| Extension | Enabled | Decided At |
|---|---|---|
| Property-Based Testing | Yes (Full) | Requirements Analysis |
| Security Baseline | Yes (Full) | Requirements Analysis |

---

## Acceptance Criteria

- [ ] All 5 sub-issues (ENG-35196 through ENG-35200) implemented
- [ ] All models implement Parcelable and fromJson()
- [ ] Request builders follow inner builder class pattern with cursor pagination
- [ ] All CometChat static methods delegate to ApiConnection for HTTP calls
- [ ] WebSocket listener filters and dispatches only notification_feed_item messages
- [ ] All engagement methods are idempotent
- [ ] Error handling follows existing SDK patterns (CometChatException)
- [ ] No build errors or regressions in existing SDK functionality
