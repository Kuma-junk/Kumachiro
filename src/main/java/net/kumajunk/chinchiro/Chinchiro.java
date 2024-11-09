package net.kumajunk.chinchiro;

import org.bukkit.plugin.java.JavaPlugin;

public final class Chinchiro extends JavaPlugin {
    public static boolean isEnable;
    public static String pluginTitle = "§f§l[§e§lChinchiro§f§l]§r";
    public static JavaPlugin chinchiro;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        isEnable = getConfig().getBoolean("isEnable");
        getCommand("chinchiro").setExecutor(new Commands());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
