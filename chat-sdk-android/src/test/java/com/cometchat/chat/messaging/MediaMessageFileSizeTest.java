package com.cometchat.chat.messaging;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.MediaMessage;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Feature Area: Messaging / Media messages.
 *
 * <p>Covers MSG-05 — App asks the SDK for the total size of all files in a message.
 * Total file size sums every attachment's size and treats missing/null sizes and
 * messages with no attachments as zero, never throwing.
 */
public class MediaMessageFileSizeTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final String RECEIVER_UID = "receiver-1";

    /** Creates a real file of exactly {@code byteCount} bytes, since size is read off disk. */
    private File fileOfSize(String name, int byteCount) throws Exception {
        File file = temporaryFolder.newFile(name);
        byte[] content = new byte[byteCount];
        Arrays.fill(content, (byte) 'x');
        Files.write(file.toPath(), content);
        return file;
    }

    private MediaMessage mediaMessageWith(List<File> files) {
        return new MediaMessage(RECEIVER_UID, files, CometChatConstants.MESSAGE_TYPE_FILE,
                CometChatConstants.RECEIVER_TYPE_USER);
    }

    @Test
    public void msg05_totalFileSize_sumsEveryAttachment() throws Exception {
        List<File> files = Arrays.asList(fileOfSize("a.txt", 100), fileOfSize("b.txt", 250));

        assertEquals(350L, mediaMessageWith(files).getTotalFileSize());
    }

    @Test
    public void msg05_totalFileSize_ofASingleFileMessage_isThatFilesSize() throws Exception {
        File file = fileOfSize("only.txt", 512);
        MediaMessage message = new MediaMessage(RECEIVER_UID, file, CometChatConstants.MESSAGE_TYPE_FILE,
                CometChatConstants.RECEIVER_TYPE_USER);

        assertEquals(512L, message.getTotalFileSize());
    }

    @Test
    public void msg05_totalFileSize_withNoFiles_isZero() {
        MediaMessage message = new MediaMessage(RECEIVER_UID, CometChatConstants.MESSAGE_TYPE_FILE,
                CometChatConstants.RECEIVER_TYPE_USER);

        assertEquals("a message with no attachments must report zero, not throw",
                0L, message.getTotalFileSize());
    }

    @Test
    public void msg05_totalFileSize_withEmptyFileList_isZero() {
        assertEquals(0L, mediaMessageWith(Collections.<File>emptyList()).getTotalFileSize());
    }

    @Test
    public void msg05_totalFileSize_skipsNullEntriesWithoutThrowing() throws Exception {
        List<File> files = new ArrayList<>();
        files.add(fileOfSize("present.txt", 80));
        files.add(null);

        assertEquals("a null entry must be skipped rather than crash",
                80L, mediaMessageWith(files).getTotalFileSize());
    }

    @Test
    public void msg05_totalFileSize_skipsFilesThatDoNotExistOnDisk() throws Exception {
        List<File> files = Arrays.asList(
                fileOfSize("real.txt", 60),
                new File(temporaryFolder.getRoot(), "never-created.txt"));

        assertEquals("a file that isn't on disk contributes zero",
                60L, mediaMessageWith(files).getTotalFileSize());
    }

    @Test
    public void msg05_totalFileSize_ofAnEmptyFile_contributesZero() throws Exception {
        List<File> files = Arrays.asList(fileOfSize("empty.txt", 0), fileOfSize("sized.txt", 40));

        assertEquals(40L, mediaMessageWith(files).getTotalFileSize());
    }

    @Test
    public void msg05_totalFileSize_reflectsActualBytesWritten() throws Exception {
        File file = temporaryFolder.newFile("greeting.txt");
        byte[] content = "hello".getBytes(StandardCharsets.UTF_8);
        Files.write(file.toPath(), content);

        assertEquals(content.length, mediaMessageWith(Collections.singletonList(file)).getTotalFileSize());
    }
}
