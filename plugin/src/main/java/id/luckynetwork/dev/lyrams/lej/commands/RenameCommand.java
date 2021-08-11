package id.luckynetwork.dev.lyrams.lej.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.specifier.Greedy;
import id.luckynetwork.dev.lyrams.lej.commands.main.CommandClass;
import id.luckynetwork.dev.lyrams.lej.config.Config;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

public class RenameCommand extends CommandClass {

    @CommandMethod("rename <name>")
    @CommandDescription("Renames the item that you are currently holding")
    public void flyCommand(
            final @NonNull Player player,
            final @NonNull @Argument(value = "name", description = "The new name for the held item") @Greedy String name
    ) {
        ItemStack itemInHand = plugin.getVersionSupport().getItemInHand(player);
        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            player.sendMessage(Config.PREFIX + " §cYou are not holding anything!");
            return;
        }

        ItemMeta itemMeta = itemInHand.getItemMeta();
        itemMeta.setDisplayName(Utils.colorize(name));

        itemInHand.setItemMeta(itemMeta);
        player.updateInventory();
    }
}
