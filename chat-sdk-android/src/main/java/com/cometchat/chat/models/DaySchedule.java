package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatNotificationsConstants;
import com.cometchat.chat.helpers.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Rohit Giri on 12/02/24.
 */
public class DaySchedule implements Parcelable, Cloneable {
    private int from = 0;
    private int to = 0;
    private boolean dnd = false;

    public DaySchedule() {}

    protected DaySchedule(Parcel in) {
        from = in.readInt();
        to = in.readInt();
        dnd = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(from);
        dest.writeInt(to);
        dest.writeByte((byte) (dnd ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DaySchedule> CREATOR = new Creator<DaySchedule>() {
        @Override
        public DaySchedule createFromParcel(Parcel in) {
            return new DaySchedule(in);
        }

        @Override
        public DaySchedule[] newArray(int size) {
            return new DaySchedule[size];
        }
    };

    @Override
    public DaySchedule clone() {
        try {
            return (DaySchedule) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public boolean getDnd() {
        return dnd;
    }

    public void setDnd(boolean dnd) {
        this.dnd = dnd;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(CometChatNotificationsConstants.DayScheduleKeys.KEY_FROM, from);
            jsonObject.put(CometChatNotificationsConstants.DayScheduleKeys.KEY_TO, to);
            jsonObject.put(CometChatNotificationsConstants.DayScheduleKeys.KEY_DND, dnd);
        } catch (JSONException e) {
            Logger.error(e.toString());
        }
        return jsonObject;
    }

    public static DaySchedule fromJson(JSONObject jsonObject) {
        DaySchedule daySchedule = new DaySchedule();
        try {
            if (jsonObject.has(CometChatNotificationsConstants.DayScheduleKeys.KEY_FROM)){
                daySchedule.setFrom(jsonObject.getInt(CometChatNotificationsConstants.DayScheduleKeys.KEY_FROM));
            }
            if (jsonObject.has(CometChatNotificationsConstants.DayScheduleKeys.KEY_TO)){
                daySchedule.setTo(jsonObject.getInt(CometChatNotificationsConstants.DayScheduleKeys.KEY_TO));
            }
            if (jsonObject.has(CometChatNotificationsConstants.DayScheduleKeys.KEY_DND)){
                daySchedule.setDnd(jsonObject.getBoolean(CometChatNotificationsConstants.DayScheduleKeys.KEY_DND));
            }
        } catch (Exception e){
            Logger.error(e.toString());
        }
        return daySchedule;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(CometChatNotificationsConstants.DayScheduleKeys.KEY_FROM, from);
        map.put(CometChatNotificationsConstants.DayScheduleKeys.KEY_TO, to);
        map.put(CometChatNotificationsConstants.DayScheduleKeys.KEY_DND, dnd);
        return map;
    }

    public static DaySchedule fromMap(Map<String, Object> map) {
        DaySchedule daySchedule = new DaySchedule();
        daySchedule.setFrom(map.containsKey(CometChatNotificationsConstants.DayScheduleKeys.KEY_FROM) ? (int) map.get(CometChatNotificationsConstants.DayScheduleKeys.KEY_FROM) : 0);
        daySchedule.setTo(map.containsKey(CometChatNotificationsConstants.DayScheduleKeys.KEY_TO) ? (int) map.get(CometChatNotificationsConstants.DayScheduleKeys.KEY_TO) : 0);
        daySchedule.setDnd(map.containsKey(CometChatNotificationsConstants.DayScheduleKeys.KEY_DND) ? (boolean) map.get(CometChatNotificationsConstants.DayScheduleKeys.KEY_DND) : false);
        return daySchedule;
    }

    @Override
    public String toString() {
        return "DaySchedule{" +
                "from=" + from +
                ", to=" + to +
                ", dnd=" + dnd +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }

    /**
     * Compares this DaySchedule with another object for content equality.
     * <p>
     * Unlike {@link #equals(Object)} which compares by identity, this method
     * compares all fields for value equality. Two DaySchedule objects are
     * considered content-equal if they have the same from, to, and dnd values.
     * </p>
     *
     * @param other the object to compare with
     * @return true if all fields have equal values, false otherwise
     */
    public boolean contentEquals(Object other) {
        // Same reference check
        if (this == other) return true;

        // Null check
        if (other == null) return false;

        // Type check
        if (!(other instanceof DaySchedule)) return false;

        // Cast
        DaySchedule that = (DaySchedule) other;

        // Compare all primitive fields using == operator
        return from == that.from &&
                to == that.to &&
                dnd == that.dnd;
    }
}
