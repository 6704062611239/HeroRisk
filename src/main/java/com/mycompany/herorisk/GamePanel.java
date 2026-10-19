package com.mycompany.herorisk;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.imageio.ImageIO;

public class GamePanel extends JPanel implements Runnable {

    final int screenWidth = 800;
    final int screenHeight = 600;

    int gameState = 0; 

    Thread gameThread;
    Image bgImage;
    Image logoImage;
    Image btnStage1, btnStage2, btnStage3;

    int btnWidth = 200;
    int btnHeight = 60;

    int btn1X = 100, btn1Y = 400;
    int btn2X = 300, btn2Y = 400;
    int btn3X = 500, btn3Y = 400;

    Image btnBack;
    int backX = 25;
    int backY = 25;
    int backWidth = 60;
    int backHeight = 60;

    Image bgStageWater;
    Image bgStageFire;
    Image bgStageEarth;

    Image enemy1W, enemy2W, enemy3W;
    Image enemy1F, enemy2F, enemy3F;
    Image enemy1E, enemy2E, enemy3E;

    Hero playerHero;
    Minigame activeMinigame;
    
    Image weapon;

    public GamePanel() throws Exception {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setDoubleBuffered(true);

        bgImage = ImageIO.read(new File("res/bg_main.jpg"));
        logoImage = ImageIO.read(new File("res/logo.png"));
        btnStage1 = ImageIO.read(new File("res/btn_stage1.jpg"));
        btnStage2 = ImageIO.read(new File("res/btn_stage2.jpg"));
        btnStage3 = ImageIO.read(new File("res/btn_stage3.jpg"));

        bgStageWater = ImageIO.read(new File("res/bg_water.jpg"));
        bgStageFire = ImageIO.read(new File("res/bg_fire.jpg"));
        bgStageEarth = ImageIO.read(new File("res/bg_earth.jpg"));

        btnBack = ImageIO.read(new File("res/back_btn.png"));

        enemy1W = ImageIO.read(new File("res/enemy1W.png"));
        enemy2W = ImageIO.read(new File("res/enemy2W.png"));
        enemy3W = ImageIO.read(new File("res/enemy3W.png"));
        enemy1F = ImageIO.read(new File("res/enemy1F.png"));
        enemy2F = ImageIO.read(new File("res/enemy2F.png"));
        enemy3F = ImageIO.read(new File("res/enemy3F.png"));
        enemy1E = ImageIO.read(new File("res/enemy1E.png"));
        enemy2E = ImageIO.read(new File("res/enemy2E.png"));
        enemy3E = ImageIO.read(new File("res/enemy3E.png"));
        
        weapon = ImageIO.read(new File("res/weapon.png"));

        playerHero = new Hero(100, 15);

        this.setFocusable(true);

        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (activeMinigame != null) {
                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) {
                        activeMinigame.checkHit();
                    }
                    if (activeMinigame instanceof FireMinigame) {
                        ((FireMinigame) activeMinigame).handleKeyPress(e.getKeyCode());
                    }
                    if (activeMinigame instanceof EarthMinigame) {
                        ((EarthMinigame) activeMinigame).handleKeyPressed(e.getKeyCode());
                    }
                }
            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (activeMinigame instanceof EarthMinigame) {
                    ((EarthMinigame) activeMinigame).handleKeyReleased(e.getKeyCode());
                }
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();

                if (gameState == 0) {
                    if (mouseX >= btn1X && mouseX <= btn1X + btnWidth && mouseY >= btn1Y && mouseY <= btn1Y + btnHeight) {
                        activeMinigame = new WaterMinigame(playerHero, enemy1W, enemy2W, enemy3W);
                        activeMinigame.start();
                        gameState = 10;
                    } else if (mouseX >= btn2X && mouseX <= btn2X + btnWidth && mouseY >= btn2Y && mouseY <= btn2Y + btnHeight) {
                        activeMinigame = new FireMinigame(playerHero, enemy1F, enemy2F, enemy3F, weapon);
                        activeMinigame.start();
                        gameState = 20;
                    } else if (mouseX >= btn3X && mouseX <= btn3X + btnWidth && mouseY >= btn3Y && mouseY <= btn3Y + btnHeight) {
                        activeMinigame = new EarthMinigame(playerHero, enemy1E, enemy2E, enemy3E, weapon);
                        activeMinigame.start();
                        gameState = 30;
                    }
                } else if (gameState >= 10) {
                    if (mouseX >= backX && mouseX <= backX + backWidth && mouseY >= backY && mouseY <= backY + backHeight) {
                        gameState = 0;
                    } else {
                        if (activeMinigame != null) {
                            if (activeMinigame instanceof WaterMinigame) {
                                WaterMinigame waterGame = (WaterMinigame) activeMinigame;
                                if (waterGame.isUpgrading()) {
                                    waterGame.handleUpgradeClick(mouseX, mouseY);
                                    return;
                                }
                            }
                            activeMinigame.checkHit();
                        }
                    }
                }
            }
        });
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (gameThread != null) {
            update();
            repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        if (gameState >= 10 && activeMinigame != null) {
            activeMinigame.update();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int screenW = getWidth();
        int screenH = getHeight();

        if (gameState == 0) {
            g2.drawImage(bgImage, 0, 0, screenW, screenH, this);

            int logoWidth = (int) (screenW * 0.65);
            int logoHeight = (int) (logoWidth * 0.35);
            int logoX = (screenW - logoWidth) / 2;
            int logoY = (int) (screenH * 0.12);
            g2.drawImage(logoImage, logoX, logoY, logoWidth, logoHeight, this);

            btnWidth = (int) (screenW * 0.18);
            btnHeight = (int) (btnWidth * 0.45);
            int gap = (int) (screenW * 0.03);

            int totalButtonsWidth = (btnWidth * 3) + (gap * 2);
            int startX = (screenW - totalButtonsWidth) / 2;
            int buttonsY = (int) (screenH * 0.58);

            btn1X = startX;
            btn1Y = buttonsY;

            btn2X = startX + btnWidth + gap;
            btn2Y = buttonsY;

            btn3X = startX + (btnWidth + gap) * 2;
            btn3Y = buttonsY;

            g2.drawImage(btnStage1, btn1X, btn1Y, btnWidth, btnHeight, this);
            g2.drawImage(btnStage2, btn2X, btn2Y, btnWidth, btnHeight, this);
            g2.drawImage(btnStage3, btn3X, btn3Y, btnWidth, btnHeight, this);

        } else if (gameState == 10) {
            g2.drawImage(bgStageWater, 0, 0, screenW, screenH, this);
            g2.drawImage(btnBack, backX, backY, backWidth, backHeight, this);
            activeMinigame.draw(g2, screenW, screenH);

        } else if (gameState == 20) {
            g2.drawImage(bgStageFire, 0, 0, screenW, screenH, this);
            g2.drawImage(btnBack, backX, backY, backWidth, backHeight, this);
            activeMinigame.draw(g2, screenW, screenH);

        } else if (gameState == 30) {
            g2.drawImage(bgStageEarth, 0, 0, screenW, screenH, this);
            g2.drawImage(btnBack, backX, backY, backWidth, backHeight, this);
            activeMinigame.draw(g2, screenW, screenH);

        }
        g2.dispose();
    }
}
