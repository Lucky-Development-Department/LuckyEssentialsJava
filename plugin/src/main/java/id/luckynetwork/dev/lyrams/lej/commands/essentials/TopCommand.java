package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TopCommand extends CommandClass {

    public TopCommand() {
        super("top");
    }

    public void topCommand(CommandSender sender, String targetName, Boolean silent) {
        TargetsCallback targets = this.getTargets(sender, targetName);
        if (targets.notifyIfEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        boolean others = targets.size() > 1 || (sender instanceof Player && targets.doesNotContain((Player) sender));
        if (others && !Utils.checkPermission(sender, true, "top")) {
            return;
        }

        plugin.getConfirmationManager().requestConfirmation(() -> {
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
                if (targets.size() == 1) {
                    targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported §d" + target.getName() + "§e."));
                } else {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported §d" + targets.size() + " §eplayers.");
                }
            } else if ((!(sender instanceof Player)) || (targets.doesNotContain((Player) sender) && !targetName.equals("self"))) {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported §d" + target.getName() + "§e."));
            }
        }, this.canSkip("teleport player to highest block", targets, sender));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "top")) {
            return;
        }

        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cYou must specify a player!");
            return;
        }

        String targetName = "self";
        boolean silent = false;
        if (args.length > 0) {
            targetName = args[0];
        }

        if (args[args.length - 1].equalsIgnoreCase("-s")) {
            silent = true;
        }

        this.topCommand(sender, targetName, silent);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eTop command:");
        sender.sendMessage("§8└─ §e/top §8- §7Teleport to the highest block");
        sender.sendMessage("§8└─ §e/top <player> §8- §7Teleport a player to the highest block");
        sender.sendMessage("§8└─ §e/top <player> -s §8- §7Teleport a player to the highest block silently");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "top")) {
            return null;
        }

        if (args.length == 1) {
            List<String> suggestions =
                    Stream.of("-s")
                            .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                            .collect(Collectors.toList());
            suggestions.addAll(this.players(args[0]));

            return suggestions;
        }

        return Stream.of("-s")
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}
