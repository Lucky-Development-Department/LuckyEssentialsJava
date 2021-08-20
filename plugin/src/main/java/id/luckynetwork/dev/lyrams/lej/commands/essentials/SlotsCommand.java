package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.config.Config;
import id.luckynetwork.dev.lyrams.lej.config.SlotsConfig;
import id.luckynetwork.dev.lyrams.lej.enums.ToggleType;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SlotsCommand extends CommandClass {

    @CommandMethod("slots|slot info|i|check|c")
    @CommandDescription("Gets the information about the current LuckyEssentials slots system configuration")
    public void slotsInfoCommand(
            final @NonNull CommandSender sender
    ) {
        if (!Utils.checkPermission(sender, "slots")) {
            return;
        }

        sender.sendMessage("§eSlots system info:");
        sender.sendMessage("§8├─ §eState: " + Utils.colorizeTrueFalse(SlotsConfig.enabled, TrueFalseType.ON_OFF));
        sender.sendMessage("§8└─ §eMax Players: §a" + SlotsConfig.maxPlayers);
    }

    @CommandMethod("slots set <amount>")
    @CommandDescription("Sets the max player for the LuckyEssentials slots system")
    public void slotsSetCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "amount", description = "The target player", defaultValue = "self", suggestions = "players") Integer amount
    ) {
        if (!Utils.checkPermission(sender, "slots")) {
            return;
        }

        SlotsConfig.maxPlayers = amount;
        sender.sendMessage(Config.PREFIX + "§eSet the max players to §d" + amount + "§e!");
        SlotsConfig.save();
    }

    @CommandMethod("slots toggle [toggle]")
    @CommandDescription("Toggles on or off the LuckyEssentials slots system")
    public void slotsToggleCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        if (!Utils.checkPermission(sender, "slots")) {
            return;
        }

        ToggleType toggleType = ToggleType.getToggle(toggle);
        if (toggleType.equals(ToggleType.UNKNOWN)) {
            sender.sendMessage(Config.PREFIX + "§cUnknown toggle type §l" + toggle + "§c!");
            return;
        }

        switch (toggleType) {
            case ON: {
                SlotsConfig.enabled = true;
                break;
            }
            case OFF: {
                SlotsConfig.enabled = false;
                break;
            }
            case TOGGLE: {
                SlotsConfig.enabled = !SlotsConfig.enabled;
                break;
            }
        }

        SlotsConfig.save();
        sender.sendMessage(Config.PREFIX + "§eToggled slots system " + Utils.colorizeTrueFalse(SlotsConfig.enabled, TrueFalseType.ON_OFF) + "§e!");
    }

    @CommandMethod("slots reload")
    @CommandDescription("Reloads the LuckyEssentials slots system")
    public void slotsReloadCommand(
            final @NonNull CommandSender sender
    ) {
        if (!Utils.checkPermission(sender, "slots")) {
            return;
        }

        SlotsConfig.reload();
        sender.sendMessage(Config.PREFIX + "§eReloaded the slots system!");
    }
}
