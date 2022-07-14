package id.luckynetwork.dev.lyrams.lej.listeners.trolls;

import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.enums.TrollType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TrollListeners {

    public TrollListeners(LuckyEssentials plugin) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new EntityDamage(), plugin);
        pluginManager.registerEvents(new BlockPlace(plugin), plugin);
        pluginManager.registerEvents(new Movement(plugin), plugin);

        String version = Utils.getNmsVersion();
        switch (version) {
            case "v1_8_R3":
            case "v1_9_R1":
            case "v1_9_R2":
            case "v1_10_R1":
            case "v1_11_R1":
            case "v1_12_R1": {
                pluginManager.registerEvents(new ItemPickup(), plugin);
                pluginManager.registerEvents(new BlockBreak(plugin), plugin);
                break;
            }
            default: {
                plugin.getLogger().info("ItemPickup troll and BlockBreak troll is disabled on this version due to deprecated method");
                break;
            }
        }
    }

    public static class ItemPickup implements Listener {
        @SuppressWarnings("deprecation")
        @EventHandler
        public void onItemPickup(PlayerPickupItemEvent event) {
            Player player = event.getPlayer();
            if (player.hasMetadata(TrollType.NO_PICKUP.getMetadataKey())) {
                event.setCancelled(true);
            }
        }
    }

    public static class EntityDamage implements Listener {
        @EventHandler
        public void onEntityDamage(EntityDamageEvent event) {
            Entity victim = event.getEntity();
            if (victim != null && event.getEntityType() == EntityType.PLAYER) {
                Player player = (Player) victim;
                if (player.hasMetadata(TrollType.ONE_TAP.getMetadataKey())) {
                    player.setHealth(0.0);
                }
            }
        }

        @EventHandler
        public void onDamageByEntity(EntityDamageByEntityEvent event) {
            Entity damager = event.getDamager();
            if (damager != null && damager.getType() == EntityType.PLAYER) {
                if (damager.hasMetadata(TrollType.NO_DAMAGE.getMetadataKey())) {
                    event.setDamage(0.0);
                }
                if (damager.hasMetadata(TrollType.NO_HIT.getMetadataKey())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @AllArgsConstructor
    public static class BlockPlace implements Listener {

        private final LuckyEssentials plugin;

        @EventHandler
        public void onPlace(BlockPlaceEvent event) {
            Player player = event.getPlayer();
            if (player.hasMetadata(TrollType.NO_PLACE.getMetadataKey())) {
                event.setCancelled(true);
            }
            if (player.hasMetadata(TrollType.FAKE_PLACE.getMetadataKey())) {
                Block block = event.getBlock();
                Bukkit.getScheduler().runTaskLater(plugin, () -> block.setType(Material.AIR), 17L);
            }
        }
    }

    @AllArgsConstructor
    public static class BlockBreak implements Listener {

        private final LuckyEssentials plugin;

        @SuppressWarnings("deprecation")
        @EventHandler
        public void onBreak(BlockBreakEvent event) {
            Player player = event.getPlayer();
            if (player.hasMetadata(TrollType.NO_BREAK.getMetadataKey())) {
                event.setCancelled(true);
            }
            if (player.hasMetadata(TrollType.FAKE_BREAK.getMetadataKey())) {
                Block block = event.getBlock();
                byte data = block.getData();
                Material type = block.getType();
                Location location = block.getLocation();

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    location.getWorld().getBlockAt(location).setType(type);
                    location.getWorld().getBlockAt(location).setData(data);
                }, 17L);
            }
        }
    }

    @RequiredArgsConstructor
    public static class Movement implements Listener {

        private final LuckyEssentials plugin;
        private final List<Player> pendingTeleportList = new ArrayList<>();

        @EventHandler
        public void onMove(PlayerMoveEvent event) {
            Player player = event.getPlayer();
            if (player.hasMetadata(TrollType.LAGBACK.getMetadataKey())) {
                if (!this.pendingTeleportList.contains(player)) {
                    this.pendingTeleportList.add(player);

                    Location location = player.getLocation();
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        player.teleport(location);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            this.pendingTeleportList.remove(player);
                        }, ThreadLocalRandom.current().nextInt(15, 31));
                    }, ThreadLocalRandom.current().nextInt(15, 21));
                }
            }
            if (player.hasMetadata(TrollType.STICKY.getMetadataKey())) {
                Location location = player.getLocation();
                Block highest = location.getWorld().getHighestBlockAt(location.getBlockX(), location.getBlockZ());
                if (location.getBlockY() > highest.getY() + 1.5) {
                    Location newLocation = location.clone();
                    newLocation.setY(highest.getY());
                    player.teleport(newLocation);
                }
            }
        }
    }
}
