package org.bxo.batchlib;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;


public final class BatchTask extends TimerTask {

    private static List<Batching> batchList = new ArrayList<>();

    public static void addBatch(Batching batch) {
        System.out.println("BatchTask new batch " + batch.getBatchName());
        batchList.add(batch);
    }

    /**
     * BatchTask run()
     * Whenever run is called, it processes all the batches which are either too big or have reached their time limits.
     * When Batching instances have multiple batches to be processed, it processes only one,
     * then adds the instance to the end of the queue for being processed again.
     **/
    public void run() {
        Queue<Batching> toProcess = new ConcurrentLinkedQueue<>();
        toProcess.addAll(batchList);
        System.out.println("BatchTask started");

        while (!toProcess.isEmpty()) {
            Batching b = toProcess.poll();
            if (null != b && b.processBatch()) {
                // if we processed a batch, add it to end of queue
                // for fairness, loop over all batches to be processed
                toProcess.add(b);
            }
        }
    }

}
