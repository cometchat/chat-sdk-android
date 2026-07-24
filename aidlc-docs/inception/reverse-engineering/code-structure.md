# Code Structure

## Build System
- **Type**: Gradle 7.4.2 (Android Gradle Plugin)
- **Configuration**: Root `build.gradle` + module `chat-sdk-android/build.gradle`
- **Plugin**: `com.android.library` + `maven-publish`
- **Variants**: debug, release, distributable (minified)

## Package Organization

```
com.cometchat.chat/
├── core/           (49 files) — SDK core logic, API, WebSocket, controllers
├── models/         (51 files) — Data models and entities
├── constants/      (2 files)  — String constants and keys
├── enums/          (10 files) — Type-safe enumerations
├── exceptions/     (1 file)   — CometChatException
├── helpers/        (2 files)  — CometChatHelper, Logger
└── utils/          (1 file)   — ContentEqualsHelper
```

## Key Classes/Modules

### Core Package (com.cometchat.chat.core)

**Public API:**
- `CometChat.java` — Main facade class (~10,700 lines), all public SDK methods
- `AppSettings.java` — SDK configuration (region, subscription type, hosts)
- `CallSettings.java` — Call configuration for voice/video

**Request Builders (Public):**
- `UsersRequest.java` — Paginated user list fetching
- `GroupsRequest.java` — Paginated group list fetching
- `MessagesRequest.java` — Paginated message fetching with filters
- `ConversationsRequest.java` — Paginated conversation list
- `GroupMembersRequest.java` — Paginated group member list
- `BannedGroupMembersRequest.java` — Banned members list
- `BlockedUsersRequest.java` — Blocked users list
- `ReactionsRequest.java` — Message reactions list

**Network (Internal):**
- `ApiConnection.java` — REST API client (OkHttp), all HTTP endpoints
- `WSConnection.java` — WebSocket client (OkHttp WebSocket)
- `AbstractRTTConnection.java` — Base class for real-time connections

**Controllers (Internal):**
- `DispatchController.java` — Routes WebSocket events to listeners
- `ConnectionController.java` — WebSocket connection lifecycle
- `ReconnectionController.java` — Auto-reconnection with backoff
- `PingController.java` — WebSocket keep-alive
- `AnalyticsController.java` — SDK analytics/metrics
- `CallManager.java` — Call lifecycle management

**Persistence (Internal):**
- `SQLiteHelper.java` — SQLiteOpenHelper subclass
- `SQLiteManager.java` — Database access manager
- `CurrentUserRepo.java` — Current user CRUD in SQLite
- `SettingsRepo.java` — Settings CRUD in SQLite
- `PreferenceHelper.java` — SharedPreferences wrapper
- `ConversationUpdateSettingsRepo.java` — Conversation update settings

**Events (Internal):**
- `CometChatEvent.java` — Base event class
- `CometChatMessageEvent.java` — Message received event
- `CometChatPresenceEvent.java` — User presence change
- `CometChatTypingEvent.java` — Typing indicator event
- `CometChatReceiptEvent.java` — Read/delivered receipt
- `CometChatAuthEvent.java` — Authentication event
- `CometChatPingEvent.java` / `CometChatPongEvent.java` — Keep-alive
- `CometChatMessageReactionEvent.java` — Reaction event
- `CometChatTransientMessageEvent.java` — Ephemeral message
- `CometChatStreamMessageEvent.java` — AI streaming event
- `CometChatInteractionEvent.java` — Interactive message interaction
- `CometChatModerationStatusChangeEvent.java` — Moderation status change

**Utilities (Internal):**
- `CometChatUtils.java` — SDK version, device info, resource ID, JSON helpers
- `MessageHelper.java` — Message parsing and construction
- `ExtensionManager.java` — Extension management
- `CometChatNotifications.java` — Push notification management
- `AuthResponse.java` — Auth response parsing

### Models Package (com.cometchat.chat.models)

**Core Entities:**
- `AppEntity.java` — Base class for User and Group
- `User.java` — User entity (uid, name, avatar, status, metadata)
- `Group.java` — Group entity (guid, name, type, members, scope)
- `GroupMember.java` — Group member with scope
- `CurrentUser.java` — Authenticated user with auth token
- `Conversation.java` — Chat conversation (1:1 or group)

**Message Types:**
- `BaseMessage.java` — Base message class (id, sender, receiver, timestamps)
- `TextMessage.java` — Text content message
- `MediaMessage.java` — Media attachment message (image, video, audio, file)
- `CustomMessage.java` — Custom JSON payload message
- `InteractiveMessage.java` — Structured UI message (forms, cards)
- `Action.java` — System action message (member joined, etc.)
- `TransientMessage.java` — Ephemeral non-persisted message

**AI Models:**
- `AIAssistantMessage.java` — AI assistant message
- `AIAssistantBaseEvent.java` — Base AI event
- `AIAssistantContentReceivedEvent.java` — AI content streaming
- `AIAssistantRunStartedEvent.java` / `AIAssistantRunFinishedEvent.java`
- `AIAssistantToolStartedEvent.java` / `AIAssistantToolEndedEvent.java`
- `AIAssistantToolArgumentEvent.java` / `AIAssistantToolResultEvent.java`
- `AIToolCall.java` / `AIToolCallFunction.java` / `AIToolArgumentMessage.java` / `AIToolResultMessage.java`

**Supporting Models:**
- `Attachment.java` — File attachment metadata
- `MessageReceipt.java` — Delivery/read receipt
- `TypingIndicator.java` — Typing event data
- `Reaction.java` / `ReactionCount.java` / `ReactionEvent.java` — Reactions
- `InteractionReceipt.java` / `Interaction.java` / `InteractionGoal.java` — Interactive message interactions
- `FlagDetail.java` / `FlagReason.java` — Content moderation
- `UserPresence.java` — User online/offline status
- `CCExtension.java` — SDK extension metadata
- `AudioMode.java` — Audio routing mode for calls
- `ConversationUpdateSettings.java` — Conversation update configuration

**Notification Models:**
- `NotificationPreferences.java` — Push notification preferences
- `PushPreferences.java` — Push platform preferences
- `MutePreferences.java` — Mute settings
- `MutedConversation.java` / `UnmutedConversation.java` — Mute state
- `OneOnOnePreferences.java` / `GroupPreferences.java` — Per-type preferences
- `DaySchedule.java` — DND schedule
- `ItemSettings.java` — Notification item settings
- `MainVideoContainerSetting.java` — Video call UI settings

### Constants Package
- `CometChatConstants.java` — All SDK constants (params, keys, types, errors)
- `CometChatNotificationsConstants.java` — Notification-specific constants

### Enums Package
- `AttachmentType.java` — Media attachment types
- `ModerationStatus.java` — Content moderation statuses
- `PushPlatforms.java` — Push notification platforms (FCM, APNS)
- `DayOfWeek.java` — Days for DND scheduling
- `DNDOptions.java` — Do Not Disturb options
- `MemberActionsOptions.java` — Group member action types
- `MessagesOptions.java` — Message notification options
- `ReactionsOptions.java` — Reaction notification options
- `RepliesOptions.java` — Reply notification options
- `MutedConversationType.java` — Muted conversation types

### Exceptions Package
- `CometChatException.java` — SDK exception with error code and message

### Helpers Package
- `CometChatHelper.java` — JSON parsing utilities for messages
- `Logger.java` — Internal logging wrapper

### Utils Package
- `ContentEqualsHelper.java` — Deep value comparison utility

## Design Patterns

### Singleton Pattern
- **Location**: `CometChat` class (static instance)
- **Purpose**: Single SDK instance per application
- **Implementation**: Static fields and methods, lazy initialization in `init()`

### Observer/Listener Pattern
- **Location**: `CometChat` listener registration methods
- **Purpose**: Decouple event producers from consumers
- **Implementation**: `ConcurrentHashMap<String, Listener>` with string keys for add/remove

### Builder Pattern
- **Location**: `AppSettings.AppSettingsBuilder`, all `*Request.Builder` classes
- **Purpose**: Fluent configuration of complex objects
- **Implementation**: Inner builder classes with method chaining

### Facade Pattern
- **Location**: `CometChat` class
- **Purpose**: Simplify complex subsystem (API + WS + persistence) behind single interface
- **Implementation**: All public methods delegate to internal components

### Repository Pattern
- **Location**: `CurrentUserRepo`, `SettingsRepo`
- **Purpose**: Abstract data persistence operations
- **Implementation**: Static methods wrapping SQLite operations

## Critical Dependencies

### OkHttp 3.12.x
- **Version**: 3.12.+ (floating patch version)
- **Usage**: HTTP client for REST API calls and WebSocket connections
- **Purpose**: Network communication layer

### AndroidX Lifecycle
- **Version**: 1.1.1 (extensions, common-java8)
- **Usage**: `ProcessLifecycleOwner` for app foreground/background detection
- **Purpose**: Auto-connect/disconnect WebSocket based on app state

### CometChat Calls SDK
- **Version**: 4.0.0
- **Usage**: compileOnly — optional voice/video calling
- **Purpose**: Calling features (not bundled, consumer must add separately)
