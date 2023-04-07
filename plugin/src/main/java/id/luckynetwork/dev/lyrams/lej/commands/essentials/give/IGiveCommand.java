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

public class IGiveCommand extends CommandClass {

    public IGiveCommand() {
        super("i");
    }

    public void iCommand(Player sender, String itemName, Integer amount, String options) {
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

        sender.getInventory().addItem(item);
        sender.updateInventory();
        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eYou have been given §d" + item.getAmount() + "x " + item.getType().toString() + "§e.");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "give")) {
            return;
        }

        if (args.length == 0) {
            this.sendDefaultMessage(sender);
            return;
        }

        String itemName = args[0];
        int amount = -1;
        String options = null;

        if (args.length >= 2) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid amount: §l" + args[1] + "§c!");
                return;
            }
        }

        if (args.length >= 3) {
            options = Joiner.on(" ").join(Arrays.copyOfRange(args, 2, args.length));
        }

        this.iCommand((Player) sender, itemName, amount, options);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eGive command:");
        sender.sendMessage("§8└─ §e/i <item> [amount] [options] §8- §7Give yourself an item");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        return null;
    }
}
