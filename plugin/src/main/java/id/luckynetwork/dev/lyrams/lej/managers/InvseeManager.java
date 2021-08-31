package id.luckynetwork.dev.lyrams.lej.managers;

import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.utils.ItemBuilder;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public class InvseeManager {

    private final LuckyEssentials plugin;
    private final List<Integer> separatorSlots = Arrays.asList(36, 37, 38, 39, 40, 41, 42, 43, 44, 49, 50, 51, 52, 53);
    private final Map<Player, List<Player>> invseeMap = new HashMap<>();
    private final ItemStack separatorItem;

    private final ItemStack infoItem;
    private final ItemStack locationItem;
    private final ItemStack effectsItem;

    public InvseeManager(LuckyEssentials plugin) {
        this.plugin = plugin;
        this.separatorItem = LuckyEssentials.getInstance().getVersionSupport().getItemByName("blackglasspane", 1, 0);
        this.infoItem = LuckyEssentials.getInstance().getVersionSupport().getItemByName("paper", 1, 0);
        this.locationItem = LuckyEssentials.getInstance().getVersionSupport().getItemByName("compass", 1, 0);
        this.effectsItem = LuckyEssentials.getInstance().getVersionSupport().getItemByName("brewing_stand_item", 1, 0);

        Bukkit.getScheduler().runTaskTimer(plugin, () -> invseeMap.forEach((key, value) -> value.forEach(invseer -> this.refresh(invseer, key))), 1L, 10L);
    }

    public void addInvseer(Player player, Player invseer) {
        if (invseeMap.containsKey(player)) {
            invseeMap.get(player).add(invseer);
            return;
        }

        List<Player> invseers = new ArrayList<>();
        invseers.add(invseer);
        invseeMap.put(player, invseers);
    }

    public void removeInvseer(Player player, Player invseer) {
        if (!invseeMap.containsKey(player)) {
            return;
        }

        invseeMap.get(player).remove(invseer);
        if (invseeMap.get(player).size() < 1) {
            invseeMap.remove(player);
        }
    }

    public List<Player> getInvseers(Player player) {
        return invseeMap.getOrDefault(player, new ArrayList<>());
    }

    public void invsee(Player player, Player target) {
        Utils.applyMetadata(player, "INVSEE", true);
        this.addInvseer(target, player);

        Inventory inventory = Bukkit.createInventory(target, 54, "§e" + target.getName() + "'s inventory");
        inventory.setContents(target.getInventory().getContents());

        separatorSlots.forEach(it -> inventory.setItem(it, separatorItem));

        int armorSlot = 45;
        for (ItemStack armorContent : target.getInventory().getArmorContents()) {
            inventory.setItem(armorSlot, armorContent);
            armorSlot++;
        }

        inventory.setItem(49, plugin.getVersionSupport().getItemInHand(target));

        Location location = target.getLocation();
        String distanceString = "§cUnknown";
        if (location.getWorld() == player.getLocation().getWorld()) {
            distanceString = String.valueOf(location.distanceSquared(player.getLocation()));
        }
        inventory.setItem(51, new ItemBuilder(locationItem.getType())
                .setName("§d" + target.getName() + "§e's location:")
                .addLoreLine("§8├─ §eWorld: §a" + location.getWorld())
                .addLoreLine("§8├─ §eX: §a" + location.getX())
                .addLoreLine("§8├─ §eY: §a" + location.getY())
                .addLoreLine("§8├─ §eZ: §a" + location.getZ())
                .addLoreLine("§8├─ §eYaw: §a" + location.getYaw())
                .addLoreLine("§8├─ §ePitch: §a" + location.getPitch())
                .addLoreLine("§8└─ §eDistance: §a" + distanceString)
                .toItemStack());

        inventory.setItem(52, new ItemBuilder(infoItem.getType())
                .setName("§d" + target.getName() + "§e's info:")
                .addLoreLine("§8├─ §eDisplayName: §a" + target.getDisplayName())
                .addLoreLine("§8├─ §eHealth: §a" + target.getHealth())
                .addLoreLine("§8├─ §eFood: §a" + target.getFoodLevel())
                .addLoreLine("§8├─ §eSaturation: §a" + target.getSaturation())
                .addLoreLine("§8├─ §eGamemode: §a" + target.getGameMode().name())
                .addLoreLine("§8├─ §eLevel: §a" + target.getLevel())
                .addLoreLine("§8├─ §eExp: §a" + target.getTotalExperience())
                .addLoreLine("§8├─ §eCan Fly: §a" + target.getAllowFlight())
                .addLoreLine("§8├─ §eIs Flying: §a" + target.isFlying())
                .addLoreLine("§8├─ §eIs On-Ground: §a" + target.isOnGround())
                .addLoreLine("§8├─ §eIs Sneaking: §a" + target.isSneaking())
                .addLoreLine("§8├─ §eIs Sprinting: §a" + target.isSprinting())
                .addLoreLine("§8├─ §eWalk Speed: §a" + target.getWalkSpeed())
                .addLoreLine("§8└─ §eFly Speed: §a" + target.getFlySpeed())
                .toItemStack());

        ItemBuilder activeEffects = new ItemBuilder(effectsItem.getType()).setName("§d" + target.getName() + "§e's active potion effects:");
        List<String> effects = this.getActiveEffects(target);
        if (!effects.isEmpty()) {
            effects.forEach(activeEffects::addLoreLine);
        }
        inventory.setItem(53, activeEffects.toItemStack());

        player.closeInventory();
        player.openInventory(inventory);
        player.updateInventory();
    }

    public void close(Player player) {
        Utils.removeMetadata(player, "INVSEE");

        final Player[] toBeRemoved = {null};
        invseeMap.forEach((key, value) -> {
            if (value.contains(player)) {
                toBeRemoved[0] = key;
            }
        });

        if (toBeRemoved[0] != null) {
            this.removeInvseer(toBeRemoved[0], player);
        }
    }

    public void refresh(Player player, Player target, Inventory topInventory) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            player.updateInventory();

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                int armorSlot = 45;
                for (ItemStack armorContent : target.getInventory().getArmorContents()) {
                    topInventory.setItem(armorSlot, armorContent);
                    armorSlot++;
                }

                topInventory.setItem(49, plugin.getVersionSupport().getItemInHand(target));

                Location location = target.getLocation();
                String distanceString = "§cUnknown";
                if (location.getWorld() == player.getLocation().getWorld()) {
                    distanceString = String.valueOf(location.distanceSquared(player.getLocation()));
                }
                topInventory.setItem(51, new ItemBuilder(locationItem.getType())
                        .setName("§d" + target.getName() + "§e's location:")
                        .addLoreLine("§8├─ §eWorld: §a" + location.getWorld())
                        .addLoreLine("§8├─ §eX: §a" + location.getX())
                        .addLoreLine("§8├─ §eY: §a" + location.getY())
                        .addLoreLine("§8├─ §eZ: §a" + location.getZ())
                        .addLoreLine("§8├─ §eYaw: §a" + location.getYaw())
                        .addLoreLine("§8├─ §ePitch: §a" + location.getPitch())
                        .addLoreLine("§8└─ §eDistance: §a" + distanceString)
                        .toItemStack());

                topInventory.setItem(52, new ItemBuilder(this.infoItem.getType())
                        .setName("§d" + target.getName() + "§e's info:")
                        .addLoreLine("§8├─ §eDisplayName: §a" + target.getDisplayName())
                        .addLoreLine("§8├─ §eHealth: §a" + target.getHealth())
                        .addLoreLine("§8├─ §eFood: §a" + target.getFoodLevel())
                        .addLoreLine("§8├─ §eSaturation: §a" + target.getSaturation())
                        .addLoreLine("§8├─ §eGamemode: §a" + target.getGameMode().name())
                        .addLoreLine("§8├─ §eLevel: §a" + target.getLevel())
                        .addLoreLine("§8├─ §eExp: §a" + target.getTotalExperience())
                        .addLoreLine("§8├─ §eCan Fly: §a" + target.getAllowFlight())
                        .addLoreLine("§8├─ §eIs Flying: §a" + target.isFlying())
                        .addLoreLine("§8├─ §eIs On-Ground: §a" + target.isOnGround())
                        .addLoreLine("§8├─ §eIs Sneaking: §a" + target.isSneaking())
                        .addLoreLine("§8├─ §eIs Sprinting: §a" + target.isSprinting())
                        .addLoreLine("§8├─ §eWalk Speed: §a" + target.getWalkSpeed())
                        .addLoreLine("§8└─ §eFly Speed: §a" + target.getFlySpeed())
                        .toItemStack());

                ItemBuilder activeEffects = new ItemBuilder(this.effectsItem.getType()).setName("§d" + target.getName() + "§e's active potion effects:");
                List<String> effects = this.getActiveEffects(target);
                if (!effects.isEmpty()) {
                    effects.forEach(activeEffects::addLoreLine);
                }
                topInventory.setItem(53, activeEffects.toItemStack());
                player.updateInventory();
            }, 1L);
        }, 1L);
    }

    public void refresh(Player player, Player target) {
        Inventory topInventory = player.getOpenInventory().getTopInventory();
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            player.updateInventory();

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                topInventory.setContents(target.getInventory().getContents());

                separatorSlots.forEach(it -> topInventory.setItem(it, separatorItem));

                int armorSlot = 45;
                for (ItemStack armorContent : target.getInventory().getArmorContents()) {
                    topInventory.setItem(armorSlot, armorContent);
                    armorSlot++;
                }

                topInventory.setItem(49, plugin.getVersionSupport().getItemInHand(target));

                Location location = target.getLocation();
                String distanceString = "§cUnknown";
                if (location.getWorld() == player.getLocation().getWorld()) {
                    distanceString = String.valueOf(location.distanceSquared(player.getLocation()));
                }
                topInventory.setItem(51, new ItemBuilder(locationItem.getType())
                        .setName("§d" + target.getName() + "§e's location:")
                        .addLoreLine("§8├─ §eWorld: §a" + location.getWorld())
                        .addLoreLine("§8├─ §eX: §a" + location.getX())
                        .addLoreLine("§8├─ §eY: §a" + location.getY())
                        .addLoreLine("§8├─ §eZ: §a" + location.getZ())
                        .addLoreLine("§8├─ §eYaw: §a" + location.getYaw())
                        .addLoreLine("§8├─ §ePitch: §a" + location.getPitch())
                        .addLoreLine("§8└─ §eDistance: §a" + distanceString)
                        .toItemStack());

                topInventory.setItem(52, new ItemBuilder(this.infoItem.getType())
                        .setName("§d" + target.getName() + "§e's info:")
                        .addLoreLine("§8├─ §eDisplayName: §a" + target.getDisplayName())
                        .addLoreLine("§8├─ §eHealth: §a" + target.getHealth())
                        .addLoreLine("§8├─ §eFood: §a" + target.getFoodLevel())
                        .addLoreLine("§8├─ §eSaturation: §a" + target.getSaturation())
                        .addLoreLine("§8├─ §eGamemode: §a" + target.getGameMode().name())
                        .addLoreLine("§8├─ §eLevel: §a" + target.getLevel())
                        .addLoreLine("§8├─ §eExp: §a" + target.getTotalExperience())
                        .addLoreLine("§8├─ §eCan Fly: §a" + target.getAllowFlight())
                        .addLoreLine("§8├─ §eIs Flying: §a" + target.isFlying())
                        .addLoreLine("§8├─ §eIs On-Ground: §a" + target.isOnGround())
                        .addLoreLine("§8├─ §eIs Sneaking: §a" + target.isSneaking())
                        .addLoreLine("§8├─ §eIs Sprinting: §a" + target.isSprinting())
                        .addLoreLine("§8├─ §eWalk Speed: §a" + target.getWalkSpeed())
                        .addLoreLine("§8└─ §eFly Speed: §a" + target.getFlySpeed())
                        .toItemStack());

                ItemBuilder activeEffects = new ItemBuilder(this.effectsItem.getType()).setName("§d" + target.getName() + "§e's active potion effects:");
                List<String> effects = this.getActiveEffects(target);
                if (!effects.isEmpty()) {
                    effects.forEach(activeEffects::addLoreLine);
                }
                topInventory.setItem(53, activeEffects.toItemStack());
                player.updateInventory();
            }, 1L);
        }, 1L);
    }

    public List<String> getActiveEffects(Player player) {
        List<String> activeEffects = new ArrayList<>();
        player.getActivePotionEffects().forEach(it -> {
            activeEffects.add("§a" + it.getType().getName() + " §7effect:");
            activeEffects.add("§8├─ §eAmplifier: §a" + it.getAmplifier());
            activeEffects.add("§8└─ §eDuration: §a" + it.getDuration());
        });
        return activeEffects;
    }


}
