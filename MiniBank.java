import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Id {}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Positive {
    String message() default "must be > 0";
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface MaxLength {
    int value();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Audit {}

class BankException extends Exception {
    public BankException(String message) {
        super(message);
    }
}

class InsufficientFundsException extends BankException {
    private final long shortfall;

    public InsufficientFundsException(long shortfall) {
        super("Insufficient funds. Shortfall: " + shortfall);
        this.shortfall = shortfall;
    }

    public long getShortfall() {
        return shortfall;
    }
}

class AccountNotFoundException extends BankException {
    public AccountNotFoundException(String accountNumber) {
        super("Account not found: " + accountNumber);
    }
}

class InvalidAmountException extends BankException {
    public InvalidAmountException(String message) {
        super(message);
    }
}

record BankInfo(String name, String branch) implements Serializable {}

enum MenuOption {
    OPEN_ACCOUNT, DEPOSIT, WITHDRAW, TRANSFER, STATEMENT, LIST_ALL, BATCH_PROCESS, REPORT, EXIT
}

interface Transactable {
    void deposit(long amount) throws InvalidAmountException;
    boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException;
}

interface InterestBearing {
    double interestRate();
    default double yearlyInterest(long balance) {
        return (balance * interestRate()) / 100.0;
    }
}

abstract class Account implements Transactable, InterestBearing, Comparable<Account>, Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    protected final String accountNumber;
    protected String ownerName;

    @Positive
    protected long balance;
    protected boolean active;

    public Account(String accountNumber, String ownerName, long balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
        this.active = true;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public synchronized long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero.");
        }
        this.balance += amount;
    }

    @Override
    public synchronized boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be greater than zero.");
        }
        if (!canWithdraw(amount)) {
            long shortfall = amount - this.balance;
            throw new InsufficientFundsException(shortfall > 0 ? shortfall : 0);
        }
        this.balance -= amount;
        return true;
    }

    public abstract boolean canWithdraw(long amount);

    @Override
    public int compareTo(Account other) {
        return this.accountNumber.compareTo(other.accountNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account acc)) return false;
        return Objects.equals(accountNumber, acc.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public String toString() {
        return String.format("%s | %-12s | Rs.%-8d | Active: %b", accountNumber, ownerName, balance, active);
    }
}

class SavingsAccount extends Account {
    private static final long serialVersionUID = 1L;
    private final long minBalance;

    public SavingsAccount(String accountNumber, String ownerName, long balance, long minBalance) {
        super(accountNumber, ownerName, balance);
        this.minBalance = minBalance;
    }

    @Override
    public double interestRate() {
        return 4.0;
    }

    @Override
    public synchronized boolean canWithdraw(long amount) {
        return (balance - amount) >= minBalance;
    }

    @Override
    public String toString() {
        return super.toString() + " | Type: Savings (Min: " + minBalance + ")";
    }
}

class CurrentAccount extends Account {
    private static final long serialVersionUID = 1L;
    private final long overdraftLimit;

    public CurrentAccount(String accountNumber, String ownerName, long balance, long overdraftLimit) {
        super(accountNumber, ownerName, balance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public double interestRate() {
        return 0.0;
    }

    @Override
    public synchronized boolean canWithdraw(long amount) {
        return (balance - amount) >= -overdraftLimit;
    }

    @Override
    public String toString() {
        return super.toString() + " | Type: Current (OD: " + overdraftLimit + ")";
    }
}

class Validator {
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[6-9][0-9]{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public static boolean isValidMobile(String mobile) {
        return mobile != null && MOBILE_PATTERN.matcher(mobile).matches();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}

class Repository<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<String, T> data = new ConcurrentHashMap<>();

    public synchronized T save(T entity) {
        String id = extractId(entity);
        if (id != null) {
            data.put(id, entity);
        }
        return entity;
    }

    public T findById(String id) {
        return data.get(id);
    }

    public List<T> findAll() {
        return new ArrayList<>(data.values());
    }

    private String extractId(T entity) {
        Class<?> clazz = entity.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    try {
                        Object val = field.get(entity);
                        return val != null ? val.toString() : null;
                    } catch (IllegalAccessException e) {
                        return null;
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }
}

class TransactionLogger {
    private static final Path LOG_FILE = Paths.get("transactions.log");

    public static synchronized void append(String entry) {
        try {
            Files.writeString(LOG_FILE, entry + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Audit log failure: " + e.getMessage());
        }
    }
}

class BankService {
    private final Repository<Account> repository;
    private static int accountSeq = 1000;

    public BankService(Repository<Account> repository) {
        this.repository = repository;
    }

    @Audit
    public synchronized Account openAccount(String type, String ownerName, long initialDeposit) throws BankException {
        if (ownerName == null || ownerName.trim().isEmpty()) {
            throw new BankException("Owner name cannot be empty.");
        }
        if (initialDeposit <= 0) {
            throw new InvalidAmountException("Initial deposit must be greater than zero.");
        }

        String accNumber = "AC" + (++accountSeq);
        Account account;
        if (type.equalsIgnoreCase("CURRENT")) {
            account = new CurrentAccount(accNumber, ownerName.trim(), initialDeposit, 5000);
        } else {
            account = new SavingsAccount(accNumber, ownerName.trim(), initialDeposit, 1000);
        }

        repository.save(account);
        TransactionLogger.append("OPEN " + accNumber + " " + initialDeposit + " " + ownerName);
        return account;
    }

    @Audit
    public void deposit(String accNumber, long amount) throws BankException {
        Account account = getAccountOrThrow(accNumber);
        account.deposit(amount);
        TransactionLogger.append("DEPOSIT " + accNumber + " " + amount);
    }

    @Audit
    public void withdraw(String accNumber, long amount) throws BankException {
        Account account = getAccountOrThrow(accNumber);
        account.withdraw(amount);
        TransactionLogger.append("WITHDRAW " + accNumber + " " + amount);
    }

    @Audit
    public void transfer(String fromAccNumber, String toAccNumber, long amount) throws BankException {
        if (fromAccNumber.equalsIgnoreCase(toAccNumber)) {
            throw new BankException("Sender and receiver accounts must be different.");
        }
        Account from = getAccountOrThrow(fromAccNumber);
        Account to = getAccountOrThrow(toAccNumber);

        Account first = from.getAccountNumber().compareTo(to.getAccountNumber()) < 0 ? from : to;
        Account second = (first == from) ? to : from;

        synchronized (first) {
            synchronized (second) {
                from.withdraw(amount);
                to.deposit(amount);
                TransactionLogger.append("TRANSFER " + fromAccNumber + " " + toAccNumber + " " + amount);
            }
        }
    }

    public Account getAccountOrThrow(String accNumber) throws AccountNotFoundException {
        Account acc = repository.findById(accNumber);
        if (acc == null) {
            throw new AccountNotFoundException(accNumber);
        }
        return acc;
    }

    public List<Account> listAccountsSortedByBalance() {
        List<Account> accounts = repository.findAll();
        accounts.sort(Comparator.comparingLong(Account::getBalance).reversed());
        return accounts;
    }

    public String buildStatement(String accNumber) throws BankException {
        Account account = getAccountOrThrow(accNumber);
        StringBuilder sb = new StringBuilder();
        sb.append("\n====================================\n");
        sb.append("         ACCOUNT STATEMENT          \n");
        sb.append("====================================\n");
        sb.append("Account No   : ").append(account.getAccountNumber()).append("\n");
        sb.append("Holder Name  : ").append(account.getOwnerName()).append("\n");
        sb.append("Account Type : ").append(account.getClass().getSimpleName()).append("\n");
        sb.append("Current Bal  : Rs. ").append(account.getBalance()).append("\n");
        sb.append("Interest Rate: ").append(account.interestRate()).append("%\n");
        sb.append("Est. Int/Year: Rs. ").append(account.yearlyInterest(account.getBalance())).append("\n");
        sb.append("Active Status: ").append(account.isActive() ? "ENABLED" : "DISABLED").append("\n");
        sb.append("====================================\n");
        return sb.toString();
    }
}

class TransactionProcessor {
    public static void runSampleBatch(BankService service, String acc1, String acc2) {
        ExecutorService pool = Executors.newFixedThreadPool(4);
        System.out.println("Executing 8 concurrent transactions across thread pool...");

        for (int i = 0; i < 4; i++) {
            pool.submit(() -> {
                try {
                    service.deposit(acc1, 250);
                    System.out.println(Thread.currentThread().getName() + " deposited Rs.250 into " + acc1);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });
            pool.submit(() -> {
                try {
                    service.withdraw(acc2, 100);
                    System.out.println(Thread.currentThread().getName() + " withdrew Rs.100 from " + acc2);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });
        }

        pool.shutdown();
        try {
            pool.awaitTermination(5, TimeUnit.SECONDS);
            System.out.println("Batch operations completed successfully.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class MiniBank {
    private static final String DATA_FILE = "accounts.dat";

    public static void main(String[] args) {
        BankInfo header = new BankInfo("MiniBank Financial Services", "Main Branch, Tech Park");
        System.out.println("==================================================");
        System.out.println(" " + header.name() + " | " + header.branch());
        System.out.println("==================================================");

        Repository<Account> repository = loadState();
        BankService service = new BankService(repository);
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        logAuditedMethods(service);

        while (running) {
            System.out.println("\n----------------- MAIN MENU -----------------");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit Funds");
            System.out.println("3. Withdraw Funds");
            System.out.println("4. Transfer Funds");
            System.out.println("5. Print Statement");
            System.out.println("6. List All Accounts (Sorted by Balance)");
            System.out.println("7. Run Concurrent Batch Transactions");
            System.out.println("8. Generate End-of-Day Audit Report");
            System.out.println("9. Exit Application");
            System.out.print("Enter choice (1-9): ");

            int choice = -1;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid menu number.");
                continue;
            }

            MenuOption option = (choice >= 1 && choice <= MenuOption.values().length)
                    ? MenuOption.values()[choice - 1]
                    : null;

            if (option == null) {
                System.out.println("Invalid selection. Please choose an option from 1 to 9.");
                continue;
            }

            try {
                switch (option) {
                    case OPEN_ACCOUNT -> {
                        System.out.print("Account Type (SAVINGS / CURRENT): ");
                        String type = sc.nextLine().trim().toUpperCase();
                        System.out.print("Account Holder Name: ");
                        String name = sc.nextLine().trim();
                        System.out.print("Customer Mobile (10 digits): ");
                        String mobile = sc.nextLine().trim();

                        if (!Validator.isValidMobile(mobile)) {
                            System.out.println("Validation Error: Invalid mobile number format.");
                            break;
                        }

                        System.out.print("Initial Deposit Amount: ");
                        long deposit = Long.parseLong(sc.nextLine().trim());

                        Account created = service.openAccount(type, name, deposit);
                        System.out.println("Account created successfully: " + created.getAccountNumber());
                    }
                    case DEPOSIT -> {
                        System.out.print("Enter Account Number: ");
                        String acc = sc.nextLine().trim();
                        System.out.print("Enter Deposit Amount: ");
                        long amt = Long.parseLong(sc.nextLine().trim());
                        service.deposit(acc, amt);
                        System.out.println("Deposit successful. Updated balance: Rs. " + service.getAccountOrThrow(acc).getBalance());
                    }
                    case WITHDRAW -> {
                        System.out.print("Enter Account Number: ");
                        String acc = sc.nextLine().trim();
                        System.out.print("Enter Withdrawal Amount: ");
                        long amt = Long.parseLong(sc.nextLine().trim());
                        service.withdraw(acc, amt);
                        System.out.println("Withdrawal successful. Remaining balance: Rs. " + service.getAccountOrThrow(acc).getBalance());
                    }
                    case TRANSFER -> {
                        System.out.print("Source Account Number: ");
                        String from = sc.nextLine().trim();
                        System.out.print("Destination Account Number: ");
                        String to = sc.nextLine().trim();
                        System.out.print("Transfer Amount: ");
                        long amt = Long.parseLong(sc.nextLine().trim());
                        service.transfer(from, to, amt);
                        System.out.println("Transfer completed successfully.");
                    }
                    case STATEMENT -> {
                        System.out.print("Enter Account Number: ");
                        String acc = sc.nextLine().trim();
                        System.out.println(service.buildStatement(acc));
                    }
                    case LIST_ALL -> {
                        List<Account> all = service.listAccountsSortedByBalance();
                        if (all.isEmpty()) {
                            System.out.println("No accounts registered yet.");
                        } else {
                            System.out.println("\nRegistered Accounts (Ordered by Balance Descending):");
                            for (Account a : all) {
                                System.out.println(a);
                            }
                        }
                    }
                    case BATCH_PROCESS -> {
                        List<Account> all = service.listAccountsSortedByBalance();
                        if (all.size() < 2) {
                            System.out.println("Need at least 2 accounts created to run batch simulation.");
                        } else {
                            TransactionProcessor.runSampleBatch(service, all.get(0).getAccountNumber(), all.get(1).getAccountNumber());
                        }
                    }
                    case REPORT -> generateReport();
                    case EXIT -> {
                        saveState(repository);
                        running = false;
                        System.out.println("State persisted to " + DATA_FILE + ". Exiting MiniBank.");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Input Format Error: Numeric amount expected.");
            } catch (InsufficientFundsException e) {
                System.out.println("Transaction Failed: " + e.getMessage());
            } catch (BankException e) {
                System.out.println("Operation Error: " + e.getMessage());
            }
        }
        sc.close();
    }

    private static void logAuditedMethods(Object serviceObj) {
        System.out.print("Active audited endpoints: [ ");
        for (Method method : serviceObj.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Audit.class)) {
                System.out.print(method.getName() + " ");
            }
        }
        System.out.println("]");
    }

    private static void generateReport() {
        Path path = Paths.get("transactions.log");
        if (!Files.exists(path)) {
            System.out.println("No transaction activity recorded yet.");
            return;
        }

        long totalDeposits = 0;
        long totalWithdrawals = 0;
        int transactionCount = 0;

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length >= 3) {
                    transactionCount++;
                    if (parts[0].equalsIgnoreCase("DEPOSIT") || parts[0].equalsIgnoreCase("OPEN")) {
                        totalDeposits += Long.parseLong(parts[2]);
                    } else if (parts[0].equalsIgnoreCase("WITHDRAW")) {
                        totalWithdrawals += Long.parseLong(parts[2]);
                    } else if (parts[0].equalsIgnoreCase("TRANSFER")) {
                        long transferAmt = Long.parseLong(parts[3]);
                        totalDeposits += transferAmt;
                        totalWithdrawals += transferAmt;
                    }
                }
            }

            System.out.println("\n===== END OF DAY AUDIT REPORT =====");
            System.out.println("Total Transaction Entries : " + transactionCount);
            System.out.println("Total Inflow (Deposits)   : Rs. " + totalDeposits);
            System.out.println("Total Outflow (Withdraws) : Rs. " + totalWithdrawals);
            System.out.println("Net Daily Balance Change  : Rs. " + (totalDeposits - totalWithdrawals));
            System.out.println("===================================");

        } catch (Exception e) {
            System.out.println("Error reading audit log: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static Repository<Account> loadState() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                return (Repository<Account>) ois.readObject();
            } catch (Exception e) {
                System.out.println("Could not load persisted state; initializing fresh repository.");
            }
        }
        return new Repository<>();
    }

    private static void saveState(Repository<Account> repository) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(repository);
        } catch (IOException e) {
            System.err.println("Failed to persist accounts: " + e.getMessage());
        }
    }
}