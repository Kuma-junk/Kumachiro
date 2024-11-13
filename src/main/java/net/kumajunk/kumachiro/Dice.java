package net.kumajunk.kumachiro;

import java.util.Random;

public class Dice extends Thread{
    @Override
    public synchronized void run() {
        Random random = new Random();
        Game.rawDiceResult = new int[3];
        for (int i = 0; i < Game.rawDiceResult.length; i++) {
            Game.rawDiceResult[i] = random.nextInt(6) + 1; // 1～6のランダムな数字
        }

        Yaku yaku = new Yaku();
        yaku.start();
        try {
            yaku.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
