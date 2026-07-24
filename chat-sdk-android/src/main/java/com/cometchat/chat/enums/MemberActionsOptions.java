package com.cometchat.chat.enums;

/**
 * Created by Rohit Giri on 09/02/24.
 */
public enum MemberActionsOptions {
    DONT_SUBSCRIBE(1),
    SUBSCRIBE(2);

    private final int value;

    MemberActionsOptions(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MemberActionsOptions get(int value) {
        for (MemberActionsOptions option : values()) {
            if (option.getValue() == value) {
                return option;
            }
        }
        return null;
    }

}
