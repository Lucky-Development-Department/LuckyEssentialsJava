package id.luckynetwork.dev.lyrams.lej.listeners;

import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.utils.InvseeUtils;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@AllArgsConstructor
public class InvseeListeners implements Listener {

    private final LuckyEssentials plugin;

    @EventHandler
    public void onInventoryInteract(PlayerInteractAtEntityEvent event) {
        Entity rightClicked = event.getRightClicked();
        if (rightClicked.getType() == EntityType.PLAYER) {
            Player player = event.getPlayer();
            if (player.hasMetadata("vanished") && Utils.checkPermission(player, "invsee", false, false, false, null)) {
                InvseeUtils.invsee(player, (Player) rightClicked);
                Utils.applyMetadata(player, "INVSEE", true);
            }
        }
    }

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
                } else if (InvseeUtils.separatorSlots.contains(event.getSlot())) {
                    event.setCancelled(true);
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
                InvseeUtils.refresh(refreshPlayer, ownerPlayer, topInventory);
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

        Utils.removeMetadata(player, "INVSEE");
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, player::updateInventory, 1L);
    }
}
