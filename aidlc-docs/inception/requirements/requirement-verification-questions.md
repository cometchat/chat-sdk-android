# Requirements Verification Questions — ENG-35195

The common design doc is comprehensive. These questions focus on Android-specific implementation details not covered in the common spec.

## Question 1
The common design doc shows `NotificationFeedRequestBuilder` as the class name (matching the TypeScript canonical reference). However, the existing Android SDK uses a pattern where the builder is a static inner class (e.g., `UsersRequest` has `UsersRequest.UsersRequestBuilder`). Which naming convention should we follow?

A) `NotificationFeedRequest` with inner `NotificationFeedRequest.NotificationFeedRequestBuilder` (matches existing SDK pattern: `UsersRequest` / `UsersRequest.UsersRequestBuilder`)
B) `NotificationFeedRequestBuilder` as a standalone class with `build()` returning itself (matches the common design doc literally)
C) Other (please describe after [Answer]: tag below)

[Answer]: Should match the current pattern. 

## Question 2
The `PushNotification` model is described as "provided by the CometChat Push Notification SDK — not defined in Chat SDK." However, the Chat SDK methods `markPushNotificationDelivered` and `markPushNotificationClicked` accept this object. How should we handle this dependency?

A) Define a minimal `PushNotification` class in the Chat SDK (just the fields needed: id, announcementId, campaignId, source) — the Push SDK can extend or wrap it later
B) Accept a `JSONObject` parameter instead of a typed model — the consumer passes the raw push payload
C) Accept individual parameters (String id, String announcementId, String campaignId) instead of a model object
D) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 3
Should the `NotificationFeedItem.content` field (Card_Schema JSON) be stored as `JSONObject` (as shown in the Kotlin reference) or as a `HashMap<String, Object>` for easier consumption by UI Kit?

A) `JSONObject` — matches the Kotlin reference in the common design doc and is consistent with how the SDK handles JSON internally
B) `HashMap<String, Object>` — easier for consumers to work with, avoids JSONException handling
C) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 4
Should property-based testing (PBT) rules be enforced for this project?

A) Yes — enforce all PBT rules as blocking constraints (recommended for projects with business logic, data transformations, serialization, or stateful components)
B) Partial — enforce PBT rules only for pure functions and serialization round-trips (suitable for projects with limited algorithmic complexity)
C) No — skip all PBT rules (suitable for simple CRUD applications, UI-only projects, or thin integration layers with no significant business logic)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 5
Should security extension rules be enforced for this project?

A) Yes — enforce all SECURITY rules as blocking constraints (recommended for production-grade applications)
B) No — skip all SECURITY rules (suitable for PoCs, prototypes, and experimental projects)
C) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 6
The existing SDK uses page-based pagination (`nextPage`, `totalPages`) in `UsersRequest`. The common design doc specifies cursor-based pagination for the notification feed. The backend API uses cursor-based. Should we follow the cursor-based approach from the design doc (which differs from existing SDK patterns)?

A) Yes — use cursor-based pagination as specified in the common design doc (the backend API uses cursors, and this is the correct approach for this feature)
B) No — adapt to page-based pagination to match existing SDK patterns
C) Other (please describe after [Answer]: tag below)

[Answer]: A
