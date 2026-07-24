package com.cometchat.chat.enums;

/**
 * Created by Rohit Giri on 09/02/24.
 */
public enum ReactionsOptions {
    DONT_SUBSCRIBE(1),
    SUBSCRIBE_TO_REACTIONS_ON_OWN_MESSAGES(2),
    SUBSCRIBE_TO_REACTIONS_ON_ALL_MESSAGES(3);

    private final int value;

    ReactionsOptions(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ReactionsOptions get(int value) {
        for (ReactionsOptions option : values()) {
            if (option.getValue() == value) {
                return option;
            }
        }
        return null;
    }

}
