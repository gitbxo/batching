package org.bxo.batchlib;

import java.util.Timer;


public class BatchTimer {

    private static long TIMER_DELAY_MILLIS = 1_000L;
    private static Timer timer;

    /**
     * This is synchronized to ensure that only one timer is created.
     * Calling this again will not create another timer.
     **/
    synchronized static
    public void startTimer() {
        // Schedule to run after every TIMER_DELAY_MILLIS
        if (null != timer) {
            return;
        }
        timer = new Timer(true);
        timer.scheduleAtFixedRate( new BatchTask(), TIMER_DELAY_MILLIS, TIMER_DELAY_MILLIS);
    }

    synchronized static
    public void stopTimer() {
	timer.cancel();
	timer = null;
    }

}
