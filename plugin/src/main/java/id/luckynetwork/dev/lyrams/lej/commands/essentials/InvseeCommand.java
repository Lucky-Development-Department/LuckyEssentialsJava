package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class InvseeCommand extends CommandClass {

    public InvseeCommand() {
        super("invsee");
    }

    public void invseeCommand(Player player, String targetName) {
        if (!Utils.checkPermission(player, "invsee")) {
            return;
        }

        TargetsCallback targets = this.getTargets(player, targetName);
        if (targets.notifyIfEmpty()) {
            player.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }
        if (targets.size() > 1) {
            player.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cYou can only check one player at a time!");
            return;
        }

        targets.stream().findFirst().ifPresent(target -> plugin.getInvseeManager().invsee(player, target));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "invsee")) {
            return;
        }

        if (args.length == 0) {
            this.sendDefaultMessage(sender);
            return;
        }

        if (sender instanceof Player) {
            this.invseeCommand((Player) sender, args[0]);
        } else {
            sender.sendMessage("§cThis command can only be executed by players!");
        }
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eInvsee command:");
        sender.sendMessage("§8└─ §e/invsee <player> §8- §7Open a player's inventory");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "invsee")) {
            return null;
        }

        if (args.length == 1) {
            return this.players(args[0]);
        }

        return null;
    }
}
