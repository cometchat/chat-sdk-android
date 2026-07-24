package com.cometchat.chat.utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.skyscreamer.jsonassert.JSONAssert;

public class AssertHelper {

    public static void assertTrue(String description, boolean condition) {
        try {
            Assert.assertTrue(description, condition);
            System.out.println(description + " Success");
        } catch (AssertionError e) {
            System.out.println(description + " Fail");
            fail(description);
        }
    }

    public static void fail(String description) {
        try {
            System.out.println(description + " Fail");
            Assert.fail(description);
        }catch (Exception e){
            System.out.println(description);
        }
    }

    public static void jsonAssert(String description, JSONObject obj1, JSONObject obj2){
        try {
            System.out.println(description);
            JSONAssert.assertEquals(obj1, obj2, false);
        }catch (JSONException e){
            e.printStackTrace();
            fail(description + " " + e.getMessage());
        }
    }

}
