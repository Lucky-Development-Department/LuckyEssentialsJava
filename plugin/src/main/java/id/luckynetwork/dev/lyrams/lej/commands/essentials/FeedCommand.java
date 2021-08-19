package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.config.Config;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Set;

public class FeedCommand extends CommandClass {

    @CommandMethod("feed|eat [target]")
    @CommandDescription("Feeds you or other player")
    public void feedCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName
    ) {
        if (!Utils.checkPermission(sender, "feed")) {
            return;
        }

        Set<Player> targets = this.getTargets(sender, targetName);
        if (targets.isEmpty()) {
            sender.sendMessage(Config.PREFIX + "§cNo targets found!");
            return;
        }

        boolean others = targets.size() > 1;
        if (others && !Utils.checkPermission(sender, true, "feed")) {
            return;
        }

        targets.forEach(target -> {
            target.setFoodLevel(20);
            target.setSaturation(20f);
            target.sendMessage(Config.PREFIX + "§eYou have been fed!");
        });

        if (others) {
            sender.sendMessage(Config.PREFIX + "§eFed §d" + targets.size() + " §eplayers!");
        } else if ((!(sender instanceof Player)) || (!targets.contains((Player) sender) && !targetName.equals("self"))) {
            targets.stream().findFirst().ifPresent(target -> sender.sendMessage(Config.PREFIX + "§eFed §d" + target.getName() + "§e!"));
        }
    }

}
