package id.luckynetwork.dev.lyrams.lej.listeners;

import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@AllArgsConstructor
public class ChatListener implements Listener {

    private final LuckyEssentials plugin;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (plugin.getMainConfigManager().isChatLocked()) {
            if (!Utils.checkPermission(event.getPlayer(), "chatlock.bypass", false, false, true, null)) {
                event.setCancelled(true);
            }
        }
    }
}
