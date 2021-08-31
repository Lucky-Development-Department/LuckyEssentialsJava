package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MoreCommand extends CommandClass {

    @CommandMethod("more [amount]")
    @CommandDescription("Sets the currently held item amount to its max stack size or to a set amount")
    public void moreCommand(
            final @NonNull Player player,
            final @Nullable @Argument(value = "amount", description = "The set amount for the held item") Integer amount
    ) {
        if (!Utils.checkPermission(player, "more")) {
            return;
        }

        ItemStack itemInHand = plugin.getVersionSupport().getItemInHand(player);
        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            player.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cYou are not holding anything!");
            return;
        }

        int newAmount;
        if (amount != null && amount > 1) {
            newAmount = amount;
        } else {
            newAmount = itemInHand.getMaxStackSize();
        }
        itemInHand.setAmount(newAmount);

        player.updateInventory();
        player.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet item in hand to §d" + newAmount + "x " + itemInHand.getType().toString() + "§e.");
    }
}
