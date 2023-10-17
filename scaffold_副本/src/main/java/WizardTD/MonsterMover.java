package WizardTD;

import java.util.ArrayList;

public class MonsterMover {
    private Monster monster;
    private ArrayList<Point> pathPoints;
    private int currentPathIndex;
    private int x, y;

    public MonsterMover(Monster monster, ArrayList<Point> pathPoints) {
        this.monster = monster;
        this.pathPoints = pathPoints;
        this.currentPathIndex = 0;
        Point startPoint = pathPoints.get(0);
        this.x = startPoint.getX();
        this.y = startPoint.getY();
    }

    public void move() {
        double speed = monster.getSpeed();
        if (currentPathIndex < pathPoints.size()) {
            Point targetPoint = pathPoints.get(currentPathIndex);
            // 更新怪物位置逻辑...
            if (Math.abs(x - targetPoint.getX()) < speed) {
                x = targetPoint.getX();
            } else {
                x += (targetPoint.getX() > x ? speed : -speed);
            }
            if (Math.abs(y - targetPoint.getY()) < speed) {
                y = targetPoint.getY();
            } else {
                y += (targetPoint.getY() > y ? speed : -speed);
            }

            // 如果怪物到达当前目标点，移动到路径的下一个点
            if (x == targetPoint.getX() && y == targetPoint.getY()) {
                currentPathIndex++;
            }
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Monster getMonster() {
        return monster;
    }
}
