package com.cometchat.chat.enums;

public enum AttachmentType {
    IMAGE("image"),
    VIDEO("video"),
    AUDIO("audio"),
    FILE("file");

    private final String type;

    AttachmentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static AttachmentType get(String type) {
        for (AttachmentType attachmentType : values()) {
            if (attachmentType.getType().equalsIgnoreCase(type)) {
                return attachmentType;
            }
        }
        return null;
    }
}