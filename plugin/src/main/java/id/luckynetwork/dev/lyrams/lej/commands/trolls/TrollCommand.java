package id.luckynetwork.dev.lyrams.lej.commands.trolls;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.ProxiedBy;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.enums.TrollType;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public class TrollCommand extends CommandClass {

    @CommandMethod("luckytrolls|luckytroll|troll|lt check [target]")
    @CommandDescription("Checks active trolls on target")
    public void checkCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName
    ) {
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

    @ProxiedBy("trolls")
    @CommandMethod("luckytrolls|luckytroll|troll|lt list")
    @CommandDescription("Lists all available trolls")
    public void listCommand(
            final @NonNull CommandSender sender
    ) {
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

}
