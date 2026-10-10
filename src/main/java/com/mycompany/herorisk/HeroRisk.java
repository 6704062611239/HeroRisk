package com.mycompany.herorisk;

import javax.swing.JFrame;

public class HeroRisk {

    public static void main(String[] args) throws Exception {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setTitle("Hero Risk");
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        window.setVisible(true);
        gamePanel.startGameThread();
    }
}