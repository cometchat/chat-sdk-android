package com.cometchat.chat.enums;

import java.util.Objects;

public enum ModerationStatus {
    UNMODERATED("unmoderated"),
    PENDING("pending"),
    APPROVED("approved"),
    DISAPPROVED("disapproved");

    private final String value;

    ModerationStatus(String status) {
        this.value = status;
    }

    public String getValue() {
        return value;
    }

    public static ModerationStatus get(String value) {
        for (ModerationStatus status : values()) {
            if (Objects.equals(status.getValue(), value)) {
                return status;
            }
        }
        return null;
    }
}
