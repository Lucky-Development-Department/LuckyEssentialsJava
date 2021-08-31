package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.Flag;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SmiteCommand extends CommandClass {

    @CommandMethod("smite [target]")
    @CommandDescription("Summons lighting where you're looking at or at other player")
    public void smiteCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @Nullable @Flag(value = "silent", aliases = "s", description = "Should the target not get notified?") Boolean silent
    ) {
        if (!Utils.checkPermission(sender, "smite")) {
            return;
        }

        Set<Location> locations = new HashSet<>();
        TargetsCallback targets = new TargetsCallback();
        if (targetName.equals("self") && sender instanceof Player) {
            locations.add(((Player) sender).getTargetBlock(null, 120).getLocation());
        } else {
            targets = this.getTargets(sender, targetName);
        }

        if (!targets.isEmpty()) {
            locations.addAll(targets.stream().map(Player::getLocation).collect(Collectors.toList()));
            if (silent == null || !silent) {
                targets.forEach(target -> target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eYou have been smitten!"));
            }
        }

        locations.forEach(location -> location.getWorld().strikeLightning(location));

        boolean others = !targets.isEmpty() && targets.size() > 1;
        if (others) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSmitten §d" + targets.size() + " §eplayers!");
        } else if ((!(sender instanceof Player)) || (targets.doesNotContain((Player) sender) && !targetName.equals("self"))) {
            targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSmitten §d" + target.getName() + "§e!"));
        }
    }

}
