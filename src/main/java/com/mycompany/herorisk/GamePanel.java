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

    Thread gameThread;
    Image bgImage;
    Image logoImage;
    Image btnStage1, btnStage2, btnStage3;

    int btnWidth = 200;
    int btnHeight = 60;

    int btn1X = 100, btn1Y = 400;
    int btn2X = 300, btn2Y = 400;
    int btn3X = 500, btn3Y = 400;

    int gameState = 0;

    public GamePanel() throws Exception {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setDoubleBuffered(true);

        bgImage = ImageIO.read(new File("res/bg_main.jpg"));
        logoImage = ImageIO.read(new File("res/logo.png"));
        btnStage1 = ImageIO.read(new File("res/btn_stage1.jpg"));
        btnStage2 = ImageIO.read(new File("res/btn_stage2.jpg"));
        btnStage3 = ImageIO.read(new File("res/btn_stage3.jpg"));

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();

                if (gameState == 0) {
                    if (mouseX >= btn1X && mouseX <= btn1X + btnWidth &&
                        mouseY >= btn1Y && mouseY <= btn1Y + btnHeight) {
                        gameState = 1;
                    }
                    else if (mouseX >= btn2X && mouseX <= btn2X + btnWidth &&
                             mouseY >= btn2Y && mouseY <= btn2Y + btnHeight) {
                        gameState = 2;
                    }
                    else if (mouseX >= btn3X && mouseX <= btn3X + btnWidth &&
                             mouseY >= btn3Y && mouseY <= btn3Y + btnHeight) {
                        gameState = 3;
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
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int screenW = getWidth();
        int screenH = getHeight();

        if (gameState == 0) {
            if (bgImage != null) {
                g2.drawImage(bgImage, 0, 0, screenW, screenH, this);
            }

            if (logoImage != null) {
                int logoWidth = (int) (screenW * 0.65); 
                int logoHeight = (int) (logoWidth * 0.35); 
                int logoX = (screenW - logoWidth) / 2;     
                int logoY = (int) (screenH * 0.12);        

                g2.drawImage(logoImage, logoX, logoY, logoWidth, logoHeight, this);
            }

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

            if (btnStage1 != null) g2.drawImage(btnStage1, btn1X, btn1Y, btnWidth, btnHeight, this);
            if (btnStage2 != null) g2.drawImage(btnStage2, btn2X, btn2Y, btnWidth, btnHeight, this);
            if (btnStage3 != null) g2.drawImage(btnStage3, btn3X, btn3Y, btnWidth, btnHeight, this);

        } else if (gameState == 1) {
            g2.drawString("Welcome to Stage Water", 350, 300);
        } else if (gameState == 2) {
            g2.drawString("Welcome to Stage Fire", 350, 300);
        } else if (gameState == 3) {
            g2.drawString("Welcome to Stage Earth", 350, 300);
        }
        g2.dispose();
    }
}