package id.luckynetwork.dev.lyrams.lej.commands.trolls.api;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.enums.ToggleType;
import id.luckynetwork.dev.lyrams.lej.enums.TrollType;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class TrollCommand extends CommandClass {

    public TrollCommand(String command) {
        super(command);
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "trolls." + alias, true)) {
            return null;
        }

        if (args.length == 1) {
            return this.players(args[0]);
        } else if (args.length == 2) {
            if (alias.equalsIgnoreCase("launch")) {
                return Stream.of("-d", "-p")
                        .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            } else if (alias.equalsIgnoreCase("explode")) {
                return Stream.of("-d", "-p")
                        .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }

            return Stream.of("on", "off", "toggle")
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return null;
    }

    protected String[] getTargetToggle(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendDefaultMessage(sender);
            return null;
        }

        String targetName = args[0];
        String toggle = "toggle";
        if (args.length > 1) {
            toggle = args[1];
        }

        return new String[]{targetName, toggle};
    }

    /**
     * toggles a troll for a player
     *
     * @param sender     the command sender
     * @param targetName the targetName
     * @param toggle     the toggle
     * @param trollType  the {@link TrollType}
     */
    protected void toggleTroll(CommandSender sender, String targetName, String toggle, TrollType trollType) {
        if (!Utils.checkPermission(sender, "trolls." + trollType.getDisplay())) {
            return;
        }

        TargetsCallback targets;
        ToggleType toggleType;
        if (!ToggleType.getToggle(targetName).equals(ToggleType.UNKNOWN) && sender instanceof Player) {
            // the sender wants to change their own troll state
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

        plugin.getConfirmationManager().requestConfirmation(() -> {
            targets.forEach(target -> {
                switch (toggleType) {
                    case ON: {
                        Utils.applyMetadata(target, trollType.getMetadataKey(), true);
                        break;
                    }
                    case OFF: {
                        Utils.removeMetadata(target, trollType.getMetadataKey());
                        break;
                    }
                    case TOGGLE: {
                        if (target.hasMetadata(trollType.getMetadataKey())) {
                            Utils.removeMetadata(target, trollType.getMetadataKey());
                        } else {
                            Utils.applyMetadata(target, trollType.getMetadataKey(), true);
                        }
                        break;
                    }
                }
            });

            if (targets.size() > 1) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eToggled §6" + trollType.getDisplay() + " §efor §d" + targets.size() + " §eplayers.");
            } else if (targets.size() == 1 || (!(sender instanceof Player) || targets.doesNotContain((Player) sender))) {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eToggled §6" + trollType.getDisplay() + " §efor §d" + target.getName() + "§e: " + Utils.colorizeTrueFalse(target.hasMetadata(trollType.getMetadataKey()), TrueFalseType.ON_OFF)));
            }
        }, this.canSkip("troll-" + trollType.getDisplay(), targets, sender));
    }

}
