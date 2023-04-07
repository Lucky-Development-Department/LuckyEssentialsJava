package id.luckynetwork.dev.lyrams.lej.commands.essentials.teleport;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TeleportHereCommand extends CommandClass {

    public TeleportHereCommand() {
        super("tphere", Collections.singletonList("s"));
        this.registerCommandInfo("tphere", "Teleports a player to you");
    }

    public void teleportHereCommand(Player sender, String targetName, Boolean silent) {
        CommandClass.TargetsCallback targets = this.getTargets(sender, targetName);
        if (targets.isEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        plugin.getConfirmationManager().requestConfirmation(() -> {
            targets.forEach(toTeleport -> {
                toTeleport.teleport(sender.getLocation());
                if (silent == null || !silent) {
                    toTeleport.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported you to §d" + sender.getName() + "§e.");
                }
            });

            if (targets.size() > 1 && targets.doesNotContain(sender)) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported §d" + targets.size() + " §eplayers to your location.");
            } else if (targets.doesNotContain(sender)) {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported §d" + target.getName() + "§e to your location."));
            }
        }, this.canSkip("teleport players", targets, sender));
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can execute this command!");
            return;
        }

        if (!Utils.checkPermission(sender, "teleport.here")) {
            return;
        }

        if (args.length == 0) {
            this.sendDefaultMessage(sender);
            return;
        }

        String targetName = args[0];
        Boolean silent = args.length > 1 && args[1].equalsIgnoreCase("-s");

        this.teleportHereCommand((Player) sender, targetName, silent);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eTeleport command:");
        sender.sendMessage("§8└─ §e/tphere <player> §8- §7Teleport a player to you");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "teleport.here")) {
            return null;
        }

        return this.players(args[0]);
    }
}
