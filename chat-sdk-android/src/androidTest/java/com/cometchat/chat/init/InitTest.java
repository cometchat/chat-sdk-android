package com.cometchat.chat.init;

import android.content.Context;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.AppSettings;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.utils.AssertHelper;
import com.cometchat.chat.utils.CometChatTestConstants;
import com.cometchat.chat.utils.TestPreferenceHelper;
import com.cometchat.chat.utils.TestUtils;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InitTest {
    private final int TEST_CASE_TIMEOUT = 3;

    private static Context context;

    @BeforeClass
    public static void enableLogs() {
        context = InstrumentationRegistry.getInstrumentation().getContext();
        TestPreferenceHelper.init(context);
        Logger.enableLogs("221089");
    }

    @Test
    public void a1_initWithNullAppIdShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        AppSettings.AppSettingsBuilder appSettingsBuilder = new AppSettings.AppSettingsBuilder();
        appSettingsBuilder.setRegion(CometChatTestConstants.REGION);
        appSettingsBuilder.subscribePresenceForAllUsers();
        if (CometChatTestConstants.isStaging){
            appSettingsBuilder.overrideAdminHost(CometChatTestConstants.APP_ID + ".api-" + CometChatTestConstants.REGION + ".cometchat-staging.com/v3.0");
            appSettingsBuilder.overrideClientHost(CometChatTestConstants.APP_ID + ".apiclient-" + CometChatTestConstants.REGION + ".cometchat-staging.com/v3.0");
        }
        AppSettings appSettings = appSettingsBuilder.build();
        CometChat.init(context, (String) CometChatTestConstants.NULL_DATA, appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.assertTrue(methodName , e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_APPID));
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a2_initWithEmptyAppIdShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        AppSettings.AppSettingsBuilder appSettingsBuilder = new AppSettings.AppSettingsBuilder();
        appSettingsBuilder.setRegion(CometChatTestConstants.REGION);
        appSettingsBuilder.subscribePresenceForAllUsers();
        if (CometChatTestConstants.isStaging){
            appSettingsBuilder.overrideAdminHost(CometChatTestConstants.APP_ID + ".api-" + CometChatTestConstants.REGION + ".cometchat-staging.com/v3.0");
            appSettingsBuilder.overrideClientHost(CometChatTestConstants.APP_ID + ".apiclient-" + CometChatTestConstants.REGION + ".cometchat-staging.com/v3.0");
        }
        AppSettings appSettings = appSettingsBuilder.build();
        CometChat.init(context, CometChatTestConstants.EMPTY_DATA, appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.fail(methodName + "  must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.assertTrue(methodName,e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_APPID));
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a3_initWithNullAppSettingsShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.init(context, CometChatTestConstants.APP_ID, (AppSettings) CometChatTestConstants.NULL_DATA, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.assertTrue(methodName,e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_APP_SETTINGS_NULL));
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a4_initWithNullRegionShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        AppSettings.AppSettingsBuilder appSettingsBuilder = new AppSettings.AppSettingsBuilder();
        appSettingsBuilder.setRegion((String) CometChatTestConstants.NULL_DATA);
        appSettingsBuilder.subscribePresenceForAllUsers();
        if (CometChatTestConstants.isStaging){
            appSettingsBuilder.overrideAdminHost(CometChatTestConstants.APP_ID + ".api-" + CometChatTestConstants.REGION + ".cometchat-staging.com/v3.0");
            appSettingsBuilder.overrideClientHost(CometChatTestConstants.APP_ID + ".apiclient-" + CometChatTestConstants.REGION + ".cometchat-staging.com/v3.0");
        }
        AppSettings appSettings = appSettingsBuilder.build();
        CometChat.init(context, CometChatTestConstants.APP_ID, appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.assertTrue(methodName,e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_REGION_MISSING));
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a5_initWithEmptyRegionShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        AppSettings.AppSettingsBuilder appSettingsBuilder = new AppSettings.AppSettingsBuilder();
        appSettingsBuilder.setRegion((String) CometChatTestConstants.EMPTY_DATA);
        appSettingsBuilder.subscribePresenceForAllUsers();
        if (CometChatTestConstants.isStaging){
            appSettingsBuilder.overrideAdminHost(CometChatTestConstants.APP_ID + ".api-" + CometChatTestConstants.REGION + ".cometchat-staging.com/v3.0");
            appSettingsBuilder.overrideClientHost(CometChatTestConstants.APP_ID + ".apiclient-" + CometChatTestConstants.REGION + ".cometchat-staging.com/v3.0");
        }
        AppSettings appSettings = appSettingsBuilder.build();
        CometChat.init(context, CometChatTestConstants.APP_ID, appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.assertTrue(methodName,e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_REGION_MISSING));
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a6_initWithValidAppIdAndRegionShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        AppSettings.AppSettingsBuilder appSettingsBuilder = new AppSettings.AppSettingsBuilder();
        appSettingsBuilder.setRegion(CometChatTestConstants.REGION);
        appSettingsBuilder.subscribePresenceForAllUsers();
        if (CometChatTestConstants.isStaging){
            appSettingsBuilder.overrideAdminHost(CometChatTestConstants.APP_ID + ".api-" + CometChatTestConstants.REGION + ".cometchat-staging.com/v3.0");
            appSettingsBuilder.overrideClientHost(CometChatTestConstants.APP_ID + ".apiclient-" + CometChatTestConstants.REGION + ".cometchat-staging.com/v3.0");
        }
        AppSettings appSettings = appSettingsBuilder.build();
        CometChat.init(context, CometChatTestConstants.APP_ID, appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.assertTrue(methodName,s.equalsIgnoreCase(CometChatConstants.SuccessMessages.MESSAGE_INIT_SUCCESS));
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + " must not return error " + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }
}
