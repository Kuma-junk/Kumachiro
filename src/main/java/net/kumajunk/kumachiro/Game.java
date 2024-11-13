package net.kumajunk.kumachiro;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

import static net.kumajunk.kumachiro.Kumachiro.*;

public class Game extends Thread {
    public static boolean inProgress;
    public static List<UUID> players = new ArrayList<>();
    public static double betAmount;
    public static UUID hostUuid;
    public static int playerCount;
    public static int[] rawDiceResult;
    public static String roleName;
    public static int roleNumber;
    public static String hostRole;
    public static int hostRoleNumber;
    public static String childRole;
    public static int childRoleNumber;
    public static int roleType;
    public static String roleQuote;
    public static double hostWin;
    public static double childWin;
    public static int hostRoleType;
    public static int childRoleType;

    public void run() {
        // 表示する秒数
        int remainingTime = 30;
        List<Integer> countdownList = Arrays.asList(30, 20, 10, 5, 3, 2, 1);

        // カウントダウン処理
        for (int time = remainingTime; time > 0; time--) {
            // 参加者が揃った場合にメッセージを送信
            if (players.size() == playerCount) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(pluginTitle + "§a参加者が揃いました。ゲームを開始します!");
                }
                break;
            }

            // カウントダウンメッセージを送信
            if (countdownList.contains(time)) {  // 30, 20, 10, 5, 3, 2, 1 のタイミングでメッセージを送信
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(pluginTitle + "§a§lチンチロ参加者募集中!" +
                            " 必要金額: " + betAmount + " 円 " +
                            " 子の人数: " + playerCount + "人" +
                            " 残り " + time + " 秒");
                }
            }

            if (time == 1) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(pluginTitle + "§c募集時間が終了しました。");
                }
                vaultAPI.deposit(hostUuid, betAmount*playerCount);
                for (UUID player : players) {
                    vaultAPI.deposit(player, betAmount);
                }
                hostWin = betAmount*playerCount;


                Finish finish = new Finish();
                finish.start();
                return;
            }
            // 1秒待機
            try {
                Thread.sleep(1000); // 1秒待機
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 中断された場合は処理を中断
                break; // スレッドを終了
            }
        }

        if (players.size() < playerCount) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(hostUuid).getName() + "§cの部屋は参加者が揃っていないので解散しました。");
            }
            for (int i = 0; i < playerCount; i++) {
                vaultAPI.deposit((players.get(i)), betAmount);
            }

            Finish finish = new Finish();
            finish.start();
        }

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(pluginTitle + "§e§l親のターン");
            player.sendMessage(pluginTitle + "§a§l" + Bukkit.getOfflinePlayer(hostUuid).getName() + "§a§lがサイコロを振っています...§e§k aaa");
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        hostRoleNumber = 3;

        //親サイコロ
        for (int i = 0; i < 3; i++) {
            Dice dice = new Dice();
            dice.start();
            try {
                dice.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (hostRoleNumber != 3) {
                break;
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(pluginTitle + "§a§lチンチロリン♪ §e§l[§f§l" + rawDiceResult[0] + "§e§l] , [§f§l" + rawDiceResult[1] + "§e§l] , [§f§l" + rawDiceResult[2] + "§e§l]");
                player.sendMessage(pluginTitle + roleName + "!");
                hostRole = roleName;
                hostRoleNumber = roleNumber;
                hostRoleType = roleType;
            }
            if (hostRoleNumber == 3 && i < 2) {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(pluginTitle + "§a§lサイコロを振り直しています... §e§k aaa");
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //即終了役
        switch (hostRoleType) {
            case 1:
                hostWin = betAmount / 5 * 3 * playerCount;
                childWin = betAmount / 5 * 7;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(hostUuid).getName() + "§aが" + hostRole + "を出して子が" + childWin + "円獲得しました!" + roleQuote);
                    player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(hostUuid) + " の " + roleQuote);
                }
                vaultAPI.deposit(hostUuid, hostWin);
                for (int i = 0; i < players.size(); i++) {
                    vaultAPI.deposit(players.get(i), childWin);
                }

                Finish finish1 = new Finish();
                finish1.start();
                return;
            case 2:
                hostWin = betAmount / 5 * 7 * playerCount;
                childWin = betAmount / 5 * 3;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(hostUuid).getName() + "§aが" + hostRole + "を出して親が" + hostWin + "円獲得しました!" + roleQuote);
                    player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(hostUuid) + " の " + roleQuote);
                }
                vaultAPI.deposit(hostUuid, hostWin);
                for (int i = 0; i < players.size(); i++) {
                    vaultAPI.deposit(players.get(i), childWin);
                }

                Finish finish2 = new Finish();
                finish2.start();
                return;
            case 3:
                hostWin = betAmount * 2 * playerCount;
                childWin = 0;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(hostUuid).getName() + "§aが" + hostRole + "を出して親が" + hostWin + "円獲得しました!");
                    player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(hostUuid) + " の " + roleQuote);
                }
                vaultAPI.deposit(hostUuid, hostWin);
                for (int i = 0; i < players.size(); i++) {
                    vaultAPI.deposit(players.get(i), childWin);
                }

                Finish finish3 = new Finish();
                finish3.start();
                return;
            default:
                break;
        }

        //親役無し
        if (hostRoleNumber == 3) {
            hostWin = betAmount / 5 * 4 * playerCount;
            childWin = betAmount / 5 * 6;

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(hostUuid).getName() + "§aが" + hostRole + "を出して子が" + childWin + "円獲得しました!");
                player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(hostUuid) + " の " + roleQuote);
            }
            vaultAPI.deposit(hostUuid, hostWin);
            for (int i = 0; i < players.size(); i++) {
                vaultAPI.deposit(players.get(i), childWin);
            }

            Finish finish = new Finish();
            finish.start();
            return;
        }

        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //子サイコロ
        first: for (int p = 0; p < players.size(); p++) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (p == 0) {
                    player.sendMessage(pluginTitle + "§e§l子のターン");
                }
                player.sendMessage(pluginTitle + "§a§l" + Bukkit.getOfflinePlayer(players.get(p)).getName() + "§a§lがサイコロを振っています...§e§k aaa");
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            childRoleNumber = 3;

            for (int i = 0; i < 3; i++) {
                Dice dice = new Dice();
                dice.start();
                try {
                    dice.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (childRoleNumber != 3) {
                    break;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(pluginTitle + "§a§lチンチロリン♪ §e§l[§f§l" + rawDiceResult[0] + "§e§l] , [§f§l" + rawDiceResult[1] + "§e§l] , [§f§l" + rawDiceResult[2] + "§e§l]");
                    player.sendMessage(pluginTitle + roleName + "!");
                    childRole = roleName;
                    childRoleNumber = roleNumber;
                    childRoleType = roleType;
                }
                if (childRoleNumber == 3 && i < 2) {
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(pluginTitle + "§a§lサイコロを振り直しています... §e§k aaa");
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            //即終了役
            switch (childRoleType) {
                case 1:
                    childWin = betAmount / 5 * 3;
                    hostWin = hostWin + betAmount / 5 * 7;

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + "§aが" + childRole + "を出して親が" + hostWin + "円獲得しました!" + roleQuote);
                        player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + betAmount + " → " + childWin + " 円 ");
                        player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + " の " + roleQuote);
                    }
                    vaultAPI.deposit(players.get(p), childWin);

                    break first;
                case 2:
                    childWin = betAmount / 5 * 7;
                    hostWin = hostWin + betAmount / 5 * 3;

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + "§aが" + childRole + "を出して子が" + childWin + "円獲得しました!" + roleQuote);
                        player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + betAmount + " → " + childWin + " 円 ");
                        player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + " の " + roleQuote);
                    }
                    vaultAPI.deposit(players.get(p), childWin);

                    break first;
                case 3:
                    childWin = betAmount * 2;
                    hostWin = hostWin + 0;

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + "§aが" + childRole + "を出して子が" + childWin + "円獲得しました!");
                        player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + betAmount + " → " + childWin + " 円 ");
                        player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + " の " + roleQuote);
                    }
                    vaultAPI.deposit(players.get(p), childWin);

                    break first;
            }

            //子役無し
            if (childRoleNumber == 3) {
                childWin = betAmount / 5 * 4;
                hostWin = hostWin + betAmount / 5 * 6;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + "§aが" + childRole + "を出して親が" + betAmount / 5 * 6 + "円獲得しました!");
                    player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + betAmount + " → " + childWin + " 円 ");
                    player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + " の " + roleQuote);
                }
                vaultAPI.deposit(players.get(p), childWin);

                break;
            }

            //ゾロ目と通常役
            if (hostRoleType == childRoleType) {
                switch (hostRoleType) {
                    case 4:
                        if (hostRoleNumber > childRoleNumber) {
                            hostWin = hostWin + betAmount / 5 * 8;
                            childWin = betAmount / 5 * 2;

                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(hostUuid).getName() + "§aが" + hostRole + "を出して親が" + hostWin + "円獲得しました!");
                                player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + betAmount + " → " + childWin + " 円 ");
                                player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(hostUuid).getName() + " の " + roleQuote);
                            }

                            vaultAPI.deposit(players.get(p), childWin);
                        } else if (hostRoleNumber < childRoleNumber) {
                            hostWin = hostWin + betAmount / 5 * 2;
                            childWin = betAmount / 5 * 8;

                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + "§aが" + childRole + "を出して子が" + childWin + "円獲得しました!");
                                player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + betAmount + " → " + childWin + " 円 ");
                                player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + " の " + roleQuote);
                            }

                            vaultAPI.deposit(players.get(p), childWin);
                        } else {
                            hostWin = hostWin + betAmount;
                            childWin = betAmount;

                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + "§aが" + childRole + "を出して引き分け!" + roleQuote);
                                player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + betAmount + " → " + childWin + " 円 ");
                                player.sendMessage(pluginTitle + "引き分け......!");
                            }

                            vaultAPI.deposit(players.get(p), childWin);
                        }
                        break;
                    case 5:
                        if (hostRoleNumber > childRoleNumber) {
                            hostWin = hostWin + betAmount / 5 * 6;
                            childWin = betAmount / 5 * 4;

                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(hostUuid).getName() + "§aが" + hostRole + "を出して親が" + hostWin + "円獲得しました!");
                                player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + betAmount + " → " + childWin + " 円 ");
                                player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(hostUuid).getName() + " の " + roleQuote);
                            }

                            vaultAPI.deposit(players.get(p), childWin);
                        } else if (hostRoleNumber < childRoleNumber) {
                            hostWin = hostWin + betAmount / 5 * 4;
                            childWin = betAmount / 5 * 6;

                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + "§aが" + childRole + "を出して子が" + childWin + "円獲得しました!");
                                player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + betAmount + " → " + childWin + " 円 ");
                                player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + " の " + roleQuote);
                            }

                            vaultAPI.deposit(players.get(p), childWin);
                        } else {
                            hostWin = hostWin + betAmount;
                            childWin = betAmount;

                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + "§aが" + childRole + "を出して引き分け!" + roleQuote);
                                player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + betAmount + " → " + childWin + " 円 ");
                                player.sendMessage(pluginTitle + "引き分け......!");
                            }

                            vaultAPI.deposit(players.get(p), childWin);
                        }
                        break;
                }
            } else if (hostRoleType > childRoleType) {
                hostWin = hostWin + betAmount / 5 * 2;
                childWin = betAmount / 5 * 8;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + "§aが" + childRole + "を出して子が" + childWin + "円獲得しました!");
                    player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + betAmount + " → " + childWin + " 円 ");
                    player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + " の " + roleQuote);
                }

                vaultAPI.deposit(players.get(p), childWin);
            } else {
                hostWin = hostWin + betAmount / 5 * 8;
                childWin = betAmount / 5 * 2;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(hostUuid).getName() + "§aが" + hostRole + "を出して親が" + hostWin + "円獲得しました!");
                    player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(players.get(p)).getName() + betAmount + " → " + childWin + " 円 ");
                    player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(hostUuid).getName() + " の " + roleQuote);
                }

                vaultAPI.deposit(players.get(p), childWin);
            }

            if (p ==players.size() - 1) {
                vaultAPI.deposit(hostUuid, hostWin);

                Finish finish = new Finish();
                finish.start();
            }


        }
    }
}
