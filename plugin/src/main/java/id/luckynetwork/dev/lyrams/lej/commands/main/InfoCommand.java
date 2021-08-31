package id.luckynetwork.dev.lyrams.lej.commands.main;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public class InfoCommand extends CommandClass {

    @CommandMethod("luckyessentials info|ver")
    @CommandDescription("Information about the plugin")
    public void infoCommand(
            final @NonNull CommandSender sender
    ) {
        sender.sendMessage(Utils.getPluginDescription());
    }

}
