package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.config.MainConfig;
import id.luckynetwork.dev.lyrams.lej.enums.ToggleType;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

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

        TargetsCallback targets;
        ToggleType toggleType;
        if (!ToggleType.getToggle(targetName).equals(ToggleType.UNKNOWN) && sender instanceof Player) {
            // the sender wants to change their own flight state
            targets = this.getTargets(sender, "self");
            toggleType = ToggleType.getToggle(targetName);
        } else {
            targets = this.getTargets(sender, targetName);
            toggleType = ToggleType.getToggle(toggle);
        }

        if (targets.notifyIfEmpty()) {
            sender.sendMessage(MainConfig.PREFIX + "§cNo targets found!");
            return;
        }

        if (toggleType.equals(ToggleType.UNKNOWN)) {
            sender.sendMessage(MainConfig.PREFIX + "§cUnknown toggle type §l" + toggle + "§c!");
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
            target.sendMessage(MainConfig.PREFIX + "§eFlight mode: " + Utils.colorizeTrueFalse(allowFlight, TrueFalseType.ON_OFF) + "§e!");
        });

        if (others) {
            sender.sendMessage(MainConfig.PREFIX + "§eToggled flight for §d" + targets.size() + " §eplayers!");
        } else if (!(sender instanceof Player) || targets.doesNotContain((Player) sender)) {
            targets.stream().findFirst().ifPresent(target -> sender.sendMessage(MainConfig.PREFIX + "§eFlight mode for §d" + target.getName() + "§e: " + Utils.colorizeTrueFalse(target.getAllowFlight(), TrueFalseType.ON_OFF) + "§e!"));
        }
    }

}
