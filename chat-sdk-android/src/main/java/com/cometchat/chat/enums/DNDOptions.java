package com.cometchat.chat.enums;

/**
 * Created by Rohit Giri on 09/02/24.
 */
public enum DNDOptions {
    DISABLED(1),
    ENABLED(2);

    private final int value;

    DNDOptions(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static DNDOptions get(int value) {
        for (DNDOptions option : values()) {
            if (option.getValue() == value) {
                return option;
            }
        }
        return null;
    }

}
