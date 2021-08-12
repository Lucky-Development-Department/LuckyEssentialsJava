package id.luckynetwork.dev.lyrams.lej.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import id.luckynetwork.dev.lyrams.lej.commands.main.CommandClass;
import id.luckynetwork.dev.lyrams.lej.config.Config;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SmiteCommand extends CommandClass {

    @CommandMethod("smite [target]")
    @CommandDescription("Summons lighting where you're looking at or at other player")
    public void smiteCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName
    ) {
        if (!Utils.checkPermission(sender, "smite")) {
            return;
        }

        Set<Location> locations = new HashSet<>();
        Set<Player> targets = new HashSet<>();
        if (targetName.equals("self") && sender instanceof Player) {
            locations.add(((Player) sender).getTargetBlock(null, 120).getLocation());
        } else {
            targets = this.getTargets(sender, targetName);
        }

        if (!targets.isEmpty()) {
            locations.addAll(targets.stream().map(Player::getLocation).collect(Collectors.toList()));
            targets.forEach(target -> target.sendMessage(Config.PREFIX + "§eYou have been smitten!"));
        }

        locations.forEach(location -> location.getWorld().strikeLightning(location));

        boolean others = !targets.isEmpty() && targets.size() > 1;
        if (others) {
            sender.sendMessage(Config.PREFIX + "§eSmitten §d" + targets.size() + " §eplayers!");
        } else if ((!(sender instanceof Player)) || (!targets.contains((Player) sender) && !targetName.equals("self"))) {
            targets.stream().findFirst().ifPresent(target -> sender.sendMessage(Config.PREFIX + "§eSmitten §d" + target.getName() + "§e!"));
        }
    }

}
