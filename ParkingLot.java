public class ParkingLot {
    private int twoWheelers=0;
    private int fourWheelers=0;
    private final int twocup;
    private final int fourcup;
    private static long revenue=0;

    ParkingLot(int twocup,  int fourcup) {
        this.twocup = twocup;
        this.fourcup= fourcup;
    }
    public  void park(String type){
        if(type.equals("two")){
            if(twoWheelers<twocup){
                revenue+=20;
                System.out.println("Parked twowheelers" );
            }
            else {
                System.out.println("Full/Reject");
            }
        } else if (type.equals("four")) {

            if(fourWheelers<fourcup){
                revenue+=40;
                System.out.println("Parked fourwheelers" );
            }
            else {
                System.out.println("Full/Reject");
            }

        }

    }
    public void leave(String type){
if (type.equals("two")){
    if(twoWheelers>0){
    System.out.println("Two Wheelers gone");
    twoWheelers--;}
    else {
        System.out.println("Two Wheelers empty");
    }
}
else if (type.equals("four")){
    if(fourWheelers>0){
    System.out.println("Four Wheelers gone");
    fourWheelers--;
}
    else {
    System.out.println("Four Wheelers empty");}
}

    }

    long Revenue() {
        return revenue;
    }

    public static void main(String[] args) {
        ParkingLot p1 = new ParkingLot(50,100);
p1.park("two");
p1.park("four");
p1.park("two");
p1.park("four");
p1.park("two");
p1.park("four");
p1.park("two");

p1.park("four");
p1.park("two");
p1.leave("four");
 p1.leave("two");
 p1.leave("four");
p1.leave("two");
p1.leave("four");
p1.leave("two");
p1.leave("four");
p1.leave("two");
p1.leave("four");
p1.leave("two");
p1.leave("four");

System.out.println("Total Revenu is " + p1.Revenue());

    }
    }

