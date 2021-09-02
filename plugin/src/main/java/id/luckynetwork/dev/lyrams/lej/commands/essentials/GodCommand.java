package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.Flag;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.enums.ToggleType;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class GodCommand extends CommandClass {

    @CommandMethod("god [target] [toggle]")
    @CommandDescription("Toggles godmode for you or other player")
    public void godCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle,
            final @Nullable @Flag(value = "silent", aliases = "s", description = "Should the target not be notified?") Boolean silent
    ) {
        if (!Utils.checkPermission(sender, "god")) {
            return;
        }

        TargetsCallback targets;
        ToggleType toggleType;
        if (!ToggleType.getToggle(targetName).equals(ToggleType.UNKNOWN) && sender instanceof Player) {
            // the sender wants to change their own god-mode state
            targets = this.getTargets(sender, "self");
            toggleType = ToggleType.getToggle(targetName);
        } else {
            targets = this.getTargets(sender, targetName);
            toggleType = ToggleType.getToggle(toggle);
        }

        if (targets.notifyIfEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        if (toggleType.equals(ToggleType.UNKNOWN)) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown toggle type §l" + toggle + "§c!");
            return;
        }

        boolean others = targets.size() > 1 || (sender instanceof Player && targets.doesNotContain((Player) sender));
        if (others && !Utils.checkPermission(sender, true, "god")) {
            return;
        }

        targets.forEach(target -> {
            switch (toggleType) {
                case ON: {
                    Utils.applyMetadata(target, "GOD", true);
                    break;
                }
                case OFF: {
                    Utils.removeMetadata(target, "GOD");
                    break;
                }
                case TOGGLE: {
                    if (target.hasMetadata("GOD")) {
                        Utils.removeMetadata(target, "GOD");
                    } else {
                        Utils.applyMetadata(target, "GOD", true);
                    }
                    break;
                }
            }

            boolean godMode = target.hasMetadata("GOD");
            if (silent == null || !silent) {
                target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eGod mode: " + Utils.colorizeTrueFalse(godMode, TrueFalseType.ON_OFF) + "§e.");
            }
        });

        if (others) {
            if (targets.size() == 1) {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eGod mode for §d" + target.getName() + "§e: " + Utils.colorizeTrueFalse(target.hasMetadata("GOD"), TrueFalseType.ON_OFF) + "§e."));
            } else {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eToggled god for §d" + targets.size() + " §eplayers.");
            }
        } else if (!(sender instanceof Player) || targets.doesNotContain((Player) sender)) {
            targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eGod mode for §d" + target.getName() + "§e: " + Utils.colorizeTrueFalse(target.hasMetadata("GOD"), TrueFalseType.ON_OFF) + "§e."));
        }
    }

}
