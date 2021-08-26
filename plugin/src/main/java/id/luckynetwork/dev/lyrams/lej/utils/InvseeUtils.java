package id.luckynetwork.dev.lyrams.lej.utils;

import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class InvseeUtils {

    public final List<Integer> separatorSlots = Arrays.asList(36, 37, 38, 39, 40, 41, 42, 43, 44, 49, 50, 51, 52, 53);
    public ItemStack separatorItem = null;

    public ItemStack infoItem = null;
    public ItemStack effectsItem = null;

    public void init() {
        if (separatorItem == null) {
            separatorItem = LuckyEssentials.getInstance().getVersionSupport().getItemByName("blackglasspane", 1, 0);
        }

        if (infoItem == null) {
            infoItem = LuckyEssentials.getInstance().getVersionSupport().getItemByName("paper", 1, 0);
        }
        if (effectsItem == null) {
            effectsItem = LuckyEssentials.getInstance().getVersionSupport().getItemByName("brewing_stand_item", 1, 0);
        }
    }

    public void invsee(Player player, Player target) {
        Inventory inventory = Bukkit.createInventory(target, 54, "§e" + target.getName() + "'s inventory");
        inventory.setContents(target.getInventory().getContents());

        separatorSlots.forEach(it -> inventory.setItem(it, separatorItem));
        int armorSlot = 45;
        for (ItemStack armorContent : target.getInventory().getArmorContents()) {
            inventory.setItem(armorSlot, armorContent);
            armorSlot++;
        }

        inventory.setItem(49, LuckyEssentials.getInstance().getVersionSupport().getItemInHand(target));

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
        List<String> effects = InvseeUtils.getActiveEffects(target);
        if (!effects.isEmpty()) {
            effects.forEach(activeEffects::addLoreLine);
        }
        inventory.setItem(53, activeEffects.toItemStack());

        player.closeInventory();
        player.openInventory(inventory);
        player.updateInventory();
    }

    public void refresh(Player player, Player target, Inventory topInventory) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(LuckyEssentials.getInstance(), () -> {
            player.updateInventory();

            Bukkit.getScheduler().scheduleSyncDelayedTask(LuckyEssentials.getInstance(), () -> {
                int armorSlot = 45;
                for (ItemStack armorContent : target.getInventory().getArmorContents()) {
                    topInventory.setItem(armorSlot, armorContent);
                    armorSlot++;
                }

                topInventory.setItem(49, LuckyEssentials.getInstance().getVersionSupport().getItemInHand(target));

                topInventory.setItem(52, new ItemBuilder(InvseeUtils.infoItem.getType())
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

                ItemBuilder activeEffects = new ItemBuilder(InvseeUtils.effectsItem.getType()).setName("§d" + target.getName() + "§e's active potion effects:");
                List<String> effects = InvseeUtils.getActiveEffects(target);
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
            activeEffects.add("§8├─ §eDuration: §a" + it.getDuration());
        });
        return activeEffects;
    }
}
