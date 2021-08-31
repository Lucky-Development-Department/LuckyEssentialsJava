package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.enums.ToggleType;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DiscouragedModeCommand extends CommandClass {

    @CommandMethod("discouraged|discour info|i|check|c")
    @CommandDescription("Gets the information about the discouraged mode system")
    public void checkCommand(
            final @NonNull CommandSender sender
    ) {
        if (!Utils.checkPermission(sender, "discouraged")) {
            return;
        }

        sender.sendMessage("§eDiscouraged mode info:");
        sender.sendMessage("§8└─ §eState: " + Utils.colorizeTrueFalse(plugin.getMainConfigManager().isDiscouraged(), TrueFalseType.ON_OFF));
    }

    @CommandMethod("discouraged|discour toggle [toggle]")
    @CommandDescription("Toggles discouraged mode")
    public void toggleCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        if (!Utils.checkPermission(sender, "discouraged")) {
            return;
        }

        ToggleType toggleType = ToggleType.getToggle(toggle);
        if (toggleType.equals(ToggleType.UNKNOWN)) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown toggle type §l" + toggle + "§c!");
            return;
        }

        switch (toggleType) {
            case ON: {
                plugin.getMainConfigManager().setDiscouraged(true);
                break;
            }
            case OFF: {
                plugin.getMainConfigManager().setDiscouraged(false);
                break;
            }
            case TOGGLE: {
                plugin.getMainConfigManager().setDiscouraged(!plugin.getMainConfigManager().isDiscouraged());
                break;
            }
        }

        plugin.getMainConfigManager().save();

        boolean chatLocked = plugin.getMainConfigManager().isDiscouraged();
        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eToggled discouraged mode: " + Utils.colorizeTrueFalse(chatLocked, TrueFalseType.ON_OFF) + "§e.");
    }
}
