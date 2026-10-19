package com.mycompany.herorisk;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Hero extends Character {

    private BufferedImage[] idleFrames = new BufferedImage[3]; 
    private int currentFrame = 0;
    private Thread animThread;   

    public Hero(int hp, int atk) {
        super(hp, atk, null); 

        try {
            idleFrames[0] = ImageIO.read(new File("res/hero.png"));
            idleFrames[1] = ImageIO.read(new File("res/hero2.png"));
            idleFrames[2] = ImageIO.read(new File("res/hero3.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        animThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(250); 
                        currentFrame++;
                        if (currentFrame >= idleFrames.length) {
                            currentFrame = 0; 
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        animThread.start();
    }

    public BufferedImage getSprite() {
        return idleFrames[currentFrame];
    }

    @Override
    public void draw(Graphics2D g2, int x, int y, int width, int height) {
        if (idleFrames[currentFrame] != null) {
            g2.drawImage(idleFrames[currentFrame], x, y, width, height, null);
        }
    }
}