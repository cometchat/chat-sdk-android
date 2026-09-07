package com.cometchat.chat.constants;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Pin &amp; Save Message / the {@code PIN_SAVE_CONTRACT} invariant flags.
 *
 * <p>{@link PinSaveContract} documents the cross-platform parse contract as constants. The one
 * load-bearing value is {@link PinSaveContract#SYSTEM_PINNER_SENTINEL}: both
 * {@code BaseMessage.isSystemPinned()} and {@code Conversation.isSystemPinned()} compare
 * {@code pinnedBy} against it, so if it ever drifts from the wire value
 * {@code CometChatConstants.MessageKeys.PINNED_BY_SYSTEM} system-pin detection silently breaks on
 * both models. These are cheap regression guards for the documented contract.
 */
public class PinSaveContractTest {

    @Test
    public void systemPinnerSentinel_aliasesTheWireValue() {
        assertEquals("the sentinel must stay identical to the pinnedBy wire value",
                CometChatConstants.MessageKeys.PINNED_BY_SYSTEM,
                PinSaveContract.SYSTEM_PINNER_SENTINEL);
    }

    @Test
    public void invariantFlags_encodeTheConfirmedContract() {
        assertTrue("absent pinnedAt ⇒ not pinned", PinSaveContract.ABSENT_PIN_MEANS_NOT_PINNED);
        assertTrue("absent savedAt ⇒ not saved", PinSaveContract.ABSENT_SAVED_MEANS_NOT_SAVED);
        assertTrue("single-pinner model", PinSaveContract.SINGLE_PINNER);
        assertTrue("the socket frame carries the pin/save attributes", PinSaveContract.SOCKET_FRAME_CARRIES_PIN_ATTRS);
    }
}
