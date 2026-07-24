package com.cometchat.chat.enums;

/**
 * Created by Rohit Giri on 15/02/24.
 */
public enum PushPlatforms {
    FCM_FLUTTER_ANDROID("fcm_flutter_android"),
    FCM_ANDROID("fcm_android");

    private final String value;

    PushPlatforms(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PushPlatforms get(String value) {
        for (PushPlatforms platform : values()) {
            if (platform.getValue().equals(value)) {
                return platform;
            }
        }
        return null;
    }

}
