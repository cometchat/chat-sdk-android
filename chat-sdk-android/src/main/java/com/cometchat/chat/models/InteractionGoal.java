package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChatUtils;
import com.cometchat.chat.utils.ContentEqualsHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The InteractionGoal represents the desired outcome of an interaction with an InteractiveMessage. It includes:
 *
 * elementIds: A list of identifiers for the interactive elements.
 * type: The type of interaction goal from the CometChatConstants.
 */
public class InteractionGoal implements Parcelable, Cloneable {
    private List<String> elementIds;
    private @CometChatConstants.InteractionType String type;

    private InteractionGoal() {
    }

    public InteractionGoal(@CometChatConstants.InteractionType String type, List<String> elementIds) {
        this.elementIds = elementIds;
        this.type = type;
    }

    protected InteractionGoal(Parcel in) {
        elementIds = in.createStringArrayList();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(elementIds);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InteractionGoal> CREATOR = new Creator<InteractionGoal>() {
        @Override
        public InteractionGoal createFromParcel(Parcel in) {
            return new InteractionGoal(in);
        }

        @Override
        public InteractionGoal[] newArray(int size) {
            return new InteractionGoal[size];
        }
    };

    @Override
    public InteractionGoal clone() {
        try {
            InteractionGoal cloned = (InteractionGoal) super.clone();
            if (this.elementIds != null) {
                cloned.elementIds = new ArrayList<>(this.elementIds);
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Compares this InteractionGoal with another object for content equality.
     * <p>
     * Unlike equals() which compares by identity, this method compares all fields
     * for value equality. Two InteractionGoal objects are considered content-equal
     * if they have the same elementIds list and type.
     * </p>
     *
     * @param other the object to compare with
     * @return true if all fields have equal values, false otherwise
     */
    public boolean contentEquals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (!(other instanceof InteractionGoal)) return false;

        InteractionGoal that = (InteractionGoal) other;

        return ContentEqualsHelper.listsEqual(this.elementIds, that.elementIds) &&
                ContentEqualsHelper.stringsEqual(this.type, that.type);
    }

    public void setElementIds(List<String> elementIds) {
        this.elementIds = elementIds;
    }

    public void setType(@CometChatConstants.InteractionType String type) {
        this.type = type;
    }

    public List<String> getElementIds() {
        return elementIds;
    }

    public @CometChatConstants.InteractionType String getType() {
        return type;
    }

    public static InteractionGoal fromJsom(JSONObject jsonObject) {
        InteractionGoal interactionGoal = new InteractionGoal();
        try {
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTION_TYPE)) {
                interactionGoal.setType(jsonObject.getString(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTION_TYPE));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTION_ELEMENT_IDS)) {
                JSONArray jsonArray = jsonObject.getJSONArray(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTION_ELEMENT_IDS);
                interactionGoal.setElementIds(CometChatUtils.getListFromJSONArray(jsonArray));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return interactionGoal;
    }

    public HashMap<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        if (this.getType() != null) {
            map.put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTION_TYPE, this.getType());
        }
        if (this.getElementIds() != null) {
            map.put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTION_ELEMENT_IDS, CometChatUtils.getJSONArrayFromList(this.getElementIds()).toString());
        }
        return map;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (this.getType() != null) {
                jsonObject.put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTION_TYPE, this.getType());
            }
            if (this.getElementIds() != null) {
                if (this.getElementIds().size() > 0) {
                    JSONArray jsonArray = CometChatUtils.getJSONArrayFromList(this.getElementIds());
                    jsonObject.put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTION_ELEMENT_IDS, jsonArray);
                }
            }
        } catch (JSONException jSONException) {
            jSONException.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }

    public String toString() {
        return "InteractionGoals{ elementIds = " + this.getElementIds() + ", type = " + this.getType() + " }";
    }
}
