# API Documentation

## Public API Surface

The SDK exposes its API through the static `CometChat` class and associated model/builder classes.

## REST APIs (via ApiConnection)

### Authentication
| Method | Path | Purpose |
|--------|------|---------|
| POST | `/users/{uid}/auth_tokens` | Login with UID + API key |
| GET | `/me` | Login with auth token |
| DELETE | `/me` | Logout |

### Messages
| Method | Path | Purpose |
|--------|------|---------|
| POST | `/messages` | Send message (text, media, custom, interactive) |
| GET | `/messages` | Fetch messages (paginated) |
| GET | `/messages/{id}` | Get message details |
| PUT | `/messages/{id}` | Edit message |
| DELETE | `/messages/{id}` | Delete message |
| POST | `/messages/{id}/flagged` | Flag message for moderation |
| POST | `/messages/{id}/interacted` | Mark interactive message as interacted |

### Users
| Method | Path | Purpose |
|--------|------|---------|
| GET | `/users` | List users (paginated) |
| GET | `/users/{uid}` | Get user details |
| POST | `/blockedusers` | Block users |
| DELETE | `/blockedusers` | Unblock users |
| GET | `/blockedusers` | List blocked users |

### Groups
| Method | Path | Purpose |
|--------|------|---------|
| POST | `/groups` | Create group |
| GET | `/groups` | List groups (paginated) |
| GET | `/groups/{guid}` | Get group details |
| PUT | `/groups/{guid}` | Update group |
| DELETE | `/groups/{guid}` | Delete group |
| POST | `/groups/{guid}/members` | Join group |
| DELETE | `/groups/{guid}/members` | Leave group |
| GET | `/groups/{guid}/members` | List group members |
| DELETE | `/groups/{guid}/members/{uid}` | Kick member |
| PUT | `/groups/{guid}/members/{uid}` | Change member scope |
| PUT | `/groups/{guid}/bannedusers/{uid}` | Ban member |
| DELETE | `/groups/{guid}/bannedusers/{uid}` | Unban member |
| GET | `/groups/{guid}/bannedusers` | List banned members |

### Conversations
| Method | Path | Purpose |
|--------|------|---------|
| GET | `/users/{uid}/messages` | Get user conversation messages |
| GET | `/groups/{guid}/messages` | Get group conversation messages |

### Receipts
| Method | Path | Purpose |
|--------|------|---------|
| POST | `/{type}/{id}/conversation/delivered` | Mark as delivered |
| POST | `/{type}/{id}/conversation/read` | Mark as read |

### Calls
| Method | Path | Purpose |
|--------|------|---------|
| POST | `/calls` | Initiate call |
| PUT | `/calls/{sessionId}` | Update call status (accept/reject/cancel) |

## Public SDK Methods (CometChat class)

### Initialization
```java
static void init(Context context, String appID, AppSettings settings, CallbackListener<String> listener)
static boolean isInitialized()
```

### Authentication
```java
static void login(String uid, String apiKey, CallbackListener<User> listener)
static void login(String authToken, CallbackListener<User> listener)
static void logout(CallbackListener<String> listener)
static User getLoggedInUser()
```

### Connection Management
```java
static void connect(CallbackListener<String> listener)
static void disconnect(CallbackListener<String> listener)
static String getConnectionStatus() // "connected", "connecting", "disconnected"
```

### Messaging
```java
static void sendMessage(TextMessage message, CallbackListener<TextMessage> listener)
static void sendMediaMessage(MediaMessage message, CallbackListener<MediaMessage> listener)
static void sendCustomMessage(CustomMessage message, CallbackListener<CustomMessage> listener)
static void sendInteractiveMessage(InteractiveMessage message, CallbackListener<InteractiveMessage> listener)
static void editMessage(BaseMessage message, CallbackListener<BaseMessage> listener)
static void deleteMessage(long messageId, CallbackListener<BaseMessage> listener)
static void sendTransientMessage(TransientMessage message)
```

### Typing Indicators
```java
static void startTyping(TypingIndicator typingIndicator)
static void endTyping(TypingIndicator typingIndicator)
```

### Receipts
```java
static void markAsRead(BaseMessage message, CallbackListener<Void> listener)
static void markAsDelivered(BaseMessage message, CallbackListener<Void> listener)
```

### Users
```java
static void getUser(String uid, CallbackListener<User> listener)
static void blockUsers(List<String> uids, CallbackListener<HashMap<String, String>> listener)
static void unblockUsers(List<String> uids, CallbackListener<HashMap<String, String>> listener)
```

### Groups
```java
static void createGroup(Group group, CallbackListener<Group> listener)
static void joinGroup(String guid, String groupType, String password, CallbackListener<Group> listener)
static void leaveGroup(String guid, CallbackListener<String> listener)
static void updateGroup(Group group, CallbackListener<Group> listener)
static void deleteGroup(String guid, CallbackListener<String> listener)
static void getGroup(String guid, CallbackListener<Group> listener)
static void kickGroupMember(String uid, String guid, CallbackListener<String> listener)
static void banGroupMember(String uid, String guid, CallbackListener<String> listener)
static void unbanGroupMember(String uid, String guid, CallbackListener<String> listener)
static void updateGroupMemberScope(String uid, String guid, String scope, CallbackListener<String> listener)
static void transferGroupOwnership(String guid, String uid, CallbackListener<String> listener)
```

### Reactions
```java
static void addReaction(long messageId, String emoji, CallbackListener<BaseMessage> listener)
static void removeReaction(long messageId, String emoji, CallbackListener<BaseMessage> listener)
```

### Content Moderation
```java
static void flagMessage(BaseMessage message, String reason, CallbackListener<Void> listener)
static void getFlagReasons(CallbackListener<List<FlagReason>> listener)
```

### Listener Registration
```java
static void addMessageListener(String listenerId, MessageListener listener)
static void removeMessageListener(String listenerId)
static void addUserListener(String listenerId, UserListener listener)
static void removeUserListener(String listenerId)
static void addGroupListener(String listenerId, GroupListener listener)
static void removeGroupListener(String listenerId)
static void addCallListener(String listenerId, CallListener listener)
static void removeCallListener(String listenerId)
static void addConnectionListener(String listenerId, ConnectionListener listener)
static void removeConnectionListener(String listenerId)
static void addLoginListener(String listenerId, LoginListener listener)
static void removeLoginListener(String listenerId)
```

## Listener Interfaces

### MessageListener
```java
void onTextMessageReceived(TextMessage message)
void onMediaMessageReceived(MediaMessage message)
void onCustomMessageReceived(CustomMessage message)
void onInteractiveMessageReceived(InteractiveMessage message)
void onTypingStarted(TypingIndicator typingIndicator)
void onTypingEnded(TypingIndicator typingIndicator)
void onMessagesDelivered(MessageReceipt receipt)
void onMessagesRead(MessageReceipt receipt)
void onMessageEdited(BaseMessage message)
void onMessageDeleted(BaseMessage message)
void onTransientMessageReceived(TransientMessage message)
void onMessageReactionAdded(ReactionEvent event)
void onMessageReactionRemoved(ReactionEvent event)
```

### UserListener
```java
void onUserOnline(User user)
void onUserOffline(User user)
```

### GroupListener
```java
void onGroupMemberJoined(Action action, User joinedUser, Group joinedGroup)
void onGroupMemberLeft(Action action, User leftUser, Group leftGroup)
void onGroupMemberKicked(Action action, User kickedUser, User kickedBy, Group kickedFrom)
void onGroupMemberBanned(Action action, User bannedUser, User bannedBy, Group bannedFrom)
void onGroupMemberUnbanned(Action action, User unbannedUser, User unbannedBy, Group unbannedFrom)
void onGroupMemberScopeChanged(Action action, User updatedBy, User updatedUser, String scopeChangedTo, String scopeChangedFrom, Group group)
void onMemberAddedToGroup(Action action, User addedby, User userAdded, Group addedTo)
void onGroupMemberOwnershipTransferred(Group group, GroupMember newOwner)
```

### ConnectionListener
```java
void onConnected()
void onConnecting()
void onDisconnected()
void onFeatureThrottled()
```

## Data Models

### User
| Field | Type | Description |
|-------|------|-------------|
| uid | String | Unique user identifier |
| name | String | Display name |
| avatar | String | Avatar URL |
| status | String | "online" or "offline" |
| role | String | User role |
| metadata | JSONObject | Custom metadata |
| lastActiveAt | long | Last active timestamp |
| tags | List<String> | User tags |

### Group
| Field | Type | Description |
|-------|------|-------------|
| guid | String | Unique group identifier |
| name | String | Group name |
| type | String | "public", "private", "password" |
| icon | String | Group icon URL |
| description | String | Group description |
| owner | String | Owner UID |
| membersCount | int | Number of members |
| scope | String | Current user's scope in group |
| hasJoined | boolean | Whether current user has joined |

### BaseMessage
| Field | Type | Description |
|-------|------|-------------|
| id | long | Message ID |
| muid | String | Client-generated unique ID |
| sender | User | Message sender |
| receiverUid | String | Receiver UID or GUID |
| receiverType | String | "user" or "group" |
| type | String | Message type |
| category | String | Message category |
| sentAt | long | Sent timestamp |
| deliveredAt | long | Delivered timestamp |
| readAt | long | Read timestamp |
| metadata | JSONObject | Custom metadata |
| parentMessageId | long | Thread parent message ID |
| replyCount | int | Number of replies |
| reactions | List<ReactionCount> | Reaction counts |

### Conversation
| Field | Type | Description |
|-------|------|-------------|
| conversationId | String | Conversation identifier |
| conversationType | String | "user" or "group" |
| lastMessage | BaseMessage | Last message in conversation |
| conversationWith | AppEntity | User or Group entity |
| unreadMessageCount | int | Unread message count |
| updatedAt | long | Last update timestamp |
| tags | List<String> | Conversation tags |
