package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.enums.ToggleType;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FlyCommand extends CommandClass {

    public FlyCommand() {
        super("fly");
        this.registerCommandInfo("fly", "Toggles flight mode");
    }

    public void flyCommand(CommandSender sender, String targetName, String toggle, Boolean silent) {
        TargetsCallback targets;
        ToggleType toggleType;
        if (!ToggleType.getToggle(targetName).equals(ToggleType.UNKNOWN) && sender instanceof Player) {
            // the sender wants to change their own flight status
            targets = this.getTargets(sender, "self");
            toggleType = ToggleType.getToggle(targetName);
        } else {
            targets = this.getTargets(sender, targetName);
            toggleType = ToggleType.getToggle(toggle);
        }

        if (targets.notifyIfEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        if (toggleType.equals(ToggleType.UNKNOWN)) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown toggle type §l" + toggle + "§c!");
            return;
        }

        boolean others = targets.size() > 1 || (sender instanceof Player && targets.doesNotContain((Player) sender));
        if (others && !Utils.checkPermission(sender, true, "fly")) {
            return;
        }

        plugin.getConfirmationManager().requestConfirmation(() -> {
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
                if (silent == null || !silent) {
                    target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eFlight mode: " + Utils.colorizeTrueFalse(allowFlight, TrueFalseType.ON_OFF) + "§e.");
                }
            });

            if (others) {
                if (targets.size() == 1) {
                    targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eFlight mode for §d" + target.getName() + "§e: " + Utils.colorizeTrueFalse(target.getAllowFlight(), TrueFalseType.ON_OFF) + "§e."));
                } else {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eToggled flight for §d" + targets.size() + " §eplayers.");
                }
            } else if (!(sender instanceof Player) || targets.doesNotContain((Player) sender)) {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eFlight mode for §d" + target.getName() + "§e: " + Utils.colorizeTrueFalse(target.getAllowFlight(), TrueFalseType.ON_OFF) + "§e."));
            }
        }, this.canSkip("toggle flight", targets, sender));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "fly")) {
            return;
        }

        if (args.length == 0 && !(sender instanceof Player)) {
            sender.sendMessage("§cPlease specify a player!");
            return;
        }

        String targetName = "self";
        String toggle = "toggle";
        Boolean silent = null;
        if (args.length == 0) {
            this.flyCommand(sender, targetName, toggle, false);
            return;
        }

        targetName = args[0];

        if (args.length >= 2) {
            toggle = args[1];
        }

        if (args[args.length - 1].equalsIgnoreCase("-s")) {
            silent = true;
        }

        this.flyCommand(sender, targetName, toggle, silent);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eFly command:");
        sender.sendMessage("§8└─ §e/fly §8- §7Toggle your flight mode");
        sender.sendMessage("§8└─ §e/fly [<player/on/off/toggle>] [<on/off/toggle>] §8- §7Toggle the flight mode of a player");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "fly")) {
            return null;
        }

        if (args.length == 1) {
            return this.players(args[0]);
        } else if (args.length == 2) {
            return Stream.of("on", "off", "toggle")
                    .filter(it -> it.toLowerCase().startsWith(args[1]))
                    .collect(Collectors.toList());
        } else if (args.length == 3) {
            return Stream.of("-s")
                    .filter(it -> it.toLowerCase().startsWith(args[2]))
                    .collect(Collectors.toList());
        }

        return Stream.of("-s")
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}
