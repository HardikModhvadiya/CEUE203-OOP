abstract class Employee {
    protected String id;
    protected String name;

    Employee(String id, String name) {
        this.id = id;
        this.name = name;
    }

    abstract double monthlySalary();

    public void printDetails() {
        System.out.printf("[%s] %-12s | Salary: $%.2f", id, name, monthlySalary());
    }
}

class FullTime extends Employee {
    private double fixedSalary;

    FullTime(String id, String name, double fixedSalary) {
        super(id, name);
        this.fixedSalary = fixedSalary;
    }

    @Override
    double monthlySalary() {
        return fixedSalary;
    }
}

class PartTime extends Employee {
    private int hoursWorked;
    private double hourlyRate;

    PartTime(String id, String name, int hoursWorked, double hourlyRate) {
        super(id, name);
        this.hoursWorked = hoursWorked;
        this.hourlyRate = hourlyRate;
    }

    @Override
    double monthlySalary() {
        return hoursWorked * hourlyRate;
    }
}

class Intern extends Employee {
    private double stipend;

    Intern(String id, String name, double stipend) {
        super(id, name);
        this.stipend = stipend;
    }

    @Override
    double monthlySalary() {
        return stipend;
    }
}

 class payroll{
    public static void main(String[] args) {
        Employee[] employees = {
            new FullTime("E101", "Hardik", 5000.0),
            new PartTime("E102", "Ram", 80, 25.0),
            new Intern("E103", "Prit", 1200.0),
            new FullTime("E104", "yash", 6200.0)
        };

        double totalPayroll = 0.0;

        System.out.println("--- Monthly Payroll Report ---");
        for (Employee emp : employees) {
            emp.printDetails();

            if (emp instanceof Intern) {
                System.out.print("  <-- [Note: Intern Stipend - Fixed Term]");
            }
            System.out.println();

            totalPayroll += emp.monthlySalary();
        }

        System.out.printf("\nTotal Monthly Payroll Expense: $%.2f\n", totalPayroll);
    }

    
}
