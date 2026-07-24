package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.utils.ContentEqualsHelper;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by adityagokula on 04/10/18.
 */

public class UserPresence implements Parcelable, Cloneable {

    public static final String TABLE_PRESENCE = "Presence";

    public static final String COLUMN_UID = "uid";
    public static final String COLUMN_JID = "jid";

    private String jid;
    private String status;
    private long lastActiveAt;

    public UserPresence() {}

    protected UserPresence(Parcel in) {
        jid = in.readString();
        status = in.readString();
        lastActiveAt = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(jid);
        dest.writeString(status);
        dest.writeLong(lastActiveAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserPresence> CREATOR = new Creator<UserPresence>() {
        @Override
        public UserPresence createFromParcel(Parcel in) {
            return new UserPresence(in);
        }

        @Override
        public UserPresence[] newArray(int size) {
            return new UserPresence[size];
        }
    };

    @Override
    public UserPresence clone() {
        try {
            return (UserPresence) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
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
        if (!(other instanceof UserPresence)) return false;

        // 4. Cast
        UserPresence that = (UserPresence) other;

        // 5. Compare all fields
        return ContentEqualsHelper.stringsEqual(jid, that.jid) &&
                ContentEqualsHelper.stringsEqual(status, that.status) &&
                lastActiveAt == that.lastActiveAt;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getLastActiveAt() {
        return lastActiveAt;
    }

    public void setLastActiveAt(long lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    public static UserPresence fromXml(String presenceResponse) {
        UserPresence userPresence = new UserPresence();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(presenceResponse));
            int eventType = parser.next();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String elementName = parser.getName();
                        if (elementName.equalsIgnoreCase(CometChatConstants.PresenceResponse.PRESENCE_ELEMENT_NAME)) {
                            String jidString = parser.getAttributeValue(null, CometChatConstants.PresenceResponse.PRESENCE_ATTRIBUTE_FROM);
                            userPresence.setJid(jidString);
                            String status = parser.getAttributeValue(null, CometChatConstants.PresenceResponse.PRESENCE_ATTRIBUTE_TYPE);
                            if (status != null && status.equalsIgnoreCase("unavailable"))
                                userPresence.setStatus(CometChatConstants.USER_STATUS_OFFLINE);
                            else
                                userPresence.setStatus(CometChatConstants.USER_STATUS_ONLINE);
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            Logger.error("Presence Data Processing Exception : " + e.getMessage());
        }
        userPresence.setLastActiveAt(System.currentTimeMillis());
        return userPresence;
    }

    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }
}
