package com.mycompany.herorisk;

import java.awt.Graphics2D;

public abstract class Minigame {
    protected Hero hero;
    protected Enemy currentEnemy;
    protected boolean isFinished = false;

    public Minigame(Hero hero) {
        this.hero = hero;
    }

    public abstract void start();
    public abstract void update();
    public abstract void draw(Graphics2D g2, int screenW, int screenH);
    public abstract void checkHit(); 

    public boolean isFinished() { return isFinished; }
}