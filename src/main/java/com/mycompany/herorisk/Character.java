package com.mycompany.herorisk;

import java.awt.Graphics2D;
import java.awt.Image;

public abstract class Character {
    protected int hp;
    protected int maxHp;
    protected int atk;
    protected Image sprite;

    public Character(int hp, int atk, Image sprite) {
        this.maxHp = hp;
        this.hp = hp;
        this.atk = atk;
        this.sprite = sprite;
    }

    public void takeDamage(int damage) {
        this.hp -= damage;
        if (this.hp < 0) this.hp = 0;
    }

    public void draw(Graphics2D g2, int x, int y, int width, int height) {
        if (sprite != null) {
            g2.drawImage(sprite, x, y, width, height, null);
        }
    }

    public int getHp() { 
        return hp; 
    }
    public int getMaxHp() { 
        return maxHp; 
    }
    public int getAtk() { 
        return atk; 
    }
    public boolean isAlive() { 
        return hp > 0; 
    }
    public void resetHp() { 
        this.hp = this.maxHp; 
    }
}