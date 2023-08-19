package com.github.jonasrutishauser.cdi.test.concurrent;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;

import jakarta.enterprise.concurrent.LastExecution;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.concurrent.ManagedScheduledExecutorService;
import jakarta.enterprise.concurrent.ManagedThreadFactory;
import jakarta.enterprise.concurrent.Trigger;
import jakarta.inject.Inject;

@ExtendWith(CdiTestJunitExtension.class)
class ExecutorsTest {
    @Inject
    private ManagedExecutorService executorService;

    @Inject
    private ManagedScheduledExecutorService scheduledExecutorService;

    @Inject
    private ManagedThreadFactory threadFactory;

    @Test
    void inject() {
        assertNotNull(executorService);
        assertNotNull(scheduledExecutorService);
        assertNotNull(threadFactory);
    }

    @Test
    void executorService() throws InterruptedException, ExecutionException, TimeoutException {
        testExecutorService(executorService);
    }

    private void testExecutorService(ExecutorService executor)
            throws InterruptedException, ExecutionException, TimeoutException {
        Future<Long> future = executor.submit(() -> Thread.currentThread().getId());
        assertNotNull(future);
        assertNotEquals(Thread.currentThread().getId(), future.get(1, SECONDS));
        executor.execute(() -> {
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    void scheduledExecutorService() throws InterruptedException, ExecutionException, TimeoutException {
        testExecutorService(scheduledExecutorService);
        AtomicInteger counter = new AtomicInteger();
        ScheduledFuture<Integer> future = scheduledExecutorService.schedule(() -> counter.incrementAndGet(),
                new Trigger() {
                    @Override
                    public boolean skipRun(LastExecution lastExecutionInfo, Date scheduledRunTime) {
                        return counter.incrementAndGet() % 3 == 0;
                    }

                    @Override
                    public Date getNextRunTime(LastExecution lastExecutionInfo, Date taskScheduledTime) {
                        if (lastExecutionInfo == null) {
                            return taskScheduledTime;
                        }
                        return lastExecutionInfo.getRunEnd();
                    }
                });
        await().atMost(Duration.ofSeconds(1)).until(() -> counter.get() > 10);
        future.cancel(true);
        assertTrue(future.isCancelled());
    }

    @AfterAll
    static void allThreadsTerminated() {
        await("all threads terminted").atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> Thread.getAllStackTraces().keySet()
                        .forEach(thread -> assertFalse(thread.getName().startsWith("default-"),
                                "managed thread " + thread.getName() + " is still running")));
    }
}
