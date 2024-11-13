package net.kumajunk.kumachiro;

import java.util.Arrays;

public class Yaku extends Thread{
    @Override
    public synchronized void run() {
        int[] sortDiceResult = Game.rawDiceResult.clone();
        Arrays.sort(sortDiceResult);
        int first = sortDiceResult[0], second = sortDiceResult[1], third = sortDiceResult[2];

        ///123
        if (first == 1 && second == 2 && third == 3) {
            Game.roleNumber = 1;
            Game.roleName = "ヒフミ";
            Game.roleType = 1;
            Game.roleQuote = "2倍払い......!";
        }
        ///456
        else if (first == 4 && second == 5 && third == 6) {
            Game.roleNumber = 2;
            Game.roleName = "シゴロ";
            Game.roleType = 2;
            Game.roleQuote = "2倍づけ......!";
        }
        ///111
        else if (first == 1 && second == 1 && third == 1) {
            Game.roleNumber = 111;
            Game.roleName = "ピンゾロ";
            Game.roleType = 3;
            Game.roleQuote = "5倍づけ......!";
        }
        ///ゾロ目
        else if (first == second && second == third) {
            Game.roleNumber = first * 111;
            Game.roleName = first + "のゾロ目";
            Game.roleType = 4;
            Game.roleQuote = "3倍づけ......!";
        }
        ///通常目
        else if (first == second || second == third) {
            int pairNumber;
            int singleNumber;

            if (first == second) {
                pairNumber = first;
                singleNumber = third;
            } else {
                pairNumber = second;
                singleNumber = first;
            }
            String[] numberNames = {"", "イチ", "ニ", "サン", "シ", "ゴ", "ロク"};
            Game.roleNumber = 10 + singleNumber;
            Game.roleName = numberNames[pairNumber] + "の" + numberNames[singleNumber];
            Game.roleType = 5;
            Game.roleQuote = "1倍づけ......!";
        }
        ///役無し
        else {
            Game.roleNumber = 3;
            Game.roleName = "役無し";
            Game.roleQuote = "1倍払い......!";
        }
    }
}
