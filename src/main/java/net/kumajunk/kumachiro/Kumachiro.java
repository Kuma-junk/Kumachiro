package net.kumajunk.kumachiro;

import org.bukkit.plugin.java.JavaPlugin;

public final class Kumachiro extends JavaPlugin {
    public static JavaPlugin kumachiro;
    public static boolean isEnable;
    public static int maxPlayers;
    public static int minBet;
    public static int maxBet;
    public static String pluginTitle = "§f§l[§e§lKumachiro§f§l]§r";
    public static VaultAPI vaultAPI;

    @Override
    public void onEnable() {
        // Plugin startup logic
        kumachiro = this;
        saveDefaultConfig();
        isEnable = getConfig().getBoolean("isEnable");
        maxPlayers = getConfig().getInt("maxPlayers") - 1;
        minBet = getConfig().getInt("minBet");
        maxBet = getConfig().getInt("maxBet");
        vaultAPI = new VaultAPI();
        getCommand("kcr").setExecutor(new Commands());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
