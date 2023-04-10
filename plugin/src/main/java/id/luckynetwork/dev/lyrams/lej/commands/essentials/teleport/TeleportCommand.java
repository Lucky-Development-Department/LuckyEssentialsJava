package id.luckynetwork.dev.lyrams.lej.commands.essentials.teleport;

import com.google.common.base.Joiner;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TeleportCommand extends CommandClass {

    public TeleportCommand() {
        super("teleport", Collections.singletonList("tp"));
        this.registerCommandInfo("teleport", "Teleports a player to another player");
    }

    public void teleportCommand(CommandSender sender, String player, String target, Boolean silent) {
        TargetsCallback players = this.getTargets(sender, player);
        if (players.notifyIfEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        if (target.equals("undefinedTarget2Placeholder")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid usage: §l" + player + "§c!");
                return;
            }

            // the player wants to teleport themselves to another player
            if (players.size() > 1) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid usage: §l" + player + "§c!");
                return;
            }

            players.stream().findFirst().ifPresent(destination -> {
                ((Player) sender).teleport(destination);
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported you to §d" + destination.getName() + "§e.");
            });
        } else {
            TargetsCallback targets = this.getTargets(sender, target);
            if (targets.isEmpty()) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
                return;
            }

            if (targets.size() > 1) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid usage: §l" + target + "§c!");
                return;
            }

            if (!Utils.checkPermission(sender, true, "teleport")) {
                return;
            }

            // the sender wants to teleport other player to another player
            targets.stream().findFirst().ifPresent(destination -> {
                players.forEach(toTeleport -> {
                    toTeleport.teleport(destination);
                    if (silent == null || !silent) {
                        toTeleport.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported you to §d" + destination.getName() + "§e.");
                    }
                });

                if (players.size() > 1) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported §d" + players.size() + " §eplayers to §6" + destination.getName() + "§e.");
                } else {
                    players.stream().findFirst().ifPresent(it -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported §d" + it.getName() + " §eto §6" + destination.getName() + "§e."));
                }
            });
        }
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "teleport")) {
            return;
        }

        if (args.length < 1) {
            this.sendDefaultMessage(sender);
            return;
        }

        String playerName = args[0];
        String targetName = "undefinedTarget2Placeholder";
        boolean silent = Joiner.on(" ").join(args).contains("-s");

        if (args.length > 1) {
            targetName = args[1];
        }

        this.teleportCommand(sender, playerName, targetName, silent);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eTeleport command:");
        sender.sendMessage("§8└─ §e/tp <player> §8- §7Teleport to a player");
        sender.sendMessage("§8└─ §e/tp <player> <player> §8- §7Teleport a player to another player");
        sender.sendMessage("§8└─ §e/tpw <world> §8- §7Teleport to a world");
        sender.sendMessage("§8└─ §e/tphere <player> §8- §7Teleport a player to you");
        sender.sendMessage("§e└─ §d/tppos <x> <y> <z> §7- Teleport to a specific position");
        sender.sendMessage("§e└─ §d/tppos <x> <y> <z> -w <world> §7- Teleport to a specific position in a specific world");
        sender.sendMessage("§e└─ §d/tppos <x> <y> <z> -y <yaw> §7- Teleport to a specific position with a specific yaw");
        sender.sendMessage("§e└─ §d/tppos <x> <y> <z> -p <pitch> §7- Teleport to a specific position with a specific pitch");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "teleport", true)) {
            return null;
        }

        if (args.length == 1) {
            return this.players(args[0]);
        } else if (args.length == 2) {
            return this.players(args[1]);
        }

        return null;
    }
}
