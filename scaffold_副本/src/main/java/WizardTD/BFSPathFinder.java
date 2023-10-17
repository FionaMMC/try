package WizardTD;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class BFSPathFinder {

    // Directions defined for up, down, left, right
    private static final int[][] DIRECTIONS = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

    private static class Node {
        Point point;
        Node parent;

        public Node(Point point, Node parent) {
            this.point = point;
            this.parent = parent;
        }

        // Equals and hashCode based on point's coordinates
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Node node = (Node) obj;
            return point.equals(node.point);
        }

        @Override
        public int hashCode() {
            return point.hashCode();
        }
    }

    public ArrayList<Point> bfs(Point start, Point end, char[][] layoutBoard) {
        Node startNode = new Node(start, null);
        Queue<Node> queue = new LinkedList<>();
        ArrayList<Node> explored = new ArrayList<>();
        queue.add(startNode);

        while (!queue.isEmpty()) {
            Node current = queue.remove();
            if (current.point.equals(end)) { // If we reached the destination
                return buildPath(current); // Build and return the path
            }

            // Explore neighbors
            for (int[] direction : DIRECTIONS) {
                Point newPoint = new Point(current.point.getX() + direction[0], current.point.getY() + direction[1]); // assuming Point has getX() and getY() methods
                if (isValid(newPoint, layoutBoard) && !isExplored(newPoint, explored)) {
                    Node newNode = new Node(newPoint, current);
                    if (!queue.contains(newNode)) {
                        queue.add(newNode);
                    }
                }
            }
            explored.add(current);
        }

        return new ArrayList<>(); // return an empty list if no path is found
    }

    private ArrayList<Point> buildPath(Node node) {
        ArrayList<Point> path = new ArrayList<>();
        while (node != null) {
            path.add(0, node.point); // add to the beginning of the list
            node = node.parent;
        }
        return path;
    }

    private boolean isValid(Point point, char[][] layoutBoard) {
        int x = point.getX();
        int y = point.getY();
        return x >= 0 && x < layoutBoard.length && y >= 0 && y < layoutBoard[0].length && layoutBoard[x][y] == 'X';
    }

    private boolean isExplored(Point point, ArrayList<Node> explored) {
        return explored.stream().anyMatch(node -> node.point.equals(point));
    }

}



