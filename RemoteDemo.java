interface Switchable {
    void on();
    void off();

    default void toggle() {
        if (isOn()) {
            off();
        } else {
            on();
        }
    }

    boolean isOn();
}

class Fan implements Switchable {
    private boolean state = false;

    public void on() {
        state = true;
        System.out.println("Fan turned ON");
    }

    public void off() {
        state = false;
        System.out.println("Fan turned OFF");
    }

    public boolean isOn() {
        return state;
    }
}

class Light implements Switchable {
    private boolean state = false;

    public void on() {
        state = true;
        System.out.println("Light turned ON");
    }

    public void off() {
        state = false;
        System.out.println("Light turned OFF");
    }

    public boolean isOn() {
        return state;
    }
}

@FunctionalInterface
interface PowerPolicy {
    boolean canSwitchOn(Switchable device, int hour);
}

public class RemoteDemo {
    public static void main(String[] args) {
        Switchable[] devices = { new Fan(), new Light() };

        for (Switchable s : devices) {
            s.toggle();
        }

        PowerPolicy anonPolicy = new PowerPolicy() {
            @Override
            public boolean canSwitchOn(Switchable device, int hour) {
                return hour >= 6 && hour <= 22;
            }
        };

        PowerPolicy lambdaPolicy = (device, hour) -> hour >= 18 && hour <= 23;

        int currentHour = 20;

        System.out.println("Anon Policy at hour 20: " + anonPolicy.canSwitchOn(devices[0], currentHour));
        System.out.println("Lambda Policy at hour 20: " + lambdaPolicy.canSwitchOn(devices[1], currentHour));
    }
}