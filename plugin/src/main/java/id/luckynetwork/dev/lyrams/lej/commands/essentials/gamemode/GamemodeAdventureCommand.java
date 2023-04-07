package id.luckynetwork.dev.lyrams.lej.commands.essentials.gamemode;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GamemodeAdventureCommand extends CommandClass {

    public GamemodeAdventureCommand() {
        super("gma");
        this.registerCommandInfo("gma", "Sets a player's gamemode to adventure");
    }

    public void gmAdventureCommand(CommandSender sender, String targetName) {
        if (!Utils.checkPermission(sender, "gamemode.adventure")) {
            return;
        }

        TargetsCallback targets = this.getTargets(sender, targetName);
        if (targets.notifyIfEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        boolean others = targets.size() > 1 || (sender instanceof Player && targets.doesNotContain((Player) sender));
        if (others && !Utils.checkPermission(sender, true, "gamemode.adventure")) {
            return;
        }

        plugin.getConfirmationManager().requestConfirmation(() -> {
            targets.forEach(target -> {
                target.setGameMode(GameMode.ADVENTURE);
                target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eYour gamemode has been set to §dadventure§e.");
            });

            if (others) {
                if (targets.size() == 1) {
                    targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet gamemode to §6adventure §efor §d" + target.getName() + "§e."));
                } else {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet gamemode to §6adventure §efor §d" + targets.size() + " §eplayers.");
                }
            } else if (!(sender instanceof Player) || targets.doesNotContain((Player) sender)) {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet gamemode to §6adventure §efor §d" + target.getName() + "§e."));
            }
        }, this.canSkip("gamemode change", targets, sender));
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "gamemode.adventure")) {
            return;
        }

        if (args.length == 0 && !(sender instanceof Player)) {
            sender.sendMessage("§cPlease specify a player!");
            return;
        }

        String targetName = "self";
        if (args.length > 0) {
            targetName = args[0];
        }

        this.gmAdventureCommand(sender, targetName);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eGamemode command:");
        sender.sendMessage("§8└─ §e/gma <player> §8- §7Set your gamemode to adventure");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "gamemode.adventure")) {
            return null;
        }

        if (args.length == 1) {
            return this.players(args[0]);
        }

        return null;
    }
}
