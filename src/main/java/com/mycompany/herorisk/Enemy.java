package com.mycompany.herorisk;

import java.awt.Image;

public class Enemy extends Character {
    private String name;

    public Enemy(String name, int hp, int atk, Image sprite) {
        super(hp, atk, sprite);
        this.name = name;
    }

    public String getName() { return name; }
}