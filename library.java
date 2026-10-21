abstract class MediaItem {
    protected String title;
    protected int daysLate;

    MediaItem(String title, int daysLate) {
        this.title = title;
        this.daysLate = daysLate;
    }

    abstract double calculateLateFee();

    public String getTitle() {
        return title;
    }

    public int getDaysLate() {
        return daysLate;
    }
}

class Book extends MediaItem {
    Book(String title, int daysLate) {
        super(title, daysLate);
    }

    @Override
    double calculateLateFee() {
        return daysLate > 0 ? daysLate * 0.50 : 0.0;
    }
}

class Dvd extends MediaItem {
    Dvd(String title, int daysLate) {
        super(title, daysLate);
    }

    @Override
    double calculateLateFee() {
        return daysLate > 0 ? daysLate * 1.50 : 0.0;
    }
}

class ReferenceBook extends MediaItem {
    private static final double FLAT_FEE = 10.0;

    ReferenceBook(String title, int daysLate) {
        super(title, daysLate);
    }

    @Override
    double calculateLateFee() {
        return daysLate > 0 ? FLAT_FEE : 0.0;
    }
}

 class library {
    public static void main(String[] args) {
        MediaItem[] returnedBatch = {
            new Book("Operating Systems Concepts", 4),
            new Dvd("Inception Movie", 2),
            new ReferenceBook("Oxford English Dictionary", 1),
            new Book("Clean Code", 0)
        };

        double batchTotalFees = 0.0;

        System.out.println("--- Overdue Media Returns ---");
        for (MediaItem item : returnedBatch) {
            double fee = item.calculateLateFee();
            batchTotalFees += fee;

            System.out.printf("Item: %-28s | Overdue: %d days | Late Fee: $%.2f\n", 
                item.getTitle(), item.getDaysLate(), fee);
        }

        System.out.printf("\nTotal Outstanding Late Fees: $%.2f\n", batchTotalFees);
    }
}
