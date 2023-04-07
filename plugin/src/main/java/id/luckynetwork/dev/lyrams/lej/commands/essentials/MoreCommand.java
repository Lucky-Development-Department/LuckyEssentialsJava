package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MoreCommand extends CommandClass {

    public MoreCommand() {
        super("more");
        this.registerCommandInfo("more", "Set the currently held item amount to its max stack size");
    }

    public void moreCommand(Player player, Integer amount) {
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

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be executed by players!");
            return;
        }

        if (!Utils.checkPermission(sender, "more")) {
            return;
        }

        if (args.length == 0) {
            this.moreCommand((Player) sender, null);
        } else {
            int amount;

            try {
                amount = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid number!");
                return;
            }

            this.moreCommand((Player) sender, amount);
        }
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eMore command:");
        sender.sendMessage("§8└─ §e/more §8- §7Sets the currently held item amount to its max stack size");
        sender.sendMessage("§8└─ §e/more <amount> §8- §7Sets the currently held item amount to a set amount");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        return null;
    }
}
