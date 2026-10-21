import java.util.LinkedList;
import java.util.Queue;

class SharedBuffer {
    private final Queue<Integer> queue = new LinkedList<>();
    private final int capacity;

    SharedBuffer(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void produce(int value) throws InterruptedException {
        while (queue.size() == capacity) {
            wait();
        }

        queue.add(value);
        System.out.println("Produced: " + value + " | Buffer size: " + queue.size());

        notifyAll();
    }

    public synchronized int consume() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }

        int value = queue.poll();
        System.out.println("Consumed: " + value + " | Buffer size: " + queue.size());

        notifyAll();
        return value;
    }
}

public class ProducerConsumerDemo {
    public static void main(String[] args) throws InterruptedException {
        int bufferCapacity = 3;
        int totalItems = 10;
        SharedBuffer buffer = new SharedBuffer(bufferCapacity);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= totalItems; i++) {
                    buffer.produce(i);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= totalItems; i++) {
                    buffer.consume();
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        System.out.println("\nAll " + totalItems + " items produced and consumed in order with none lost.");
    }
}