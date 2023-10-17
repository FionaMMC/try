package WizardTD;

public class Point {
    private int x;
    private int y;

    // Assuming you have a constructor like this
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Getter for the x coordinate
    public int getX() {
        return this.x;
    }

    // Getter for the y coordinate
    public int getY() {
        return this.y;
    }

    // Depending on your needs, you might also want to override equals and hashCode methods

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Point point = (Point) obj;
        return x == point.x && y == point.y;
    }

}

