package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.enums.ToggleType;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SlotsCommand extends CommandClass {

    public SlotsCommand() {
        super("slots", Collections.singletonList("maxplayers"));
    }

    public void infoCommand(CommandSender sender) {
        sender.sendMessage("§eSlots system info:");
        sender.sendMessage("§8├─ §eState: " + Utils.colorizeTrueFalse(plugin.getSlotsManager().isEnabled(), TrueFalseType.ON_OFF));
        sender.sendMessage("§8└─ §eMax Players: §a" + plugin.getSlotsManager().getMaxPlayers());
    }

    public void setCommand(CommandSender sender, Integer amount) {
        plugin.getSlotsManager().setMaxPlayers(amount);
        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet the max players to §d" + amount + "§e.");
        plugin.getSlotsManager().save();
    }

    public void toggleCommand(CommandSender sender, String toggle) {
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

    public void onCommand(CommandSender sender) {
        plugin.getSlotsManager().setEnabled(true);
        plugin.getSlotsManager().save();
        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eToggled slots system " + Utils.colorizeTrueFalse(plugin.getSlotsManager().isEnabled(), TrueFalseType.ON_OFF) + "§e.");
    }

    public void offCommand(CommandSender sender) {
        plugin.getSlotsManager().setEnabled(false);
        plugin.getSlotsManager().save();
        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eToggled slots system " + Utils.colorizeTrueFalse(plugin.getSlotsManager().isEnabled(), TrueFalseType.ON_OFF) + "§e.");
    }

    public void reloadCommand(CommandSender sender) {
        plugin.getSlotsManager().reload();
        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eReloaded the slots system.");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "slots")) {
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
                this.infoCommand(sender);
                break;
            }
            case "set": {
                if (args.length == 1) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUsage: /slots set <amount>");
                    return;
                }

                int amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid number: §l" + args[1]);
                    return;
                }

                this.setCommand(sender, amount);
                break;
            }
            case "toggle": {
                if (args.length == 1) {
                    this.toggleCommand(sender, "toggle");
                    return;
                }

                this.toggleCommand(sender, args[1]);
                break;
            }
            case "on": {
                this.onCommand(sender);
                break;
            }
            case "off": {
                this.offCommand(sender);
                break;
            }
            case "reload": {
                this.reloadCommand(sender);
                break;
            }
            default: {
                this.sendDefaultMessage(sender);
                break;
            }
        }
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eSlots command:");
        sender.sendMessage("§8└─ §e/slots info §8- §7Check the slots system info");
        sender.sendMessage("§8└─ §e/slots set <amount> §8- §7Set the max players");
        sender.sendMessage("§8└─ §e/slots toggle [<on/off/toggle>] §8- §7Toggle the slots system");
        sender.sendMessage("§8└─ §e/slots on §8- §7Enable the slots system");
        sender.sendMessage("§8└─ §e/slots off §8- §7Disable the slots system");
        sender.sendMessage("§8└─ §e/slots reload §8- §7Reload the slots system");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "slots")) {
            return null;
        }

        if (args.length == 1) {
            return Stream.of("info", "set", "toggle", "on", "off", "reload")
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return null;
    }
}
