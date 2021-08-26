package id.luckynetwork.dev.lyrams.lej.listeners;

import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.config.SlotsConfig;
import id.luckynetwork.dev.lyrams.lej.config.WhitelistConfig;
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
        if (WhitelistConfig.enabled && event.getResult() == PlayerLoginEvent.Result.ALLOWED) {
            Player player = event.getPlayer();
            boolean allowed = false;
            switch (WhitelistConfig.checkMode) {
                case UUID: {
                    allowed = WhitelistConfig.whitelistedList.stream().map(WhitelistConfig.WhitelistData::getUuid).anyMatch(it -> it.equals(player.getUniqueId().toString()));
                    break;
                }
                case NAME: {
                    allowed = WhitelistConfig.whitelistedList.stream().map(WhitelistConfig.WhitelistData::getName).anyMatch(it -> it.equals(player.getName()));
                    break;
                }
                case BOTH: {
                    allowed = WhitelistConfig.whitelistedList.stream().anyMatch(it -> it.getUuid().equals(player.getUniqueId().toString()) && it.getName().equals(player.getName()));
                    break;
                }
            }

            if (!allowed) {
                event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, WhitelistConfig.denyMessage);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin2(PlayerLoginEvent event) {
        if (SlotsConfig.enabled) {
            PlayerLoginEvent.Result result = event.getResult();
            if (result.equals(PlayerLoginEvent.Result.KICK_FULL) || result.equals(PlayerLoginEvent.Result.ALLOWED)) {
                int currentOnline = Bukkit.getOnlinePlayers().size();
                if (currentOnline < SlotsConfig.maxPlayers) {
                    event.allow();
                    return;
                }

                Player player = event.getPlayer();
                if (Utils.checkPermission(player, "join_full")) {
                    event.allow();
                } else {
                    event.disallow(PlayerLoginEvent.Result.KICK_FULL, SlotsConfig.denyMessage);
                }
            }
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getInvseeManager().close(player);
        plugin.getInvseeManager().getInvseers(player).forEach(it -> plugin.getInvseeManager().close(it));
    }
}
