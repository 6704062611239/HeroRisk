package com.mycompany.herorisk;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;
import java.awt.image.BufferedImage;

public class EarthMinigame extends Minigame {

    private Enemy[] enemies;
    private int currentWave = 0;

    private double heroX = 200;
    private double heroY = 250;
    private int heroW = 50;
    private int heroH = 60;
    private double heroSpeed = 5.0;

    private boolean moveUp, moveDown, moveLeft, moveRight;

    private int arenaX = 50;
    private int arenaY = 110;
    private int arenaW = 700;
    private int arenaH = 430;

    private ArrayList<Rock> rocks = new ArrayList<>();
    private ArrayList<Sword> swords = new ArrayList<>();
    private Random random = new Random();
    private int rockSpawnTimer = 0;
    private int swordSpawnTimer = 0;

    private String message;

    private Image weaponImage;

    private class Rock {

        int targetX, targetY;
        int currentY;
        int warningTimer = 30;
        boolean isDropped = false;
        int size = 40;

        public Rock(int x, int y) {
            this.targetX = x;
            this.targetY = y;
            this.currentY = y - 300;
        }
    }

    private class Sword {

        int x, y;
        int size = 35;

        public Sword(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public EarthMinigame(Hero hero, Image enemy1, Image enemy2, Image enemy3, Image weaponImage) {
        super(hero);

        BufferedImage[] golemFrames = new BufferedImage[2];
        try {
            golemFrames[0] = javax.imageio.ImageIO.read(new java.io.File("res/enemy1E.png"));
            golemFrames[1] = javax.imageio.ImageIO.read(new java.io.File("res/enemy1E2.png")); 
        } catch (Exception e) {
            e.printStackTrace();
        }

        BufferedImage[] elementalFrames = new BufferedImage[2];
        try {
            elementalFrames[0] = javax.imageio.ImageIO.read(new java.io.File("res/enemy2E2.png")); 
            elementalFrames[1] = javax.imageio.ImageIO.read(new java.io.File("res/enemy2E3.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        BufferedImage[] bossFrames = new BufferedImage[2];
        try {
            bossFrames[0] = javax.imageio.ImageIO.read(new java.io.File("res/enemy3E.png"));
            bossFrames[1] = javax.imageio.ImageIO.read(new java.io.File("res/enemy3E2.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        enemies = new Enemy[]{
            new Enemy("Earth Golem", 80, 12, golemFrames),
            new Enemy("Rock Elemental", 130, 18, elementalFrames),
            new Enemy("Earth BOSS", 220, 28, bossFrames)
        };
        this.weaponImage = weaponImage;
    }

    @Override
    public void start() {
        hero.resetHp();
        currentWave = 0;

        arenaX = 50;
        arenaY = 110;
        arenaW = 700;
        arenaH = 430;

        heroX = arenaX + arenaW / 2 - heroW / 2;
        heroY = arenaY + arenaH / 2 - heroH / 2;
        rocks.clear();
        swords.clear();
        loadWave(currentWave);
        isFinished = false;
    }

    private void loadWave(int waveIndex) {
        currentEnemy = enemies[waveIndex];
        message = "Wave " + (waveIndex + 1) + " " + currentEnemy.getName() + "!";
    }

    public void handleKeyPressed(int keyCode) {
        if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) {
            moveUp = true;
        }
        if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) {
            moveDown = true;
        }
        if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
            moveLeft = true;
        }
        if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
            moveRight = true;
        }
    }

    public void handleKeyReleased(int keyCode) {
        if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) {
            moveUp = false;
        }
        if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) {
            moveDown = false;
        }
        if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
            moveLeft = false;
        }
        if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
            moveRight = false;
        }
    }

    @Override
    public void update() {
        if (isFinished || !hero.isAlive() || !currentEnemy.isAlive()) {
            return;
        }

        if (moveUp && heroY > arenaY) {
            heroY -= heroSpeed;
        }
        if (moveDown && heroY + heroH < arenaY + arenaH) {
            heroY += heroSpeed;
        }
        if (moveLeft && heroX > arenaX) {
            heroX -= heroSpeed;
        }
        if (moveRight && heroX + heroW < arenaX + arenaW) {
            heroX += heroSpeed;
        }

        Rectangle heroRect = new Rectangle((int) heroX, (int) heroY, heroW, heroH);

        rockSpawnTimer++;
        if (rockSpawnTimer >= 40 - (currentWave * 8)) {
            rockSpawnTimer = 0;
            int rx = arenaX + random.nextInt(Math.max(1, arenaW - 40));
            int ry = arenaY + random.nextInt(Math.max(1, arenaH - 40));
            rocks.add(new Rock(rx, ry));
        }

        swordSpawnTimer++;
        if (swordSpawnTimer >= 90) {
            swordSpawnTimer = 0;
            if (swords.size() < 3) {
                int sx = arenaX + random.nextInt(Math.max(1, arenaW - 30));
                int sy = arenaY + random.nextInt(Math.max(1, arenaH - 30));
                swords.add(new Sword(sx, sy));
            }
        }

        for (int i = 0; i < rocks.size(); i++) {
            Rock rock = rocks.get(i);
            if (rock.warningTimer > 0) {
                rock.warningTimer--;
            } else {
                rock.currentY += 15;
                if (rock.currentY >= rock.targetY) {
                    rock.currentY = rock.targetY;
                    rock.isDropped = true;

                    Rectangle rockRect = new Rectangle(rock.targetX, rock.targetY, rock.size, rock.size);
                    if (heroRect.intersects(rockRect)) {
                        hero.takeDamage(currentEnemy.getAtk());
                    }

                    rocks.remove(i);
                    i--;
                }
            }
        }

        for (int i = 0; i < swords.size(); i++) {
            Sword sword = swords.get(i);
            Rectangle swordRect = new Rectangle(sword.x, sword.y, sword.size, sword.size);
            if (heroRect.intersects(swordRect)) {
                currentEnemy.takeDamage(hero.getAtk());
                swords.remove(i);
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
        arenaX = (int) (screenW * 0.05);
        arenaY = (int) (screenH * 0.18);
        arenaW = (int) (screenW * 0.52);
        arenaH = (int) (screenH * 0.62);

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(arenaX, arenaY, arenaW, arenaH);
        g2.setColor(new Color(255, 255, 255, 220));
        g2.setStroke(new BasicStroke(4));
        g2.drawRect(arenaX, arenaY, arenaW, arenaH);

        for (Rock rock : rocks) {
            if (rock.warningTimer > 0) {
                g2.setColor(Color.RED);
                g2.setFont(new Font("Tahoma", Font.BOLD, 32));
                g2.drawString("!", rock.targetX + 12, rock.targetY + 32);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(rock.targetX, rock.targetY, rock.size, rock.size);
            } else {
                g2.setColor(new Color(130, 95, 65));
                g2.fillOval(rock.targetX, rock.currentY, rock.size, rock.size);
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(rock.targetX, rock.currentY, rock.size, rock.size);
            }
        }

        for (Sword sword : swords) {
            if (weaponImage != null) {
                g2.drawImage(weaponImage, sword.x, sword.y, sword.size, sword.size, null);
            } else {
                g2.setColor(Color.YELLOW);
                g2.fillOval(sword.x, sword.y, sword.size, sword.size);
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Tahoma", Font.BOLD, 18));
                g2.drawString("🗡", sword.x + 6, sword.y + 23);
            }
        }

        hero.draw(g2, (int) heroX, (int) heroY, heroW, heroH);

        int enemyW = (currentWave == 2) ? (int) (screenW * 0.22) : (int) (screenW * 0.17);
        int enemyH = (int) (enemyW * 1.2);
        int enemyX = (int) (screenW * 0.70);
        int enemyY = (int) (screenH * 0.35);
        currentEnemy.draw(g2, enemyX, enemyY, enemyW, enemyH);

        g2.setFont(new Font("Tahoma", Font.BOLD, 16));
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(arenaX, arenaY + arenaH + 12, 210, 35);
        g2.setColor(Color.GREEN);
        g2.drawString("Hero HP: " + hero.getHp() + "/" + hero.getMaxHp(), arenaX + 10, arenaY + arenaH + 35);

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(enemyX, enemyY + enemyH + 10, 210, 35);
        g2.setColor(Color.RED);
        g2.drawString(currentEnemy.getName() + " HP: " + currentEnemy.getHp(), enemyX + 10, enemyY + enemyH + 33);

        g2.setFont(new Font("Tahoma", Font.BOLD, 18));
        g2.setColor(Color.YELLOW);
        g2.drawString(message, (screenW - g2.getFontMetrics().stringWidth(message)) / 2, (int) (screenH * 0.10));
    }
}
