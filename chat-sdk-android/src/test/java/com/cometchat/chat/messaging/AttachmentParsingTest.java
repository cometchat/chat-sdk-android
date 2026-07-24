package com.cometchat.chat.messaging;

import com.cometchat.chat.models.Attachment;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Feature Area: Messaging / Media messages and attachment field mapping.
 *
 * <p>Covers:
 * <ul>
 *   <li>MSG-04 — Each attachment exposes file URL, name, extension, MIME type and size.</li>
 *   <li>MSG-24 — Server field names are translated to the SDK's public attachment fields
 *       ({@code fileUrl}, {@code fileName}, {@code fileExtension}, {@code fileMimeType},
 *       {@code fileSize}) so the app has a naming contract independent of the wire format.</li>
 * </ul>
 *
 * <p>{@link Attachment} carries the secure-media signing context in static fields. They are
 * cleared around each test so signing state cannot leak between tests.
 */
public class AttachmentParsingTest {

    @Before
    @After
    public void clearSecureMediaContext() {
        Attachment.setFat(null);
        Attachment.setSecureMediaHost(null);
    }

    // ==================== MSG-24: wire format to public field mapping ====================

    @Test
    public void msg24_serverAttachmentFields_mapToPublicAttachmentFields() throws Exception {
        JSONObject json = MessagePayloads.attachment(
                "https://media.example.com/photo.png", "photo", "png", "image/png", 2048);

        Attachment attachment = Attachment.fromJson(json);

        assertEquals("wire 'url' maps to fileUrl", "https://media.example.com/photo.png", attachment.getFileUrl());
        assertEquals("wire 'name' maps to fileName", "photo", attachment.getFileName());
        assertEquals("wire 'extension' maps to fileExtension", "png", attachment.getFileExtension());
        assertEquals("wire 'mimeType' maps to fileMimeType", "image/png", attachment.getFileMimeType());
        assertEquals("wire 'size' maps to fileSize", 2048, attachment.getFileSize());
    }

    @Test
    public void msg24_attachmentWithMissingFields_leavesThemUnsetRatherThanFailing() {
        Attachment attachment = Attachment.fromJson(new JSONObject());

        assertNull(attachment.getFileUrl());
        assertNull(attachment.getFileName());
        assertNull(attachment.getFileExtension());
        assertNull(attachment.getFileMimeType());
        assertEquals("an absent size reads as zero", 0, attachment.getFileSize());
    }

    // ==================== MSG-04: uniform shape across media subtypes ====================

    @Test
    public void msg04_attachmentShapeIsUniformAcrossImageVideoAudioAndFile() throws Exception {
        assertAttachmentRoundTrips("https://media.example.com/a.png", "a", "png", "image/png", 100);
        assertAttachmentRoundTrips("https://media.example.com/b.mp4", "b", "mp4", "video/mp4", 200);
        assertAttachmentRoundTrips("https://media.example.com/c.mp3", "c", "mp3", "audio/mpeg", 300);
        assertAttachmentRoundTrips("https://media.example.com/d.pdf", "d", "pdf", "application/pdf", 400);
    }

    private void assertAttachmentRoundTrips(String url, String name, String ext, String mime, int size)
            throws Exception {
        Attachment attachment = Attachment.fromJson(MessagePayloads.attachment(url, name, ext, mime, size));

        assertEquals(mime + " url", url, attachment.getFileUrl());
        assertEquals(mime + " name", name, attachment.getFileName());
        assertEquals(mime + " extension", ext, attachment.getFileExtension());
        assertEquals(mime + " mimeType", mime, attachment.getFileMimeType());
        assertEquals(mime + " size", size, attachment.getFileSize());
    }

    @Test
    public void msg04_attachmentOnSecureMediaHost_isSignedWithTheSessionToken() throws Exception {
        Attachment.setFat("session-token-abc");
        Attachment.setSecureMediaHost("secure.example.com");

        Attachment attachment = Attachment.fromJson(MessagePayloads.attachment(
                "https://secure.example.com/private.png", "private", "png", "image/png", 10));

        assertEquals("a URL on the secure host must carry the access token",
                "https://secure.example.com/private.png?fat=session-token-abc", attachment.getFileUrl());
    }

    @Test
    public void msg04_attachmentOffSecureMediaHost_isLeftUnsigned() throws Exception {
        Attachment.setFat("session-token-abc");
        Attachment.setSecureMediaHost("secure.example.com");

        Attachment attachment = Attachment.fromJson(MessagePayloads.attachment(
                "https://cdn.example.com/public.png", "public", "png", "image/png", 10));

        assertEquals("a public URL must not be signed",
                "https://cdn.example.com/public.png", attachment.getFileUrl());
    }

    @Test
    public void msg04_attachmentWithoutSessionContext_isLeftUnsigned() throws Exception {
        Attachment attachment = Attachment.fromJson(MessagePayloads.attachment(
                "https://secure.example.com/private.png", "private", "png", "image/png", 10));

        assertEquals("without a session token the URL passes through unchanged",
                "https://secure.example.com/private.png", attachment.getFileUrl());
    }
}
