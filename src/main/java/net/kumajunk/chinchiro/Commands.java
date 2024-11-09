package net.kumajunk.chinchiro;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.kumajunk.chinchiro.Chinchiro.*;

public class Commands implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String  label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("コンソールからは実行できません!!");
            return true;
        }
        if (label.equalsIgnoreCase("chinchiro")) {
            if (!sender.hasPermission("chinchiro.player")) {
                sender.sendMessage(pluginTitle + "§c§l権限がありません!");
                return true;
            }
            switch (args.length) {
                case 1 -> {
                    if (sender.hasPermission("chinchiro.op")) {
                        switch (args[0]) {
                            case "enable" -> {
                                if (isEnable) {
                                    sender.sendMessage(pluginTitle + "§cすでに有効になっています!");
                                    return true;
                                }
                                isEnable = true;
                                chinchiro.getConfig().set("isEnable", true);
                                chinchiro.saveConfig();
                                sender.sendMessage(pluginTitle + "§aコマンドが有効になりました!");
                                return true;
                            }
                            case "disable" -> {
                                if (!isEnable) {
                                    sender.sendMessage(pluginTitle + "§cすでに無効になっています!");
                                    return true;
                                }
                                isEnable = false;
                                chinchiro.getConfig().set("isEnable", false);
                                chinchiro.saveConfig();
                                sender.sendMessage(pluginTitle + "§aコマンドが無効になりました!");
                                return true;
                            }
                            case "reload" -> {
                                chinchiro.reloadConfig();
                                isEnable = chinchiro.getConfig().getBoolean("isEnable");
                                sender.sendMessage(pluginTitle + "§aコンフィグをリロードしました!");
                                return true;
                            }
                        }
                    }
                }
                default -> {
                    sender.sendMessage("§c");
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("chinchiro")) {
            if (!isEnable) {
                return null;
            }
            if (args.length == 1) {
                List<String> completions = Arrays.asList("open", "join", "leave", "help");

                if (sender.hasPermission("chinchiro.op")) {
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