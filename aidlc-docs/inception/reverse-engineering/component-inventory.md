# Component Inventory

## Application Packages

### Core Package (com.cometchat.chat.core) — 49 files
- `CometChat.java` — Public SDK facade (all public API methods)
- `ApiConnection.java` — REST API client
- `WSConnection.java` — WebSocket real-time client
- `AbstractRTTConnection.java` — Base real-time connection class
- `AppSettings.java` — SDK configuration
- `CallSettings.java` — Call configuration
- `Call.java` — Call model/operations
- `CallManager.java` — Call lifecycle management
- `DispatchController.java` — WebSocket event routing
- `ConnectionController.java` — Connection lifecycle management
- `ReconnectionController.java` — Auto-reconnection logic
- `PingController.java` — WebSocket keep-alive
- `AnalyticsController.java` — SDK analytics
- `AnalyticsRunnable.java` — Analytics background task
- `DisconnectionTimerTask.java` — Disconnection timer
- `CometChatUtils.java` — SDK utilities
- `MessageHelper.java` — Message parsing utilities
- `ExtensionManager.java` — Extension management
- `CometChatNotifications.java` — Push notification management
- `CometChatExtension.java` — Extension base
- `AuthResponse.java` — Auth response parsing
- `Settings.java` — Internal settings model
- `SQLiteHelper.java` — SQLite database helper
- `SQLiteManager.java` — Database access manager
- `CurrentUserRepo.java` — Current user persistence
- `SettingsRepo.java` — Settings persistence
- `ConversationUpdateSettingsRepo.java` — Conversation settings persistence
- `PreferenceHelper.java` — SharedPreferences wrapper
- `UsersRequest.java` — Users pagination builder
- `GroupsRequest.java` — Groups pagination builder
- `MessagesRequest.java` — Messages pagination builder
- `ConversationsRequest.java` — Conversations pagination builder
- `GroupMembersRequest.java` — Group members pagination builder
- `BannedGroupMembersRequest.java` — Banned members pagination builder
- `BlockedUsersRequest.java` — Blocked users pagination builder
- `ReactionsRequest.java` — Reactions pagination builder
- `CometChatEvent.java` — Base WebSocket event
- `CometChatMessageEvent.java` — Message event
- `CometChatPresenceEvent.java` — Presence event
- `CometChatTypingEvent.java` — Typing event
- `CometChatReceiptEvent.java` — Receipt event
- `CometChatAuthEvent.java` — Auth event
- `CometChatPingEvent.java` — Ping event
- `CometChatPongEvent.java` — Pong event
- `CometChatMessageReactionEvent.java` — Reaction event
- `CometChatTransientMessageEvent.java` — Transient message event
- `CometChatStreamMessageEvent.java` — AI streaming event
- `CometChatInteractionEvent.java` — Interactive message event
- `CometChatModerationStatusChangeEvent.java` — Moderation event

### Models Package (com.cometchat.chat.models) — 51 files
- `AppEntity.java` — Base entity (User/Group parent)
- `User.java` — User model
- `CurrentUser.java` — Authenticated user model
- `Group.java` — Group model
- `GroupMember.java` — Group member model
- `Conversation.java` — Conversation model
- `BaseMessage.java` — Base message model
- `TextMessage.java` — Text message
- `MediaMessage.java` — Media message
- `CustomMessage.java` — Custom message
- `InteractiveMessage.java` — Interactive message
- `Action.java` — System action message
- `TransientMessage.java` — Ephemeral message
- `Attachment.java` — File attachment
- `MessageReceipt.java` — Delivery/read receipt
- `TypingIndicator.java` — Typing indicator
- `Reaction.java` — Single reaction
- `ReactionCount.java` — Reaction count per emoji
- `ReactionEvent.java` — Reaction event
- `Interaction.java` — Interactive message interaction
- `InteractionGoal.java` — Interaction goal
- `InteractionReceipt.java` — Interaction receipt
- `FlagDetail.java` — Flag detail
- `FlagReason.java` — Flag reason
- `UserPresence.java` — User presence
- `CCExtension.java` — Extension metadata
- `AudioMode.java` — Audio routing mode
- `ConversationUpdateSettings.java` — Conversation update config
- `NotificationPreferences.java` — Notification preferences
- `PushPreferences.java` — Push preferences
- `MutePreferences.java` — Mute preferences
- `MutedConversation.java` — Muted conversation
- `UnmutedConversation.java` — Unmuted conversation
- `OneOnOnePreferences.java` — 1:1 notification preferences
- `GroupPreferences.java` — Group notification preferences
- `DaySchedule.java` — DND day schedule
- `ItemSettings.java` — Notification item settings
- `MainVideoContainerSetting.java` — Video call UI settings
- `AIAssistantMessage.java` — AI assistant message
- `AIAssistantBaseEvent.java` — Base AI event
- `AIAssistantContentReceivedEvent.java` — AI content streaming
- `AIAssistantMessageEndedEvent.java` — AI message ended
- `AIAssistantRunStartedEvent.java` — AI run started
- `AIAssistantRunFinishedEvent.java` — AI run finished
- `AIAssistantToolStartedEvent.java` — AI tool started
- `AIAssistantToolEndedEvent.java` — AI tool ended
- `AIAssistantToolArgumentEvent.java` — AI tool argument
- `AIAssistantToolResultEvent.java` — AI tool result
- `AIToolCall.java` — AI tool call model
- `AIToolCallFunction.java` — AI tool function model
- `AIToolArgumentMessage.java` — AI tool argument message
- `AIToolResultMessage.java` — AI tool result message

### Constants Package (com.cometchat.chat.constants) — 2 files
- `CometChatConstants.java` — All SDK constants
- `CometChatNotificationsConstants.java` — Notification constants

### Enums Package (com.cometchat.chat.enums) — 10 files
- `AttachmentType.java`, `ModerationStatus.java`, `PushPlatforms.java`
- `DayOfWeek.java`, `DNDOptions.java`, `MemberActionsOptions.java`
- `MessagesOptions.java`, `ReactionsOptions.java`, `RepliesOptions.java`
- `MutedConversationType.java`

### Exceptions Package (com.cometchat.chat.exceptions) — 1 file
- `CometChatException.java`

### Helpers Package (com.cometchat.chat.helpers) — 2 files
- `CometChatHelper.java`, `Logger.java`

### Utils Package (com.cometchat.chat.utils) — 1 file
- `ContentEqualsHelper.java`

## Test Packages

### Unit Tests (chat-sdk-android/src/test/) — 9 files
- Content equality tests for models
- TimeoutResolutionTest

### Android Tests (chat-sdk-android/src/androidTest/)
- Instrumentation test infrastructure
- Custom test runner script (`scripts/instrumentation.sh`)

## Total Count
- **Total Source Files**: 116
- **Core**: 49
- **Models**: 51
- **Constants**: 2
- **Enums**: 10
- **Exceptions**: 1
- **Helpers**: 2
- **Utils**: 1
- **Test Files**: ~9 unit + instrumentation tests
