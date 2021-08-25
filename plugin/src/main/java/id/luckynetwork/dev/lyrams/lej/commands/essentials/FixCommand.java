package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.Flag;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.config.Config;
import id.luckynetwork.dev.lyrams.lej.enums.FixType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FixCommand extends CommandClass {

    @CommandMethod("fix|repair [target] [type]")
    @CommandDescription("Repair items")
    public void fixCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "type", description = "hand/all/armor", defaultValue = "hand", suggestions = "fixTypes") String type,
            final @Nullable @Flag(value = "silent", aliases = "s", description = "Should it not notify the target of the teleportation?") Boolean silent
    ) {
        if (!Utils.checkPermission(sender, "fix")) {
            return;
        }

        TargetsCallback targets;
        FixType fixType;
        if (!FixType.getType(targetName).equals(FixType.UNKNOWN) && sender instanceof Player) {
            // the sender wants to change their own troll state
            targets = this.getTargets(sender, "self");
            fixType = FixType.getType(targetName);
        } else {
            targets = this.getTargets(sender, targetName);
            fixType = FixType.getType(type);
        }

        if (targets.notifyIfEmpty()) {
            sender.sendMessage(Config.PREFIX + "§cNo targets found!");
            return;
        }

        if (fixType.equals(FixType.UNKNOWN)) {
            sender.sendMessage(Config.PREFIX + "§cUnknown fix type §l" + type + "§c!");
            return;
        }

        boolean others = targets.size() > 1;
        if (others && !Utils.checkPermission(sender, true, "fix")) {
            return;
        }

        targets.forEach(target -> {
            switch (fixType) {
                case ALL: {
                    for (ItemStack content : target.getInventory().getContents()) {
                        if (content == null || content.getType().isBlock() || content.getDurability() == 0 || content.getType().getMaxDurability() < 1) {
                            continue;
                        }

                        content.setDurability((short) 0);
                    }
                    for (ItemStack content : target.getInventory().getArmorContents()) {
                        if (content == null || content.getType().isBlock() || content.getDurability() == 0 || content.getType().getMaxDurability() < 1) {
                            continue;
                        }

                        content.setDurability((short) 0);
                    }
                    target.updateInventory();

                    if (silent == null || !silent) {
                        target.sendMessage(Config.PREFIX + "§eYour items have been repaired!");
                    }
                    break;
                }
                case HAND: {
                    ItemStack itemInHand = plugin.getVersionSupport().getItemInHand(target);
                    if (itemInHand == null || itemInHand.getType().isBlock() || itemInHand.getDurability() == 0 || itemInHand.getType().getMaxDurability() < 1) {
                        return;
                    }

                    itemInHand.setDurability((short) 0);
                    target.updateInventory();

                    if (silent == null || !silent) {
                        target.sendMessage(Config.PREFIX + "§eYour item in hand has been repaired!");
                    }
                    break;
                }
                case ARMOR: {
                    for (ItemStack content : target.getInventory().getArmorContents()) {
                        if (content == null || content.getType().isBlock() || content.getDurability() == 0 || content.getType().getMaxDurability() < 1) {
                            continue;
                        }

                        content.setDurability((short) 0);
                    }
                    target.updateInventory();

                    if (silent == null || !silent) {
                        target.sendMessage(Config.PREFIX + "§eYour armor has been repaired!");
                    }
                    break;
                }
            }
        });

        if (others) {
            sender.sendMessage(Config.PREFIX + "§eRepaired §6" + fixType.getDisplay() + " §for §d" + targets.size() + " players!");
        } else if ((!(sender instanceof Player)) || (targets.doesNotContain((Player) sender) && !targetName.equals("self"))) {
            targets.stream().findFirst().ifPresent(target -> sender.sendMessage(Config.PREFIX + "§eRepaired §6" + fixType.getDisplay() + " §efor §d" + target.getName() + "§e!"));
        }
    }

    @Suggestions("fixTypes")
    public List<String> fixTypes(CommandContext<CommandSender> context, String current) {
        return Stream.of("all", "hand", "armor")
                .filter(it -> it.toLowerCase().startsWith(current.toLowerCase()))
                .collect(Collectors.toList());
    }

}
