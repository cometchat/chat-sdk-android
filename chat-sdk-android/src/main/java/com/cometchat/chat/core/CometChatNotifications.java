package com.cometchat.chat.core;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cometchat.chat.constants.CometChatNotificationsConstants;
import com.cometchat.chat.enums.DayOfWeek;
import com.cometchat.chat.enums.PushPlatforms;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.models.DaySchedule;
import com.cometchat.chat.models.MutedConversation;
import com.cometchat.chat.models.NotificationPreferences;
import com.cometchat.chat.models.PushPreferences;
import com.cometchat.chat.models.UnmutedConversation;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Rohit Giri on 09/02/24.
 */
public final class CometChatNotifications {
    private static final String TAG = "CometChatNotifications";

    /**
     * Updates the timezone for the logged-in user.
     * This method makes an asynchronous API call with the provided TimeZone object
     * to update the notifications timezone settings.
     * The changes are reflected upon a successful update and the result is provided via the callback listener.
     *
     * @param timezone The TimeZone object containing the new timezone to be updated.
     * @param listener A CometChat.CallbackListener<Boolean> to handle the success or failure of the update operation.
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public static void updateTimezone(TimeZone timezone, @NonNull final CometChat.CallbackListener<String> listener) {
        final String methodName = new Throwable().getStackTrace()[0].getMethodName();
        try {
            JSONObject requestJsonObj = new JSONObject();
            requestJsonObj.put(CometChatNotificationsConstants.RequestKeys.KEY_TIMEZONE, timezone.getID());
            ApiConnection.getInstance().updateTimezone(requestJsonObj, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    try {
                        if (ce != null) {
                            handleResponseOnMainThread(listener, null, ce, methodName);
                        } else {
                            handleResponseOnMainThread(listener, CometChatNotificationsConstants.Success.TIMEZONE_UPDATE_SUCCESS, null, methodName);
                        }
                    } catch (Exception e) {
                        Logger.error(e.toString());
                        final CometChatException uncaughtException = new CometChatException(CometChatNotificationsConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
                        handleResponseOnMainThread(listener, null, uncaughtException, methodName);
                    }
                }
            });
        } catch (Exception e) {
            Logger.error(e.toString());
            final CometChatException uncaughtException = new CometChatException(CometChatNotificationsConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            handleResponseOnMainThread(listener, null, uncaughtException, methodName);
        }
    }

    /**
     * Retrieves the timezone for the logged-in user.
     * This method performs an asynchronous API call to get the user's current timezone settings.
     * The retrieved timezone is returned via the callback listener upon a successful operation.
     *
     * @param listener A CometChat.CallbackListener<TimeZone> to handle the success or failure of the retrieval operation.
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public static void getTimezone(@NonNull final CometChat.CallbackListener<TimeZone> listener) {
        final String methodName = new Throwable().getStackTrace()[0].getMethodName();
        ApiConnection.getInstance().getNotificationTimezone(new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, final CometChatException ce) {
                try {
                    if (ce != null) {
                        handleResponseOnMainThread(listener, null, ce, methodName);
                    } else {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject dataObject = jsonObject.getJSONObject(CometChatNotificationsConstants.ResponseKeys.KEY_DATA);
                        String timezoneId = dataObject.getString(CometChatNotificationsConstants.ResponseKeys.KEY_TIMEZONE);
                        TimeZone timeZone = TimeZone.getTimeZone(timezoneId);
                        handleResponseOnMainThread(listener, timeZone, null, methodName);
                    }
                } catch (Exception e) {
                    Logger.error(e.toString());
                    final CometChatException uncaughtException = new CometChatException(CometChatNotificationsConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
                    handleResponseOnMainThread(listener, null, uncaughtException, methodName);
                }
            }
        });
    }

    /**
     * Fetches the push notification preferences for the logged-in user.
     * This method makes an asynchronous API call to retrieve the user's push notification settings
     * and invokes the provided callback listener with the results.
     *
     * @param listener A CometChat.CallbackListener<PushPreferences> to handle the success or failure of the fetch operation.
     * @version <b>v4</b>
     * @since <b>v4</b>
     * @deprecated This method is deprecated as of version 4.0. Use {@link #fetchPreferences(CometChat.CallbackListener)} instead.
     */
    @Deprecated
    public static void fetchPushPreferences(@NonNull final CometChat.CallbackListener<PushPreferences> listener) {
        final String methodName = new Throwable().getStackTrace()[0].getMethodName();
        ApiConnection.getInstance().getNotificationPreferences(new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, final CometChatException ce) {
                try {
                    if (ce != null) {
                        handleResponseOnMainThread(listener, null, ce, methodName);
                    } else {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject dataObject = jsonObject.getJSONObject(CometChatNotificationsConstants.ResponseKeys.KEY_DATA);
                        final PushPreferences pushPreferences = PushPreferences.fromJson(dataObject);
                        handleResponseOnMainThread(listener, pushPreferences, null, methodName);
                    }
                } catch (Exception e) {
                    Logger.error(e.toString());
                    final CometChatException uncaughtException = new CometChatException(CometChatNotificationsConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
                    handleResponseOnMainThread(listener, null, uncaughtException, methodName);
                }
            }
        });
    }

    /**
     * Fetches the push notification preferences for the logged-in user.
     * This method makes an asynchronous API call to retrieve the user's push notification settings
     * and invokes the provided callback listener with the results.
     *
     * @param listener A CometChat.CallbackListener<NotificationPreferences> to handle the success or failure of the fetch operation.
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public static void fetchPreferences(@NonNull final CometChat.CallbackListener<NotificationPreferences> listener) {
        final String methodName = new Throwable().getStackTrace()[0].getMethodName();
        ApiConnection.getInstance().getNotificationPreferences(new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, final CometChatException ce) {
                try {
                    if (ce != null) {
                        handleResponseOnMainThread(listener, null, ce, methodName);
                    } else {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject dataObject = jsonObject.getJSONObject(CometChatNotificationsConstants.ResponseKeys.KEY_DATA);
                        final NotificationPreferences notificationPreferences = NotificationPreferences.fromJson(dataObject);
                        handleResponseOnMainThread(listener, notificationPreferences, null, methodName);
                    }
                } catch (Exception e) {
                    Logger.error(e.toString());
                    final CometChatException uncaughtException = new CometChatException(CometChatNotificationsConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
                    handleResponseOnMainThread(listener, null, uncaughtException, methodName);
                }
            }
        });
    }

    /**
     * Updates the push notification preferences for the logged-in user.
     * This method makes an asynchronous API call with the provided PushPreferences object
     * to update the user's push notification settings.
     * The changes are reflected upon a successful update and the result is provided via the callback listener.
     *
     * @param pushPreferences The PushPreferences object containing the new settings to be updated.
     * @param listener        A CometChat.CallbackListener<PushPreferences> to handle the success or failure of the update operation.
     * @version <b>v4</b>
     * @since <b>v4</b>
     * @deprecated This method is deprecated as of version 4.0. Use {@link #updatePreferences(NotificationPreferences, CometChat.CallbackListener)} instead.
     **/
    @Deprecated
    public static void updatePushPreferences(@NonNull PushPreferences pushPreferences, @NonNull final CometChat.CallbackListener<PushPreferences> listener) {
        final String methodName = new Throwable().getStackTrace()[0].getMethodName();
        try {
            JSONObject requestJsonObject = pushPreferences.toJson();
            if (requestJsonObject.length() > 0) {
                ApiConnection.getInstance().updatedPreferences(requestJsonObject, new ApiConnection.APIConnectionListener() {
                    @Override
                    public void onResponse(String response, final CometChatException ce) {
                        try {
                            if (ce != null) {
                                handleResponseOnMainThread(listener, null, ce, methodName);
                            } else {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject dataObject = jsonObject.getJSONObject(CometChatNotificationsConstants.ResponseKeys.KEY_DATA);
                                final PushPreferences pushPreferences = PushPreferences.fromJson(dataObject);
                                handleResponseOnMainThread(listener, pushPreferences, null, methodName);
                            }
                        } catch (Exception e) {
                            Logger.error(e.toString());
                            final CometChatException uncaughtException = new CometChatException(CometChatNotificationsConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
                            handleResponseOnMainThread(listener, null, uncaughtException, methodName);
                        }
                    }
                });
            } else {
                final CometChatException uncaughtException = new CometChatException(CometChatNotificationsConstants.Errors.ERROR_INVALID_REQUEST, CometChatNotificationsConstants.Errors.ERROR_INVALID_UPDATE_REQUEST_MESSAGE);
                handleResponseOnMainThread(listener, null, uncaughtException, methodName);
            }
        } catch (Exception e) {
            Logger.error(e.toString());
            final CometChatException uncaughtException = new CometChatException(CometChatNotificationsConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            handleResponseOnMainThread(listener, null, uncaughtException, methodName);
        }
    }

    /**
     * Updates the push notification preferences for the logged-in user.
     * This method makes an asynchronous API call with the provided PushPreferences object
     * to update the user's push notification settings.
     * The changes are reflected upon a successful update and the result is provided via the callback listener.
     *
     * @param notificationPreferences The PushPreferences object containing the new settings to be updated.
     * @param listener                A CometChat.CallbackListener<NotificationPreferences> to handle the success or failure of the update operation.
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public static void updatePreferences(@NonNull NotificationPreferences notificationPreferences, @NonNull final CometChat.CallbackListener<NotificationPreferences> listener) {
        final String methodName = new Throwable().getStackTrace()[0].getMethodName();
        try {
            JSONObject requestJsonObject = notificationPreferences.toJson();
            if (requestJsonObject.length() > 0) {
                ApiConnection.getInstance().updatedPreferences(requestJsonObject, new ApiConnection.APIConnectionListener() {
                    @Override
                    public void onResponse(String response, final CometChatException ce) {
                        try {
                            if (ce != null) {
                                handleResponseOnMainThread(listener, null, ce, methodName);
                            } else {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject dataObject = jsonObject.getJSONObject(CometChatNotificationsConstants.ResponseKeys.KEY_DATA);
                                final NotificationPreferences preferences = NotificationPreferences.fromJson(dataObject);
                                handleResponseOnMainThread(listener, preferences, null, methodName);
                            }
                        } catch (Exception e) {
                            Logger.error(e.toString());
                            final CometChatException uncaughtException = new CometChatException(CometChatNotificationsConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
                            handleResponseOnMainThread(listener, null, uncaughtException, methodName);
                        }
                    }
                });
            } else {
                final CometChatException uncaughtException = new CometChatException(CometChatNotificationsConstants.Errors.ERROR_INVALID_REQUEST, CometChatNotificationsConstants.Errors.ERROR_INVALID_UPDATE_REQUEST_MESSAGE);
                handleResponseOnMainThread(listener, null, uncaughtException, methodName);
            }
        } catch (Exception e) {
            Logger.error(e.toString());
            final CometChatException uncaughtException = new CometChatException(CometChatNotificationsConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            handleResponseOnMainThread(listener, null, uncaughtException, methodName);
        }
    }


    /**
     * Resets the push notification preferences to their default values for the logged-in user.
     * This method performs an asynchronous API call to restore the default settings for user's push notifications.
     * Upon successful reset, the updated PushPreferences object with default settings is returned via the callback listener.
     *
     * @param listener A CometChat.CallbackListener<PushPreferences> to handle the success or failure of the reset operation.
     * @version <b>v4</b>
     * @since <b>v4</b>
     * @deprecated This method is deprecated as of version 4.0. Use {@link #resetPreferences(CometChat.CallbackListener)} instead.
     */
    @Deprecated
    public static void resetPushPreferences(@NonNull final CometChat.CallbackListener<PushPreferences> listener) {
        final String methodName = new Throwable().getStackTrace()[0].getMethodName();
        ApiConnection.getInstance().resetPushPreferences(new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, final CometChatException ce) {
                try {
                    if (ce != null) {
                        handleResponseOnMainThread(listener, null, ce, methodName);
                    } else {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject dataObject = jsonObject.getJSONObject(CometChatNotificationsConstants.ResponseKeys.KEY_DATA);
                        final PushPreferences pushPreferences = PushPreferences.fromJson(dataObject);
                        handleResponseOnMainThread(listener, pushPreferences, null, methodName);
                    }
                } catch (Exception e) {
                    Logger.error(e.toString());
                    final CometChatException uncaughtException = new CometChatException(CometChatNotificationsConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
                    handleResponseOnMainThread(listener, null, uncaughtException, methodName);
                }
            }
        });
    }

    /**
     * Resets the push notification preferences to their default values for the logged-in user.
     * This method performs an asynchronous API call to restore the default settings for user's push notifications.
     * Upon successful reset, the updated PushPreferences object with default settings is returned via the callback listener.
     *
     * @param listener A CometChat.CallbackListener<NotificationPreferences> to handle the success or failure of the reset operation.
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public static void resetPreferences(@NonNull final CometChat.CallbackListener<NotificationPreferences> listener) {
        final String methodName = new Throwable().getStackTrace()[0].getMethodName();
        ApiConnection.getInstance().resetPushPreferences(new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, final CometChatException ce) {
                try {
                    if (ce != null) {
                        handleResponseOnMainThread(listener, null, ce, methodName);
                    } else {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject dataObject = jsonObject.getJSONObject(CometChatNotificationsConstants.ResponseKeys.KEY_DATA);
                        final NotificationPreferences notificationPreferences = NotificationPreferences.fromJson(dataObject);
                        handleResponseOnMainThread(listener, notificationPreferences, null, methodName);
                    }
                } catch (Exception e) {
                    Logger.error(e.toString());
                    final CometChatException uncaughtException = new CometChatException(CometChatNotificationsConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
                    handleResponseOnMainThread(listener, null, uncaughtException, methodName);
                }
            }
        });
    }


    /**
     * Retrieves the list of conversations that have been muted by the logged-in user.
     * This method makes an asynchronous API call to the CometChat server to obtain information
     * about all muted conversations. The resulting list of MutedConversation objects is then provided
     * via the callback listener on success.
     *
     * @param listener A CometChat.CallbackListener<List<MutedConversation>> to handle the retrieved list or any error that occurs during the process.
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public static void getMutedConversations(@NonNull final CometChat.CallbackListener<List<MutedConversation>> listener) {
        final String methodName = new Throwable().getStackTrace()[0].getMethodName();
        ApiConnection.getInstance().getMutedConversations(new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, final CometChatException ce) {
                try {
                    if (ce != null) {
                        handleResponseOnMainThread(listener, null, ce, methodName);
                    } else {
                        JSONObject jsonObject = new JSONObject(response);
                        List<MutedConversation> mutedConversationList = MutedConversation.fromJsonArray(jsonObject);
                        handleResponseOnMainThread(listener, mutedConversationList, null, methodName);
                    }
                } catch (Exception e) {
                    Logger.error(e.toString());
                    final CometChatException uncaughtException = new CometChatException(CometChatNotificationsConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
                    handleResponseOnMainThread(listener, null, uncaughtException, methodName);
                }
            }
        });
    }

    /**
     * Mutes the conversations specified in the list of MutedConversation objects.
     * This method performs an asynchronous API call to the CometChat server to update the mute status for the specified conversations.
     * Upon successful mute operation, a confirmation message is delivered to the provided callback listener.
     *
     * @param mutedConversations A list of MutedConversation objects representing the conversations to be muted.
     * @param listener           A CometChat.CallbackListener<String> to handle the success message or any error that occurs during the mute process.
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public static void muteConversations(List<MutedConversation> mutedConversations, @NonNull final CometChat.CallbackListener<String> listener) {
        final String methodName = new Throwable().getStackTrace()[0].getMethodName();
        if (mutedConversations.isEmpty()) {
            final CometChatException uncaughtException = new CometChatException(CometChatNotificationsConstants.Errors.ERROR_INVALID_REQUEST, CometChatNotificationsConstants.Errors.ERROR_INVALID_MUTE_REQUEST_MESSAGE);
            handleResponseOnMainThread(listener, null, uncaughtException, methodName);
        } else {
            try {
                JSONObject requestJsonObj = new JSONObject();
                requestJsonObj.put(CometChatNotificationsConstants.RequestKeys.KEY_CONVERSATIONS, MutedConversation.toJsonArray(mutedConversations));
                ApiConnection.getInstance().muteConversations(requestJsonObj, new ApiConnection.APIConnectionListener() {
                    @Override
                    public void onResponse(String response, final CometChatException ce) {
                        try {
                            if (ce != null) {
                                handleResponseOnMainThread(listener, null, ce, methodName);
                            } else {
                                handleResponseOnMainThread(listener, CometChatNotificationsConstants.Success.SUCCESS_CONVERSATION_MUTE_MESSAGE, null, methodName);
                            }
                        } catch (Exception e) {
                            Logger.error(e.toString());
                            final CometChatException uncaughtException = new CometChatException(CometChatNotificationsConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
                            handleResponseOnMainThread(listener, null, uncaughtException, methodName);
                        }
                    }
                });
            } catch (Exception e) {
                Logger.error(e.toString());
                final CometChatException uncaughtException = new CometChatException(CometChatNotificationsConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
                handleResponseOnMainThread(listener, null, uncaughtException, methodName);
            }
        }
    }


    /**
     * Unmutes previously muted conversations based on the provided list of UnmutedConversation objects.
     * This method performs an asynchronous API call to the CometChat server to update the mute status of the specified conversations.
     * Upon successful unmute operation, a confirmation message is returned through the callback listener.
     *
     * @param unmutedConversations A list of UnmutedConversation objects representing the conversations to be unmuted.
     * @param listener             A CometChat.CallbackListener<String> to handle the success message or any error that occurs during the unmute process.
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public static void unmuteConversations(List<UnmutedConversation> unmutedConversations, @NonNull final CometChat.CallbackListener<String> listener) {
        final String methodName = new Throwable().getStackTrace()[0].getMethodName();
        if (unmutedConversations.isEmpty()) {
            final CometChatException uncaughtException = new CometChatException(CometChatNotificationsConstants.Errors.ERROR_INVALID_REQUEST, CometChatNotificationsConstants.Errors.ERROR_INVALID_UNMUTE_REQUEST_MESSAGE);
            handleResponseOnMainThread(listener, null, uncaughtException, methodName);
        } else {
            try {
                JSONObject requestJsonObj = new JSONObject();
                requestJsonObj.put(CometChatNotificationsConstants.RequestKeys.KEY_CONVERSATIONS, UnmutedConversation.toJsonArray(unmutedConversations));
                ApiConnection.getInstance().unmuteConversations(requestJsonObj, new ApiConnection.APIConnectionListener() {
                    @Override
                    public void onResponse(String response, final CometChatException ce) {
                        try {
                            if (ce != null) {
                                handleResponseOnMainThread(listener, null, ce, methodName);
                            } else {
                                handleResponseOnMainThread(listener, CometChatNotificationsConstants.Success.SUCCESS_CONVERSATION_UNMUTE_MESSAGE, null, methodName);
                            }
                        } catch (Exception e) {
                            Logger.error(e.toString());
                            final CometChatException uncaughtException = new CometChatException(CometChatNotificationsConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
                            handleResponseOnMainThread(listener, null, uncaughtException, methodName);
                        }
                    }
                });
            } catch (Exception e) {
                Logger.error(e.toString());
                final CometChatException uncaughtException = new CometChatException(CometChatNotificationsConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
                handleResponseOnMainThread(listener, null, uncaughtException, methodName);
            }
        }
    }


    /**
     * Registers a push notification token with the CometChat server for the current user.
     * This token allows the server to send push notifications to the user's device through the specified platform.
     * When the token is successfully registered, a confirmation message is returned through the provided callback listener.
     *
     * @param pushToken     The push notification token provided by the notification service (e.g., Firebase Cloud Messaging).
     * @param pushPlatforms An enum representing the push notification platform (e.g., PushPlatforms.FCM_ANDROID or PushPlatforms.FCM_FLUTTER_ANDROID).
     * @param providerId    An optional identifier for the provider, useful for distinguishing between different devices or services.
     * @param listener      A CometChat.CallbackListener<String> to handle the success message or any error that occurs during registration.
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public static void registerPushToken(@NonNull String pushToken, @NonNull PushPlatforms pushPlatforms, @Nullable String providerId, @NonNull final CometChat.CallbackListener<String> listener) {
        final String methodName = new Throwable().getStackTrace()[0].getMethodName();
        ApiConnection.getInstance().registerPushToken(pushToken, pushPlatforms, providerId, new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, final CometChatException ce) {
                try {
                    if (ce != null) {
                        handleResponseOnMainThread(listener, null, ce, methodName);
                    } else {
                        handleResponseOnMainThread(listener, CometChatNotificationsConstants.Success.SUCCESS_TOKEN_REGISTER_MESSAGE, null, methodName);
                    }
                } catch (Exception e) {
                    Logger.error(e.toString());
                    final CometChatException uncaughtException = new CometChatException(CometChatNotificationsConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
                    handleResponseOnMainThread(listener, null, uncaughtException, methodName);
                }
            }
        });
    }


    /**
     * Unregisters the push notification token associated with the logged-in user from the CometChat server.
     * This action prevents the server from sending push notifications to the user's device.
     * On successful unregistration, a confirmation message is returned through the provided callback listener.
     *
     * @param listener A CometChat.CallbackListener<String> to handle the success message or any error that occurs during unregistration.
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public static void unregisterPushToken(@NonNull final CometChat.CallbackListener<String> listener) {
        final String methodName = new Throwable().getStackTrace()[0].getMethodName();
        ApiConnection.getInstance().unregisterPushToken(new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, final CometChatException ce) {
                try {
                    if (ce != null) {
                        handleResponseOnMainThread(listener, null, ce, methodName);
                    } else {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has(CometChatNotificationsConstants.ResponseKeys.KEY_DATA)) {
                            boolean status = jsonObject.getJSONObject(CometChatNotificationsConstants.ResponseKeys.KEY_DATA).getBoolean(CometChatNotificationsConstants.ResponseKeys.KEY_SUCCESS);
                            if (status) {
                                handleResponseOnMainThread(listener, CometChatNotificationsConstants.Success.SUCCESS_TOKEN_UNREGISTER_MESSAGE, null, methodName);
                            } else {
                                handleResponseOnMainThread(listener, jsonObject.getJSONObject(CometChatNotificationsConstants.ResponseKeys.KEY_DATA).getString(CometChatNotificationsConstants.ResponseKeys.KEY_MESSAGE), null, methodName);
                            }
                        }
                    }
                } catch (Exception e) {
                    Logger.error(e.toString());
                    final CometChatException uncaughtException = new CometChatException(CometChatNotificationsConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
                    handleResponseOnMainThread(listener, null, uncaughtException, methodName);
                }
            }
        });
    }

    private static <T> void handleResponseOnMainThread(final CometChat.CallbackListener<T> listener, final T response, final CometChatException ce, final String methodName) {
        CometChat.postOnMainThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (ce != null) {
                        listener.onError(ce);
                    } else {
                        listener.onSuccess(response);
                    }
                } catch (Exception e) {
                    final CometChatException uncaughtException = new CometChatException(CometChatNotificationsConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
                    HashMap<String, String> detailsMap = new HashMap<>();
                    CometChat.handleException(methodName, e, detailsMap);
                    listener.onError(uncaughtException);
                }
            }
        });
    }

    private static JSONObject getUpdatePushPreferenceJsonRequest(PushPreferences pushPreferences) {
        final JSONObject requestJsonObject = new JSONObject();
        try {
            //OneOnOne Pref
            if (pushPreferences.getOneOnOnePreferences() != null) {
                JSONObject oneOnOnePreferencesJsonObj = new JSONObject();
                if (pushPreferences.getOneOnOnePreferences().getMessagesPreference() != null) {
                    oneOnOnePreferencesJsonObj.put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_MESSAGES, pushPreferences.getOneOnOnePreferences().getMessagesPreference().getValue());
                }
                if (pushPreferences.getOneOnOnePreferences().getRepliesPreference() != null) {
                    oneOnOnePreferencesJsonObj.put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REPLIES, pushPreferences.getOneOnOnePreferences().getRepliesPreference().getValue());
                }
                if (pushPreferences.getOneOnOnePreferences().getReactionsPreference() != null) {
                    oneOnOnePreferencesJsonObj.put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REACTIONS, pushPreferences.getOneOnOnePreferences().getReactionsPreference().getValue());
                }
                if (oneOnOnePreferencesJsonObj.length() > 0) {
                    requestJsonObject.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_ONE_ON_ONE_PREFERENCES, oneOnOnePreferencesJsonObj);
                }
            }

            //GroupPreferences Pref
            if (pushPreferences.getGroupPreferences() != null) {
                JSONObject groupPreferencesJsonObj = new JSONObject();
                if (pushPreferences.getGroupPreferences().getMessagesPreference() != null) {
                    groupPreferencesJsonObj.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MESSAGES, pushPreferences.getGroupPreferences().getMessagesPreference().getValue());
                }
                if (pushPreferences.getGroupPreferences().getRepliesPreference() != null) {
                    groupPreferencesJsonObj.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_REPLIES, pushPreferences.getGroupPreferences().getRepliesPreference().getValue());
                }
                if (pushPreferences.getGroupPreferences().getMemberLeftPreference() != null) {
                    groupPreferencesJsonObj.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_LEFT, pushPreferences.getGroupPreferences().getMemberLeftPreference().getValue());
                }
                if (pushPreferences.getGroupPreferences().getMemberAddedPreference() != null) {
                    groupPreferencesJsonObj.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_ADDED, pushPreferences.getGroupPreferences().getMemberAddedPreference().getValue());
                }
                if (pushPreferences.getGroupPreferences().getMemberJoinedPreference() != null) {
                    groupPreferencesJsonObj.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_JOINED, pushPreferences.getGroupPreferences().getMemberJoinedPreference().getValue());
                }
                if (pushPreferences.getGroupPreferences().getMemberKickedPreference() != null) {
                    groupPreferencesJsonObj.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_KICKED, pushPreferences.getGroupPreferences().getMemberKickedPreference().getValue());
                }
                if (pushPreferences.getGroupPreferences().getMemberBannedPreference() != null) {
                    groupPreferencesJsonObj.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_BANNED, pushPreferences.getGroupPreferences().getMemberBannedPreference().getValue());
                }
                if (pushPreferences.getGroupPreferences().getMemberUnbannedPreference() != null) {
                    groupPreferencesJsonObj.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_UNBANNED, pushPreferences.getGroupPreferences().getMemberUnbannedPreference().getValue());
                }
                if (pushPreferences.getGroupPreferences().getMemberScopeChangedPreference() != null) {
                    groupPreferencesJsonObj.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_SCOPE_CHANGED, pushPreferences.getGroupPreferences().getMemberScopeChangedPreference().getValue());
                }
                if (pushPreferences.getGroupPreferences().getReactionsPreference() != null) {
                    groupPreferencesJsonObj.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_REACTIONS, pushPreferences.getGroupPreferences().getReactionsPreference().getValue());
                }
                if (groupPreferencesJsonObj.length() > 0) {
                    requestJsonObject.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_GROUP_PREFERENCES, groupPreferencesJsonObj);
                }
            }

            //Mute Pref
            if (pushPreferences.getMutePreferences() != null) {
                JSONObject mutePreferencesJsonObj = new JSONObject();
                JSONObject dayObj = new JSONObject();
                if (pushPreferences.getMutePreferences().getDNDPreference() != null) {
                    mutePreferencesJsonObj.put(CometChatNotificationsConstants.MutePreferencesKeys.KEY_DND, pushPreferences.getMutePreferences().getDNDPreference().getValue());
                }
                if (pushPreferences.getMutePreferences().getSchedulePreference() != null) {
                    for (Map.Entry<DayOfWeek, DaySchedule> entry : pushPreferences.getMutePreferences().getSchedulePreference().entrySet()) {
                        JSONObject dayScheduleObj = new JSONObject();
                        DaySchedule daySchedule = entry.getValue();
                        if (daySchedule.getFrom() > 0 && daySchedule.getTo() > 0) {
                            dayScheduleObj.put(CometChatNotificationsConstants.DayScheduleKeys.KEY_FROM, daySchedule.getFrom());
                            dayScheduleObj.put(CometChatNotificationsConstants.DayScheduleKeys.KEY_TO, daySchedule.getTo());
                            dayScheduleObj.put(CometChatNotificationsConstants.DayScheduleKeys.KEY_DND, daySchedule.getDnd());
                        }
                        if (dayScheduleObj.length() > 0) {
                            DayOfWeek day = entry.getKey();
                            dayObj.put(day.getDayName(), dayScheduleObj);
                        }
                    }
                    if (dayObj.length() > 0) {
                        mutePreferencesJsonObj.put(CometChatNotificationsConstants.MutePreferencesKeys.KEY_SCHEDULE, dayObj);
                    }
                }
                if (mutePreferencesJsonObj.length() > 0) {
                    requestJsonObject.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_MUTE_PREFERENCES, mutePreferencesJsonObj);
                }
            }
        } catch (Exception e) {
            Logger.error(e.toString());
        }
        return requestJsonObject;
    }

}
