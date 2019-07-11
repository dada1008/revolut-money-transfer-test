package com.dada.revolut.services;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ConcurrencyTestUtil {

    private final ExecutorService executor;
    private final int threadCount;


    ConcurrencyTestUtil(int threadCount) {
        this.threadCount = threadCount;
        this.executor = Executors.newCachedThreadPool();
    }

    void concurrentRun(final Runnable action) throws InterruptedException {
        spawnThreads(action).await();
    }

    private CountDownLatch spawnThreads(final Runnable action) {
        final CountDownLatch finished = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                try {
                    action.run();
                } finally {
                    finished.countDown();
                }
            });
        }
        return finished;
    }


    void shutdown() {
        executor.shutdown();
    }
}