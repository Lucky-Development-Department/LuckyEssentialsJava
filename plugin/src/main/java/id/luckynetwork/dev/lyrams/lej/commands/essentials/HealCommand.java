package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.Flag;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class HealCommand extends CommandClass {

    @CommandMethod("heal [target]")
    @CommandDescription("Heals you or other player")
    public void healCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @Nullable @Flag(value = "silent", aliases = "s", description = "Should the target not be notified?") Boolean silent
    ) {
        if (!Utils.checkPermission(sender, "heal")) {
            return;
        }

        TargetsCallback targets = this.getTargets(sender, targetName);
        if (targets.notifyIfEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        boolean others = targets.size() > 1 || (sender instanceof Player && targets.doesNotContain((Player) sender));
        if (others && !Utils.checkPermission(sender, true, "heal")) {
            return;
        }

        plugin.getConfirmationManager().requestConfirmation(() -> {
            targets.forEach(target -> {
                target.setHealth(plugin.getVersionSupport().getMaxHealth(target));
                if (silent == null || !silent) {
                    target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eYou have been healed!");
                }
            });

            if (others) {
                if (targets.size() == 1) {
                    targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eHealed §d" + target.getName() + "§e."));
                } else {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eHealed §d" + targets.size() + " §eplayers.");
                }
            } else if ((!(sender instanceof Player)) || (targets.doesNotContain((Player) sender) && !targetName.equals("self"))) {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eHealed §d" + target.getName() + "§e."));
            }
        }, this.canSkip("heal player", targets, sender));
    }

}
