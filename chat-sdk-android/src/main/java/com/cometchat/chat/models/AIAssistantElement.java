package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Represents a single element within the {@code data.elements} array of an AI assistant message.
 * Each element has a {@code type} (e.g. "text", "card", "graph") and a {@code data} payload
 * whose shape depends on the type.
 *
 * <p>The SDK treats element values as opaque: for {@code type=="text"} the value is a String,
 * for {@code type=="card"} it's a JSONObject with {@code {card, cardId}}, and for any other
 * type it's the raw JSON value.</p>
 *
 * @author CometChat Team
 * @version 5.0
 */
public class AIAssistantElement implements Parcelable, Cloneable {

    private String type;
    private Object data;

    /**
     * Default constructor.
     */
    public AIAssistantElement() {
    }

    /**
     * Gets the element type string (e.g. "text", "card", "graph").
     *
     * @return The element type identifier
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the raw value payload for this element.
     * <ul>
     *   <li>For type=="text": returns a String</li>
     *   <li>For type=="card": returns a JSONObject {card: {...}, cardId: "uuid"}</li>
     *   <li>For any other type: returns the raw JSON value (JSONObject, JSONArray, String, Number, etc.)</li>
     * </ul>
     *
     * @return The raw value payload, or null if the value was absent
     */
    public Object getData() {
        return data;
    }

    /**
     * Sets the element type string.
     *
     * @param type The element type identifier to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets the raw value payload for this element.
     *
     * @param data The data payload to set
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * Factory method to create an AIAssistantElement from a JSON entry.
     * Input format: {@code {"type": "<string>", "value": <payload>}}
     *
     * <p>Returns null if input is null, or if "type" key is missing or empty.
     * Never throws exceptions to the caller.</p>
     *
     * @param json The JSON object containing the element data
     * @return A new AIAssistantElement instance, or null if input is invalid
     */
    public static AIAssistantElement fromJson(JSONObject json) {
        if (json == null) return null;

        String type = json.optString(CometChatConstants.MessageKeys.KEY_AGENTIC_ELEMENT_TYPE, null);
        if (type == null || type.isEmpty()) return null;

        Object value = json.opt(CometChatConstants.MessageKeys.KEY_AGENTIC_ELEMENT_VALUE);

        AIAssistantElement element = new AIAssistantElement();
        element.type = type;
        element.data = value;
        return element;
    }

    /**
     * Writes this element to a Parcel using type-discriminated byte markers for the data field.
     * <ul>
     *   <li>0 = null data</li>
     *   <li>1 = String data</li>
     *   <li>2 = JSONObject data (serialized as toString())</li>
     *   <li>3 = JSONArray data (serialized as toString())</li>
     *   <li>4 = other (serialized via String.valueOf())</li>
     * </ul>
     *
     * @param dest  The Parcel to write to
     * @param flags Additional flags about how the object should be written
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);

        if (data == null) {
            dest.writeByte((byte) 0);
        } else if (data instanceof String) {
            dest.writeByte((byte) 1);
            dest.writeString((String) data);
        } else if (data instanceof JSONObject) {
            dest.writeByte((byte) 2);
            dest.writeString(data.toString());
        } else if (data instanceof JSONArray) {
            dest.writeByte((byte) 3);
            dest.writeString(data.toString());
        } else {
            dest.writeByte((byte) 4);
            dest.writeString(String.valueOf(data));
        }
    }

    /**
     * Constructs an AIAssistantElement from a Parcel.
     *
     * @param in The Parcel to read from
     */
    protected AIAssistantElement(Parcel in) {
        type = in.readString();

        byte marker = in.readByte();
        switch (marker) {
            case 0:
                data = null;
                break;
            case 1:
                data = in.readString();
                break;
            case 2:
                try {
                    data = new JSONObject(in.readString());
                } catch (JSONException e) {
                    data = null;
                }
                break;
            case 3:
                try {
                    data = new JSONArray(in.readString());
                } catch (JSONException e) {
                    data = null;
                }
                break;
            default:
                data = in.readString();
                break;
        }
    }

    /**
     * Parcelable CREATOR field for generating instances from a Parcel.
     */
    public static final Creator<AIAssistantElement> CREATOR = new Creator<AIAssistantElement>() {
        @Override
        public AIAssistantElement createFromParcel(Parcel in) {
            return new AIAssistantElement(in);
        }

        @Override
        public AIAssistantElement[] newArray(int size) {
            return new AIAssistantElement[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Compares this element with another object for content equality.
     * <ul>
     *   <li>Same reference → true</li>
     *   <li>null or wrong type → false</li>
     *   <li>Compares type with ContentEqualsHelper.stringsEqual</li>
     *   <li>Compares data: if both JSONObject, uses toString().equals();
     *       if both JSONArray, uses toString().equals(); otherwise uses Objects.equals()</li>
     * </ul>
     *
     * @param other The object to compare with
     * @return true if content is equal, false otherwise
     */
    public boolean contentEquals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (!(other instanceof AIAssistantElement)) return false;

        AIAssistantElement that = (AIAssistantElement) other;

        if (!ContentEqualsHelper.stringsEqual(this.type, that.type)) return false;

        // Compare data fields
        if (this.data == null && that.data == null) return true;
        if (this.data == null || that.data == null) return false;

        if (this.data instanceof JSONObject && that.data instanceof JSONObject) {
            return this.data.toString().equals(that.data.toString());
        }
        if (this.data instanceof JSONArray && that.data instanceof JSONArray) {
            return this.data.toString().equals(that.data.toString());
        }

        return Objects.equals(this.data, that.data);
    }

    /**
     * Compares this element with another object for equality.
     * Delegates to {@link #contentEquals(Object)}.
     *
     * @param obj The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        return contentEquals(obj);
    }

    /**
     * Creates a deep copy of this element.
     * <ul>
     *   <li>String type is immutable, assigned directly</li>
     *   <li>JSONObject data is deep-copied via {@code new JSONObject(data.toString())}</li>
     *   <li>JSONArray data is deep-copied via {@code new JSONArray(data.toString())}</li>
     *   <li>Other data types are assigned directly</li>
     * </ul>
     *
     * @return A deep copy of this element
     */
    @Override
    public AIAssistantElement clone() {
        try {
            AIAssistantElement clone = (AIAssistantElement) super.clone();
            clone.type = this.type;

            if (this.data instanceof JSONObject) {
                try {
                    clone.data = new JSONObject(this.data.toString());
                } catch (JSONException e) {
                    clone.data = this.data;
                }
            } else if (this.data instanceof JSONArray) {
                try {
                    clone.data = new JSONArray(this.data.toString());
                } catch (JSONException e) {
                    clone.data = this.data;
                }
            } else {
                clone.data = this.data;
            }

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Clone not supported", e);
        }
    }

    /**
     * Returns a string representation of this element for debugging purposes.
     *
     * @return A string representation of the element
     */
    @NonNull
    @Override
    public String toString() {
        return "AIAssistantElement{" +
                "type='" + type + '\'' +
                ", data=" + data +
                '}';
    }
}
