class Counter {
    int count = 0;

    void incrementUnsafe() {
        count++;
    }

    synchronized void incrementSafe() {
        count++;
    }
}

public class CounterRaceDemo {
    public static void main(String[] args) throws InterruptedException {
        int threadCount = 10;
        int incrementsPerThread = 10000;
        int expectedTotal = threadCount * incrementsPerThread;

        Counter unsafeCounter = new Counter();
        Thread[] unsafeThreads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            unsafeThreads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    unsafeCounter.incrementUnsafe();
                }
            });
            unsafeThreads[i].start();
        }

        for (Thread t : unsafeThreads) {
            t.join();
        }

        System.out.println("Expected count: " + expectedTotal);
        System.out.println("Unsynchronized total: " + unsafeCounter.count + " (race condition occurred)");

        Counter safeCounter = new Counter();
        Thread[] safeThreads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            safeThreads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    safeCounter.incrementSafe();
                }
            });
            safeThreads[i].start();
        }

        for (Thread t : safeThreads) {
            t.join();
        }

        System.out.println("Synchronized total:   " + safeCounter.count + " (exact match)");
    }
}
