package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.Flag;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class TopCommand extends CommandClass {

    @CommandMethod("top [target]")
    @CommandDescription("Teleports you or other player to the highest block on their location")
    public void topCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @Nullable @Flag(value = "silent", aliases = "s", description = "Should the target not be notified?") Boolean silent
    ) {
        if (!Utils.checkPermission(sender, "top")) {
            return;
        }

        TargetsCallback targets = this.getTargets(sender, targetName);
        if (targets.notifyIfEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        boolean others = targets.size() > 1 || (sender instanceof Player && targets.doesNotContain((Player) sender));
        if (others && !Utils.checkPermission(sender, true, "top")) {
            return;
        }

        targets.forEach(target -> {
            Location targetLocation = target.getLocation();
            Block highest = target.getWorld().getHighestBlockAt(targetLocation.getBlockX(), targetLocation.getBlockZ());

            Location newLocation = targetLocation.clone();
            newLocation.setY(highest.getY());
            target.teleport(newLocation);

            if (silent == null || !silent) {
                target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eYou have been teleported to the highest block!");
            }
        });

        if (others) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported §d" + targets.size() + " §eplayers!");
        } else if ((!(sender instanceof Player)) || (targets.doesNotContain((Player) sender) && !targetName.equals("self"))) {
            targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported §d" + target.getName() + "§e!"));
        }
    }

}
