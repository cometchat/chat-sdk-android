package com.cometchat.chat.models;

/**
 * Created by Rohit Giri on 12/02/24.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatNotificationsConstants;
import com.cometchat.chat.enums.DNDOptions;
import com.cometchat.chat.enums.DayOfWeek;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;


public class MutePreferences implements Parcelable, Cloneable {

    private DNDOptions dnd;
    private Map<DayOfWeek, DaySchedule> schedule;

    public MutePreferences(){}

    public MutePreferences(DNDOptions dnd, Map<DayOfWeek, DaySchedule> schedule) {
        this.dnd = dnd;
        this.schedule = schedule;
    }

    protected MutePreferences(Parcel in) {
        int dndVal = in.readInt();
        dnd = dndVal != -1 ? DNDOptions.get(dndVal) : null;
        int scheduleSize = in.readInt();
        if (scheduleSize > 0) {
            schedule = new HashMap<>();
            for (int i = 0; i < scheduleSize; i++) {
                String dayName = in.readString();
                DaySchedule daySchedule = in.readParcelable(DaySchedule.class.getClassLoader());
                if (dayName != null) {
                    schedule.put(DayOfWeek.get(dayName), daySchedule);
                }
            }
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dnd != null ? dnd.getValue() : -1);
        if (schedule != null && !schedule.isEmpty()) {
            dest.writeInt(schedule.size());
            for (Map.Entry<DayOfWeek, DaySchedule> entry : schedule.entrySet()) {
                dest.writeString(entry.getKey().getDayName());
                dest.writeParcelable(entry.getValue(), flags);
            }
        } else {
            dest.writeInt(0);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MutePreferences> CREATOR = new Creator<MutePreferences>() {
        @Override
        public MutePreferences createFromParcel(Parcel in) {
            return new MutePreferences(in);
        }

        @Override
        public MutePreferences[] newArray(int size) {
            return new MutePreferences[size];
        }
    };

    @Override
    public MutePreferences clone() {
        try {
            MutePreferences cloned = (MutePreferences) super.clone();
            if (this.schedule != null) {
                cloned.schedule = new HashMap<>();
                for (Map.Entry<DayOfWeek, DaySchedule> entry : this.schedule.entrySet()) {
                    cloned.schedule.put(entry.getKey(), entry.getValue().clone());
                }
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public DNDOptions getDNDPreference() {
        return dnd;
    }

    public void setDNDPreference(DNDOptions dnd) {
        this.dnd = dnd;
    }

    public Map<DayOfWeek, DaySchedule> getSchedulePreference() {
        return schedule;
    }

    public void setSchedulePreference(Map<DayOfWeek, DaySchedule> schedule) {
        this.schedule = schedule;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (getDNDPreference() != null) {
                jsonObject.put(CometChatNotificationsConstants.MutePreferencesKeys.KEY_DND, dnd.getValue());
            }
            if (schedule != null && !schedule.isEmpty()) {
                JSONObject scheduleJsonObject = new JSONObject();
                for (Map.Entry<DayOfWeek, DaySchedule> entry : schedule.entrySet()) {
                    scheduleJsonObject.put(entry.getKey().getDayName(), entry.getValue().toJson());
                }
                jsonObject.put(CometChatNotificationsConstants.MutePreferencesKeys.KEY_SCHEDULE, scheduleJsonObject);
            }
        } catch (Exception e) {
            Logger.error(e.toString());
        }
        return jsonObject;
    }

    public static MutePreferences fromJson(JSONObject jsonObject) {
        MutePreferences mutePreferences = new MutePreferences();
        try {
            if (jsonObject.has(CometChatNotificationsConstants.MutePreferencesKeys.KEY_DND)) {
                mutePreferences.setDNDPreference(DNDOptions.get(jsonObject.getInt(CometChatNotificationsConstants.MutePreferencesKeys.KEY_DND)));
            }
            if (jsonObject.has(CometChatNotificationsConstants.MutePreferencesKeys.KEY_SCHEDULE)){
                JSONObject scheduleJsonObject = jsonObject.getJSONObject(CometChatNotificationsConstants.MutePreferencesKeys.KEY_SCHEDULE);
                Iterator<String> keys = scheduleJsonObject.keys();
                Map<DayOfWeek, DaySchedule> schedule = new HashMap<>();
                while (keys.hasNext()) {
                    String day = keys.next();
                    JSONObject dayObject = scheduleJsonObject.getJSONObject(day);
                    DaySchedule daySchedule = DaySchedule.fromJson(dayObject);
                    schedule.put(DayOfWeek.get(day), daySchedule);
                }
                mutePreferences.setSchedulePreference(schedule);
            }
        }catch (Exception e) {
            Logger.error(e.toString());
        }
        return mutePreferences;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (dnd != null){
            map.put(CometChatNotificationsConstants.MutePreferencesKeys.KEY_DND, dnd.getValue());
        }
        if (schedule != null && !schedule.isEmpty()) {
            Map<String, Object> scheduleMap = new HashMap<>();
            for (Map.Entry<DayOfWeek, DaySchedule> entry : schedule.entrySet()) {
                scheduleMap.put(entry.getKey().getDayName(), entry.getValue().toMap());
            }
            map.put(CometChatNotificationsConstants.MutePreferencesKeys.KEY_SCHEDULE, scheduleMap);
        }
        return map;
    }

    public static MutePreferences fromMap(Map<String, Object> map) {
        MutePreferences mutePreferences = new MutePreferences();
        if (map.containsKey(CometChatNotificationsConstants.MutePreferencesKeys.KEY_DND)){
            mutePreferences.setDNDPreference(DNDOptions.get((int) map.get(CometChatNotificationsConstants.MutePreferencesKeys.KEY_DND)));
        }
        if (map.containsKey(CometChatNotificationsConstants.MutePreferencesKeys.KEY_SCHEDULE)) {
            Map<String, Object> scheduleMap = (Map<String, Object>) map.get(CometChatNotificationsConstants.MutePreferencesKeys.KEY_SCHEDULE);
            Map<DayOfWeek, DaySchedule> dayScheduleMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : scheduleMap.entrySet()) {
                DayOfWeek day = DayOfWeek.get(entry.getKey());
                DaySchedule daySchedule = DaySchedule.fromMap((Map<String, Object>) entry.getValue());
                dayScheduleMap.put(day, daySchedule);
            }
            mutePreferences.setSchedulePreference(dayScheduleMap);
        }
        return mutePreferences;
    }

    @Override
    public String toString() {
        return "MutePreferences{" +
                "dnd=" + dnd +
                ", schedule=" + schedule +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
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
        if (!(other instanceof MutePreferences)) return false;

        // 4. Cast
        MutePreferences that = (MutePreferences) other;

        // 5. Compare all fields
        // Compare enum field using ==
        if (dnd != that.dnd) return false;

        // Compare Map<DayOfWeek, DaySchedule> field
        if (!mapsContentEqual(schedule, that.schedule)) return false;

        return true;
    }

    /**
     * Helper method to compare two Maps for content equality.
     * Uses contentEquals() for values if available, otherwise uses equals().
     *
     * @param a first Map (may be null)
     * @param b second Map (may be null)
     * @return true if both are null or both have equal content, false otherwise
     */
    private boolean mapsContentEqual(Map<DayOfWeek, DaySchedule> a, Map<DayOfWeek, DaySchedule> b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a.size() != b.size()) return false;

        for (Map.Entry<DayOfWeek, DaySchedule> entry : a.entrySet()) {
            DayOfWeek key = entry.getKey();
            DaySchedule valueA = entry.getValue();
            DaySchedule valueB = b.get(key);

            if (!ContentEqualsHelper.objectsContentEqual(valueA, valueB)) {
                return false;
            }
        }
        return true;
    }
}
