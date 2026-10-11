package com.mycompany.herorisk;

import java.awt.Image;

public class Hero extends Character {

    public Hero(int hp, int atk, Image sprite) {
        super(hp, atk, sprite);
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void addMaxHp(int amount) {
        this.maxHp += amount;
        this.hp = this.maxHp; 
    }
}
