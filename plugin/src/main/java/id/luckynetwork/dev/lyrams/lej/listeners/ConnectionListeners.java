package id.luckynetwork.dev.lyrams.lej.listeners;

import id.luckynetwork.dev.lyrams.lej.config.WhitelistConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class ConnectionListeners implements Listener {

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (WhitelistConfig.whitelisted && event.getResult() == PlayerLoginEvent.Result.ALLOWED) {
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
}
