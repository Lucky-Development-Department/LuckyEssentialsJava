package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SmiteCommand extends CommandClass {

    public SmiteCommand() {
        super("smite");
        this.registerCommandInfo("smite", "Smite a player");
    }

    public void smiteCommand(CommandSender sender, String targetName, Boolean damage, Boolean silent) {
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

        TargetsCallback finalTargets = targets;
        plugin.getConfirmationManager().requestConfirmation(() -> {
            if (!finalTargets.isEmpty()) {
                locations.addAll(finalTargets.stream().map(Player::getLocation).collect(Collectors.toList()));
                if (silent == null || !silent) {
                    finalTargets.forEach(target -> target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eYou have been smitten!"));
                }
            }

            if (damage == null || !damage) {
                locations.forEach(location -> location.getWorld().strikeLightning(location));
            } else {
                locations.forEach(location -> location.getWorld().strikeLightningEffect(location));
            }

            boolean others = !finalTargets.isEmpty() && finalTargets.size() > 1;
            if (others) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSmitten §d" + finalTargets.size() + " §eplayers.");
            } else if ((!(sender instanceof Player)) || (finalTargets.doesNotContain((Player) sender) && !targetName.equals("self"))) {
                finalTargets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSmitten §d" + target.getName() + "§e."));
            }
        }, this.canSkip("smite", targets, sender));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "smite")) {
            return;
        }

        String targetName = "self";
        if (args.length >= 1) {
            targetName = args[0];
        }

        String allArgs = String.join(" ", args);
        boolean damage = allArgs.contains("-d");
        boolean silent = allArgs.contains("-s");

        this.smiteCommand(sender, targetName, damage, silent);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eSmite command:");
        sender.sendMessage("§8└─ §e/smite §8- §7Smite yourself");
        sender.sendMessage("§8└─ §e/smite <player> §8- §7Smite a player");
        sender.sendMessage("§8└─ §e/smite <player> -d §8- §7Smite a player with damage");
        sender.sendMessage("§8└─ §e/smite <player> -s §8- §7Smite a player silently");
        sender.sendMessage("§8└─ §e/smite <player> -d -s §8- §7Smite a player with damage and silently");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "smite", true)) {
            return null;
        }

        if (args.length == 1) {
            return this.players(args[0]);
        } else if (args.length == 2) {
            return Stream.of("-s")
                    .filter(it -> it.toLowerCase().startsWith(args[1]))
                    .collect(Collectors.toList());
        }

        return null;
    }
}
