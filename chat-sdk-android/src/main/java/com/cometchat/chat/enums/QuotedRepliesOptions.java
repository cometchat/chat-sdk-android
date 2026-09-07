package com.cometchat.chat.enums;

/**
 * Subscription levels for the <b>quoted replies</b> notification preference.
 *
 * <p>A quoted reply is a message that quotes another message. It is a separate preference from
 * {@link RepliesOptions}, which covers threaded replies, and the two are set independently. This
 * option set is dedicated to quoted replies and must not be interchanged with any other preference's
 * option set, even where the numeric values coincide.
 *
 * <p>The numeric values are the wire contract and are fixed.
 */
public enum QuotedRepliesOptions {
    /** Do not notify me about quoted replies. */
    DONT_SUBSCRIBE(1),
    /** Notify me about all quoted replies. */
    SUBSCRIBE_TO_ALL(2),
    /** Notify me about quoted replies that mention me. */
    SUBSCRIBE_TO_MENTIONS(3),
    /** Notify me when a message I authored is quoted. */
    SUBSCRIBE_TO_QUOTES_ON_OWN_MESSAGES(4);

    private final int value;

    QuotedRepliesOptions(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Resolves a wire value to its option.
     *
     * @param value the numeric wire value
     * @return the matching option, or {@code null} if the value is not recognised
     */
    public static QuotedRepliesOptions get(int value) {
        for (QuotedRepliesOptions option : values()) {
            if (option.getValue() == value) {
                return option;
            }
        }
        return null;
    }

}
