package id.luckynetwork.dev.lyrams.lej.listeners;

import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class ConnectionListeners implements Listener {

    private final LuckyEssentials plugin;

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (event.getResult().equals(PlayerLoginEvent.Result.ALLOWED)) {
            boolean canJoin = plugin.getWhitelistManager().canJoin(event.getPlayer());
            if (!canJoin) {
                event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, plugin.getWhitelistManager().getDenyMessage());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin2(PlayerLoginEvent event) {
        plugin.getSlotsManager().checkJoin(event);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getConfirmationManager().deleteConfirmation(player);
    }
}
