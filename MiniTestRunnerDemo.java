import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Run {}

class MyTestSuite {
    @Run
    public void testLogin() {
        System.out.println("Executing: testLogin passed");
    }

    public void helperMethod() {
        System.out.println("This is a helper and should not run");
    }

    @Run
    public void testPayment() {
        System.out.println("Executing: testPayment passed");
    }

    @Run
    public void testLogout() {
        System.out.println("Executing: testLogout passed");
    }
}

public class MiniTestRunnerDemo {
    public static void main(String[] args) {
        Class testClass = MyTestSuite.class;
        int executedCount = 0;

        try {
            Object instance = testClass.getDeclaredConstructor().newInstance();

            for (Method method : testClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Run.class) && method.getParameterCount() == 0) {
                    method.setAccessible(true);
                    method.invoke(instance);
                    executedCount++;
                }
            }

            System.out.println("\nTotal @Run tests executed: " + executedCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}