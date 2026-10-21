abstract class LibraryResource {
    private String resourceId;
    private String title;

    LibraryResource(String resourceId, String title) {
        this.resourceId = resourceId;
        this.title = title;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    abstract boolean issue();

    abstract int lateFee(int days);

    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj instanceof LibraryResource) {
            LibraryResource r = (LibraryResource) obj;
            return resourceId.equals(r.resourceId);
        }

        return false;
    }

    public String toString() {
        return resourceId + " - " + title;
    }
}


class Book extends LibraryResource {
    private String author;
    private boolean issued;

    Book(String id, String title, String author) {
        super(id, title);
        this.author = author;
        issued = false;
    }

    public boolean issue() {
        if (!issued) {
            issued = true;
            return true;
        }
        return false;
    }

    public int lateFee(int days) {
        return days * 5;
    }

    public String getAuthor() {
        return author;
    }

    public String toString() {
        return "Book: " + super.toString() + ", Author: " + author;
    }
}


class Magazine extends LibraryResource {
    private int issueNumber;
    private boolean issued;

    Magazine(String id, String title, int issueNumber) {
        super(id, title);
        this.issueNumber = issueNumber;
        issued = false;
    }

    public boolean issue() {
        if (!issued) {
            issued = true;
            return true;
        }
        return false;
    }

    public int lateFee(int days) {
        return days * 3;
    }

    public String toString() {
        return "Magazine: " + super.toString()
                + ", Issue No: " + issueNumber;
    }
}


class ReferenceBook extends LibraryResource {

    ReferenceBook(String id, String title) {
        super(id, title);
    }

    public boolean issue() {
        return false;
    }

    public int lateFee(int days) {
        return 0;
    }

    public String toString() {
        return "Reference Book: " + super.toString();
    }
}


class DVD extends LibraryResource {
    private boolean issued;

    DVD(String id, String title) {
        super(id, title);
        issued = false;
    }

    public boolean issue() {
        if (!issued) {
            issued = true;
            return true;
        }
        return false;
    }

    public int lateFee(int days) {
        return days * 10;
    }

    public String toString() {
        return "DVD: " + super.toString();
    }
}


abstract class SmartLibrary {
    public static void main(String[] args) {

        LibraryResource[] resources = {
            new Book("101", "Java", "HARDIK"),
            new Magazine("101", "Tech Today", 25),
            new ReferenceBook("101", "Java Reference"),
            new DVD("101", "Java Tutorial DVD")
        };

       
        System.out.println("Library Resources:");

        for (LibraryResource r : resources) {
            System.out.println(r);
        }

        System.out.println("\nBook issue: "
                + resources[0].issue());

        System.out.println("Reference book issue: "
                + resources[2].issue());

    
        System.out.println("\nLate Fee:");
        System.out.println("Book for 4 days: Rs. "
                + resources[0].lateFee(4));

        System.out.println("Magazine for 4 days: Rs. "
                + resources[1].lateFee(4));

        System.out.println("Reference Book for 4 days: Rs. "
                + resources[2].lateFee(4));

        System.out.println("DVD for 4 days: Rs. "
                + resources[3].lateFee(4));

        System.out.println("\nUsing instanceof:");

        for (LibraryResource r : resources) {
            if (r instanceof Book) {
                Book b = (Book) r;
                System.out.println("Book author: " + b.getAuthor());
            }
        }

        Book b1 = new Book("200", "C Programming", "RAM");
        Book b2 = new Book("200", "Another Book", "JAYESH");

        System.out.println("\nSame resource ID: "
                + b1.equals(b2));
    }
}