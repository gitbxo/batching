package org.bxo.example;

import java.util.List;

import org.bxo.batchlib.Batching;


/**
 * Sample class that extends Batching to process some strings
 **/
public class StringBatch extends Batching<String> {

    public StringBatch(String name, int maxSize, long interval) {
        super(name, maxSize, interval);
    }

    @Override
    public void process(List<String> data) {
        for (String s : data) {
            System.out.println("Batch " + getBatchName() + " read >" + s + "<");
        }
    }
}
