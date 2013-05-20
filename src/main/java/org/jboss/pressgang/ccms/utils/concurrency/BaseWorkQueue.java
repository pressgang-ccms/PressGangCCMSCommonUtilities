package org.jboss.pressgang.ccms.utils.concurrency;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * See http://www.ibm.com/developerworks/library/j-jtp0730/index.html
 * <p/>
 * Will use the system property NumberOfWorkerThreads to determine the number of
 * worker threads to create. If the value is not set, or is not a valid integer,
 * the DEFAULT_NUM_THREADS property will be used.
 */
public abstract class BaseWorkQueue<T extends Runnable> {
    private static final Logger LOG = LoggerFactory.getLogger(BaseWorkQueue.class);

    /**
     * The default number of threads to create
     */
    private static final int DEFAULT_NUM_THREADS = 3;
    private final int numThreads;

    private final List<PoolWorker<T>> threads;
    protected final LinkedList<T> queue;
    private boolean disabled = false;

    @SuppressWarnings("unchecked")
    public LinkedList<T> getQueueCopy() {
        synchronized (queue) {
            return (LinkedList<T>) queue.clone();
        }
    }

    LinkedList<T> getQueue() {
        return queue;
    }

    public int getQueuedItemCount() {
        synchronized (queue) {
            return queue.size();
        }
    }

    public int getNumThreads() {
        return numThreads;
    }

    public int getRunningThreadsCount() {
        int retValue = 0;
        for (final PoolWorker<T> poolWorker : threads)
            if (poolWorker.isRunning()) ++retValue;
        return retValue;
    }

    public List<PoolWorker<T>> getRunningThreads() {
        List<PoolWorker<T>> retValue = new ArrayList<PoolWorker<T>>();
        for (final PoolWorker<T> poolWorker : threads)
            if (poolWorker.isRunning()) retValue.add(poolWorker);
        return retValue;
    }

    public BaseWorkQueue() {
        int threadCount = DEFAULT_NUM_THREADS;
        try {
            final String numThreadsString = System.getProperty("NumberOfWorkerThreads");
            if (numThreadsString != null) {
                threadCount = Integer.parseInt(System.getProperty("NumberOfWorkerThreads"));
            }
        } catch (final NumberFormatException ex) {
            LOG.debug("Unable to parse NumberOfWorkedThreads System Property as an Integer",ex);
        }

        numThreads = threadCount;
        queue = new LinkedList<T>();

        if (getNumThreads() <= 0) {
            disabled = true;
            threads = null;
        } else {
            threads = new ArrayList<PoolWorker<T>>();

            for (int i = 0; i < getNumThreads(); i++) {
                final PoolWorker<T> poolworker = new PoolWorker<T>(this);
                threads.add(poolworker);
                poolworker.start();
            }
        }
    }

    /**
     * Add a Runnable object to the pool to be executed once a thread is available
     */
    public void execute(final T runnable) {
        synchronized (queue) {
            if (runnable != null && !disabled) {
                queue.addLast(runnable);
                queue.notify();
                System.out.println("WorkQueue has added runable - WorkQueue size is " + queue.size());
            }
        }
    }

    /**
     * Provides the boilerplate code for running and waiting on a Runnable.
     */
    public static void runAndWait(final Runnable r) {
        try {
            if (r != null) {
                final Thread thread = new Thread(r);
                thread.start();
                thread.join();
            }
        } catch (final Exception ex) {
            LOG.debug("Thread has been interrupted and cannot be joined", ex);
        }
    }

    T getRunnableFromQueue() {
        return queue.removeFirst();
    }

    void runnableExecutionCompleted(final T runnable) {

    }
}
