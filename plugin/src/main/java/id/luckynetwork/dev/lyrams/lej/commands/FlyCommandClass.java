package id.luckynetwork.dev.lyrams.lej.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import id.luckynetwork.dev.lyrams.lej.commands.main.CommandClass;
import id.luckynetwork.dev.lyrams.lej.config.Config;
import id.luckynetwork.dev.lyrams.lej.enums.ToggleType;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FlyCommandClass extends CommandClass {

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
        ToggleType toggleType = ToggleType.getToggle(toggle);
        if (!ToggleType.getToggle(targetName).equals(ToggleType.UNKNOWN) && sender instanceof Player) {
            // the sender wants to change their own flight state
            targets = Utils.getTargets(sender, "self");
            toggleType = ToggleType.getToggle(targetName);
        } else {
            targets = Utils.getTargets(sender, targetName);
        }

        if (targets.isEmpty()) {
            return;
        }

        boolean others = targets.size() > 1;
        if (others && !Utils.checkPermission(sender, true, "fly")) {
            return;
        }

        ToggleType finalToggleType = toggleType;
        targets.forEach(target -> {
            switch (finalToggleType) {
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
        }
    }

    @Suggestions("players")
    public List<String> players(CommandContext<CommandSender> context, String current) {
        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName)
                .filter(it -> it.toLowerCase().startsWith(current.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Suggestions("toggles")
    public List<String> toggles(CommandContext<CommandSender> context, String current) {
        return Stream.of("on", "off", "toggle")
                .filter(it -> it.toLowerCase().startsWith(current.toLowerCase()))
                .collect(Collectors.toList());
    }
}
