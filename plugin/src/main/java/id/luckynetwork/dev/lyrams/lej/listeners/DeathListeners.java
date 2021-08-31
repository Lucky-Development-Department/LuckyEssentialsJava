package id.luckynetwork.dev.lyrams.lej.listeners;

import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

@AllArgsConstructor
public class DeathListeners implements Listener {

    private final LuckyEssentials plugin;

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (plugin.getMainConfigManager().isDiscouraged()) {
            Player player = event.getEntity();
            if (!Utils.checkPermission(player, "discouraged.bypass", false, false, true, null)) {
                player.kickPlayer(plugin.getWhitelistManager().getDenyMessage());
            }
        }
    }
}
