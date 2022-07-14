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

public class SlotsCommand extends CommandClass {

    @CommandMethod("slots|slot info|i|check|c")
    @CommandDescription("Gets the information about the current LuckyEssentials slots system configuration")
    public void infoCommand(
            final @NonNull CommandSender sender
    ) {
        if (!Utils.checkPermission(sender, "slots")) {
            return;
        }

        sender.sendMessage("§eSlots system info:");
        sender.sendMessage("§8├─ §eState: " + Utils.colorizeTrueFalse(plugin.getSlotsManager().isEnabled(), TrueFalseType.ON_OFF));
        sender.sendMessage("§8└─ §eMax Players: §a" + plugin.getSlotsManager().getMaxPlayers());
    }

    @CommandMethod("slots set <amount>")
    @CommandDescription("Sets the max player for the LuckyEssentials slots system")
    public void setCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "amount", description = "The target player", defaultValue = "self", suggestions = "players") Integer amount
    ) {
        if (!Utils.checkPermission(sender, "slots")) {
            return;
        }

        plugin.getSlotsManager().setMaxPlayers(amount);
        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet the max players to §d" + amount + "§e.");
        plugin.getSlotsManager().save();
    }

    @CommandMethod("slots toggle [toggle]")
    @CommandDescription("Toggles on or off the LuckyEssentials slots system")
    public void toggleCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        if (!Utils.checkPermission(sender, "slots")) {
            return;
        }

        ToggleType toggleType = ToggleType.getToggle(toggle);
        if (toggleType.equals(ToggleType.UNKNOWN)) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown toggle type §l" + toggle + "§c!");
            return;
        }

        switch (toggleType) {
            case ON: {
                plugin.getSlotsManager().setEnabled(true);
                break;
            }
            case OFF: {
                plugin.getSlotsManager().setEnabled(false);
                break;
            }
            case TOGGLE: {
                plugin.getSlotsManager().setEnabled(!plugin.getSlotsManager().isEnabled());
                break;
            }
        }

        plugin.getSlotsManager().save();
        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eToggled slots system " + Utils.colorizeTrueFalse(plugin.getSlotsManager().isEnabled(), TrueFalseType.ON_OFF) + "§e.");
    }

    @CommandMethod("slots on")
    @CommandDescription("Toggles on the LuckyEssentials slots system")
    public void onCommand(
            final @NonNull CommandSender sender
    ) {
        if (!Utils.checkPermission(sender, "slots")) {
            return;
        }

        plugin.getSlotsManager().setEnabled(true);
        plugin.getSlotsManager().save();
        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eToggled slots system " + Utils.colorizeTrueFalse(plugin.getSlotsManager().isEnabled(), TrueFalseType.ON_OFF) + "§e.");
    }

    @CommandMethod("slots off")
    @CommandDescription("Toggles off the LuckyEssentials slots system")
    public void offCommand(
            final @NonNull CommandSender sender
    ) {
        if (!Utils.checkPermission(sender, "slots")) {
            return;
        }

        plugin.getSlotsManager().setEnabled(false);
        plugin.getSlotsManager().save();
        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eToggled slots system " + Utils.colorizeTrueFalse(plugin.getSlotsManager().isEnabled(), TrueFalseType.ON_OFF) + "§e.");
    }

    @CommandMethod("slots reload")
    @CommandDescription("Reloads the LuckyEssentials slots system")
    public void reloadCommand(
            final @NonNull CommandSender sender
    ) {
        if (!Utils.checkPermission(sender, "slots")) {
            return;
        }

        plugin.getSlotsManager().reload();
        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eReloaded the slots system.");
    }
}
