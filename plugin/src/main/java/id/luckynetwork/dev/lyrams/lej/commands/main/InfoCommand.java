package id.luckynetwork.dev.lyrams.lej.commands.main;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import com.google.common.base.Joiner;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public class InfoCommand extends CommandClass {

    private final String pluginDescription = plugin.getMainConfigManager().getPrefix() + "§eThis server is running §aLuckyEssentials §c" + plugin.getDescription().getVersion() + " §eby §d" + Joiner.on(",").join(plugin.getDescription().getAuthors());

    @CommandMethod("luckyessentials info|ver")
    @CommandDescription("Information about the plugin")
    public void infoCommand(
            final @NonNull CommandSender sender
    ) {
        sender.sendMessage(pluginDescription);
    }

}
