package com.cometchat.chat.upload;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

/**
 * One file to upload, paired with the <b>app-supplied {@code fileId}</b> that
 * identifies it for the whole upload lifecycle — every
 * {@link UploadFileListener} event, batch getter, remove and retry call echoes
 * this id back. The SDK does not assign, derive, or reorder file ids; the app
 * owns them and is responsible for their uniqueness within the batch.
 *
 * <p>Backed by either a {@link File} or a content {@link Uri} (the Android
 * platform-native file inputs). Uri-backed items need a {@code Context} at
 * upload time to resolve name/size/mime — use the
 * {@code uploadAttachments(Context, ...)} overload for them.</p>
 *
 * @since <b>v5</b>
 */
public class UploadFileItem {

    private final String fileId;
    private final File file;
    private final Uri uri;

    /** Pairs the app's {@code fileId} with an on-disk {@link File}. */
    public UploadFileItem(@NonNull String fileId, @NonNull File file) {
        this.fileId = fileId;
        this.file = file;
        this.uri = null;
    }

    /** Pairs the app's {@code fileId} with a content {@link Uri} (document/media picker). */
    public UploadFileItem(@NonNull String fileId, @NonNull Uri uri) {
        this.fileId = fileId;
        this.file = null;
        this.uri = uri;
    }

    @NonNull
    public String getFileId() {
        return fileId;
    }

    @Nullable
    public File getFile() {
        return file;
    }

    @Nullable
    public Uri getUri() {
        return uri;
    }
}
