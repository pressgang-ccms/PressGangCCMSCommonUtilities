/*
  Copyright 2011-2014 Red Hat, Inc

  This file is part of PressGang CCMS.

  PressGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PressGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PressGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

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
