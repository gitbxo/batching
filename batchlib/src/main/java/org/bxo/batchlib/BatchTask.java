package org.bxo.batchlib;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


public final class BatchTask extends TimerTask {

    private static List<Batching> batchList = new ArrayList<>();
    private static ConcurrentHashMap<String, String> batchSet = new ConcurrentHashMap<>();

    public static void addBatch(Batching batch) {
        synchronized (batchList) {
            String batchName = batch.getBatchName();
            if (batchSet.containsKey(batchName)) {
                throw new RuntimeException(
                    "BatchTask " + batchName + " already exists!");
            }
            batchSet.putIfAbsent(batchName, batchName);
            batchList.add(batch);
            System.out.println("BatchTask new batch " + batchName);
        }
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

        int addCount = 0;
        while (!toProcess.isEmpty()) {
            Batching b = toProcess.poll();
            if (null != b && b.processBatch()) {
                // if we processed a batch, add it to end of queue
                // for fairness, loop over all batches to be processed
                toProcess.add(b);
                addCount++;
                if (addCount > batchList.size()) {
                    // Another fairness consideration
                    // if one type has 1,000 batches, make sure
                    // that other batch types are also processed
                    addCount = 0;
                    toProcess.addAll(batchList);
                }
            }
        }
    }

}
