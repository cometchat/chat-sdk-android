package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CurrentUserRepo;
import com.cometchat.chat.core.Settings;
import com.cometchat.chat.core.SettingsRepo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import com.cometchat.chat.utils.ContentEqualsHelper;


/**
 * Attachment class gives details of the media file
 */
public class Attachment implements Parcelable, Cloneable {

    private String fileName;
    private String fileExtension;
    private int fileSize;
    private String fileMimeType;
    private String fileUrl;
    private JSONObject metadata;
    private static String fat;
    private static String secureMediaHost;

    public Attachment() {}

    protected Attachment(Parcel in) {
        fileName = in.readString();
        fileExtension = in.readString();
        fileSize = in.readInt();
        fileMimeType = in.readString();
        fileUrl = in.readString();
        String metadataStr = in.readString();
        if (metadataStr != null) {
            try {
                metadata = new JSONObject(metadataStr);
            } catch (JSONException ignored) {
            }
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileName);
        dest.writeString(fileExtension);
        dest.writeInt(fileSize);
        dest.writeString(fileMimeType);
        dest.writeString(fileUrl);
        dest.writeString(metadata != null ? metadata.toString() : null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Attachment> CREATOR = new Creator<Attachment>() {
        @Override
        public Attachment createFromParcel(Parcel in) {
            return new Attachment(in);
        }

        @Override
        public Attachment[] newArray(int size) {
            return new Attachment[size];
        }
    };

    @Override
    public Attachment clone() {
        try {
            return (Attachment) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Get name of the media file
     *
     * @return Name of the file attached
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return Extension of the file shared
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    /**
     * Get size of the file
     *
     * @return Size of the file shared.
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * Get mime type of the file
     *
     * @return Mime type of the file shared.
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getFileMimeType() {
        return fileMimeType;
    }

    public void setFileMimeType(String fileMimeType) {
        this.fileMimeType = fileMimeType;
    }

    /**
     * Get url of the file
     *
     * @return URL of the file shared.
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    /**
     * Get optional metadata of the file (e.g. {@code width}, {@code height},
     * {@code duration}). May be {@code null}.
     *
     * @return A {@link JSONObject} of file metadata, or {@code null} if none.
     * @since <b>v5</b>
     */
    public JSONObject getMetadata() {
        return metadata;
    }

    public void setMetadata(JSONObject metadata) {
        this.metadata = metadata;
    }

    public static void setFat(String fat) {
        Attachment.fat = fat;
    }

    public static void setSecureMediaHost(String secureMediaHost) {
        Attachment.secureMediaHost = secureMediaHost;
    }


    /**
     * Get object of the <code>Attachment</code> class with file details
     *
     * @param attachmentObject
     * @return An object of the <code>Attachment</code> class
     * @version <b>v2</b>
     * @see Attachment
     * @since <b>v1</b>
     */
    public static Attachment fromJson(JSONObject attachmentObject) {
        Attachment attachment = new Attachment();
        try {
            if (attachmentObject.has(CometChatConstants.MessageKeys.KEY_ATTACHMENT_NAME))
                attachment.setFileName(attachmentObject.getString(CometChatConstants.MessageKeys.KEY_ATTACHMENT_NAME));
            if (attachmentObject.has(CometChatConstants.MessageKeys.KEY_ATTACHMENT_EXTENSION))
                attachment.setFileExtension(attachmentObject.getString(CometChatConstants.MessageKeys.KEY_ATTACHMENT_EXTENSION));
            if (attachmentObject.has(CometChatConstants.MessageKeys.KEY_ATTACHMENT_SIZE))
                attachment.setFileSize(attachmentObject.getInt(CometChatConstants.MessageKeys.KEY_ATTACHMENT_SIZE));
            if (attachmentObject.has(CometChatConstants.MessageKeys.KEY_ATTACHMENT_MIMETYPE))
                attachment.setFileMimeType(attachmentObject.getString(CometChatConstants.MessageKeys.KEY_ATTACHMENT_MIMETYPE));
            if (attachmentObject.has(CometChatConstants.MessageKeys.KEY_ATTACHMENT_URL)){
                if (Attachment.fat != null && Attachment.secureMediaHost != null){
                    if (attachmentObject.getString(CometChatConstants.MessageKeys.KEY_ATTACHMENT_URL).contains(Attachment.secureMediaHost)){
                        String mediaUrl = attachmentObject.getString(CometChatConstants.MessageKeys.KEY_ATTACHMENT_URL) + "?fat=" + fat;
                        attachment.setFileUrl(mediaUrl);
                    }else{
                        attachment.setFileUrl(attachmentObject.getString(CometChatConstants.MessageKeys.KEY_ATTACHMENT_URL));
                    }
                }else{
                    attachment.setFileUrl(attachmentObject.getString(CometChatConstants.MessageKeys.KEY_ATTACHMENT_URL));
                }
            }
            if (attachmentObject.has(CometChatConstants.MessageKeys.KEY_ATTACHMENT_METADATA))
                attachment.setMetadata(attachmentObject.getJSONObject(CometChatConstants.MessageKeys.KEY_ATTACHMENT_METADATA));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attachment;
    }

    JSONObject toJson() throws JSONException{
        JSONObject jsonObject = new JSONObject();
        if(this.getFileUrl()!=null)
            jsonObject.put(CometChatConstants.MessageKeys.KEY_ATTACHMENT_URL, this.getFileUrl());
        if(this.getFileExtension()!=null)
            jsonObject.put(CometChatConstants.MessageKeys.KEY_ATTACHMENT_EXTENSION, this.getFileExtension());
        if(this.getFileMimeType()!=null)
            jsonObject.put(CometChatConstants.MessageKeys.KEY_ATTACHMENT_MIMETYPE, this.getFileMimeType());
        if(this.getFileName()!=null)
            jsonObject.put(CometChatConstants.MessageKeys.KEY_ATTACHMENT_NAME, this.getFileName());
        if(this.getFileSize() > 0)
            jsonObject.put(CometChatConstants.MessageKeys.KEY_ATTACHMENT_SIZE, this.getFileSize());
        if(this.getMetadata() != null)
            jsonObject.put(CometChatConstants.MessageKeys.KEY_ATTACHMENT_METADATA, this.getMetadata());
        return jsonObject;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return contentEquals(obj);
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
        if (!(other instanceof Attachment)) return false;

        // 4. Cast
        Attachment that = (Attachment) other;

        // 5. Compare all fields
        String thisMetadata = metadata != null ? metadata.toString() : null;
        String thatMetadata = that.metadata != null ? that.metadata.toString() : null;
        return fileSize == that.fileSize &&
                ContentEqualsHelper.stringsEqual(fileName, that.fileName) &&
                ContentEqualsHelper.stringsEqual(fileExtension, that.fileExtension) &&
                ContentEqualsHelper.stringsEqual(fileMimeType, that.fileMimeType) &&
                ContentEqualsHelper.stringsEqual(fileUrl, that.fileUrl) &&
                ContentEqualsHelper.stringsEqual(thisMetadata, thatMetadata);
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "fileName='" + fileName + '\'' +
                ", fileExtension='" + fileExtension + '\'' +
                ", fileSize=" + fileSize +
                ", fileMimeType='" + fileMimeType + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                '}';
    }
}
