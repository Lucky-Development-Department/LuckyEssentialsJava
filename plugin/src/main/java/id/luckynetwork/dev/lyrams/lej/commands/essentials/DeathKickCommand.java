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

public class DeathKickCommand extends CommandClass {

    @CommandMethod("deathkick info|i|check|c")
    @CommandDescription("Gets the information about the current death kick system state")
    public void checkCommand(
            final @NonNull CommandSender sender
    ) {
        if (!Utils.checkPermission(sender, "deathkick")) {
            return;
        }

        sender.sendMessage("§eDeath kick mode info:");
        sender.sendMessage("§8└─ §eState: " + Utils.colorizeTrueFalse(plugin.getMainConfigManager().isDeathKick(), TrueFalseType.ON_OFF));
    }

    @CommandMethod("deathkick toggle [toggle]")
    @CommandDescription("Toggles death kick mode")
    public void toggleCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        if (!Utils.checkPermission(sender, "deathkick")) {
            return;
        }

        ToggleType toggleType = ToggleType.getToggle(toggle);
        if (toggleType.equals(ToggleType.UNKNOWN)) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown toggle type §l" + toggle + "§c!");
            return;
        }

        switch (toggleType) {
            case ON: {
                plugin.getMainConfigManager().setDeathKick(true);
                break;
            }
            case OFF: {
                plugin.getMainConfigManager().setDeathKick(false);
                break;
            }
            case TOGGLE: {
                plugin.getMainConfigManager().setDeathKick(!plugin.getMainConfigManager().isDeathKick());
                break;
            }
        }

        plugin.getMainConfigManager().save();

        boolean deathKick = plugin.getMainConfigManager().isDeathKick();
        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eToggled death kick mode: " + Utils.colorizeTrueFalse(deathKick, TrueFalseType.ON_OFF) + "§e.");
    }
}
