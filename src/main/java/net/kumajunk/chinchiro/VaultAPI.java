package net.kumajunk.chinchiro;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import static org.bukkit.Bukkit.getServer;

public class VaultAPI {
    private static Economy economy = null;

    //VaultAPIのsetup
    private boolean setupEconomy() {
        Bukkit.getLogger().info("Setting up VaultAPI...");
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            Bukkit.getLogger().warning("Vault is not installed!");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            Bukkit.getLogger().warning("Cant get economy service!");
            return false;
        }
        economy = rsp.getProvider();
        Bukkit.getLogger().info("VaultAPI setup complete");
        return economy != null;
    }
    public static Economy getEconomy() {
        return economy;
    }
}
