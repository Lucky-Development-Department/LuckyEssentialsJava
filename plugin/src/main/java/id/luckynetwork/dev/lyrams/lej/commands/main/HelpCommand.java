package id.luckynetwork.dev.lyrams.lej.commands.main;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.specifier.Greedy;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class HelpCommand extends CommandClass {

    @CommandMethod("luckyessentials help|? [query]")
    @CommandDescription("Help menu for LuckyEssentials")
    public void helpCommand(
            final @NonNull CommandSender sender,
            final @Nullable @Argument(value = "query", description = "The subcommand or the help page") @Greedy String query
    ) {
        plugin.getMainCommand().getMinecraftHelp().queryCommands(query == null ? "" : query, sender);
    }

}
