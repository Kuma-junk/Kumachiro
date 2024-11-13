package net.kumajunk.kumachiro;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static net.kumajunk.kumachiro.Kumachiro.*;

public class Finish extends Thread{
    @Override
    public void run() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(pluginTitle + Bukkit.getOfflinePlayer(Game.hostUuid).getName() + "§cのゲームが終了しました。");
            player.sendMessage(pluginTitle + "結果" + Bukkit.getOfflinePlayer(Game.hostUuid).getName()+ " " + Game.betAmount * Game.playerCount + " → " + Game.hostWin + "円 ");
        }

        Game.inProgress = false;
        Game.players.clear();
        Game.betAmount = 0;
        Game.hostUuid = null;
        Game.playerCount = 0;
    }
}
