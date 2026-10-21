abstract class library {
    private String resourceId;
    private String title;

    library(String id, String title) {
        this.resourceId = id;
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

        if (obj instanceof library) {
            library r = (library) obj;
            return resourceId.equals(r.resourceId);
        }

        return false;
    }

    public String toString() {
        return resourceId + " - " + title;
    }
}

class book extends library {
    private String author;
    private boolean issued;

    book(String id, String title, String author) {
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
    public void setAuthor(String author) {
        this.author = author;
    }

    public String toString() {
        return super.toString() + " - " + author;
    }

}

class magazine extends library {
    private int issueNumber;
    private boolean issued;

    magazine(String id, String title, int issueNumber) {
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

    public int getIssueNumber() {
        return issueNumber;
    }
    public void setIssueNumber(int issueNumber) {
        this.issueNumber = issueNumber;
    }

    public String toString() {
        return super.toString() + " - Issue No: " + issueNumber;
    }
}

class referenceBook extends library {
    private String subject;

    referenceBook(String id, String title, String subject) {
        super(id, title);
        this.subject = subject;
    }

    public boolean issue() {
        return false; 
    }

    public int lateFee(int days) {
        return 0; 
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String toString() {
        return super.toString() + " - Subject: " + subject;
    }
}


class dvd extends library {
    private boolean issued;

    dvd(String id, String title) {
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
        return super.toString();
    }
}

public class libraryTest {
    public static void main(String[] args) {
        book book1 = new book("101", "Java Programming", "Hardik");
        magazine magazine1 = new magazine("101", "Tech Monthly", 5);
        referenceBook referenceBook1 = new referenceBook("101", "Encyclopedia of Science", "Science");
        dvd dvd1 = new dvd("101", "Inception");

        System.out.println(book1);
        System.out.println(magazine1);
        System.out.println(referenceBook1);
        System.out.println(dvd1);

        System.out.println("Issuing book: " + book1.issue());
        System.out.println("Issuing magazine: " + magazine1.issue());
        System.out.println("Issuing reference book: " + referenceBook1.issue());
        System.out.println("Issuing DVD: " + dvd1.issue());

        System.out.println("Late fee for 3 days (book): " + book1.lateFee(3));
        System.out.println("Late fee for 3 days (magazine): " + magazine1.lateFee(3));
        System.out.println("Late fee for 3 days (reference book): " + referenceBook1.lateFee(3));
        System.out.println("Late fee for 3 days (DVD): " + dvd1.lateFee(3));
    }
}



