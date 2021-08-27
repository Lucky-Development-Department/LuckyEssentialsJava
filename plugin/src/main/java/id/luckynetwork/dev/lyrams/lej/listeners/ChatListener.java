package id.luckynetwork.dev.lyrams.lej.listeners;

import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
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
            event.setCancelled(true);
        }
    }
}
