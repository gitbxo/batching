package org.bxo.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bxo.batchlib.BatchTask;
import org.bxo.batchlib.BatchTimer;

/**
 * ExampleBatch class
 *
 * This is a sample program that:
 *   1. creates a StringBatch
 *   2. adds args to the batch
 *   3. waits for printout
 **/
class ExampleBatch {

    public static void main(String[] args) throws InterruptedException {

        // These are the processors for (possibly different) batch types
        StringBatch batch1 = new StringBatch("batch1", 7, 1000);
        BatchTask.addBatch(batch1);
        // Here batch2 is the same processor as batch1, but it could
        // have been delete instead of insert, for example.
        StringBatch batch2 = new StringBatch("batch2", 11, 1000);
        BatchTask.addBatch(batch2);

        // BatchTimer is required to start the background thread
        BatchTimer timer = new BatchTimer();
        timer.startTimer();


        Random rand = new Random();

        for (int i=0; i < 100; i++) {
            for (String s : args) {
                if (i%2 == 1) {
                    batch1.addItem(s);
                } else {
                    batch2.addItem(s);
                }
            }
            Thread.sleep(100);
        }

	// This sleep is to wait till everything is written out
	Thread.sleep(30_000);
	// This stops the timer so that the program then exits
	timer.stopTimer();

    }
}
