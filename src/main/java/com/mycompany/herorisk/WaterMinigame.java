package com.mycompany.herorisk;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class WaterMinigame extends Minigame {

    private Enemy[] enemies;
    private int currentWave = 0;

    private double arrowAngle = 0;
    private double arrowSpeed = 5.0;
    private int targetStartAngle = 45;
    private int targetArcAngle = 45;
    private String message = "";

    public WaterMinigame(Hero hero, Image enemy1, Image enemy2, Image enemy3) {
        super(hero);

        BufferedImage[] slimeFrames = new BufferedImage[2];
        try {
            slimeFrames[0] = javax.imageio.ImageIO.read(new java.io.File("res/enemy1W.png"));
            slimeFrames[1] = javax.imageio.ImageIO.read(new java.io.File("res/enemy1W2.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        BufferedImage[] warriorFrames = new BufferedImage[2];
        try {
            warriorFrames[0] = javax.imageio.ImageIO.read(new java.io.File("res/enemy2W2.png")); 
            warriorFrames[1] = javax.imageio.ImageIO.read(new java.io.File("res/enemy2W3.png")); 
        } catch (Exception e) {
            e.printStackTrace();
        }

        BufferedImage[] bossFrames = new BufferedImage[2];
        try {
            bossFrames[0] = javax.imageio.ImageIO.read(new java.io.File("res/enemy3W.png"));
            bossFrames[1] = javax.imageio.ImageIO.read(new java.io.File("res/enemy3W2.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        enemies = new Enemy[]{
            new Enemy("Water Slime", 50, 8, slimeFrames),
            new Enemy("Water Warrior", 80, 12, warriorFrames),
            new Enemy("Water BOSS", 150, 20, bossFrames)
        };
    }

    @Override
    public void start() {
        hero.resetHp();
        currentWave = 0;
        loadWave(currentWave);
        isFinished = false;
    }

    private void loadWave(int waveIndex) {
        currentEnemy = enemies[waveIndex];
        arrowSpeed = 5.0 + (waveIndex * 2.5);
        message = "Wave " + (waveIndex + 1) + " " + currentEnemy.getName() + "!";
    }

    @Override
    public void update() {
        if (!isFinished && currentEnemy.isAlive() && hero.isAlive()) {
            arrowAngle = (arrowAngle + arrowSpeed) % 360;
        }
    }

    @Override
    public void checkHit() {
        if (isFinished || !currentEnemy.isAlive() || !hero.isAlive()) {
            return;
        }

        double currentAngle = arrowAngle % 360;

        if (currentAngle >= targetStartAngle && currentAngle <= (targetStartAngle + targetArcAngle)) {
            currentEnemy.takeDamage(hero.getAtk());
            message = "HIT! " + currentEnemy.getName() + " " + hero.getAtk();
        } else {
            hero.takeDamage(currentEnemy.getAtk());
            message = "MISS! " + currentEnemy.getName() + " " + currentEnemy.getAtk();
        }

        if (!currentEnemy.isAlive()) {
            if (currentWave < enemies.length - 1) {
                currentWave++;
                hero.resetHp();
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

    public boolean isUpgrading() {
        return false;
    }

    public void handleUpgradeClick(int mouseX, int mouseY) {
        
    }

    @Override
    public void draw(Graphics2D g2, int screenW, int screenH) {
        int heroW = (int) (screenW * 0.16);
        int heroH = (int) (heroW * 1.2);
        int heroX = (int) (screenW * 0.15);
        int heroY = (int) (screenH * 0.45);
        hero.draw(g2, heroX, heroY, heroW, heroH);

        int enemyW = (currentWave == 2) ? (int) (screenW * 0.20) : (int) (screenW * 0.15);
        int enemyH = (int) (enemyW * 1.2);
        int enemyX = (int) (screenW * 0.68);
        int enemyY = (int) (screenH * 0.45);
        currentEnemy.draw(g2, enemyX, enemyY, enemyW, enemyH);

        g2.setFont(new Font("Tahoma", Font.BOLD, 16));
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(heroX, heroY + heroH + 10, heroW, 30);
        g2.setColor(Color.GREEN);
        g2.drawString("HP: " + hero.getHp() + " | ATK: " + hero.getAtk(), heroX + 10, heroY + heroH + 30);

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(enemyX, enemyY + enemyH + 10, enemyW, 30);
        g2.setColor(Color.RED);
        g2.drawString("HP: " + currentEnemy.getHp() + " | ATK: " + currentEnemy.getAtk(), enemyX + 10, enemyY + enemyH + 30);

        int circleSize = (int) (screenW * 0.18);
        int circleX = (screenW - circleSize) / 2;
        int circleY = (int) (screenH * 0.32);

        g2.setColor(Color.LIGHT_GRAY);
        g2.fillOval(circleX, circleY, circleSize, circleSize);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(4));
        g2.drawOval(circleX, circleY, circleSize, circleSize);

        g2.setColor(new Color(46, 204, 113, 220));
        g2.fillArc(circleX, circleY, circleSize, circleSize, targetStartAngle, targetArcAngle);

        int innerSize = (int) (circleSize * 0.55);
        int innerX = circleX + (circleSize - innerSize) / 2;
        int innerY = circleY + (circleSize - innerSize) / 2;
        g2.setColor(Color.WHITE);
        g2.fillOval(innerX, innerY, innerSize, innerSize);
        g2.setColor(Color.BLACK);
        g2.drawOval(innerX, innerY, innerSize, innerSize);

        double rad = Math.toRadians(arrowAngle);
        int arrowLength = circleSize / 2 + 10;
        int arrowX = (int) ((circleX + circleSize / 2) + Math.cos(rad) * arrowLength);
        int arrowY = (int) ((circleY + circleSize / 2) - Math.sin(rad) * arrowLength);

        g2.setColor(Color.RED);
        g2.fillOval(arrowX - 8, arrowY - 8, 16, 16);

        g2.setFont(new Font("Tahoma", Font.BOLD, 18));
        g2.setColor(Color.YELLOW);
        g2.drawString(message, (screenW - g2.getFontMetrics().stringWidth(message)) / 2, circleY + circleSize + 45);
    }
}
