package id.luckynetwork.dev.lyrams.lej.commands.essentials.give;

import com.google.common.base.Joiner;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class GiveCommand extends CommandClass {

    public GiveCommand() {
        super("give");
        this.registerCommandInfo("give", "Gives an item to a player");
    }

    public void giveCommand(CommandSender sender, String targetName, String itemName, Integer amount, String options) {
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
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown item: §l" + itemName + "§c!");
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

        plugin.getConfirmationManager().requestConfirmation(() -> {
            targets.forEach(target -> {
                target.getInventory().addItem(item);
                target.updateInventory();
                target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eYou have been given §d" + item.getAmount() + "x " + item.getType().toString() + "§e.");
            });

            if (targets.size() > 1) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eGiven §6" + item.getAmount() + "x " + item.getType().toString() + " §eto §d" + targets.size() + " §eplayers.");
            } else if ((!(sender instanceof Player)) || (targets.doesNotContain((Player) sender) && !targetName.equals("self"))) {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eGiven §6" + item.getAmount() + "x " + item.getType().toString() + " §eto §d" + target.getName() + "§e."));
            }
        }, this.canSkip("give item", targets, sender));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "give")) {
            return;
        }

        if (args.length < 2) {
            this.sendDefaultMessage(sender);
            return;
        }

        String targetName = args[0];
        String itemName = args[1];
        int amount = -1;
        String options = null;

        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid number §l" + args[2]);
            }
        }

        if (args.length >= 4) {
            options = Joiner.on(" ").join(Arrays.copyOfRange(args, 3, args.length));
        }

        this.giveCommand(sender, targetName, itemName, amount, options);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eGive command:");
        sender.sendMessage("§8└─ §e/give <target> <item> [amount] [options] §8- §7Give an item to a player");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "give")) {
            return null;
        }

        if (args.length == 1) {
            return this.players(args[0]);
        }


        return null;
    }
}
