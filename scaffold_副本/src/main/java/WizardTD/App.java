package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.MouseEvent;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;


public class App extends PApplet {

    public ArrayList<Grass> grass = new ArrayList<Grass>();
    public ArrayList<Shrub> shrubs = new ArrayList<Shrub>();
    public ArrayList<Path> paths = new ArrayList<Path>();
    public ArrayList<Wave> waves = new ArrayList<Wave>();
    public ArrayList<Monster> monsters = new ArrayList<Monster>();
    public List<Monster> activeMonsters = new ArrayList<>();
    private MonsterMover monsterMover;

    public WizardHouse wizardHouse;
    public char[][] layoutBoard = new char[20][20];

    public static final int CELLSIZE = 32;
    public static final int SIDEBAR = 120;
    public static final int TOPBAR = 40;
    public static final int BOARD_WIDTH = 20;
    public static int WIDTH = CELLSIZE * BOARD_WIDTH + SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH * CELLSIZE + TOPBAR;
    public static final int FPS = 60;

    public String configPath;

    public Random random = new Random();

    public int getCurrentWaveIndex() {
        return currentWaveIndex;
    }

    private int currentWaveIndex = -1;

    public App() {
        this.configPath = "config.json";
    }

    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    @Override
    public void setup() {
        background(96, 56, 17);
        frameRate(FPS);

        //draw the grass
        int x = 0, y = 40;
        while (x < 640) {
            y = 40;
            while (y < 680) {
                Grass g = new Grass(loadImage("src/main/resources/WizardTD/grass.png"), x, y);
                grass.add(g);
                y += 32;
            }
            x += 32;
        }

        // Read layout from config
        JSONObject jsonobject = null;
        try {
            JSONObject jsonObject = null;
            FileReader f = new FileReader(new File(this.configPath));
            jsonobject = new JSONObject(f);
            String layout = jsonobject.getString("layout");
            // Get the layout content
            File file = new File(layout);
            BufferedReader layoutReader = new BufferedReader(new FileReader(file));
            String line;
            int counter = 0;

            while ((line = layoutReader.readLine()) != null) {
                for (int i = 0; i < line.length(); i++) {
                    layoutBoard[counter][i] = line.charAt(i);
                }
                counter++;
            }
            layoutReader.close();
        } catch (FileNotFoundException e) {
            System.err.print("erro");
        } catch (IOException e) {
            System.err.print("erro2");
        }

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                char c = layoutBoard[i][j];
                if (c == 'S') {
                    shrubs.add(new Shrub(loadImage(("src/main/resources/WizardTD/shrub.png")), 32 * j, 40 + 32 * i));
                }
                if (c == 'W') {
                    wizardHouse = new WizardHouse(loadImage(("src/main/resources/WizardTD/wizard_house.png")), 32 * j, 40 + 32 * i);
                }
                if (c == 'X') {
                    PathInfo pathInfo = getPathInfo(i, j);
                    if (pathInfo != null) {
                        PImage pathImage = null;
                        double rotation = 0.0;

                        if (pathInfo.openDirections == 1) {
                            pathImage = loadImage("src/main/resources/WizardTD/path0.png");
                            if (pathInfo.up || pathInfo.down) {
                                rotation = 90.0;
                            }
                        } else if (pathInfo.openDirections == 2) {
                            if (pathInfo.up && pathInfo.down) {
                                pathImage = loadImage("src/main/resources/WizardTD/path0.png");
                                rotation = 90.0;
                            } else if (pathInfo.left && pathInfo.right) {
                                pathImage = loadImage("src/main/resources/WizardTD/path0.png");
                                rotation = 0.0;
                            } else {
                                pathImage = loadImage("src/main/resources/WizardTD/path1.png");
                                if (pathInfo.down && pathInfo.right) rotation = 270.0;
                                else if (pathInfo.up && pathInfo.right) rotation = 180.0;
                                else if (pathInfo.up && pathInfo.left) rotation = 90.0;
                                else if (pathInfo.down && pathInfo.left) rotation = 0.0;
                            }
                        } else if (pathInfo.openDirections == 3) {
                            pathImage = loadImage("src/main/resources/WizardTD/path2.png");
                            if (!pathInfo.down) rotation = 180.0;
                            else if (!pathInfo.right) rotation = 90.0;
                            else if (!pathInfo.left) rotation = 270.0;
                            else if (!pathInfo.up) rotation = 0.0;
                        } else if (pathInfo.openDirections == 4) {
                            pathImage = loadImage("src/main/resources/WizardTD/path3.png");
                        }

                        pathImage = rotateImageByDegrees(pathImage, rotation);
                        paths.add(new Path(pathImage, 32 * j, 40 + 32 * i));
                    }
                }
            }
        }
        JSONArray wavesArray = jsonobject.getJSONArray("waves");
        double preWavePause = 0;
        Random r = new Random();

        for (int i = 0; i < wavesArray.size(); i++) {
            JSONObject oneWave = wavesArray.getJSONObject(i);
            int duration = oneWave.getInt("duration");
            preWavePause += oneWave.getDouble("pre_wave_pause");
            ArrayList<Point> points = new ArrayList<Point>();
            for (int k = 0; k < 20; k++) {
                if (layoutBoard[0][k] == 'X') {
                    points.add(new Point(0, k));
                }
                if (layoutBoard[19][k] == 'X') {
                    points.add(new Point(19, k));
                }
                if (layoutBoard[k][0] == 'X') {
                    points.add(new Point(k, 0));
                }
                if (layoutBoard[k][19] == 'X') {
                    points.add(new Point(k, 19));
                }

            }
            int num = r.nextInt(points.size());
            Point start = points.get(num);
            int wizardX = wizardHouse.getX();
            int wizardY = wizardHouse.getY();
            Point end = new Point(wizardX, wizardY);
            BFSPathFinder pathFinder = new BFSPathFinder();
            ArrayList<Point> path = pathFinder.bfs(start, end, layoutBoard);



            JSONArray monsterArray = oneWave.getJSONArray("monsters");
            ArrayList<Monster> monstersForThisWave = new ArrayList<>();

            for (int j = 0; j < monsterArray.size(); j++) {
                monstersForThisWave.addAll(generateMonsters(monsterArray.getJSONObject(j)));
            }

            Wave wave = new Wave(duration, preWavePause, monstersForThisWave);
            waves.add(wave);
        }
        Wave nextWave = getNextWave();
        scheduleWave(nextWave);
    }
    public Wave getNextWave() {
        currentWaveIndex++; // Move to the next wave

        // If we've gone through all waves, you might want to end the game or restart the wave count.
        // This logic is up to you and how you want your game to progress.
        if (currentWaveIndex >= waves.size()) {
            // Option 1: End the game (or do whatever is appropriate in your game when waves are over)
            // endGame(); // You would need to implement this method (or similar) depending on your game's logic
            // return null;

            // Option 2: Restart waves from the beginning
            currentWaveIndex = 0; // Or set to -1 if you want a pause before restarting the waves
        }

        // Return the next wave
        return waves.get(currentWaveIndex);
    }
    public void scheduleWave(Wave wave) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                for (Monster monster : wave.getMonsters()) {
                    activeMonsters.add(monster);
                    // 可能还需要根据怪物的起始位置设置它们的初始位置
                }
            }
        };

        timer.schedule(task, (long) wave.getPreWavePause() * 1000);
    }



    private List<Monster> generateMonsters(JSONObject monsterDetails) { // changed parameter name for clarity
        List<Monster> monsters = new ArrayList<>();
        int quantity = monsterDetails.getInt("quantity");
        for (int i = 0; i < quantity; i++) {
            String type = monsterDetails.getString("type"); // corrected method to getString
            String imagePath = String.format("src/main/resources/WizardTD/%s.png", type);
            PImage img = loadImage(imagePath);
            int hp = monsterDetails.getInt("hp");
            double speed = monsterDetails.getDouble("speed"); // corrected method to getDouble
            double armour = monsterDetails.getDouble("armour");
            double manaGainedOnKill = monsterDetails.getDouble("mana_gained_on_kill");
            Monster m = new Monster(img, hp, speed, armour); // assuming you have a suitable constructor in your Monster class
            monsters.add(m);
        }
        return monsters;
    }


    private PathInfo getPathInfo(int i, int j) {
        if (layoutBoard[i][j] != 'X') {
            return null; // Not a path
        }

        boolean up = i > 0 && layoutBoard[i - 1][j] == 'X';
        boolean down = i < 19 && layoutBoard[i + 1][j] == 'X';
        boolean left = j > 0 && layoutBoard[i][j - 1] == 'X';
        boolean right = j < 19 && layoutBoard[i][j + 1] == 'X';

        int openDirections = 0;
        if (up) {
            openDirections++;
        }
        if (down) {
            openDirections++;
        }
        if (left) {
            openDirections++;
        }
        if (right) {
            openDirections++;
        }

        return new PathInfo(openDirections, up, down, left, right);
    }



    @Override
        public void keyPressed () {
        }

        @Override
        public void keyReleased () {
        }

        @Override
        public void mousePressed (MouseEvent e){
        }

        @Override
        public void mouseReleased (MouseEvent e){
        }

        @Override
        public void draw () {
            for (Grass g : grass) {
                g.draw(this);
            }
            for (Shrub s : shrubs) {
                s.draw(this);
            }
            for (Path p : paths) {
                p.draw(this);
            }
            wizardHouse.draw(this);
            for(Monster m:monsters){
                m.draw(this);
            }
        }




        public static void main (String[]args){
            PApplet.main("WizardTD.App");
        }

        public PImage rotateImageByDegrees (PImage pimg,double angle){
            BufferedImage img = (BufferedImage) pimg.getNative();
            double rads = Math.toRadians(angle);
            double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
            int w = img.getWidth();
            int h = img.getHeight();
            int newWidth = (int) Math.floor(w * cos + h * sin);
            int newHeight = (int) Math.floor(h * cos + w * sin);

            PImage result = this.createImage(newWidth, newHeight, ARGB);
            BufferedImage rotated = (BufferedImage) result.getNative();
            Graphics2D g2d = rotated.createGraphics();
            AffineTransform at = new AffineTransform();
            at.translate((newWidth - w) / 2, (newHeight - h) / 2);

            int x = w / 2;
            int y = h / 2;

            at.rotate(rads, x, y);
            g2d.setTransform(at);
            g2d.drawImage(img, 0, 0, null);
            g2d.dispose();
            for (int i = 0; i < newWidth; i++) {
                for (int j = 0; j < newHeight; j++) {
                    result.set(i, j, rotated.getRGB(i, j));
                }
            }

            return result;
        }
    }




