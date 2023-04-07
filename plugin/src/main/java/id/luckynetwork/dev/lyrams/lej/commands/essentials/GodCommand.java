package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import com.google.common.base.Joiner;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.enums.ToggleType;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GodCommand extends CommandClass {

    public GodCommand() {
        super("god");
    }

    public void godCommand(CommandSender sender, String targetName, String toggle, Boolean silent) {
        TargetsCallback targets;
        ToggleType toggleType;
        if (!ToggleType.getToggle(targetName).equals(ToggleType.UNKNOWN) && sender instanceof Player) {
            // the sender wants to change their own god-mode state
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
        if (others && !Utils.checkPermission(sender, true, "god")) {
            return;
        }

        plugin.getConfirmationManager().requestConfirmation(() -> {
            targets.forEach(target -> {
                switch (toggleType) {
                    case ON: {
                        Utils.applyMetadata(target, "GOD", true);
                        break;
                    }
                    case OFF: {
                        Utils.removeMetadata(target, "GOD");
                        break;
                    }
                    case TOGGLE: {
                        if (target.hasMetadata("GOD")) {
                            Utils.removeMetadata(target, "GOD");
                        } else {
                            Utils.applyMetadata(target, "GOD", true);
                        }
                        break;
                    }
                }

                boolean godMode = target.hasMetadata("GOD");
                if (silent == null || !silent) {
                    target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eGod mode: " + Utils.colorizeTrueFalse(godMode, TrueFalseType.ON_OFF) + "§e.");
                }
            });

            if (others) {
                if (targets.size() == 1) {
                    targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eGod mode for §d" + target.getName() + "§e: " + Utils.colorizeTrueFalse(target.hasMetadata("GOD"), TrueFalseType.ON_OFF) + "§e."));
                } else {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eToggled god for §d" + targets.size() + " §eplayers.");
                }
            } else if (!(sender instanceof Player) || targets.doesNotContain((Player) sender)) {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eGod mode for §d" + target.getName() + "§e: " + Utils.colorizeTrueFalse(target.hasMetadata("GOD"), TrueFalseType.ON_OFF) + "§e."));
            }
        }, this.canSkip("godmode toggle", targets, sender));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "god")) {
            return;
        }

        if (args.length == 0 && !(sender instanceof Player)) {
            sender.sendMessage("§cPlease specify a player!");
            return;
        }

        String targetName = "self";
        String toggle = "toggle";
        boolean silent = Joiner.on(" ").join(args).contains("-s");

        if (args.length >= 1) {
            targetName = args[0];
        }

        if (args.length >= 2) {
            toggle = args[1];
        }

        this.godCommand(sender, targetName, toggle, silent);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {

    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "god")) {
            return null;
        }

        if (args.length == 1) {
            return this.players(args[0]);
        } else if (args.length == 2) {
            return Stream.of("on", "off", "toggle")
                    .filter(it -> it.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return null;
    }
}
