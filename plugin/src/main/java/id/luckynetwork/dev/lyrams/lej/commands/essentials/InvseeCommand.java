package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.config.Config;
import id.luckynetwork.dev.lyrams.lej.utils.InvseeUtils;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.List;

public class InvseeCommand extends CommandClass {

    private final List<Integer> separatorSlots = Arrays.asList(36, 37, 38, 39, 40, 41, 42, 43, 44, 49);
    private final List<Integer> armorSlots = Arrays.asList(36, 37, 38, 39, 40, 41, 42, 43, 44);

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
            player.sendMessage(Config.PREFIX + "§cNo targets found!");
            return;
        }
        if (targets.size() > 1) {
            player.sendMessage(Config.PREFIX + "§cYou can only check one player at a time!");
            return;
        }

        targets.stream().findFirst().ifPresent(target -> {
            InvseeUtils.invsee(player, target);
            Utils.applyMetadata(player, "INVSEE", true);
        });

    }

}
