package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.specifier.Greedy;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class KickAllCommand extends CommandClass {

    @CommandMethod("kickall [reason]")
    @CommandDescription("Kicks all online player")
    public void kickAllCommand(
            final @NonNull CommandSender sender,
            final @Nullable @Argument(value = "reason", description = "A custom reason") @Greedy String reason
    ) {
        if (!Utils.checkPermission(sender, "kickall")) {
            return;
        }

        String message = reason == null ? "§cKicked by a staff member!" : Utils.colorize(reason);
        Bukkit.getOnlinePlayers().forEach(target -> {
            if (target != sender && !Utils.checkPermission(target, "kickall.bypass", false, false, true, null)) {
                target.kickPlayer(message);
            }
        });

        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eKicked all players.");
    }
}
