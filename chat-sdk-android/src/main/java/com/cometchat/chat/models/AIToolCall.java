package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.utils.ContentEqualsHelper;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an AI tool call in the CometChat SDK.
 * This class encapsulates information about a specific tool being called by an AI assistant,
 * including its ID, type, display name, execution text, and the function being invoked.
 *
 * @author CometChat Team
 * @version 4.0
 */
public class AIToolCall implements Parcelable, Cloneable {
    private String id;
    private String type;
    private String displayName;
    private String executionText;
    private AIToolCallFunction function;

    /**
     * Default constructor that creates an empty AIToolCall instance.
     */
    public AIToolCall() {
    }

    /**
     * Constructs an AIToolCall with the specified ID, type, and function.
     *
     * @param id       The unique identifier of the tool call
     * @param type     The type of the tool call
     * @param function The function associated with this tool call
     */
    public AIToolCall(@NonNull String id, @NonNull String type, @NonNull AIToolCallFunction function) {
        this.id = id;
        this.type = type;
        this.function = function;
    }

    /**
     * Constructs an AIToolCall with all properties specified.
     *
     * @param id            The unique identifier of the tool call
     * @param type          The type of the tool call
     * @param function      The function associated with this tool call
     * @param displayName   The display name for UI presentation (can be null)
     * @param executionText The execution text describing what the tool is doing (can be null)
     */
    public AIToolCall(@NonNull String id, @NonNull String type, @NonNull AIToolCallFunction function,
                      @Nullable String displayName, @Nullable String executionText) {
        this.id = id;
        this.type = type;
        this.function = function;
        this.displayName = displayName;
        this.executionText = executionText;
    }

    /**
     * Gets the unique identifier of the tool call.
     *
     * @return The tool call ID, or null if not set
     */
    @Nullable
    public String getId() {
        return id;
    }

    /**
     * Gets the type of the tool call.
     *
     * @return The tool call type, or null if not set
     */
    @Nullable
    public String getType() {
        return type;
    }

    /**
     * Gets the display name for the tool call, used for UI presentation.
     *
     * @return The display name, or null if not set
     */
    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the execution text describing what the tool is doing.
     *
     * @return The execution text, or null if not set
     */
    @Nullable
    public String getExecutionText() {
        return executionText;
    }

    /**
     * Gets the function associated with this tool call.
     *
     * @return The AIToolCallFunction instance, or null if not set
     */
    @Nullable
    public AIToolCallFunction getFunction() {
        return function;
    }

    /**
     * Sets the unique identifier of the tool call.
     *
     * @param id The tool call ID to set (can be null)
     */
    public void setId(@Nullable String id) {
        this.id = id;
    }

    /**
     * Sets the type of the tool call.
     *
     * @param type The tool call type to set (can be null)
     */
    public void setType(@Nullable String type) {
        this.type = type;
    }

    /**
     * Sets the display name for the tool call.
     *
     * @param displayName The display name to set (can be null)
     */
    public void setDisplayName(@Nullable String displayName) {
        this.displayName = displayName;
    }

    /**
     * Sets the execution text describing what the tool is doing.
     *
     * @param executionText The execution text to set (can be null)
     */
    public void setExecutionText(@Nullable String executionText) {
        this.executionText = executionText;
    }

    /**
     * Sets the function associated with this tool call.
     *
     * @param function The AIToolCallFunction instance to set (can be null)
     */
    public void setFunction(@Nullable AIToolCallFunction function) {
        this.function = function;
    }

    /**
     * Creates an AIToolCall instance from a JSON object.
     * This method parses the JSON representation and populates all the tool call properties.
     *
     * @param jsonObject The JSON object containing the tool call data
     * @return A fully populated AIToolCall instance
     */
    public static AIToolCall fromJson(JSONObject jsonObject) {
        AIToolCall toolCall = new AIToolCall();
        try {
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_TOOL_CALL_ID)) {
                toolCall.setId(jsonObject.getString(CometChatConstants.MessageKeys.KEY_TOOL_CALL_ID));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_TOOL_CALL_TYPE)) {
                toolCall.setType(jsonObject.getString(CometChatConstants.MessageKeys.KEY_TOOL_CALL_TYPE));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_TOOL_CALL_DISPLAY_NAME)) {
                toolCall.setDisplayName(jsonObject.getString(CometChatConstants.MessageKeys.KEY_TOOL_CALL_DISPLAY_NAME));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_TOOL_CALL_EXECUTION_TEXT)) {
                toolCall.setExecutionText(jsonObject.getString(CometChatConstants.MessageKeys.KEY_TOOL_CALL_EXECUTION_TEXT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_TOOL_CALL_FUNCTION)) {
                JSONObject functionObject = jsonObject.getJSONObject(CometChatConstants.MessageKeys.KEY_TOOL_CALL_FUNCTION);
                toolCall.setFunction(AIToolCallFunction.fromJson(functionObject));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return toolCall;
    }

    /**
     * Converts the AIToolCall to a JSON object representation.
     * This method creates a JSON object containing all the tool call properties.
     *
     * @return A JSONObject containing the tool call data
     */
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (id != null) {
                jsonObject.put(CometChatConstants.MessageKeys.KEY_TOOL_CALL_ID, id);
            }
            if (type != null) {
                jsonObject.put(CometChatConstants.MessageKeys.KEY_TOOL_CALL_TYPE, type);
            }
            if (displayName != null) {
                jsonObject.put(CometChatConstants.MessageKeys.KEY_TOOL_CALL_DISPLAY_NAME, displayName);
            }
            if (executionText != null) {
                jsonObject.put(CometChatConstants.MessageKeys.KEY_TOOL_CALL_EXECUTION_TEXT, executionText);
            }
            if (function != null) {
                jsonObject.put(CometChatConstants.MessageKeys.KEY_TOOL_CALL_FUNCTION, function.toJson());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Returns a string representation of the AIToolCall.
     * Includes all the key properties for debugging purposes.
     *
     * @return A string representation of the tool call
     */
    @Override
    public String toString() {
        return "AIToolCall{" +
            "id='" + id + '\'' +
            ", type='" + type + '\'' +
            ", displayName='" + displayName + '\'' +
            ", executionText='" + executionText + '\'' +
            ", function=" + function +
            '}';
    }

    // Parcelable implementation
    protected AIToolCall(Parcel in) {
        id = in.readString();
        type = in.readString();
        displayName = in.readString();
        executionText = in.readString();
        function = in.readParcelable(AIToolCallFunction.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(type);
        dest.writeString(displayName);
        dest.writeString(executionText);
        dest.writeParcelable(function, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AIToolCall> CREATOR = new Creator<AIToolCall>() {
        @Override
        public AIToolCall createFromParcel(Parcel in) {
            return new AIToolCall(in);
        }

        @Override
        public AIToolCall[] newArray(int size) {
            return new AIToolCall[size];
        }
    };

    @Override
    public AIToolCall clone() {
        try {
            AIToolCall clone = (AIToolCall) super.clone();
            clone.function = this.function != null ? this.function.clone() : null;
            return clone;
        } catch (CloneNotSupportedException e) {
            AIToolCall clone = new AIToolCall();
            clone.id = this.id;
            clone.type = this.type;
            clone.displayName = this.displayName;
            clone.executionText = this.executionText;
            clone.function = this.function != null ? this.function.clone() : null;
            return clone;
        }
    }

    /**
     * Compares this object with another for content equality.
     * Unlike equals() which compares by identity (ID), this method
     * compares all fields for value equality.
     *
     * @param other the object to compare with
     * @return true if all fields have equal values, false otherwise
     */
    public boolean contentEquals(Object other) {
        // 1. Same reference check
        if (this == other) return true;

        // 2. Null check
        if (other == null) return false;

        // 3. Type check
        if (!(other instanceof AIToolCall)) return false;

        // 4. Cast
        AIToolCall that = (AIToolCall) other;

        // 5. Compare all fields
        return ContentEqualsHelper.stringsEqual(id, that.id) &&
                ContentEqualsHelper.stringsEqual(type, that.type) &&
                ContentEqualsHelper.stringsEqual(displayName, that.displayName) &&
                ContentEqualsHelper.stringsEqual(executionText, that.executionText) &&
                ContentEqualsHelper.objectsContentEqual(function, that.function);
    }

    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }
}