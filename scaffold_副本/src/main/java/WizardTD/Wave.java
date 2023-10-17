package WizardTD;

import java.util.ArrayList;

public class Wave {
    private int duration;
    private double preWavePause;
    private ArrayList<Monster> monsters;

    public Wave(int duration, double preWavePause, ArrayList<Monster> monsters){
        this.duration = duration;
        this.preWavePause = preWavePause;
        this.monsters = monsters;
    }

    public ArrayList<Monster> getMonsters() {
        return monsters;
    }

    public double getPreWavePause() {
        return preWavePause;
    }

    public int getDuration() {
        return duration;
    }

}
