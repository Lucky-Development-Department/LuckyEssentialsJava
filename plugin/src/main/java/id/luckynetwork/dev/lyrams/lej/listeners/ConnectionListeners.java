package id.luckynetwork.dev.lyrams.lej.listeners;

import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListeners implements Listener {

    private final LuckyEssentials plugin;
    private final boolean flyOnJoin;

    public ConnectionListeners(LuckyEssentials plugin) {
        this.plugin = plugin;
        this.flyOnJoin = plugin.getMainConfig().getBoolean("fly-on-join");
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (event.getResult().equals(PlayerLoginEvent.Result.ALLOWED)) {
            Player player = event.getPlayer();
            boolean canJoin = plugin.getWhitelistManager().canJoin(player);
            if (!canJoin) {
                event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, plugin.getWhitelistManager().getDenyMessage());
                return;
            }

            if (flyOnJoin) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (player != null) {
                        if (Utils.checkPermission(player, "fly.on-join", false, false, true, null)) {
                            player.setAllowFlight(true);
                        }
                    }
                }, 3L);
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
