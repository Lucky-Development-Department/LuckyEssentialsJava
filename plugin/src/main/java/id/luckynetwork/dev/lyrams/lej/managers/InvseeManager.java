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
    private final List<Integer> separatorSlots;
    private final Map<Player, Set<Player>> invseeMap;
    private final ItemStack separatorItem;

    private final ItemStack infoItem;
    private final ItemStack locationItem;
    private final ItemStack effectsItem;

    public InvseeManager(LuckyEssentials plugin) {
        this.plugin = plugin;

        if (plugin.getMainConfigManager().isOldInvsee()) {
            // old invsee is lighter as it utilizes more of bukkit's way of seeing other's inventory
            // old invsee only gives the invseer the target's inventory
            // old invsee system is more responsive and less buggy, but is boring
            this.separatorSlots = null;
            this.invseeMap = null;

            this.separatorItem = null;
            this.infoItem = null;
            this.locationItem = null;
            this.effectsItem = null;
        } else {
            // new invsee system is heavier as it utilizes more of my bad programming :)
            // new invsee gives more information as it use a custom gui
            // new invsee system is less responsive and more buggy, but looks cooler
            this.separatorSlots = Arrays.asList(36, 37, 38, 39, 40, 41, 42, 43, 44, 49, 50, 51, 52, 53);
            this.invseeMap = new HashMap<>();

            this.separatorItem = LuckyEssentials.getInstance().getVersionSupport().getItemByName("blackglasspane", 1, 0);
            this.infoItem = LuckyEssentials.getInstance().getVersionSupport().getItemByName("paper", 1, 0);
            this.locationItem = LuckyEssentials.getInstance().getVersionSupport().getItemByName("compass", 1, 0);
            this.effectsItem = LuckyEssentials.getInstance().getVersionSupport().getItemByName("brewingstand", 1, 0);

            Bukkit.getScheduler().runTaskTimer(plugin, () -> invseeMap.forEach((key, value) -> value.forEach(invseer -> this.refresh(invseer, key))), 1L, 10L);
        }
    }

    /**
     * Adds a player as an invseer of another player
     *
     * @param player  the inventory owner
     * @param invseer the invseer be added
     */
    public void addInvseer(Player player, Player invseer) {
        if (invseeMap.containsKey(player)) {
            invseeMap.get(player).add(invseer);
            return;
        }

        Set<Player> invseers = new HashSet<>();
        invseers.add(invseer);
        invseeMap.put(player, invseers);
    }

    /**
     * Removes a player who is an invseer of another player
     *
     * @param player  the inventory owner
     * @param invseer the invseer to be removed
     */
    public void removeInvseer(Player player, Player invseer) {
        if (!invseeMap.containsKey(player)) {
            return;
        }

        invseeMap.get(player).remove(invseer);
        if (invseeMap.get(player).size() < 1) {
            invseeMap.remove(player);
        }
    }

    /**
     * Gets all invseer for a player
     *
     * @param player the player
     * @return all invseers
     */
    public Set<Player> getInvseers(Player player) {
        return invseeMap.getOrDefault(player, new HashSet<>());
    }

    /**
     * Peeks another player's inventory
     *
     * @param player the player
     * @param target the target player
     */
    public void invsee(Player player, Player target) {
        if (plugin.getMainConfigManager().isOldInvsee()) {
            player.closeInventory();
            player.openInventory(target.getInventory());
            Utils.applyMetadata(player, "INVSEE", true);
            return;
        }

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
                .addLoreLine("§8├─ §eWorld: §a" + location.getWorld().getName())
                .addLoreLine("§8├─ §eX: §a" + location.getX())
                .addLoreLine("§8├─ §eY: §a" + location.getY())
                .addLoreLine("§8├─ §eZ: §a" + location.getZ())
                .addLoreLine("§8├─ §eYaw: §a" + location.getYaw())
                .addLoreLine("§8├─ §ePitch: §a" + location.getPitch())
                .addLoreLine("§8└─ §eDistance: §a" + distanceString)
                .addLoreLine(" ")
                .addLoreLine("        §7(( Left-Click to teleport to player ))")
                .addLoreLine("        §7(( Right-Click to teleport to near the player ))")
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

    /**
     * Closes an invsee inventory for player
     *
     * @param player the player
     */
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

    /**
     * Refreshes the invsee inventory for an invseer
     *
     * @param player       the invseer player
     * @param target       the invsee owner
     * @param topInventory the invsee inventory
     */
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
                        .addLoreLine("§8├─ §eWorld: §a" + location.getWorld().getName())
                        .addLoreLine("§8├─ §eX: §a" + location.getX())
                        .addLoreLine("§8├─ §eY: §a" + location.getY())
                        .addLoreLine("§8├─ §eZ: §a" + location.getZ())
                        .addLoreLine("§8├─ §eYaw: §a" + location.getYaw())
                        .addLoreLine("§8├─ §ePitch: §a" + location.getPitch())
                        .addLoreLine("§8└─ §eDistance: §a" + distanceString)
                        .addLoreLine(" ")
                        .addLoreLine("        §7(( Left-Click to teleport to player ))")
                        .addLoreLine("        §7(( Right-Click to teleport to near the player ))")
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

    /**
     * Refreshes the invsee inventory for an invseer
     *
     * @param player the invseer player
     * @param target the invsee owner
     */
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
                        .addLoreLine("§8├─ §eWorld: §a" + location.getWorld().getName())
                        .addLoreLine("§8├─ §eX: §a" + location.getX())
                        .addLoreLine("§8├─ §eY: §a" + location.getY())
                        .addLoreLine("§8├─ §eZ: §a" + location.getZ())
                        .addLoreLine("§8├─ §eYaw: §a" + location.getYaw())
                        .addLoreLine("§8├─ §ePitch: §a" + location.getPitch())
                        .addLoreLine("§8└─ §eDistance: §a" + distanceString)
                        .addLoreLine(" ")
                        .addLoreLine("        §7(( Left-Click to teleport to player ))")
                        .addLoreLine("        §7(( Right-Click to teleport to behind the player ))")
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

    /**
     * Gets all active effect of player and formats it
     *
     * @param player the player
     * @return all active effects formatted
     */
    private List<String> getActiveEffects(Player player) {
        List<String> activeEffects = new ArrayList<>();
        player.getActivePotionEffects().forEach(it -> {
            activeEffects.add("§a" + it.getType().getName() + " §7effect:");
            activeEffects.add("§8├─ §eAmplifier: §a" + it.getAmplifier());
            activeEffects.add("§8└─ §eDuration: §a" + it.getDuration());
        });
        return activeEffects;
    }


}
