package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.Flag;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.enums.ToggleType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ChatLockCommand extends CommandClass {

    @CommandMethod("chatlock|lockchat <toggle>")
    @CommandDescription("Toggles chatlock")
    public void godCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle,
            final @Nullable @Flag(value = "silent", aliases = "s", description = "Should it not send a notification about the chatlock") Boolean silent
    ) {
        if (!Utils.checkPermission(sender, "chatlock")) {
            return;
        }

        ToggleType toggleType = ToggleType.getToggle(toggle);
        switch (toggleType) {
            case ON: {
                break;
            }
            case OFF: {
                break;
            }
            case TOGGLE: {
                break;
            }
            case UNKNOWN: {
                sender.sendMessage("");
                return;
            }
        }
    }
}
