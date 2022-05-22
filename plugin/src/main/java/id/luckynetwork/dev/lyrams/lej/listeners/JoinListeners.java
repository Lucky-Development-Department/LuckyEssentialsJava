package id.luckynetwork.dev.lyrams.lej.listeners;

import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@AllArgsConstructor
public class JoinListeners implements Listener {

    private LuckyEssentials plugin;
    private final boolean flyOnJoin = plugin.getMainConfig().getBoolean("fly-on-join");

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (Utils.checkPermission(e.getPlayer(), "fly")) {
            if (flyOnJoin) {
                e.getPlayer().setAllowFlight(true);
            }
        }
    }

}
