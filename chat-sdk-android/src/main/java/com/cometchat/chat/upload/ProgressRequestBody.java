package com.cometchat.chat.upload;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Wraps a {@link RequestBody} and streams byte-write progress to a callback as
 * the body is uploaded. Used for per-file upload progress on the direct-to-storage
 * POST (the storage upload form). Because each file is its own request, the bytes
 * counted here are that single file's bytes.
 *
 * @since <b>v5</b>
 */
public class ProgressRequestBody extends RequestBody {

    /** Receives byte-write progress as the body uploads. */
    public interface ProgressCallback {
        void onProgress(long bytesWritten, long contentLength);
    }

    private final RequestBody delegate;
    private final ProgressCallback callback;

    public ProgressRequestBody(RequestBody delegate, ProgressCallback callback) {
        this.delegate = delegate;
        this.callback = callback;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return delegate.contentLength();
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        BufferedSink bufferedSink = Okio.buffer(new CountingSink(sink));
        delegate.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    private long contentLengthSafe() {
        try {
            return contentLength();
        } catch (IOException e) {
            return -1;
        }
    }

    private final class CountingSink extends ForwardingSink {
        private long bytesWritten = 0;

        CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(@NonNull Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            bytesWritten += byteCount;
            if (callback != null) {
                callback.onProgress(bytesWritten, contentLengthSafe());
            }
        }
    }
}
