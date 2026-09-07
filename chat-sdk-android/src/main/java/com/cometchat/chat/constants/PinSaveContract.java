package com.cometchat.chat.constants;

/**
 * {@code PIN_SAVE_CONTRACT} — the cross-platform parse/behaviour contract for the Pin &amp; Save
 * Message feature (front-end design doc §4.1). These flags document the invariants every
 * platform's parse site must honour; on Android they are enforced by the single parse chokepoint
 * {@link com.cometchat.chat.models.BaseMessage#applyPinSaveAttributes(com.cometchat.chat.models.BaseMessage, org.json.JSONObject)}
 * and asserted by the shared golden vectors (§7.8).
 *
 * <p>The pin/save JSON keys are read in exactly one place; no other code path should reference
 * them directly.
 *
 * @since <b>v5</b>
 */
public final class PinSaveContract {

    private PinSaveContract() {
    }

    /** Absent {@code pinnedAt} ⇒ not pinned. Never an error, never {@code 0} treated as "set". */
    public static final boolean ABSENT_PIN_MEANS_NOT_PINNED = true;

    /** Absent {@code savedAt} ⇒ not saved. {@code savedAt} is only present in the acting user's context. */
    public static final boolean ABSENT_SAVED_MEANS_NOT_SAVED = true;

    /** {@code pinnedBy} sentinel denoting an admin / global (system) pin. */
    public static final String SYSTEM_PINNER_SENTINEL = CometChatConstants.MessageKeys.PINNED_BY_SYSTEM;

    /** Single-pinner model: {@code pinnedBy}/{@code pinnedAt} reflect the most recent pinner. */
    public static final boolean SINGLE_PINNER = true;

    /** The realtime socket frame carries the full message, including the pin/save attributes. */
    public static final boolean SOCKET_FRAME_CARRIES_PIN_ATTRS = true;
}
