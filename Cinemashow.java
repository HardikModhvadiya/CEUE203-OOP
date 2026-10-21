public  class Cinemashow {
    private  String title;
    private  int seatsavaliable;
    private  final int capacity;
    private static int totalbooked=0;
    Cinemashow(String title,  int capacity) {

        this.title = title;
        this.capacity = capacity;
        this.seatsavaliable = capacity;
    }
    Cinemashow(String title) {
        this(title,100);
    }
    public boolean book(int n) {
        if (n < 0) {
            System.out.println("Please enter a valid number of tickets");
            return false;
        }
        if (n == 0) {
            System.out.println("please enter a number of tickets");
            return false;
        } else if (n <= seatsavaliable) {
            seatsavaliable -= n;
            totalbooked += n;
            return true;

        } else {
            return false;
        }
    }
    public void cancel (int n){

        if (n<0){
            System.out.println("Please enter a valid number of cancel tickets");
        }
        else {
            seatsavaliable+=n;
            if (seatsavaliable>capacity){
                seatsavaliable = capacity;
            }
        }


    }
    public int getSeatsAvailable(){
        return seatsavaliable;
    }
    public static  int getTotalBooked(){
        return totalbooked;
    }

    public static void main(String[] args) {

        Cinemashow show = new Cinemashow("Inception", 50);

        System.out.println("Initial seats available: " + show.getSeatsAvailable());


        boolean result1 = show.book(30);
        System.out.println("Booking 30 seats: " + result1 + " | Seats remaining: " + show.getSeatsAvailable());


        boolean result2 = show.book(25);
        System.out.println("Booking 25 seats : " + result2 +  "| Seats remaining: " + show.getSeatsAvailable());


        show.cancel(10);
        System.out.println("Canceled seats | Seats remaining: " + show.getSeatsAvailable());


        boolean result3 = show.book(15);
        System.out.println("Booking 15 seats: " + result3 + " | Seats remaining: " + show.getSeatsAvailable());


        System.out.println("\nTotal seats booked across all shows: " + Cinemashow.getTotalBooked());

    }
}
