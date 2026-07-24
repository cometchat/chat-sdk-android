package com.cometchat.chat.models;


import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.core.CallSettings;
import com.cometchat.chat.utils.ContentEqualsHelper;

/**
 * Created by Rohit Giri on 28/10/22.
 */

public class MainVideoContainerSetting implements Parcelable, Cloneable {
    private String aspectRatio;
    private ItemSettings fullScreenButton;
    private ItemSettings userListButton;
    private ItemSettings zoomButton;
    private ItemSettings nameLabel;

    public MainVideoContainerSetting() {}

    protected MainVideoContainerSetting(Parcel in) {
        aspectRatio = in.readString();
        fullScreenButton = in.readParcelable(ItemSettings.class.getClassLoader());
        userListButton = in.readParcelable(ItemSettings.class.getClassLoader());
        zoomButton = in.readParcelable(ItemSettings.class.getClassLoader());
        nameLabel = in.readParcelable(ItemSettings.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(aspectRatio);
        dest.writeParcelable(fullScreenButton, flags);
        dest.writeParcelable(userListButton, flags);
        dest.writeParcelable(zoomButton, flags);
        dest.writeParcelable(nameLabel, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MainVideoContainerSetting> CREATOR = new Creator<MainVideoContainerSetting>() {
        @Override
        public MainVideoContainerSetting createFromParcel(Parcel in) {
            return new MainVideoContainerSetting(in);
        }

        @Override
        public MainVideoContainerSetting[] newArray(int size) {
            return new MainVideoContainerSetting[size];
        }
    };

    @Override
    public MainVideoContainerSetting clone() {
        try {
            MainVideoContainerSetting cloned = (MainVideoContainerSetting) super.clone();
            if (this.fullScreenButton != null) cloned.fullScreenButton = this.fullScreenButton.clone();
            if (this.userListButton != null) cloned.userListButton = this.userListButton.clone();
            if (this.zoomButton != null) cloned.zoomButton = this.zoomButton.clone();
            if (this.nameLabel != null) cloned.nameLabel = this.nameLabel.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     *  A method to set the configuration for video container mode.
     * @version <b>v3</b>
     * @since   <b>v3</b>
     * @param aspectRatio A configuration for video call container mode.
     */
    public void setMainVideoAspectRatio(String aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public String getMainVideoAspectRatio() {
        return this.aspectRatio == null ? CallSettings.ASPECT_RATIO_DEFAULT : this.aspectRatio;
    }

    /**
     *  A method to set the configuration for video full screen mode button.
     * @version <b>v3</b>
     * @since   <b>v3</b>
     * @param position A string value that determines the position of full screen mode button.
     * @param visibility A boolean value that determines the visibility of full screen mode button.
     */
    public void setFullScreenButtonParams(String position, Boolean visibility) {
        this.fullScreenButton = new ItemSettings(position, visibility, "");
    }

    public ItemSettings getFullScreenButtonParams() {
        return getItemSettings(this.fullScreenButton, CallSettings.POSITION_BOTTOM_RIGHT, true, "");
    }

    /**
     *  A method to set the configuration for video name label.
     * @version <b>v3</b>
     * @since   <b>v3</b>
     * @param position A string value that determines the position of name label.
     * @param visibility A boolean value that determines the visibility of name label.
     * @param backgroundColor A string value that determines the background color of name label.
     */
    public void setNameLabelParams(String position, Boolean visibility, String backgroundColor) {
        this.nameLabel = new ItemSettings(position, visibility, backgroundColor);
    }

    public ItemSettings getNameLabelParams() {
        return getItemSettings(this.nameLabel, CallSettings.POSITION_BOTTOM_LEFT, true, "#333333");
    }

    /**
     *  A method to set the configuration for video call zoom button.
     * @version <b>v3</b>
     * @since   <b>v3</b>
     * @param position A string value that determines the position of zoom button.
     * @param visibility A boolean value that determines the visibility of zoom button.
     */
    public void setZoomButtonParams(String position, Boolean visibility) {
        this.zoomButton = new ItemSettings(position, visibility, "");
    }

    public ItemSettings getZoomButtonParams() {
        return getItemSettings(this.zoomButton, CallSettings.POSITION_BOTTOM_RIGHT, true, "");
    }

    /**
     *  A method to set the configuration for video call joined user list button.
     * @version <b>v3</b>
     * @since   <b>v3</b>
     * @param position A string value that determines the position of user list button.
     * @param visibility A boolean value that determines the visibility of user list button.
     */
    public void setUserListButtonParams(String position, Boolean visibility) {
        this.userListButton = new ItemSettings(position, visibility, "");
    }

    public ItemSettings getUserListButtonParams() {
        return getItemSettings(this.userListButton, CallSettings.POSITION_BOTTOM_RIGHT, true, "");
    }

    private ItemSettings getItemSettings(
            ItemSettings itemSettings,
            String position,
            boolean visibility,
            String color
    ) {
        if (itemSettings == null) {
            return new ItemSettings(
                    position,
                    visibility,
                    color
            );
        } else {
            return new ItemSettings(
                    itemSettings.getPosition() == null ? position : itemSettings.getPosition(),
                    itemSettings.getVisibility() == null ? visibility : itemSettings.getVisibility(),
                    itemSettings.getColor() == null ? color : itemSettings.getColor()
            );
        }
    }

    public Bundle asBundle() {
        Bundle bundle = new Bundle();
        if (aspectRatio != null) {
            bundle.putString("videoFit", aspectRatio);
        }
        if (fullScreenButton != null) {
            bundle.putBundle("fullScreenButton", fullScreenButton.getBundle());
        }
        if (nameLabel != null) {
            bundle.putBundle("nameLabel", nameLabel.getBundle());
        }
        if (zoomButton != null) {
            bundle.putBundle("zoomButton", zoomButton.getBundle());
        }
        if (userListButton != null) {
            bundle.putBundle("userListButton", userListButton.getBundle());
        }
        return bundle;
    }

    @Override
    public String toString() {
        return "MainVideoContainerSetting{" +
                "videoFit='" + aspectRatio + '\'' +
                ", fullScreenButton=" + fullScreenButton +
                ", userListButton=" + userListButton +
                ", zoomButton=" + zoomButton +
                ", nameLabel=" + nameLabel +
                '}';
    }

    /**
     * Compares this MainVideoContainerSetting object with another for content equality.
     * <p>
     * Unlike equals() which compares by identity, this method compares all fields
     * for value equality. Two MainVideoContainerSetting objects are considered content-equal
     * if all their fields have equal values.
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
        if (!(other instanceof MainVideoContainerSetting)) return false;

        // Cast
        MainVideoContainerSetting that = (MainVideoContainerSetting) other;

        // Compare all fields
        return ContentEqualsHelper.stringsEqual(aspectRatio, that.aspectRatio) &&
                ContentEqualsHelper.objectsContentEqual(fullScreenButton, that.fullScreenButton) &&
                ContentEqualsHelper.objectsContentEqual(userListButton, that.userListButton) &&
                ContentEqualsHelper.objectsContentEqual(zoomButton, that.zoomButton) &&
                ContentEqualsHelper.objectsContentEqual(nameLabel, that.nameLabel);
    }
    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }
}
