package com.cometchat.chat.core;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.enums.AttachmentType;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by adityagokula on 02/10/18.
 */

public class CometChatUtils {

    private static final String TAG = CometChatUtils.class.getSimpleName();

    private static String resource;

    static String overridePlatform;
    static String overrideSdkVersion;
    static JSONObject metaInfo;
    static JSONObject demoMetaInfo;

    public static boolean isConnectedToNetwork(Context context) {
        boolean isConnected = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork && activeNetwork.isAvailable() && activeNetwork.isConnected()) {
            isConnected = true;
        }
        return isConnected;
    }

    public static String getDeviceUniqueId(Context context) {
        return android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }


    public static String getResource(boolean forceNew) {
        if (resource == null || forceNew) {
            resource = getPlatform() + "-" + getSDKVersion().replace(".", "_") + "-" + generateRandom(30) + "-" + System.currentTimeMillis();
            Logger.error("Generating Resource " + resource);
        }
        return resource;
    }

    private static String generateRandom(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    public static boolean containsSpace(String UID) {
        return UID.contains(CometChatConstants.ExtraKeys.KEY_SPACE);
    }

    public static boolean isExtensionEnabled(Settings settings, String extensionId) {
        return settings != null && settings.getEnabledExtensions().contains(extensionId);
    }

    public static String getSDKVersion() {
        if (overrideSdkVersion == null) {
            return BuildConfig.VERSION_NAME;
        } else {
            return overrideSdkVersion;
        }
    }

    public static JSONObject getMetaInfo() {
        return metaInfo;
    }

    public static JSONObject getDemoMetaInfo() {
        return demoMetaInfo;
    }

    public static String getPlatform() {
        if (overridePlatform == null) {
            return CometChatConstants.AppInfoKeys.KEY_PLATFORM_ANDROID;
        } else {
            return overridePlatform;
        }
    }

    public static String getAgent() {
        return "cc_" + getPlatform().toLowerCase() + "_sdk";
    }

    public static String getAndroidVersion() {
        return String.valueOf(Build.VERSION.RELEASE);
    }

    public static boolean isJSONObjectEmpty(JSONObject jsonObject) {
        boolean isEmpty = false;
        if (jsonObject.length() == 0)
            isEmpty = true;
        return isEmpty;
    }

    public static boolean isEmpty(String value) {
        if (value == null)
            return false;
        return (value.equalsIgnoreCase("") || value.length() == 0);
    }

    public static String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFinalChatHost(Settings settings) {
        return settings.getChatHostOverride() != null ? settings.getChatHostOverride() : (settings.getChatHostAppSpecific() != null ? settings.getChatHostAppSpecific() : settings.getChatHost());
    }

    public static String getFinalJidHost(Settings settings) {
        return settings.getJidHostOverride() != null ? settings.getJidHostOverride() : settings.getChatHost();
    }

    public static String getCSStringFromList(List<String> roles) {
        final String SEPARATOR = ",";
        StringBuilder csvBuilder = new StringBuilder();
        for (String role : roles) {
            csvBuilder.append(role);
            csvBuilder.append(SEPARATOR);
        }
        String csv = csvBuilder.toString();
        Logger.error(TAG, "Converting " + roles + " to " + csv);
        return csv.substring(0, csv.length() - SEPARATOR.length());
    }

    public static List<String> getListOfStringsFromType(List<AttachmentType> attachmentTypes) {
            if (attachmentTypes == null || attachmentTypes.isEmpty()) {
                return Collections.singletonList("");
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < attachmentTypes.size(); i++) {
                AttachmentType type = attachmentTypes.get(i);
                if (type != null && type.getType() != null && !type.getType().isEmpty()) {
                    sb.append(type.getType());
                    if (i < attachmentTypes.size() - 1) {
                        sb.append(",");
                    }
                }
            }
            return Collections.singletonList(sb.toString());
    }

    public static List<String> getListFromJSONArray(JSONArray jsonArray) {
        if (jsonArray.length() != 0) {
            try {
                List<String> strings = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    strings.add(jsonArray.getString(i));
                }
                return strings;
            } catch (JSONException je) {
                je.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public static JSONArray getJSONArrayFromList(List<String> tags) {
        JSONArray tagsArray = new JSONArray();
        for (String tag : tags) {
            tagsArray.put(tag);
        }
        return tagsArray;
    }

    public static String generateHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
