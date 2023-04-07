package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HealCommand extends CommandClass {

    public HealCommand() {
        super("heal");
    }

    public void healCommand(CommandSender sender, String targetName, Boolean silent) {
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

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "heal")) {
            return;
        }

        if (args.length == 0 && !(sender instanceof Player)) {
            sender.sendMessage("§cPlease specify a player!");
            return;
        }

        String targetName = "self";
        if (args.length == 0) {
            this.healCommand(sender, targetName, false);
            return;
        }

        targetName = args[0];
        boolean silent = args[args.length - 1].equalsIgnoreCase("-s");

        this.healCommand(sender, targetName, silent);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eHeal command:");
        sender.sendMessage("§8└─ §e/heal §8- §7Heal yourself");
        sender.sendMessage("§8└─ §e/heal <player> §8- §7Heal a player");
        sender.sendMessage("§8└─ §e/heal <player> -s §8- §7Heal a player silently");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "heal")) {
            return null;
        }

        if (args.length == 1) {
            return this.players(args[0]);
        }

        return null;
    }

}
