package net.kumajunk.kumachiro;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class VaultAPI {
    private static Economy economy = null;

    public VaultAPI() {
        setupEconomy();
    }

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

    public double getBalance(UUID uuid) {
        return economy.getBalance(Bukkit.getOfflinePlayer(uuid));
    }

    public void deposit(UUID uuid, double money) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
        if(p == null){
            Bukkit.getLogger().info(uuid.toString()+"は見つからない");
            return;
        }
        EconomyResponse resp = economy.depositPlayer(p,money);
        if(resp.transactionSuccess()){
            if(p.isOnline()){
                p.getPlayer().sendMessage(ChatColor.YELLOW + "電子マネー$"+money+"受取りました");
            }
        }
    }

    public void withdraw(UUID uuid, double money) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
        if(p == null){
            Bukkit.getLogger().info(uuid.toString()+"は見つからない");
            return;
        }
        EconomyResponse resp = economy.withdrawPlayer(p,money);
        if(resp.transactionSuccess()){
            if(p.isOnline()) {
                p.getPlayer().sendMessage(ChatColor.YELLOW + "電子マネー$" + money + "支払いました");
            }
        }
    }
}
