package com.cometchat.chat.core;

import android.content.Context;
import androidx.annotation.NonNull;

import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.User;

import java.util.List;

/**
 * Created by adityagokula on 29/03/19.
 */

public abstract class CometChatExtension {

    public String extensionId;

    public abstract @NonNull String getExtensionId();


    public void onInit(@NonNull Context context, @NonNull String appId, User user) {

    }

    public void onLogin(@NonNull User user) {
    }

    public BaseMessage beforeMessageSent(@NonNull BaseMessage message) {
        return message;
    }

    public BaseMessage afterMessageSent(@NonNull BaseMessage message) {
        return message;
    }

    public BaseMessage onMessageReceived(@NonNull BaseMessage message) {
        return message;
    }

    public List<BaseMessage> onMessageListFetched(@NonNull List<BaseMessage> messages){
        return messages;
    }

    public void onLogout(){

    }

}
