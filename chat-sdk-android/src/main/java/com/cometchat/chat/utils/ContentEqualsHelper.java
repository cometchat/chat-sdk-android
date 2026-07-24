package com.cometchat.chat.utils;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * Utility class providing helper methods for content equality comparison.
 * <p>
 * This class provides static methods to compare various types of objects
 * for content equality, including Strings, JSONObjects, and Lists.
 * It is designed to support the contentEquals() method implementation
 * across model classes in the CometChat SDK.
 * </p>
 */
public final class ContentEqualsHelper {

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private ContentEqualsHelper() {
        // Prevent instantiation
    }

    /**
     * Compares two Strings for content equality.
     * <p>
     * This method handles null values safely:
     * <ul>
     *   <li>If both strings are null, returns true</li>
     *   <li>If one is null and the other is not, returns false</li>
     *   <li>If both are non-null, compares using equals()</li>
     * </ul>
     * </p>
     *
     * @param a first String (may be null)
     * @param b second String (may be null)
     * @return true if both are null or both have equal content, false otherwise
     */
    public static boolean stringsEqual(String a, String b) {
        return Objects.equals(a, b);
    }

    /**
     * Compares two JSONObjects for content equality by comparing their string representations.
     * <p>
     * This method handles null values safely:
     * <ul>
     *   <li>If both JSONObjects are null, returns true</li>
     *   <li>If one is null and the other is not, returns false</li>
     *   <li>If both are non-null, compares their toString() representations</li>
     * </ul>
     * </p>
     * <p>
     * Note: This comparison relies on the string representation of JSONObjects,
     * which means the order of keys matters. Two JSONObjects with the same
     * key-value pairs but different key ordering may be considered unequal.
     * </p>
     *
     * @param a first JSONObject (may be null)
     * @param b second JSONObject (may be null)
     * @return true if both are null or both have equal string representations, false otherwise
     */
    public static boolean jsonObjectsEqual(JSONObject a, JSONObject b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.toString().equals(b.toString());
    }

    /**
     * Compares two Lists for content equality.
     * <p>
     * This method handles null values and performs element-by-element comparison:
     * <ul>
     *   <li>If both lists are null, returns true</li>
     *   <li>If one is null and the other is not, returns false</li>
     *   <li>If both are non-null but have different sizes, returns false</li>
     *   <li>For each element pair at the same index, uses objectsContentEqual() for comparison</li>
     * </ul>
     * </p>
     * <p>
     * For lists containing model objects that have a contentEquals() method,
     * this method will use contentEquals() for element comparison.
     * For other objects, it falls back to equals().
     * </p>
     *
     * @param a   first List (may be null)
     * @param b   second List (may be null)
     * @param <T> the type of elements in the lists
     * @return true if both are null or both have equal content, false otherwise
     */
    public static <T> boolean listsEqual(List<T> a, List<T> b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a.size() != b.size()) return false;

        for (int i = 0; i < a.size(); i++) {
            T itemA = a.get(i);
            T itemB = b.get(i);

            if (!objectsContentEqual(itemA, itemB)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compares two objects for content equality.
     * <p>
     * This method handles null values and attempts to use contentEquals() if available:
     * <ul>
     *   <li>If both objects are null, returns true</li>
     *   <li>If one is null and the other is not, returns false</li>
     *   <li>If the object has a contentEquals(Object) method, uses it for comparison</li>
     *   <li>Otherwise, falls back to equals() for comparison</li>
     * </ul>
     * </p>
     * <p>
     * This method uses reflection to check for the presence of a contentEquals() method,
     * which allows it to work with any model class that implements this method.
     * </p>
     *
     * @param a first object (may be null)
     * @param b second object (may be null)
     * @return true if both are null or both have equal content, false otherwise
     */
    public static boolean objectsContentEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;

        // Check if the object has a contentEquals method via reflection
        try {
            Method contentEqualsMethod = a.getClass().getMethod("contentEquals", Object.class);
            return (Boolean) contentEqualsMethod.invoke(a, b);
        } catch (NoSuchMethodException e) {
            // No contentEquals method, fall back to equals()
            return Objects.equals(a, b);
        } catch (Exception e) {
            // If reflection fails for any reason, fall back to equals()
            return Objects.equals(a, b);
        }
    }
}
