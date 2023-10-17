package WizardTD;
import java.awt.Point;
import java.util.Objects;

public class Node {
    Point point;
    Node prev;

    public Node(Point point, Node prev) {
        this.point = point;
        this.prev = prev;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(point, node.point);
    }

    @Override
    public int hashCode() {
        return Objects.hash(point);
    }
}
