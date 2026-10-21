class TicketBookingSystem {
    int seatsLeft = 5;
    int bookedCount = 0;

    void reset() {
        seatsLeft = 5;
        bookedCount = 0;
    }

    boolean bookUnsafe(String user) {
        if (seatsLeft > 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            seatsLeft--;
            bookedCount++;
            System.out.println(user + " booked! Remaining: " + seatsLeft);
            return true;
        }
        return false;
    }

    synchronized boolean bookSafe(String user) {
        if (seatsLeft > 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            seatsLeft--;
            bookedCount++;
            System.out.println(user + " booked! Remaining: " + seatsLeft);
            return true;
        }
        return false;
    }
}

public class SeatBookingDemo {
    public static void main(String[] args) throws InterruptedException {
        int totalUsers = 10;
        TicketBookingSystem system = new TicketBookingSystem();

        System.out.println("--- 1. UNSYNCHRONIZED RUN (Risk of Overselling) ---");
        Thread[] unsafeThreads = new Thread[totalUsers];
        for (int i = 0; i < totalUsers; i++) {
            final String user = "User-" + (i + 1);
            unsafeThreads[i] = new Thread(() -> system.bookUnsafe(user));
            unsafeThreads[i].start();
        }
        for (Thread t : unsafeThreads) {
            t.join();
        }
        System.out.println("Total booked: " + system.bookedCount + "/5 seats (Oversold if > 5)\n");

        system.reset();

        System.out.println("--- 2. SYNCHRONIZED RUN (Strict Limit) ---");
        Thread[] safeThreads = new Thread[totalUsers];
        for (int i = 0; i < totalUsers; i++) {
            final String user = "User-" + (i + 1);
            safeThreads[i] = new Thread(() -> system.bookSafe(user));
            safeThreads[i].start();
        }
        for (Thread t : safeThreads) {
            t.join();
        }
        System.out.println("Total booked: " + system.bookedCount + "/5 seats (Exact limit preserved)");
    }
}