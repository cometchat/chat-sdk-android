package com.cometchat.chat.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.os.Handler;
import android.os.Looper;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.exceptions.CometChatException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * ENG-39679 — a queued connection method must deliver exactly one terminal callback.
 *
 * Drives {@link ConnectionController}'s queue with a real {@link Handler}, which the JVM unit
 * tests cannot do. Two completers race for every queued method: the SDK result, and the watchdog
 * armed when the method overruns its window. These pin that exactly one of them wins.
 *
 * The delayed case is the one caught in review: the watchdog deadline used to equal the method's
 * own start time, so the real result could never win and every reconnection attempt reported a
 * timeout whatever actually happened.
 *
 * Run: ./gradlew :chat-sdk-android:connectedDebugAndroidTest \
 *   -Pandroid.testInstrumentationRunnerArguments.class=com.cometchat.chat.core.ConnectionQueueCompletionTest
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class ConnectionQueueCompletionTest {

    /** Matches ConnectionController.DEFAULT_FORCE_COMPLETE_DELAY. */
    private static final long GRACE_MS = 5000;

    private ConnectionController controller;
    private List<String> terminals;
    private CountDownLatch firstTerminal;

    @Before
    public void setUp() {
        controller = ConnectionController.getInstance();
        controller.clearMethodQueue();
        terminals = Collections.synchronizedList(new ArrayList<String>());
        firstTerminal = new CountDownLatch(1);
    }

    /** Records every terminal callback, so a second one is visible rather than silently ignored. */
    private CometChat.CallbackListener<String> recordingListener(final List<String> sink,
                                                                 final CountDownLatch latch) {
        return new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                sink.add("success:" + s);
                latch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                sink.add("error:" + e.getCode());
                latch.countDown();
            }
        };
    }

    /**
     * Queues a method that reports its SDK result a set time after it begins.
     *
     * @param startDelayMs       delay before the queued method starts
     * @param resultAfterStartMs how long the simulated SDK call takes once started
     * @param succeed            whether that call reports success or failure
     * @param sink               collects the terminal callbacks the caller receives
     * @param latch              counted down on the first terminal callback
     */
    private void queueMethod(final long startDelayMs,
                             final long resultAfterStartMs,
                             final boolean succeed,
                             final List<String> sink,
                             final CountDownLatch latch) {
        final ConnectionController.MethodCompletion completion =
            controller.new MethodCompletion("test", recordingListener(sink, latch), startDelayMs);

        controller.enqueueMethod(new Runnable() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (succeed) {
                            completion.onSuccess("connected");
                        } else {
                            completion.onError(new CometChatException("SDK_ERROR", "sdk failed"));
                        }
                    }
                }, resultAfterStartMs);
            }
        }, startDelayMs, completion);
    }

    /** Lets any second callback land before asserting, so a duplicate cannot slip past. */
    private void awaitSettled(long firstTerminalTimeoutMs) throws InterruptedException {
        assertTrue("no terminal callback arrived",
                   firstTerminal.await(firstTerminalTimeoutMs, TimeUnit.MILLISECONDS));
        Thread.sleep(GRACE_MS + 3000);
    }

    @Test
    public void delayedMethodDeliversTheRealResultNotTheWatchdog() throws Exception {
        // ReconnectionController's shape: queued 5s out, result 1s after it starts. Under the old
        // deadline the watchdog fired the instant the method began and the result was discarded.
        queueMethod(GRACE_MS, 1000, true, terminals, firstTerminal);

        awaitSettled(20000);

        assertEquals("expected exactly one terminal callback, got " + terminals, 1, terminals.size());
        assertEquals("success:connected", terminals.get(0));
    }

    @Test
    public void immediateMethodDeliversTheRealResult() throws Exception {
        queueMethod(0, 500, true, terminals, firstTerminal);

        awaitSettled(15000);

        assertEquals("expected exactly one terminal callback, got " + terminals, 1, terminals.size());
        assertEquals("success:connected", terminals.get(0));
    }

    @Test
    public void watchdogWinsOnOverrunAndTheLateResultIsDropped() throws Exception {
        // The SMG crash: the watchdog answers, then the real result arrives. Only one may reach
        // the caller, otherwise a one-shot adapter resumes twice.
        queueMethod(0, GRACE_MS + 4000, true, terminals, firstTerminal);

        assertTrue("watchdog did not fire", firstTerminal.await(15000, TimeUnit.MILLISECONDS));
        assertEquals("error:" + CometChatConstants.Errors.ERR_METHOD_TIMEOUT, terminals.get(0));

        Thread.sleep(6000);
        assertEquals("late result reached the caller: " + terminals, 1, terminals.size());
    }

    @Test
    public void errorResultIsAlsoDeliveredExactlyOnce() throws Exception {
        queueMethod(0, 500, false, terminals, firstTerminal);

        awaitSettled(15000);

        assertEquals("expected exactly one terminal callback, got " + terminals, 1, terminals.size());
        assertEquals("error:SDK_ERROR", terminals.get(0));
    }

    @Test
    public void queueKeepsDrainingAfterAWatchdogFires() throws Exception {
        // A late result used to advance the queue a second time, popping a method still running.
        // Queue a slow method then a fast one, and assert each completes exactly once.
        queueMethod(0, GRACE_MS + 3000, true, terminals, firstTerminal);

        List<String> secondTerminals = Collections.synchronizedList(new ArrayList<String>());
        CountDownLatch secondLatch = new CountDownLatch(1);
        queueMethod(0, 200, true, secondTerminals, secondLatch);

        assertTrue("second queued method never completed",
                   secondLatch.await(30000, TimeUnit.MILLISECONDS));
        Thread.sleep(6000);

        assertEquals("first method callbacks: " + terminals, 1, terminals.size());
        assertEquals("second method callbacks: " + secondTerminals, 1, secondTerminals.size());
        assertEquals("success:connected", secondTerminals.get(0));
    }
}
