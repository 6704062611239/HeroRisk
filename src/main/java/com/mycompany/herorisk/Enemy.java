package com.mycompany.herorisk;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Enemy extends Character {
    private String name;
    private BufferedImage[] animFrames; 
    private int currentFrame = 0;
    private Thread animThread;

    public Enemy(String name, int hp, int atk, Image sprite) {
        super(hp, atk, sprite);
        this.name = name;
    }

    public Enemy(String name, int hp, int atk, BufferedImage[] frames) {
        super(hp, atk, null);
        this.name = name;
        this.animFrames = frames;

        animThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(300); 
                        if (animFrames != null && animFrames.length > 0) {
                            currentFrame = (currentFrame + 1) % animFrames.length;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        animThread.start();
    }

    public String getName() {
        return name;
    }

    @Override
    public void draw(Graphics2D g2, int x, int y, int width, int height) {
        if (animFrames != null && animFrames.length > 0) {
            g2.drawImage(animFrames[currentFrame], x, y, width, height, null);
        } else {
            super.draw(g2, x, y, width, height);
        }
    }
}