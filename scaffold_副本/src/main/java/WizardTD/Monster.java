package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;

public class Monster {
    private PImage type;
    private double hp;
    private double speed;
    private double armour;
    private int x,y;

    public Monster(PImage type, double hp, double speed, double armour) {
        this.type = type;
        this.hp = hp;
        this.speed = speed;
        this.armour = armour;
        this.x = x;
        this.y = y;
    }


    public boolean isDead() {
        return hp <= 0;
    }

    // getter 和 setter 方法
    public double getHp() {
        return hp;
    }

    public void setHp(double hp) {
        this.hp = hp;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getArmour() {
        return armour;
    }

    // 根据伤害值，减少怪物的生命值
    public void takeDamage(double damage) {
        hp -= damage * (1 - armour);
    }
    public void draw(PApplet applet) {
        applet.image(this.type, this.x, this.y);
    }
}




