package com.cometchat.chat.enums;

/**
 * Created by Rohit Giri on 09/02/24.
 */
public enum RepliesOptions {
    DONT_SUBSCRIBE(1),
    SUBSCRIBE_TO_ALL(2),
    SUBSCRIBE_TO_MENTIONS(3);

    private final int value;

    RepliesOptions(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static RepliesOptions get(int value) {
        for (RepliesOptions option : values()) {
            if (option.getValue() == value) {
                return option;
            }
        }
        return null;
    }

}
