package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class InvseeCommand extends CommandClass {

    @CommandMethod("invsee <target>")
    @CommandDescription("Peeks into other player's inventory")
    public void invseeCommand(
            final @NonNull Player player,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName
    ) {
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

}
