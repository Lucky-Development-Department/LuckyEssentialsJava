package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.enums.ToggleType;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChatLockCommand extends CommandClass {

    public ChatLockCommand() {
        super("chatlock", Arrays.asList("lockchat", "cl"));
        this.registerCommandInfo("chatlock", "Manages the chat-lock mode");
    }

    public void checkCommand(CommandSender sender) {
        sender.sendMessage("§eChat lock info:");
        sender.sendMessage("§8└─ §eState: " + Utils.colorizeTrueFalse(plugin.getMainConfigManager().isChatLocked(), TrueFalseType.ON_OFF));
    }

    public void toggleCommand(CommandSender sender, String toggle, Boolean silent) {
        ToggleType toggleType = ToggleType.getToggle(toggle);
        if (toggleType.equals(ToggleType.UNKNOWN)) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown toggle type §l" + toggle + "§c!");
            return;
        }

        switch (toggleType) {
            case ON: {
                plugin.getMainConfigManager().setChatLocked(true);
                break;
            }
            case OFF: {
                plugin.getMainConfigManager().setChatLocked(false);
                break;
            }
            case TOGGLE: {
                plugin.getMainConfigManager().setChatLocked(!plugin.getMainConfigManager().isChatLocked());
                break;
            }
        }

        plugin.getMainConfigManager().save();

        boolean chatLocked = plugin.getMainConfigManager().isChatLocked();
        if (silent == null || !silent) {
            Bukkit.getOnlinePlayers().forEach(online -> {
                online.sendMessage("§6§m----------------------------------------------");
                online.sendMessage("§e§lChat-lock " + Utils.colorizeTrueFalseBold(chatLocked, TrueFalseType.ON_OFF));
                online.sendMessage("§6§m----------------------------------------------");
            });
        }
        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eToggled chat-lock: " + Utils.colorizeTrueFalse(chatLocked, TrueFalseType.ON_OFF) + "§e.");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "chatlock")) {
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
                boolean silent = args[args.length - 1].equalsIgnoreCase("-s");

                this.toggleCommand(sender, toggle, silent);
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
        sender.sendMessage("§eChatLock command:");
        sender.sendMessage("§8└─ §e/chatlock info §8- §7Check the chat-lock state");
        sender.sendMessage("§8└─ §e/chatlock toggle [<toggle/on/off>] §8- §7Toggle the chat-lock state");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (Utils.checkPermission(sender, "chatlock", true)) {
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
