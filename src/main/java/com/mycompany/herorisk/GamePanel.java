package com.mycompany.herorisk;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
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

    Hero playerHero;
    Minigame activeMinigame;

    Image weapon;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setDoubleBuffered(true);

        try {
            bgImage = ImageIO.read(getClass().getResourceAsStream("/res/bg_main.jpg"));
            logoImage = ImageIO.read(getClass().getResourceAsStream("/res/logo.png"));
            btnStage1 = ImageIO.read(getClass().getResourceAsStream("/res/btn_stage1.jpg"));
            btnStage2 = ImageIO.read(getClass().getResourceAsStream("/res/btn_stage2.jpg"));
            btnStage3 = ImageIO.read(getClass().getResourceAsStream("/res/btn_stage3.jpg"));

            bgStageWater = ImageIO.read(getClass().getResourceAsStream("/res/bg_water.jpg"));
            bgStageFire = ImageIO.read(getClass().getResourceAsStream("/res/bg_fire.jpg"));
            bgStageEarth = ImageIO.read(getClass().getResourceAsStream("/res/bg_earth.jpg"));

            btnBack = ImageIO.read(getClass().getResourceAsStream("/res/back_btn.png"));

            weapon = ImageIO.read(getClass().getResourceAsStream("/res/weapon.png"));
        }catch(IOException e){
            e.printStackTrace();
        }

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
                        activeMinigame = new WaterMinigame(playerHero);
                        activeMinigame.start();
                        gameState = 10;
                    } else if (mouseX >= btn2X && mouseX <= btn2X + btnWidth && mouseY >= btn2Y && mouseY <= btn2Y + btnHeight) {
                        activeMinigame = new FireMinigame(playerHero, weapon);
                        activeMinigame.start();
                        gameState = 20;
                    } else if (mouseX >= btn3X && mouseX <= btn3X + btnWidth && mouseY >= btn3Y && mouseY <= btn3Y + btnHeight) {
                        activeMinigame = new EarthMinigame(playerHero, weapon);
                        activeMinigame.start();
                        gameState = 30;
                    }
                } else if (gameState >= 10) {
                    if (mouseX >= backX && mouseX <= backX + backWidth && mouseY >= backY && mouseY <= backY + backHeight) {
                        gameState = 0;
                    } else {
                        if (activeMinigame != null) {
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
