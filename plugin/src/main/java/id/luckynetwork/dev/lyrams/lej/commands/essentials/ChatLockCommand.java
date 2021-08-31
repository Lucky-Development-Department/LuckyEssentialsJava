package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.Flag;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.enums.ToggleType;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ChatLockCommand extends CommandClass {

    @CommandMethod("chatlock|lockchat info|i|check|c")
    @CommandDescription("Gets the information about the chat lock system")
    public void checkCommand(
            final @NonNull CommandSender sender
    ) {
        if (!Utils.checkPermission(sender, "chatlock")) {
            return;
        }

        sender.sendMessage("§eChat lock info:");
        sender.sendMessage("§8└─ §eState: " + Utils.colorizeTrueFalse(plugin.getMainConfigManager().isChatLocked(), TrueFalseType.ON_OFF));
    }

    @CommandMethod("chatlock|lockchat toggle [toggle]")
    @CommandDescription("Toggles chatlock")
    public void toggleCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle,
            final @Nullable @Flag(value = "silent", aliases = "s", description = "Should the target not be notified?") Boolean silent
    ) {
        if (!Utils.checkPermission(sender, "chatlock")) {
            return;
        }

        ToggleType toggleType = ToggleType.getToggle(toggle);
        if (toggleType.equals(ToggleType.UNKNOWN)) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown toggle type §l" + toggle + "§c!");
            return;
        }

        switch (toggleType) {
            case ON: {
                plugin.getMainConfigManager().setChatLocked(true);
                break;
            }
            case OFF: {
                plugin.getMainConfigManager().setChatLocked(false);
                break;
            }
            case TOGGLE: {
                plugin.getMainConfigManager().setChatLocked(!plugin.getMainConfigManager().isChatLocked());
                break;
            }
        }

        plugin.getMainConfigManager().save();

        boolean chatLocked = plugin.getMainConfigManager().isChatLocked();
        if (silent == null || !silent) {
            Bukkit.getOnlinePlayers().forEach(online -> {
                online.sendMessage("§6§m----------------------------------------------");
                online.sendMessage("§e§lChat-lock " + Utils.colorizeTrueFalseBold(chatLocked, TrueFalseType.ON_OFF));
                online.sendMessage("§6§m----------------------------------------------");
            });
        }
        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eToggled chat-lock: " + Utils.colorizeTrueFalse(chatLocked, TrueFalseType.ON_OFF) + "§e.");
    }
}
