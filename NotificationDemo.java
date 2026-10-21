@FunctionalInterface
interface Notifier {
    void send(String message);
}

interface Urgent {}

class UrgentNotifier implements Notifier, Urgent {
    private Notifier target;

    UrgentNotifier(Notifier target) {
        this.target = target;
    }

    @Override
    public void send(String message) {
        target.send(message);
    }
}

public class NotificationDemo {
    public static void main(String[] args) {
        Notifier emailNotifier = msg -> System.out.println("[EMAIL] " + msg);
        Notifier smsNotifier = msg -> System.out.println("[SMS] " + msg);
        Notifier urgentSmsNotifier = new UrgentNotifier(msg -> System.out.println("[URGENT SMS] " + msg));

        Notifier[] notifiers = { emailNotifier, smsNotifier, urgentSmsNotifier };
        String broadcastMessage = "Server restart at midnight";

        for (Notifier n : notifiers) {
            n.send(broadcastMessage);
            if (n instanceof Urgent) {
                n.send(broadcastMessage);
            }
        }
    }
}