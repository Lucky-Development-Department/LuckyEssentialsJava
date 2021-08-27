package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.config.MainConfig;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class HealCommand extends CommandClass {

    @CommandMethod("heal [target]")
    @CommandDescription("Heals you or other player")
    public void healCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName
    ) {
        if (!Utils.checkPermission(sender, "heal")) {
            return;
        }

        TargetsCallback targets = this.getTargets(sender, targetName);
        if (targets.notifyIfEmpty()) {
            sender.sendMessage(MainConfig.PREFIX + "§cNo targets found!");
            return;
        }

        boolean others = targets.size() > 1;
        if (others && !Utils.checkPermission(sender, true, "heal")) {
            return;
        }

        targets.forEach(target -> {
            target.setHealth(plugin.getVersionSupport().getMaxHealth(target));
            target.sendMessage(MainConfig.PREFIX + "§eYou have been healed!");
        });

        if (others) {
            sender.sendMessage(MainConfig.PREFIX + "§eHealed §d" + targets.size() + " §eplayers!");
        } else if ((!(sender instanceof Player)) || (targets.doesNotContain((Player) sender) && !targetName.equals("self"))) {
            targets.stream().findFirst().ifPresent(target -> sender.sendMessage(MainConfig.PREFIX + "§eHealed §d" + target.getName() + "§e!"));
        }
    }

}
