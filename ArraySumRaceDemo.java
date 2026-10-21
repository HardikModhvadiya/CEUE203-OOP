import java.util.Arrays;

public class ArraySumRaceDemo {
    static final int SIZE = 10_000_000;
    static final int THREADS = 4;
    static final int[] numbers = new int[SIZE];

    static long wrongTotal = 0;
    static long syncTotal = 0;

    static synchronized void addToSyncTotal(int val) {
        syncTotal += val;
    }

    public static void main(String[] args) throws InterruptedException {
        Arrays.fill(numbers, 1);
        long expectedTotal = (long) SIZE * 1;
        int chunkSize = SIZE / THREADS;

        // 1. Unsynchronized Race Condition
        wrongTotal = 0;
        Thread[] unsafeThreads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            final int start = i * chunkSize;
            final int end = (i == THREADS - 1) ? SIZE : start + chunkSize;
            unsafeThreads[i] = new Thread(() -> {
                for (int j = start; j < end; j++) {
                    wrongTotal += numbers[j];
                }
            });
            unsafeThreads[i].start();
        }
        for (Thread t : unsafeThreads) t.join();

        // 2. Fix 1: Synchronized Method on Every Addition
        syncTotal = 0;
        long startSync = System.currentTimeMillis();
        Thread[] syncThreads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            final int start = i * chunkSize;
            final int end = (i == THREADS - 1) ? SIZE : start + chunkSize;
            syncThreads[i] = new Thread(() -> {
                for (int j = start; j < end; j++) {
                    addToSyncTotal(numbers[j]);
                }
            });
            syncThreads[i].start();
        }
        for (Thread t : syncThreads) t.join();
        long endSync = System.currentTimeMillis();

        // 3. Fix 2: Local Thread Accumulation (Map-Reduce)
        long startLocal = System.currentTimeMillis();
        long[] localSums = new long[THREADS];
        Thread[] localThreads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            final int threadIdx = i;
            final int start = i * chunkSize;
            final int end = (i == THREADS - 1) ? SIZE : start + chunkSize;
            localThreads[i] = new Thread(() -> {
                long tempSum = 0;
                for (int j = start; j < end; j++) {
                    tempSum += numbers[j];
                }
                localSums[threadIdx] = tempSum;
            });
            localThreads[i].start();
        }
        for (Thread t : localThreads) t.join();

        long localTotal = 0;
        for (long s : localSums) {
            localTotal += s;
        }
        long endLocal = System.currentTimeMillis();

        System.out.println("Expected Sum:            " + expectedTotal);
        System.out.println("Unsynchronized Sum:      " + wrongTotal + " [Incorrect]");
        System.out.println("Fix 1 (Synchronized):    " + syncTotal + " in " + (endSync - startSync) + " ms");
        System.out.println("Fix 2 (Local Partial):   " + localTotal + " in " + (endLocal - startLocal) + " ms");
    }
}