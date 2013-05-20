package org.jboss.pressgang.ccms.utils.concurrency;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PoolWorker<T extends Runnable> extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(PoolWorker.class);

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final BaseWorkQueue<T> workQueue;
    private T runnable;

    public boolean isRunning() {
        return running.get();
    }

    public T getRunnable() {
        return runnable;
    }

    public PoolWorker(final BaseWorkQueue<T> workQueue) {
        this.workQueue = workQueue;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (workQueue.getQueue()) {
                while (workQueue.getQueue().isEmpty()) {
                    try {
                        workQueue.getQueue().wait();
                    } catch (final InterruptedException ignored) {

                    }
                }

                runnable = workQueue.getRunnableFromQueue();
            }

            // If we don't catch RuntimeException, the pool could leak threads
            try {
                running.set(true);
                getRunnable().run();
            } catch (final RuntimeException ex) {
                LOG.error("Unable to run Runnable", ex);
            } finally {
                runnable = null;
                running.set(false);
                workQueue.runnableExecutionCompleted(getRunnable());

                // If there pool is empty and there are no other working PoolWorkers, let the world know
                if (workQueue.getRunningThreadsCount() == 0 && workQueue.getQueuedItemCount() == 0)
                    System.out.println("Last PoolWorker Finished Work");
            }
        }
    }


}
