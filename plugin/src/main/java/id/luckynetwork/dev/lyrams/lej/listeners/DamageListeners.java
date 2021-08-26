package id.luckynetwork.dev.lyrams.lej.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListeners implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity != null && entity.getType() == EntityType.PLAYER) {
            Player player = (Player) entity;
            if (player.hasMetadata("GOD")) {
                event.setCancelled(true);
            }
        }
    }
}
