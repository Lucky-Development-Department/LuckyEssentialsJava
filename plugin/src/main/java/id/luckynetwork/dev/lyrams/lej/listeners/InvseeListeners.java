package id.luckynetwork.dev.lyrams.lej.listeners;

import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class InvseeListeners implements Listener {

    private final LuckyEssentials plugin;

    public InvseeListeners(LuckyEssentials plugin) {
        this.plugin = plugin;

        PluginManager pluginManager = Bukkit.getPluginManager();
        if (plugin.getMainConfigManager().isOldInvsee()) {
            pluginManager.registerEvents(new OldInvseeListeners(plugin), plugin);
        } else {
            pluginManager.registerEvents(new NewInvseeListeners(plugin), plugin);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        Entity rightClicked = event.getRightClicked();
        if (rightClicked.getType() == EntityType.PLAYER) {
            Player player = event.getPlayer();
            if (player.hasMetadata("vanished") && Utils.checkPermission(player, "invsee", false, false, false, null)) {
                plugin.getInvseeManager().invsee(player, (Player) rightClicked);
            }
        }
    }

    @AllArgsConstructor
    public static class OldInvseeListeners implements Listener {

        private final LuckyEssentials plugin;

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            InventoryView view = event.getView();
            Inventory topInventory = view.getTopInventory();
            InventoryType type = topInventory.getType();

            Player refreshPlayer = null;
            if (type.equals(InventoryType.PLAYER)) {
                Player whoClicked = (Player) event.getWhoClicked();
                InventoryHolder inventoryOwner = topInventory.getHolder();
                if (!(topInventory.getHolder() instanceof Player)) {
                    return;
                }

                Player ownerPlayer = (Player) inventoryOwner;

                if (whoClicked.hasMetadata("INVSEE")) {
                    refreshPlayer = whoClicked;
                    if (!ownerPlayer.isOnline() || !Utils.checkPermission(whoClicked, "invsee.modify", false, false, false, null)) {
                        event.setCancelled(true);
                    } else {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, ownerPlayer::updateInventory, 1L);
                    }
                }
            }

            if (refreshPlayer != null) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, refreshPlayer::updateInventory, 1L);
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event) {
            Inventory topInventory = event.getView().getTopInventory();
            InventoryType type = topInventory.getType();
            Player player = (Player) event.getPlayer();

            if (type == InventoryType.PLAYER) {
                Utils.removeMetadata(player, "INVSEE");
            } else if (type == InventoryType.CHEST) {
                InventoryHolder holder = topInventory.getHolder();
                if (!(holder instanceof Player)) {
                    return;
                }

                Utils.removeMetadata(player, "INVSEE");
            } else {
                return;
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, player::updateInventory, 1L);
        }

    }

    @AllArgsConstructor
    public static class NewInvseeListeners implements Listener {

        private final LuckyEssentials plugin;

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            InventoryView view = event.getView();
            Inventory topInventory = view.getTopInventory();
            InventoryType type = topInventory.getType();

            Player refreshPlayer = null;
            if (type == InventoryType.CHEST && topInventory.getSize() == 54) {
                HumanEntity whoClicked = event.getWhoClicked();
                InventoryHolder inventoryOwner = topInventory.getHolder();
                if (!(inventoryOwner instanceof HumanEntity)) {
                    return;
                }

                Player ownerPlayer = (Player) inventoryOwner;
                if (whoClicked.hasMetadata("INVSEE")) {
                    refreshPlayer = (Player) whoClicked;
                    if (!ownerPlayer.isOnline() || !Utils.checkPermission(whoClicked, "invsee.modify", false, false, false, null)) {
                        event.setCancelled(true);
                    } else if (plugin.getInvseeManager().getSeparatorSlots().contains(event.getSlot())) {
                        event.setCancelled(true);
                        if (event.getSlot() == 51) {
                            if (event.isRightClick()) {
                                if (((Player) whoClicked).isFlying()) {
                                    Location ownerPlayerLocation = ownerPlayer.getLocation();
                                    Vector inverse = ownerPlayerLocation.getDirection().normalize().multiply(-1);

                                    Location location = ownerPlayer.getLocation().clone().add(inverse);
                                    location.setY(ownerPlayerLocation.getY() + 5);
                                    location.setPitch(45f);
                                    whoClicked.teleport(location);

                                    ((Player) whoClicked).setFlying(true);
                                    whoClicked.setVelocity(new Vector(0, 0, 0));
                                } else {
                                    Location ownerPlayerLocation = ownerPlayer.getLocation();
                                    Vector inverse = ownerPlayerLocation.getDirection().normalize().multiply(-1);

                                    Location location = ownerPlayer.getLocation().clone().add(inverse);
                                    whoClicked.teleport(location);
                                }
                            } else {
                                whoClicked.teleport(ownerPlayer);
                            }
                            plugin.getInvseeManager().invsee((Player) whoClicked, ownerPlayer);
                        }
                    } else {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                            ItemStack[] contents = Arrays.copyOfRange(topInventory.getContents(), 0, 35);
                            ownerPlayer.getInventory().setContents(contents);

                            ItemStack[] armorContents = Arrays.copyOfRange(topInventory.getContents(), 45, 48);
                            ownerPlayer.getInventory().setArmorContents(armorContents);
                            ownerPlayer.updateInventory();
                        }, 1L);
                    }
                }

                if (refreshPlayer != null) {
                    plugin.getInvseeManager().refresh(refreshPlayer, ownerPlayer, topInventory);
                }
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event) {
            Inventory topInventory = event.getView().getTopInventory();
            InventoryType type = topInventory.getType();
            Player player = (Player) event.getPlayer();

            if (type != InventoryType.CHEST || topInventory.getSize() != 54) {
                return;
            }
            if (!(topInventory.getHolder() instanceof Player)) {
                return;
            }

            plugin.getInvseeManager().close(player);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, player::updateInventory, 1L);
        }

        @EventHandler
        public void onDisconnect(PlayerQuitEvent event) {
            Player player = event.getPlayer();
            plugin.getInvseeManager().close(player);
            plugin.getInvseeManager().getInvseers(player).forEach(it -> plugin.getInvseeManager().close(it));
        }
    }

}
