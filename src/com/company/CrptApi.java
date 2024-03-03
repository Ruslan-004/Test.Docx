package com.company;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class CrptApi {
    private final Lock lock;
    private final long timeWindowMillis;
    private int requestLimit;
    private int requestCount;
    private long startTimeMillis;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.lock = new ReentrantLock();
        this.timeWindowMillis = timeUnit.toMillis(1);
        this.requestLimit = requestLimit;
        this.requestCount = 0;
        this.startTimeMillis = System.currentTimeMillis();
    }

    public void createDocument(Object document, String signature) {
        lock.lock();
        try {
            waitIfNeeded();

            // Perform the API request here...
            System.out.println("Creating document...");

            incrementRequestCount();
        } finally {
            lock.unlock();
        }
    }

    private void waitIfNeeded() {
        long currentTimeMillis = System.currentTimeMillis();
        long elapsedTimeMillis = currentTimeMillis - startTimeMillis;

        if (elapsedTimeMillis >= timeWindowMillis) {
            resetRequestCount();
            startTimeMillis = currentTimeMillis;
        } else if (requestCount >= requestLimit) {
            long remainingTimeMillis = timeWindowMillis - elapsedTimeMillis;
            try {
                Thread.sleep(remainingTimeMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void incrementRequestCount() {
        requestCount++;
    }

    private void resetRequestCount() {
        requestCount = 0;
    }

}
