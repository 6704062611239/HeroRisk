package com.mycompany.herorisk;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;
import java.awt.image.BufferedImage;

public class FireMinigame extends Minigame {

    private Enemy[] enemies;
    private int currentWave = 0;

    private int heroLane = 0;

    private ArrayList<GameObject> objects = new ArrayList<>();
    private double objectSpeed = 6.0;
    private int spawnTimer = 0;
    private Random random = new Random();

    private String message;

    private Image weaponImage;
    private Image fireballImage;

    private Image victoryImage;
    private Image gameOverImage;

    private class GameObject {

        int lane;
        double x;
        boolean isWeapon;

        public GameObject(int lane, double x, boolean isWeapon) {
            this.lane = lane;
            this.x = x;
            this.isWeapon = isWeapon;
        }
    }

    public FireMinigame(Hero hero, Image weaponImage) {
        super(hero);

        BufferedImage[] impFrames = new BufferedImage[2];
        try {
            impFrames[0] = javax.imageio.ImageIO.read(new java.io.File("res/enemy1F.png"));
            impFrames[1] = javax.imageio.ImageIO.read(new java.io.File("res/enemy1F2.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        BufferedImage[] demonFrames = new BufferedImage[3];
        try {
            demonFrames[0] = javax.imageio.ImageIO.read(new java.io.File("res/enemy2F.png"));
            demonFrames[1] = javax.imageio.ImageIO.read(new java.io.File("res/enemy2F2.png"));
            demonFrames[2] = javax.imageio.ImageIO.read(new java.io.File("res/enemy2F3.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        BufferedImage[] bossFrames = new BufferedImage[2];
        try {
            bossFrames[0] = javax.imageio.ImageIO.read(new java.io.File("res/enemy3F.png"));
            bossFrames[1] = javax.imageio.ImageIO.read(new java.io.File("res/enemy3F3.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            this.fireballImage = javax.imageio.ImageIO.read(new java.io.File("res/fireball.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            victoryImage = javax.imageio.ImageIO.read(new java.io.File("res/victory.png"));
            gameOverImage = javax.imageio.ImageIO.read(new java.io.File("res/gameover.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        enemies = new Enemy[]{
            new Enemy("Fire Imp", 60, 10, impFrames),
            new Enemy("Fire Demon", 100, 15, demonFrames),
            new Enemy("Fire BOSS", 180, 25, bossFrames)
        };
        this.weaponImage = weaponImage;
    }

    @Override
    public void start() {
        hero.resetHp();
        currentWave = 0;
        heroLane = 0;
        objects.clear();
        loadWave(currentWave);
        isFinished = false;
    }

    private void loadWave(int waveIndex) {
        currentEnemy = enemies[waveIndex];
        objectSpeed = 6.0 + (waveIndex * 2.0);
        message = "Wave " + (waveIndex + 1) + " " + currentEnemy.getName() + "!";
    }

    public void handleKeyPress(int keyCode) {
        if (isFinished || !hero.isAlive() || !currentEnemy.isAlive()) {
            return;
        }

        if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) {
            heroLane = 0;
        } else if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) {
            heroLane = 1;
        }
    }

    @Override
    public void update() {
        if (isFinished || !hero.isAlive() || !currentEnemy.isAlive()) {
            return;
        }

        spawnTimer++;
        if (spawnTimer >= 50) {
            spawnTimer = 0;
            int lane = random.nextInt(2);
            boolean isWeapon = random.nextBoolean();
            objects.add(new GameObject(lane, 800, isWeapon));
        }

        for (int i = 0; i < objects.size(); i++) {
            GameObject obj = objects.get(i);
            obj.x -= objectSpeed;

            if (obj.x <= 180 && obj.x >= 120 && obj.lane == heroLane) {
                if (obj.isWeapon) {
                    currentEnemy.takeDamage(hero.getAtk());
                } else {
                    hero.takeDamage(currentEnemy.getAtk());
                }
                objects.remove(i);
                i--;
                continue;
            }

            if (obj.x < -50) {
                objects.remove(i);
                i--;
            }
        }

        if (!currentEnemy.isAlive()) {
            if (currentWave < enemies.length - 1) {
                currentWave++;
                loadWave(currentWave);
            } else {
                message = "VICTORY!";
                isFinished = true;
            }
        } else if (!hero.isAlive()) {
            message = "GAME OVER!";
            isFinished = true;
        }
    }

    @Override
    public void checkHit() {

    }

    @Override
    public void draw(Graphics2D g2, int screenW, int screenH) {
        int laneUpperY = (int) (screenH * 0.35);
        int laneLowerY = (int) (screenH * 0.58);
        int heroUpperY = laneUpperY;
        int heroLowerY = laneLowerY;

        g2.setColor(new Color(255, 255, 255, 150));
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(100, laneLowerY + 60, (int) (screenW * 0.65), laneLowerY + 60);

        int heroW = (int) (screenW * 0.14);
        int heroH = (int) (heroW * 1.2);
        int heroX = 120;
        int heroY = (heroLane == 0) ? laneUpperY : laneLowerY;
        hero.draw(g2, heroX, heroY, heroW, heroH);

        int enemyW = (currentWave == 2) ? (int) (screenW * 0.22) : (int) (screenW * 0.17);
        int enemyH = (int) (enemyW * 1.2);
        int enemyX = (int) (screenW * 0.70);
        int enemyY = (int) (screenH * 0.40);
        currentEnemy.draw(g2, enemyX, enemyY, enemyW, enemyH);

        for (GameObject obj : objects) {
            int objSize = 60;

            int currentLaneHeroY = (obj.lane == 0) ? heroUpperY : heroLowerY;
            int objY = currentLaneHeroY + (heroH / 2) - (objSize / 2);

            if (obj.isWeapon) {
                if (weaponImage != null) {
                    g2.drawImage(weaponImage, (int) obj.x, objY, objSize, objSize, null);
                } else {
                    g2.setColor(Color.YELLOW);
                    g2.fillOval((int) obj.x, objY, objSize, objSize);
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Tahoma", Font.BOLD, 18));
                    g2.drawString("🗡", (int) obj.x + 8, objY + 24);
                }
            } else {
                if (fireballImage != null) {
                    g2.drawImage(fireballImage, (int) obj.x, objY, objSize, objSize, null);
                } else {
                    g2.setColor(Color.RED);
                    g2.fillOval((int) obj.x, objY, objSize, objSize);
                }
            }
        }

        g2.setFont(new Font("Tahoma", Font.BOLD, 16));

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(heroX - 10, screenH - 60, 220, 35);
        g2.setColor(Color.GREEN);
        g2.drawString("HP: " + hero.getHp() + " | ATK: " + hero.getAtk(), heroX, screenH - 37);

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(enemyX - 10, screenH - 60, 220, 35);
        g2.setColor(Color.RED);
        g2.drawString("HP: " + currentEnemy.getHp() + " | ATK: " + currentEnemy.getAtk(), enemyX, screenH - 37);

        if (isFinished) {
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRect(0, 0, screenW, screenH);
            if ("VICTORY!".equals(message) && victoryImage != null) {
                g2.drawImage(victoryImage, 0, 0, screenW, screenH, null);
            } else if ("GAME OVER!".equals(message) && gameOverImage != null) {
                g2.drawImage(gameOverImage, 0, 0, screenW, screenH, null);
            }
        } else if (message != null && !message.isEmpty()) {
            g2.setFont(new Font("Tahoma", Font.BOLD, 22));
            g2.setColor(Color.YELLOW);
            int textWidth = g2.getFontMetrics().stringWidth(message);
            g2.drawString(message, (screenW - textWidth) / 2, (int) (screenH * 0.10));
        }

    }
}
