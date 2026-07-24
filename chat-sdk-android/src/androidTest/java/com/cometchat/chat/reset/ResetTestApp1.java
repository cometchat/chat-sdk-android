package com.cometchat.chat.reset;

import android.util.Log;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.utils.CometChatTestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;


import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ResetTestApp1 {

    List<String> defaultUsers = Arrays.asList("superhero1", "superhero2", "superhero3", "superhero4", "superhero5");
    List<String> defaultGroups = Arrays.asList("supergroup");
    List<String> defaultRoles = Arrays.asList("default");

    @Test
    public void a1_clearUsers() {
        Logger.error("Getting Users");
        String url = null;
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        if(CometChatTestConstants.isStaging){
            url = CometChatTestConstants.DEFAULT_STAGING_URL + "users?per_page=1000";
        }else {
            url = CometChatTestConstants.DEFAULT_PROD_URL + "users?per_page=1000";
        }
        Headers headers = new Headers.Builder().add("appId", CometChatTestConstants.APP_ID).add("apiKey", CometChatTestConstants.VALID_API_KEY).build();
        Request getUsers = new Request.Builder().get().headers(headers).url(String.format(url, CometChatTestConstants.REGION)).build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Response response = null;
        try {
            response = okHttpClient.newCall(getUsers).execute();

            if (response != null) {
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                if (jsonObject.has("data")) {
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject userObject = dataArray.getJSONObject(i);
                        if (!defaultUsers.contains(userObject.getString("uid"))) {
                            deleteUser(userObject.getString("uid"));
                        }
                    }
                } else {
                    Log.e("RestTestApp", "ERROR");
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void a2_clearGroups() {
        Logger.error("Getting groups");
        String url = null;
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        if(CometChatTestConstants.isStaging){
            url = CometChatTestConstants.DEFAULT_STAGING_URL + "groups?per_page=1000";
        }else {
            url = CometChatTestConstants.DEFAULT_PROD_URL + "groups?per_page=1000";
        }
        Headers headers = new Headers.Builder().add("appId", CometChatTestConstants.APP_ID).add("apiKey", CometChatTestConstants.VALID_API_KEY).build();
        Request getUsers = new Request.Builder().get().headers(headers).url(String.format(url, CometChatTestConstants.REGION)).build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Response response = null;
        try {
            response = okHttpClient.newCall(getUsers).execute();

            if (response != null) {
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                if (jsonObject.has("data")) {
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject userObject = dataArray.getJSONObject(i);
                        if (!defaultGroups.contains(userObject.getString("guid"))) {
                            deleteGroup(userObject.getString("guid"));
                        }
                    }
                } else {
                    Log.e("RestTestApp", "ERROR");
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void a3_clearRoles() {
        Logger.error("Getting roles");
        String url = null;
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        if(CometChatTestConstants.isStaging){
            url = CometChatTestConstants.DEFAULT_STAGING_URL + "roles?per_page=1000";
        }else {
            url = CometChatTestConstants.DEFAULT_PROD_URL + "roles?per_page=1000";
        }
        Headers headers = new Headers.Builder().add("appId", CometChatTestConstants.APP_ID).add("apiKey", CometChatTestConstants.VALID_API_KEY).build();
        Request getUsers = new Request.Builder().get().headers(headers).url(String.format(url, CometChatTestConstants.REGION)).build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Response response = null;
        try {
            response = okHttpClient.newCall(getUsers).execute();

            if (response != null) {
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                if (jsonObject.has("data")) {
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject userObject = dataArray.getJSONObject(i);
                        if (!defaultRoles.contains(userObject.getString("role"))) {
                            deleteRole(userObject.getString("role"));
                        }
                    }
                } else {
                    Log.e("RestTestApp", "ERROR");
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void deleteUser(String uid) throws JSONException {
        Logger.error("Deleting Role");
        String url = null;
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        if(CometChatTestConstants.isStaging){
            url = CometChatTestConstants.DEFAULT_STAGING_URL + "users/%s";
        }else {
            url = CometChatTestConstants.DEFAULT_PROD_URL + "users/%s";
        }
        Headers headers = new Headers.Builder().add("appId", CometChatTestConstants.APP_ID).add("apiKey", CometChatTestConstants.VALID_API_KEY).build();
        JSONObject dataObject = new JSONObject();
        dataObject.put("permanent", true);
        Request getUsers = new Request.Builder().delete(RequestBody.create(MediaType.parse("application/json"), dataObject.toString())).headers(headers).url(String.format(url, CometChatTestConstants.REGION, uid)).build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Response response = null;
        try {
            response = okHttpClient.newCall(getUsers).execute();

            if (response != null) {
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                if (jsonObject.has("data")) {
                    Log.e("RestTestApp", "SUCCESS");
                }
            } else {
                Log.e("RestTestApp", "ERROR");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void deleteGroup(String guid) throws JSONException {
        Logger.error("Deleting Group");
        String url = null;
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        if(CometChatTestConstants.isStaging){
            url = CometChatTestConstants.DEFAULT_STAGING_URL + "groups/%s";
        }else {
            url = CometChatTestConstants.DEFAULT_PROD_URL + "groups/%s";
        }
        Headers headers = new Headers.Builder().add("appId", CometChatTestConstants.APP_ID).add("apiKey", CometChatTestConstants.VALID_API_KEY).build();
        JSONObject dataObject = new JSONObject();
        dataObject.put("permanent", true);
        Request getUsers = new Request.Builder().delete().headers(headers).url(String.format(url, CometChatTestConstants.REGION, guid)).build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Response response = null;
        try {
            response = okHttpClient.newCall(getUsers).execute();

            if (response != null) {
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                if (jsonObject.has("data")) {
                    Log.e("RestTestApp", "SUCCESS");
                }
            } else {
                Log.e("RestTestApp", "ERROR");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteRole(String role) throws JSONException {
        Logger.error("Deleting Role");
        String url = null;
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        if(CometChatTestConstants.isStaging){
            url = CometChatTestConstants.DEFAULT_STAGING_URL + "roles/%s";
        }else {
            url = CometChatTestConstants.DEFAULT_PROD_URL + "roles/%s";
        }
        Headers headers = new Headers.Builder().add("appId", CometChatTestConstants.APP_ID).add("apiKey", CometChatTestConstants.VALID_API_KEY).build();
        JSONObject dataObject = new JSONObject();
        dataObject.put("permanent", true);
        Request getUsers = new Request.Builder().delete().headers(headers).url(String.format(url, CometChatTestConstants.REGION, role)).build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Response response = null;
        try {
            response = okHttpClient.newCall(getUsers).execute();

            if (response != null) {
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                if (jsonObject.has("data")) {
                    Log.e("RestTestApp", "SUCCESS");
                }
            } else {
                Log.e("RestTestApp", "ERROR");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

