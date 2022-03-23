package org.bxo.batchlib;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Batching type T
 * Creates a batching of type T
 * Add to the batchingQ
 * Read from the batchingQ
 * Do the batch operation, which takes a list of type T
 * When adding to queue, create a thread to do batch operation after time T
 * If there are more than N items, process in batches of size N
 * Create thread to do batch operations after time T
 * Max items per operation is N
 **/
public abstract class Batching<T> {

    private static int MIN_BATCH_SIZE = 5;
    private static int MAX_BATCH_SIZE = 10_000;
    private static long MIN_DELAY_MILLIS = 10_000L;
    private static long MAX_DELAY_MILLIS = 600_000L;

    private final String batchName;
    private final AtomicLong delayMillis;
    private final AtomicLong nextTimeMillis;
    private final AtomicInteger maxBatchSize;
    private final Queue<T> queue = new ConcurrentLinkedQueue<>();
    private final Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            List<T> batch = new ArrayList<>();
            for (int i=0; i < maxBatchSize.get(); i++) {
                T item = queue.poll();
                if (null == item) {
                    break;
                }
                batch.add(item);
            }

            if (batch.size() > 0) {
                System.out.println("Batch " + getBatchName() + " processing " + batch.size() + " items.");
                process(batch);
                System.out.println("Batch " + getBatchName() + " processed " + batch.size() + " items.");
            }
        }
    };
    private Thread myThread = null;

    public Batching(String batchName, int maxBatchSize, long delayMillis) {
        this.batchName = batchName;
        this.delayMillis = new AtomicLong(
            delayMillis < MIN_DELAY_MILLIS
            ? MIN_DELAY_MILLIS
            : (delayMillis < MAX_DELAY_MILLIS ? delayMillis : MAX_DELAY_MILLIS));
        this.nextTimeMillis = new AtomicLong(System.currentTimeMillis());
        this.maxBatchSize = new AtomicInteger(
            maxBatchSize < MIN_BATCH_SIZE
            ? MIN_BATCH_SIZE
            : (maxBatchSize < MAX_BATCH_SIZE ? maxBatchSize : MAX_BATCH_SIZE));
    }

    public String getBatchName() {
        return batchName;
    }

    public void addItem(T item) {
        queue.add(item);
    }

    /**
     * processBatch returns true if something was processed, false otherwise
     * Force calls to be synchronized to prevent multiple callers to the same interface.
     */
    synchronized public
    boolean processBatch() {
        if (queue.isEmpty() || (nextTimeMillis.get() < System.currentTimeMillis() && queue.size() < maxBatchSize.get())) {
            return false;
        }
        // Set nextTimeMillis to current time + delay so that we wait before next run
        // Note: if there are too many items in the queue, it will ignore nextTimeMillis
        nextTimeMillis.set(System.currentTimeMillis() + delayMillis.get());

        if (myThread != null && myThread.isAlive()) {
            // return false if processing is taking a long time
            // this is to prevent blocking other batches
            // We also don't want two threads for the same batch type
            return false;
        }

        // Start asynchronous thread
        myThread = new Thread(myRunnable);
        myThread.start();
        return true;
    }

    abstract public void process(List<T> data);

}
