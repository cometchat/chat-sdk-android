package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cometchat.chat.constants.CometChatConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a function call within an AI tool call.
 * This class encapsulates the function name and arguments that are passed
 * when an AI assistant invokes a specific tool function.
 *
 * @author CometChat Team
 * @version 4.0
 */
public class AIToolCallFunction implements Parcelable, Cloneable {
    private String name;
    private String arguments;

    /**
     * Default constructor that creates an empty AIToolCallFunction instance.
     */
    public AIToolCallFunction() {
    }

    /**
     * Constructs an AIToolCallFunction with the specified name and arguments.
     *
     * @param name The name of the function being called
     * @param arguments The arguments being passed to the function
     */
    public AIToolCallFunction(@NonNull String name, @NonNull String arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    /**
     * Gets the name of the function being called.
     *
     * @return The function name, or null if not set
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * Gets the arguments being passed to the function.
     *
     * @return The function arguments as a string, or null if not set
     */
    @Nullable
    public String getArguments() {
        return arguments;
    }

    /**
     * Sets the name of the function being called.
     *
     * @param name The function name to set (can be null)
     */
    public void setName(@Nullable String name) {
        this.name = name;
    }

    /**
     * Sets the arguments being passed to the function.
     *
     * @param arguments The function arguments to set (can be null)
     */
    public void setArguments(@Nullable String arguments) {
        this.arguments = arguments;
    }

    /**
     * Creates an AIToolCallFunction instance from a JSON object.
     * This method parses the JSON representation and populates the function name and arguments.
     *
     * @param jsonObject The JSON object containing the function data
     * @return A fully populated AIToolCallFunction instance
     */
    public static AIToolCallFunction fromJson(JSONObject jsonObject) {
        AIToolCallFunction function = new AIToolCallFunction();
        try {
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_TOOL_CALL_FUNCTION_NAME)) {
                function.setName(jsonObject.getString(CometChatConstants.MessageKeys.KEY_TOOL_CALL_FUNCTION_NAME));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_TOOL_CALL_FUNCTION_ARGUMENTS)) {
                function.setArguments(jsonObject.getString(CometChatConstants.MessageKeys.KEY_TOOL_CALL_FUNCTION_ARGUMENTS));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return function;
    }

    /**
     * Converts the AIToolCallFunction to a JSON object representation.
     * This method creates a JSON object containing the function name and arguments.
     *
     * @return A JSONObject containing the function data
     */
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (name != null) {
                jsonObject.put(CometChatConstants.MessageKeys.KEY_TOOL_CALL_FUNCTION_NAME, name);
            }
            if (arguments != null) {
                jsonObject.put(CometChatConstants.MessageKeys.KEY_TOOL_CALL_FUNCTION_ARGUMENTS, arguments);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Returns a string representation of the AIToolCallFunction.
     * Includes the function name and arguments for debugging purposes.
     *
     * @return A string representation of the function call
     */
    @Override
    public String toString() {
        return "AIToolCallFunction{" +
            "name='" + name + '\'' +
            ", arguments='" + arguments + '\'' +
            '}';
    }

    // Parcelable implementation
    protected AIToolCallFunction(Parcel in) {
        name = in.readString();
        arguments = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(arguments);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AIToolCallFunction> CREATOR = new Creator<AIToolCallFunction>() {
        @Override
        public AIToolCallFunction createFromParcel(Parcel in) {
            return new AIToolCallFunction(in);
        }

        @Override
        public AIToolCallFunction[] newArray(int size) {
            return new AIToolCallFunction[size];
        }
    };

    @Override
    public AIToolCallFunction clone() {
        try {
            return (AIToolCallFunction) super.clone();
        } catch (CloneNotSupportedException e) {
            return new AIToolCallFunction(this.name, this.arguments);
        }
    }

    /**
     * Compares this AIToolCallFunction with another object for content equality.
     * Unlike equals() which compares by identity, this method compares all fields
     * for value equality.
     *
     * @param other the object to compare with
     * @return true if all fields have equal values, false otherwise
     */
    public boolean contentEquals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (!(other instanceof AIToolCallFunction)) return false;

        AIToolCallFunction that = (AIToolCallFunction) other;

        return com.cometchat.chat.utils.ContentEqualsHelper.stringsEqual(this.name, that.name) &&
               com.cometchat.chat.utils.ContentEqualsHelper.stringsEqual(this.arguments, that.arguments);
    }
    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }


}