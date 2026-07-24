package com.cometchat.chat.core;

import android.content.Context;
import androidx.annotation.NonNull;

import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adityagokula on 29/03/19.
 */

class ExtensionManager {

    private static List<CometChatExtension> extensionsList = new ArrayList<>();
    private static Settings settings = null;

    public static void addCometChatExtension(Context context, @NonNull CometChatExtension cometChatExtension) {
        extensionsList.add(cometChatExtension);
        callOnInit(context, PreferenceHelper.getAppID(), CurrentUserRepo.getLoggedInUser());
    }

    public static List<CometChatExtension> getExtensionsList() {
        return extensionsList;
    }

    static void callOnInit(Context context, String appId, User user) {
        if (settings == null)
            settings = SettingsRepo.getSettings();
        if (extensionsList.size() > 0) {
            for (CometChatExtension extension : extensionsList) {
                if (CometChatUtils.isExtensionEnabled(settings, extension.getExtensionId())) {
                    extension.onInit(context, appId, user);
                }
            }
        }
    }

    static void callOnLogin(User user) {
        if (settings == null)
            settings = SettingsRepo.getSettings();
        if (extensionsList.size() > 0) {
            for (CometChatExtension extension : extensionsList) {
                if (CometChatUtils.isExtensionEnabled(settings, extension.getExtensionId())) {
                    extension.onLogin(user);
                }
            }
        }
    }

    static BaseMessage callBeforeMessageSent(BaseMessage baseMessage) {
        if (settings == null)
            settings = SettingsRepo.getSettings();
        BaseMessage tempMessage = baseMessage;
        if (extensionsList.size() > 0) {
            for (CometChatExtension extension : extensionsList) {
                if (CometChatUtils.isExtensionEnabled(settings, extension.getExtensionId())) {
                    tempMessage = extension.beforeMessageSent(tempMessage);
                }
            }
        }
        return tempMessage;
    }

    static BaseMessage callAfterMessageSent(BaseMessage baseMessage) {
        if (settings == null)
            settings = SettingsRepo.getSettings();
        BaseMessage tempMessage = baseMessage;
        if (extensionsList.size() > 0) {
            for (CometChatExtension extension : extensionsList) {
                if (CometChatUtils.isExtensionEnabled(settings, extension.getExtensionId())) {
                    tempMessage = extension.afterMessageSent(tempMessage);
                }
            }
        }
        return tempMessage;
    }

    static BaseMessage callOnMessageReceived(BaseMessage baseMessage) {
        if (settings == null)
            settings = SettingsRepo.getSettings();
        BaseMessage tempMessage = baseMessage;
        if (extensionsList.size() > 0) {
            for (CometChatExtension extension : extensionsList) {
                if (CometChatUtils.isExtensionEnabled(settings, extension.getExtensionId())) {
                    tempMessage = extension.onMessageReceived(tempMessage);
                }
            }
        }
        return tempMessage;
    }

    static void callOnLogout() {
        if (settings == null)
            settings = SettingsRepo.getSettings();
        if (extensionsList.size() > 0) {
            for (CometChatExtension extension : extensionsList) {
                if (CometChatUtils.isExtensionEnabled(settings, extension.getExtensionId())) {
                    extension.onLogout();
                }
            }
        }
    }

    static List<BaseMessage> callOnMessageListFetched(List<BaseMessage> messages) {
        if (settings == null)
            settings = SettingsRepo.getSettings();
        List<BaseMessage> extMessages = messages;
        if (extensionsList.size() > 0) {
            for (CometChatExtension extension : extensionsList) {
                if (CometChatUtils.isExtensionEnabled(settings, extension.getExtensionId())) {
                    extMessages = extension.onMessageListFetched(messages);
                }
            }
        }
        return extMessages;
    }

}
