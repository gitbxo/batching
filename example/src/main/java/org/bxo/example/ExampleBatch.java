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
        StringBatch batch1 = new StringBatch("batch1", 17, 1000);
        BatchTask.addBatch(batch1);
        // Here batch2 is the same processor as batch1, but it could
        // have been delete instead of insert, for example.
        StringBatch batch2 = new StringBatch("batch2", 11, 1000);
        BatchTask.addBatch(batch2);
        StringBatch batch3 = new StringBatch("batch3", 7, 1000);
        BatchTask.addBatch(batch3);
        StringBatch batch4 = new StringBatch("batch4", 23, 1000);
        BatchTask.addBatch(batch4);
        StringBatch batch5 = new StringBatch("batch5", 19, 1000);
        BatchTask.addBatch(batch5);

        // BatchTimer is required to start the background thread
        BatchTimer timer = new BatchTimer();
        timer.startTimer();


        Random rand = new Random();

        for (int i=0; i < 100; i++) {
            for (String s : args) {
                switch (i%5) {

                case 1:
                    batch1.addItem(s);
                    break;

                case 2:
                    batch2.addItem(s);
                    break;

                case 3:
                    batch3.addItem(s);
                    break;

                case 4:
                    batch4.addItem(s);
                    break;

                default:
                    batch5.addItem(s);
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
