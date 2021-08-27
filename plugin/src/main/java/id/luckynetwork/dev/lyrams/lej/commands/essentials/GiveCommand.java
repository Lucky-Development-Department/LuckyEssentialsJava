package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.specifier.Greedy;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class GiveCommand extends CommandClass {

    @CommandMethod("give <target> <item> [amount] [options]")
    @CommandDescription("Gives another player an item")
    public void giveCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "item", description = "The item name", defaultValue = "wood") String itemName,
            final @NonNull @Argument(value = "amount", description = "The amount of item", defaultValue = "-1") Integer amount,
            final @Nullable @Argument(value = "options", description = "More options for the item") @Greedy String options
    ) {
        if (!Utils.checkPermission(sender, "give")) {
            return;
        }

        TargetsCallback targets = this.getTargets(sender, targetName);
        if (targets.notifyIfEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        ItemStack item;
        if (itemName.contains(":")) {
            item = plugin.getVersionSupport().getItemByName(itemName.split(":")[0], amount, Integer.parseInt(itemName.split(":")[1]));
        } else {
            item = plugin.getVersionSupport().getItemByName(itemName, amount, 0);
        }

        if (item == null) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown item: §l " + itemName + "§c!");
            return;
        }

        if (options != null) {
            if (options.contains("-enchants=")) {
                if (options.split("-enchants=")[1].contains("-name=")) {
                    this.parseEnchants(sender, options.split("-enchants=")[1].split(" ")[0]).forEach(item::addUnsafeEnchantment);
                } else {
                    this.parseEnchants(sender, options.split("-enchants=")[1]).forEach(item::addUnsafeEnchantment);
                }
            }

            if (options.contains("-name=")) {
                ItemMeta itemMeta = item.getItemMeta();
                String name = options.split("-name=")[1];
                if (name.contains(" -enchants=")) {
                    name = name.split(" -enchants=")[0];
                }
                itemMeta.setDisplayName(Utils.colorize(name));

                item.setItemMeta(itemMeta);
            }
        }

        targets.forEach(target -> {
            target.getInventory().addItem(item);
            target.updateInventory();
            target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eYou have been given §d" + item.getAmount() + "x " + item.getType().toString() + "§e!");
        });

        if (targets.size() > 1) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eGiven §6" + item.getAmount() + "x " + item.getType().toString() + " §d" + targets.size() + "§eplayers!");
        } else if ((!(sender instanceof Player)) || (targets.doesNotContain((Player) sender) && !targetName.equals("self"))) {
            targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eGiven §6" + item.getAmount() + "x " + item.getType().toString() + " §eto §d" + target.getName() + "§e!"));
        }
    }

    @CommandMethod("i <item> [amount] [options]")
    @CommandDescription("Gives yourself player an item")
    public void iCommand(
            final @NonNull Player sender,
            final @NonNull @Argument(value = "item", description = "The item name", defaultValue = "wood") String itemName,
            final @NonNull @Argument(value = "amount", description = "The amount of item", defaultValue = "-1") Integer amount,
            final @Nullable @Argument(value = "options", description = "More options for the item") @Greedy String options
    ) {
        if (!Utils.checkPermission(sender, "give")) {
            return;
        }

        ItemStack item;
        if (itemName.contains(":")) {
            item = plugin.getVersionSupport().getItemByName(itemName.split(":")[0], amount, Integer.parseInt(itemName.split(":")[1]));
        } else {
            item = plugin.getVersionSupport().getItemByName(itemName, amount, 0);
        }

        if (item == null) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown item: §l " + itemName + "§c!");
            return;
        }

        if (options != null) {
            if (options.contains("-enchants=")) {
                if (options.split("-enchants=")[1].contains("-name=")) {
                    this.parseEnchants(sender, options.split("-enchants=")[1].split(" ")[0]).forEach(item::addUnsafeEnchantment);
                } else {
                    this.parseEnchants(sender, options.split("-enchants=")[1]).forEach(item::addUnsafeEnchantment);
                }
            }

            if (options.contains("-name=")) {
                ItemMeta itemMeta = item.getItemMeta();
                String name = options.split("-name=")[1];
                if (name.contains(" -enchants=")) {
                    name = name.split(" -enchants=")[0];
                }
                itemMeta.setDisplayName(Utils.colorize(name));

                item.setItemMeta(itemMeta);
            }
        }

        sender.getInventory().addItem(item);
        sender.updateInventory();
        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eYou have been given §d" + item.getAmount() + "x " + item.getType().toString() + "§e!");
    }
}
