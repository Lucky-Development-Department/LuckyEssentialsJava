package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.enums.ToggleType;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeathKickCommand extends CommandClass {

    public DeathKickCommand() {
        super("deathkick");
        this.registerCommandInfo("deathkick", "Manages the death-kick mode");
    }

    public void checkCommand(CommandSender sender) {
        sender.sendMessage("§eDeath kick mode info:");
        sender.sendMessage("§8└─ §eState: " + Utils.colorizeTrueFalse(plugin.getMainConfigManager().isDeathKick(), TrueFalseType.ON_OFF));
    }

    public void toggleCommand(CommandSender sender, String toggle) {
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

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "deathkick")) {
            return;
        }

        if (args.length == 0) {
            this.sendDefaultMessage(sender);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "info":
            case "i":
            case "check":
            case "c": {
                this.checkCommand(sender);
                break;
            }
            case "toggle": {
                String toggle = args.length > 1 ? args[1] : "toggle";
                this.toggleCommand(sender, toggle);
                break;
            }
            default: {
                this.sendDefaultMessage(sender);
            }
        }
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eDeath-kick command:");
        sender.sendMessage("§8└─ §e/deathkick info §8- §7Check the deathkick mode");
        sender.sendMessage("§8└─ §e/deathkick toggle [<toggle/on/off>] §8- §7Toggle the deathkick mode");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (Utils.checkPermission(sender, "deathkick", true)) {
            if (args.length == 1) {
                return Stream.of("info", "toggle")
                        .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("toggle")) {
                    return Stream.of("toggle", "on", "off")
                            .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                }
            }
        }

        return null;
    }
}
