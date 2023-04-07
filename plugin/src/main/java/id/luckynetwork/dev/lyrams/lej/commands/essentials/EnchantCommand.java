package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class EnchantCommand extends CommandClass {

    public EnchantCommand() {
        super("enchant", Collections.singletonList("ench"));
    }

    public void enchantCommand(CommandSender sender, String enchantmentOrTarget, String enchantment) {
        if (sender instanceof Player && enchantment == null) {
            ItemStack itemInHand = plugin.getVersionSupport().getItemInHand((Player) sender);
            this.parseEnchants(sender, enchantmentOrTarget).forEach(itemInHand::addUnsafeEnchantment);
            ((Player) sender).updateInventory();
        } else {
            TargetsCallback targets = this.getTargets(sender, enchantmentOrTarget);
            if (targets.notifyIfEmpty()) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
                return;
            }

            if (enchantment == null) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlease specify an enchantment!");
                return;
            }

            HashMap<Enchantment, Integer> enchants = this.parseEnchants(sender, enchantment);
            if (enchants.isEmpty()) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo valid enchants found!");
                return;
            }

            plugin.getConfirmationManager().requestConfirmation(() -> targets.forEach(target -> {
                ItemStack itemInHand = plugin.getVersionSupport().getItemInHand(target);
                if (itemInHand != null && itemInHand.getType() != Material.AIR) {
                    enchants.keySet().forEach(key -> itemInHand.addUnsafeEnchantment(key, enchants.get(key)));
                    target.updateInventory();
                }
            }), this.canSkip("enchant", targets, sender));
        }
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "enchant")) {
            return;
        }

        String enchantmentOrTarget = "self";
        String enchantment = null;

        if (args.length > 0) {
            enchantmentOrTarget = args[0];
        }

        if (args.length > 1) {
            enchantment = args[1];
        }

        this.enchantCommand(sender, enchantmentOrTarget, enchantment);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eEnchant command:");
        sender.sendMessage("§8└─ §e/enchant <enchantmentOrTarget> [enchantment] §8- §7Enchants your item in hand");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "enchant")) {
            return null;
        }

        return plugin.getVersionSupport().getEnchantments().stream()
                .filter(it -> it.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
    }
}
