public class point {

    private int x;
    private int y;

    public point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    


    @Override
    public String toString() {

        return "(" + x + "," + y + ")";
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof point) {
            point p = (point) obj;
        }
        else  {
            return false;
        }
        return true;
    }
}