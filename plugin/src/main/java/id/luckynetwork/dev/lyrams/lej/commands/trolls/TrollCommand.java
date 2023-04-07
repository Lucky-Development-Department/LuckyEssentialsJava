package id.luckynetwork.dev.lyrams.lej.commands.trolls;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.enums.TrollType;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

public class TrollCommand extends CommandClass {

    public TrollCommand() {
        super("luckytroll", Arrays.asList("luckytrolls", "trolls", "troll", "lt"));
        this.registerCommandInfo("luckytroll", "Manage trolls");
    }

    public void checkCommand(CommandSender sender, String targetName) {
        if (!Utils.checkPermission(sender, "trolls.check")) {
            return;
        }

        TargetsCallback targets = this.getTargets(sender, targetName);
        if (targets.notifyIfEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }
        if (targets.size() > 1) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cYou can only check one player at a time!");
            return;
        }

        targets.stream().findFirst().ifPresent(target -> {
            Map<String, String> activeTrolls = new HashMap<>();
            for (TrollType trollType : TrollType.values()) {
                activeTrolls.put(trollType.getDisplay(), Utils.colorizeTrueFalse(target.hasMetadata(trollType.getMetadataKey()), TrueFalseType.ON_OFF));
            }

            final int[] size = {activeTrolls.size()};
            sender.sendMessage("§eActive trolls for §d" + target.getName() + "§e:");
            activeTrolls.forEach((key, value) -> {
                boolean last = (--size[0] < 1);
                if (last) {
                    sender.sendMessage("§8└─ §e" + key + ": " + value);
                } else {
                    sender.sendMessage("§8├─ §e" + key + ": " + value);
                }
            });
        });
    }

    public void listCommand(CommandSender sender) {
        if (!Utils.checkPermission(sender, "trolls")) {
            return;
        }

        List<String> trolls = Arrays.stream(TrollType.values()).map(TrollType::getDisplay).collect(Collectors.toCollection(ArrayList::new));
        trolls.add("Explode");
        trolls.add("Launch");

        sender.sendMessage("§eAvailable trolls:");
        final int[] size = {trolls.size()};
        trolls.forEach((key) -> {
            boolean last = (--size[0] < 1);
            if (last) {
                sender.sendMessage("§8└─ §e" + key);
            } else {
                sender.sendMessage("§8├─ §e" + key);
            }
        });
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "trolls")) {
            return;
        }

        if (args.length == 0) {
            this.sendDefaultMessage(sender);
            return;
        }

        String subCommand = args[0];
        switch (subCommand.toLowerCase()) {
            case "check": {
                String targetName = "self";
                if (args.length >= 2) {
                    targetName = args[1];
                }

                this.checkCommand(sender, targetName);
                break;
            }
            case "list": {
                this.listCommand(sender);
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

    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        return null;
    }
}
