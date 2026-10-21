class DatabaseConnection implements AutoCloseable {
    private String name;

    DatabaseConnection(String name) {
        this.name = name;
        System.out.println("Connected to: " + name);
    }

    public void runQuery() throws Exception {
        System.out.println("Executing database query...");
        throw new RuntimeException("Database query failed: Connection Timeout!");
    }

    @Override
    public void close() throws Exception {
        System.out.println("Closing connection to: " + name);
    }
}

public class AutoCloseableDemo {
    public static void main(String[] args) {
        try (DatabaseConnection db = new DatabaseConnection("ProductionDB")) {
            db.runQuery();
        } catch (Exception e) {
            System.out.println("\nCaught Exception: " + e.getMessage());
            System.out.println("Original exception reported: " + e.getClass().getSimpleName());
        }
    }
}