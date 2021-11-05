package id.luckynetwork.dev.lyrams.lej.listeners;

import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.managers.whitelist.WhitelistData;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
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
        if (plugin.getWhitelistManager().isEnabled() && event.getResult() == PlayerLoginEvent.Result.ALLOWED) {
            Player player = event.getPlayer();
            boolean allowed = false;
            switch (plugin.getWhitelistManager().getCheckMode()) {
                case UUID: {
                    allowed = plugin.getWhitelistManager().getWhitelistedList().stream().map(WhitelistData::getUuid).anyMatch(it -> it.equals(player.getUniqueId().toString()));
                    break;
                }
                case NAME: {
                    allowed = plugin.getWhitelistManager().getWhitelistedList().stream().map(WhitelistData::getName).anyMatch(it -> it.equals(player.getName()));
                    break;
                }
                case BOTH: {
                    allowed = plugin.getWhitelistManager().getWhitelistedList().stream().anyMatch(it -> it.getUuid().equals(player.getUniqueId().toString()) && it.getName().equals(player.getName()));
                    break;
                }
            }

            if (!allowed) {
                event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, plugin.getWhitelistManager().getDenyMessage());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin2(PlayerLoginEvent event) {
        if (plugin.getSlotsManager().isEnabled()) {
            PlayerLoginEvent.Result result = event.getResult();
            if (result.equals(PlayerLoginEvent.Result.KICK_FULL) || result.equals(PlayerLoginEvent.Result.ALLOWED)) {
                int currentOnline = Bukkit.getOnlinePlayers().size();
                if (currentOnline < plugin.getSlotsManager().getMaxPlayers()) {
                    event.allow();
                    return;
                }

                Player player = event.getPlayer();
                if (Utils.checkPermission(player, "join_full")) {
                    event.allow();
                } else {
                    event.disallow(PlayerLoginEvent.Result.KICK_FULL, plugin.getSlotsManager().getDenyMessage());
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getConfirmationManager().deleteConfirmation(player);
    }
}
