package id.luckynetwork.dev.lyrams.lej.commands.trolls;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.config.Config;
import id.luckynetwork.dev.lyrams.lej.enums.TrollType;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class TrollCommand extends CommandClass {

    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt check [target]")
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
            sender.sendMessage(Config.PREFIX + "§cNo targets found!");
            return;
        }
        if (targets.size() > 1) {
            sender.sendMessage(Config.PREFIX + "§cYou can only check one player at a time!");
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

}
