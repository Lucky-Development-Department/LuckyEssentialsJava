package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.ProxiedBy;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.config.Config;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EnchantCommand extends CommandClass {

    @ProxiedBy("ench")
    @CommandMethod("enchant <enchantmentOrTarget> [enchantment]")
    @CommandDescription("Enchants your item in hand")
    public void enchantCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "enchantmentOrTarget", description = "The enchantment or other player", defaultValue = "self", suggestions = "enchantments") String enchantmentOrTarget,
            final @Nullable @Argument(value = "enchantment", description = "The enchantment for the other player") String enchantment
    ) {
        if (!Utils.checkPermission(sender, "enchant")) {
            return;
        }

        if (sender instanceof Player && enchantment == null) {
            ItemStack itemInHand = plugin.getVersionSupport().getItemInHand((Player) sender);
            this.parseEnchants(sender, enchantmentOrTarget).forEach(itemInHand::addUnsafeEnchantment);
            ((Player) sender).updateInventory();
        } else {
            Set<Player> targets = this.getTargets(sender, enchantmentOrTarget);
            if (targets.isEmpty()) {
                sender.sendMessage(Config.PREFIX + "§cNo targets found!");
                return;
            }

            if (enchantment == null) {
                sender.sendMessage(Config.PREFIX + "§cPlease specify an enchantment!");
                return;
            }

            HashMap<Enchantment, Integer> enchants = this.parseEnchants(sender, enchantment);
            if (enchants.isEmpty()) {
                sender.sendMessage(Config.PREFIX + "§cNo valid enchants found!");
                return;
            }

            targets.forEach(target -> {
                ItemStack itemInHand = plugin.getVersionSupport().getItemInHand(target);
                if (itemInHand != null && itemInHand.getType() != Material.AIR) {
                    enchants.keySet().forEach(key -> itemInHand.addEnchantment(key, enchants.get(key)));
                    target.updateInventory();
                }
            });
        }
    }

    @Suggestions("enchantments")
    public List<String> enchantments(CommandContext<CommandSender> context, String current) {
        return plugin.getVersionSupport().getEnchantments().stream()
                .filter(it -> it.toLowerCase().startsWith(current.toLowerCase()))
                .collect(Collectors.toList());
    }
}
