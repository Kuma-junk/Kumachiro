package net.kumajunk.kumachiro;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static net.kumajunk.kumachiro.Kumachiro.*;

public class Commands implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("コンソールからは実行できません!!");
            return true;
        }
        if (label.equalsIgnoreCase("kcr")) {
            if (!sender.hasPermission("kcr.use")) {
                sender.sendMessage(pluginTitle + "§c§l権限がありません!");
                return true;
            }
            Player playerId = (Player) sender;
            switch (args.length) {
                case 0 -> sender.sendMessage(pluginTitle + "§a/kcr help でヘルプを表示します");

                case 1 -> {
                    if (sender.hasPermission("kcr.op")) {
                        switch (args[0]) {
                            case "enable" -> {
                                if (isEnable) {
                                    sender.sendMessage(pluginTitle + "§cすでに有効になっています!");
                                    return true;
                                }
                                isEnable = true;
                                kumachiro.getConfig().set("isEnable", true);
                                kumachiro.saveConfig();
                                sender.sendMessage(pluginTitle + "§aコマンドが有効になりました!");
                                return true;
                            }
                            case "disable" -> {
                                if (!isEnable) {
                                    sender.sendMessage(pluginTitle + "§cすでに無効になっています!");
                                    return true;
                                }
                                isEnable = false;
                                kumachiro.getConfig().set("isEnable", false);
                                kumachiro.saveConfig();
                                sender.sendMessage(pluginTitle + "§aコマンドが無効になりました!");
                                return true;
                            }
                            case "reload" -> {
                                kumachiro.reloadConfig();
                                isEnable = kumachiro.getConfig().getBoolean("isEnable");
                                maxPlayers = kumachiro.getConfig().getInt("maxPlayers") - 1;
                                minBet = kumachiro.getConfig().getInt("minBet");
                                maxBet = kumachiro.getConfig().getInt("maxBet");
                                sender.sendMessage(pluginTitle + "§aコンフィグをリロードしました!");
                                return true;
                            }
                        }
                    }
                    switch (args[0]) {
                        case "open" -> {
                            if (!isEnable) {
                                sender.sendMessage(pluginTitle + "§c§l現在コマンドは無効になっています!");
                                return true;
                            }
                            if (Game.inProgress) {
                                sender.sendMessage(pluginTitle + "§c§lすでにゲームが開始されています!");
                                return true;
                            }
                            sender.sendMessage(pluginTitle + "§a§l/kcr open [金額] [人数] でゲームを開始できます!");
                        }
                        case "join" -> {
                            if (!isEnable) {
                                sender.sendMessage(pluginTitle + "§c§l現在コマンドは無効になっています!");
                                return true;
                            }
                            if (!Game.inProgress) {
                                sender.sendMessage(pluginTitle + "§c§lゲームが開始されていません!");
                                return true;
                            }
                            if (Game.players.size() >= Game.playerCount) {
                                sender.sendMessage(pluginTitle + "§c§l参加者が上限に達しています!");
                                return true;
                            }
                            if (Game.players.contains(playerId.getUniqueId())) {
                                sender.sendMessage(pluginTitle + "§c§lすでに参加しています!");
                                return true;
                            }
                            if (Game.hostUuid.equals(playerId.getUniqueId())) {
                                sender.sendMessage(pluginTitle + "§c§l親としてすでに参加しています!");
                                return true;
                            }
                            if (vaultAPI.getBalance(playerId.getUniqueId()) < Game.betAmount) {
                                sender.sendMessage(pluginTitle + "§c§lお金が足りません!");
                                return true;
                            }
                            vaultAPI.withdraw(playerId.getUniqueId(), Game.betAmount);

                            Game.players.add(playerId.getUniqueId());
                            sender.sendMessage(pluginTitle + "§a§lゲームに参加しました!");
                            sender.sendMessage(pluginTitle + "§a§l現在の参加者数: " + Game.players.size() + "人");
                            sender.sendMessage(pluginTitle + "§c§l注意: ゲームが終了する前にログアウトすると賭け金が消失します!");
                            for (UUID playerUuid : Game.players) {
                                Player player = Bukkit.getPlayer(playerUuid);
                                if (player != null){
                                    player.sendMessage(pluginTitle + "§a§l" + playerId.getName() + "が部屋に参加しました!");
                                }
                            }
                        }
                        case "leave" -> {
                            if (!isEnable) {
                                sender.sendMessage(pluginTitle + "§c§l現在コマンドは無効になっています!");
                                return true;
                            }
                            if (!Game.inProgress) {
                                sender.sendMessage(pluginTitle + "§c§lゲームが開始されていません!");
                                return true;
                            }
                            if (!Game.players.contains(playerId.getUniqueId())) {
                                sender.sendMessage(pluginTitle + "§c§lあなたは参加していません!");
                                return true;
                            }
                            vaultAPI.deposit(playerId.getUniqueId(), Game.betAmount);
                            Game.players.remove(playerId.getUniqueId());
                            sender.sendMessage(pluginTitle + "§a§lゲームから退出しました!");
                        }
                        case "help" -> {
                            sender.sendMessage(pluginTitle + "§a=======§f§l[§e§lKumachiro§f§l]§a=======");
                            sender.sendMessage(pluginTitle + "§a/kcr help: このヘルプを表示します");
                            sender.sendMessage(pluginTitle + "§a/kcr open: ゲームを開始します");
                            sender.sendMessage(pluginTitle + "§a/kcr join: ゲームに参加します");
                            sender.sendMessage(pluginTitle + "§a/kcr leave: ゲームから退出します");
                            if (sender.hasPermission("kcr.op")) {
                                sender.sendMessage(pluginTitle + "§a/kcr enable: コマンドを有効にします");
                                sender.sendMessage(pluginTitle + "§a/kcr disable: コマンドを無効にします");
                                sender.sendMessage(pluginTitle + "§a/kcr reload: コンフィグをリロードします");
                            }
                            if (Game.inProgress) {
                                sender.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(Game.hostUuid).getName() + "§aの部屋");
                                sender.sendMessage(pluginTitle + "§a現在の参加者数: " + Game.players.size() + " 人 ");
                                int playersAllowed = Game.playerCount - Game.players.size();
                                sender.sendMessage(pluginTitle + "§a必要金額: " + Game.betAmount + " 円 " + " 残り参加可能人数: " + playersAllowed + " 人 ");
                            }
                        }
                    }
                }
                case 3 -> {
                    if (!isEnable) {
                        sender.sendMessage(pluginTitle + "§c§l現在コマンドは無効になっています!");
                        return true;
                    }
                    if (!args[0].equalsIgnoreCase("open")) {
                        sender.sendMessage(pluginTitle + "§c§lコマンドが違います! /kcr help でヘルプを表示");
                        return true;
                    }
                    if (Game.inProgress) {
                        sender.sendMessage(pluginTitle + "§c§lすでにゲームが開始されています!");
                        return true;
                    }
                    if (Integer.parseInt(args[1]) < minBet) {
                        sender.sendMessage(pluginTitle + "§c§l最低金額は" + minBet + "です!");
                        return true;
                    }
                    if (Integer.parseInt(args[1]) > maxBet) {
                        sender.sendMessage(pluginTitle + "§c§l最高金額は" + maxBet + "です!");
                        return true;
                    }
                    if (Integer.parseInt(args[2]) > 2) {
                        sender.sendMessage(pluginTitle + "§c§l参加人数は2");
                    }
                    if (Integer.parseInt(args[2]) > maxPlayers) {
                        sender.sendMessage(pluginTitle + "§c§l最大参加可能人数は" + maxPlayers + "です!");
                        return true;
                    }
                    int needHostMoney = Integer.parseInt(args[1]) * Integer.parseInt(args[2]);
                    if (vaultAPI.getBalance(playerId.getUniqueId()) < needHostMoney) {
                        sender.sendMessage(pluginTitle + "§c§lお金が足りません!");
                        return true;
                    }
                    vaultAPI.withdraw(playerId.getUniqueId(), needHostMoney);

                    Game.betAmount = Integer.parseInt(args[1]);
                    Game.playerCount = Integer.parseInt(args[2]);
                    Game.hostUuid = playerId.getUniqueId();
                    Game.inProgress = true;

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(pluginTitle + "§a§l" + playerId.getName() + "によってゲームが開始されました!");
                        player.sendMessage(pluginTitle + "§a§l" + "必要金額: " + Game.betAmount + " 円 " + " 子の人数: " + Game.playerCount + "人");
                        player.sendMessage(pluginTitle + "§c§l注意: ゲームが終了する前にログアウトすると賭け金が消失します!");
                    }

                    Game game = new Game();
                    game.start();

                    return true;
                }
                default -> {
                    sender.sendMessage(pluginTitle + "§c§lコマンドが違います! /kcr help でヘルプを表示");
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("kcr")) {
            if (!isEnable) {
                return null;
            }
            if (args.length == 1) {
                List<String> completions = new ArrayList<>(Arrays.asList("open", "join", "leave", "help"));

                if (sender.hasPermission("kcr.op")) {
                    completions.add("enable");
                    completions.add("disable");
                    completions.add("reload");
                }

                List<String> result = new ArrayList<>();
                for (String completion : completions) {
                    if (completion.toLowerCase().startsWith(args[0].toLowerCase())) {
                        result.add(completion);
                    }
                }
                return result;
            }
            return null;
        }
        return null;
    }

}