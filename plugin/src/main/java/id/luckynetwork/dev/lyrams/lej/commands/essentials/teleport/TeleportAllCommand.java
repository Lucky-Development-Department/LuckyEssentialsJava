package id.luckynetwork.dev.lyrams.lej.commands.essentials.teleport;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TeleportAllCommand extends CommandClass {

    public TeleportAllCommand() {
        super("tpall");
        this.registerCommandInfo("tpall", "Teleports all players to a player");
    }

    public void teleportAllCommand(CommandSender sender, String target, Boolean silent) {
        TargetsCallback targets = this.getTargets(sender, target);
        if (targets.isEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        if (targets.size() > 1) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid usage: §l" + target + "§c!");
            return;
        }

        // the sender wants to teleport other player to another player
        plugin.getConfirmationManager().requestConfirmation(() -> targets.stream().findFirst().ifPresent(destination -> {
            Bukkit.getOnlinePlayers().forEach(toTeleport -> {
                if (sender instanceof Player && toTeleport.equals(sender)) {
                    return;
                }

                toTeleport.teleport(destination);
                if (silent == null || !silent) {
                    toTeleport.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported you to §d" + destination.getName() + "§e.");
                }
            });

            if (destination.getName().equalsIgnoreCase(sender.getName())) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported §d" + Bukkit.getOnlinePlayers().size() + " §eplayers to §6you§e.");
            } else {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported §d" + Bukkit.getOnlinePlayers().size() + " §eplayers to §6" + destination.getName() + "§e.");
            }
        }), sender, Collections.singletonList(plugin.getMainConfigManager().getPrefix() + "§eAre you sure you want to execute §dteleport all player§e?"));
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "teleport.all")) {
            return;
        }

        if (args.length == 0 && !(sender instanceof Player)) {
            sender.sendMessage("§cPlease specify a player!");
            return;
        }

        String targetName = args.length > 0 ? args[0] : "self";
        Boolean silent = args.length > 1 && args[1].equalsIgnoreCase("-s");

        this.teleportAllCommand(sender, targetName, silent);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eTeleport command:");
        sender.sendMessage("§8└─ §e/tpall <player> §8- §7Teleport a player to you");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "teleport.all", true)) {
            return null;
        }

        return this.players(args[0]);
    }
}
