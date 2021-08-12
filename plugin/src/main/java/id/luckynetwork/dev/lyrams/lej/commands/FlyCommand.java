package id.luckynetwork.dev.lyrams.lej.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import id.luckynetwork.dev.lyrams.lej.commands.main.CommandClass;
import id.luckynetwork.dev.lyrams.lej.config.Config;
import id.luckynetwork.dev.lyrams.lej.enums.ToggleType;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Set;

public class FlyCommand extends CommandClass {

    @CommandMethod("fly [target] [toggle]")
    @CommandDescription("Toggles flight for you or other player")
    public void flyCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        if (!Utils.checkPermission(sender, "fly")) {
            return;
        }

        Set<Player> targets;
        ToggleType toggleType;
        if (!ToggleType.getToggle(targetName).equals(ToggleType.UNKNOWN) && sender instanceof Player) {
            // the sender wants to change their own flight state
            targets = Utils.getTargets(sender, "self");
            toggleType = ToggleType.getToggle(targetName);
        } else {
            targets = Utils.getTargets(sender, targetName);
            toggleType = ToggleType.getToggle(toggle);
        }

        if (targets.isEmpty()) {
            sender.sendMessage(Config.PREFIX + "§cNo targets found!");
            return;
        }

        if (toggleType.equals(ToggleType.UNKNOWN)) {
            sender.sendMessage(Config.PREFIX + "§cUnknown toggle type §l" + toggle + "§c!");
            return;
        }

        boolean others = targets.size() > 1;
        if (others && !Utils.checkPermission(sender, true, "fly")) {
            return;
        }

        targets.forEach(target -> {
            switch (toggleType) {
                case ON: {
                    target.setAllowFlight(true);
                    break;
                }
                case OFF: {
                    target.setAllowFlight(false);
                    break;
                }
                case TOGGLE: {
                    target.setAllowFlight(!target.getAllowFlight());
                    break;
                }
            }

            boolean allowFlight = target.getAllowFlight();
            target.sendMessage(Config.PREFIX + "§eFlight mode: " + Utils.colorizeTrueFalse(allowFlight, TrueFalseType.ON_OFF) + "§e!");
        });

        if (others) {
            sender.sendMessage(Config.PREFIX + "§eToggled flight for §d" + targets.size() + " §eplayers!");
        } else {
            targets.stream().findFirst().ifPresent(target -> sender.sendMessage(Config.PREFIX + "§eFlight mode for §d" + target.getName() + "§e: " + Utils.colorizeTrueFalse(target.getAllowFlight(), TrueFalseType.ON_OFF) + "§e!"));
        }
    }

}
