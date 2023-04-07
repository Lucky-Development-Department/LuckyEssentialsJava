package id.luckynetwork.dev.lyrams.lej.commands.essentials.gamemode;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GamemodeSurvivalCommand extends CommandClass {

    public GamemodeSurvivalCommand() {
        super("gms");
    }

    public void gmSurvivalCommand(CommandSender sender, String targetName) {
        TargetsCallback targets = this.getTargets(sender, targetName);
        if (targets.notifyIfEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        boolean others = targets.size() > 1 || (sender instanceof Player && targets.doesNotContain((Player) sender));
        if (others && !Utils.checkPermission(sender, true, "gamemode.survival")) {
            return;
        }

        plugin.getConfirmationManager().requestConfirmation(() -> {
            targets.forEach(target -> {
                target.setGameMode(GameMode.SURVIVAL);
                target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eYour gamemode has been set to §dsurvival§e.");
            });

            if (others) {
                if (targets.size() == 1) {
                    targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet gamemode to §6survival §efor §d" + target.getName() + "§e."));
                } else {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet gamemode to §6survival §efor §d" + targets.size() + " §eplayers.");
                }
            } else if (!(sender instanceof Player) || targets.doesNotContain((Player) sender)) {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet gamemode to §6survival §efor §d" + target.getName() + "§e."));
            }
        }, this.canSkip("gamemode change", targets, sender));
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "gamemode.survival")) {
            return;
        }

        String targetName = "self";
        if (args.length > 0) {
            targetName = args[0];
        }

        this.gmSurvivalCommand(sender, targetName);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eGamemode command:");
        sender.sendMessage("§8└─ §e/gms <player> §8- §7Set your gamemode to survival");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "gamemode.survival")) {
            return null;
        }

        if (args.length == 1) {
            return this.players(args[0]);
        }

        return null;
    }
}
