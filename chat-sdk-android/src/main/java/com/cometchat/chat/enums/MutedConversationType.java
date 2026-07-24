package com.cometchat.chat.enums;

/**
 * Created by Rohit Giri on 09/02/24.
 */
public enum MutedConversationType {
    ONE_ON_ONE("oneOnOne"),
    GROUP("group");

    private final String type;

    MutedConversationType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static MutedConversationType get(String value) {
        for (MutedConversationType type : values()) {
            if (type.getType().equals(value)) {
                return type;
            }
        }
        return null;
    }
}
