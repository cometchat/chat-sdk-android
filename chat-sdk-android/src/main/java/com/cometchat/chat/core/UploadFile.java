package com.cometchat.chat.core;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Okio;
import okio.Source;

/**
 * Normalizes a platform-native file input — either a {@link File} or a content
 * {@link Uri} — into the {@code name} / {@code size} / {@code mimeType} the
 * presign needs, and into a streaming OkHttp {@link RequestBody} for upload.
 *
 * <p>Streaming (rather than reading bytes into memory) keeps large uploads
 * within bounds and lets {@code ProgressRequestBody} report real byte progress.</p>
 *
 * @since <b>v5</b>
 */
class UploadFile {

    private final String fileId;      // app-supplied id this file uploads under
    private final File file;          // one of file / uri is non-null
    private final Uri uri;
    private final Context context;    // required for uri

    private String name;
    private long size = -1;
    private String mimeType;
    private boolean resolved;

    UploadFile(String fileId, File file) {
        this.fileId = fileId;
        this.file = file;
        this.uri = null;
        this.context = null;
    }

    UploadFile(String fileId, Uri uri, Context context) {
        this.fileId = fileId;
        this.file = null;
        this.uri = uri;
        this.context = context != null ? context.getApplicationContext() : null;
    }

    String getFileId() {
        return fileId;
    }

    private void resolve() {
        if (resolved) return;
        resolved = true;
        if (file != null) {
            name = file.getName();
            size = file.exists() ? file.length() : -1;
            mimeType = mimeFromExtension(name);
        } else if (uri != null && context != null) {
            queryUriMetadata();
            mimeType = context.getContentResolver().getType(uri);
            if (mimeType == null) mimeType = mimeFromExtension(name);
        }
        if (mimeType == null) mimeType = "application/octet-stream";
    }

    private void queryUriMetadata() {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (nameIndex >= 0) name = cursor.getString(nameIndex);
                if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) size = cursor.getLong(sizeIndex);
            }
        } catch (Exception ignored) {
        } finally {
            if (cursor != null) cursor.close();
        }
        if (name == null) name = uri.getLastPathSegment();
    }

    private static String mimeFromExtension(String name) {
        if (name == null) return null;
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) return null;
        String ext = name.substring(dot + 1).toLowerCase();
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
    }

    String getName() {
        resolve();
        return name;
    }

    long getSize() {
        resolve();
        return size;
    }

    String getMimeType() {
        resolve();
        return mimeType;
    }

    /** True if name/size could be determined and the source is readable. */
    boolean isValid() {
        resolve();
        if (size <= 0 || name == null) return false;
        if (file != null) return file.exists();
        return uri != null && context != null;
    }

    /** Builds a fresh streaming request body (callable again for retry). */
    RequestBody toRequestBody() {
        resolve();
        final MediaType mediaType = MediaType.parse(mimeType != null ? mimeType : "application/octet-stream");
        final long contentLength = size;
        if (file != null) {
            return RequestBody.create(mediaType, file);
        }
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return mediaType;
            }

            @Override
            public long contentLength() {
                return contentLength;
            }

            @Override
            public void writeTo(@NonNull okio.BufferedSink sink) throws IOException {
                InputStream in = context.getContentResolver().openInputStream(uri);
                if (in == null) throw new IOException("Unable to open input stream for " + uri);
                Source source = null;
                try {
                    source = Okio.source(in);
                    sink.writeAll(source);
                } finally {
                    if (source != null) source.close();
                    else in.close();
                }
            }
        };
    }
}
