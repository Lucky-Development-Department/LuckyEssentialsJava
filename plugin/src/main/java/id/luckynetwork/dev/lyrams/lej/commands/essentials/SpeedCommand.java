package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.Flag;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.enums.SpeedType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpeedCommand extends CommandClass {

    @CommandMethod("speed <target> <typeOrSpeed> [speed]")
    @CommandDescription("Sets walking or flying speed")
    public void speedCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The the target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "typeOrSpeed", description = "walking/flying", defaultValue = "walking", suggestions = "speedTypes") String typeOrSpeed,
            final @Nullable @Argument(value = "speed", description = "The speed") Float speed,
            final @Nullable @Flag(value = "silent", aliases = "s", description = "Should the target not be notified?") Boolean silent
    ) {
        if (!Utils.checkPermission(sender, "speed")) {
            return;
        }

        TargetsCallback targets = this.getTargets(sender, targetName);
        if (targets.notifyIfEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        boolean others = targets.size() > 1 || (sender instanceof Player && targets.doesNotContain((Player) sender));
        if (others && !Utils.checkPermission(sender, true, "speed")) {
            return;
        }

        SpeedType speedType = SpeedType.getType(sender, typeOrSpeed);
        if (speedType.equals(SpeedType.UNKNOWN)) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown speed type §l" + typeOrSpeed + "§c!");
            return;
        }

        if (speed != null) {
            speedType.setSpeed(speed);
        }

        speedType.setSpeed(Math.min(Math.max(speedType.getSpeed(), 0.0001f), 10f));
        speedType.setSpeed(speedType.getSpeed() / 10f);
        targets.forEach(target -> {
            switch (speedType) {
                case WALKING: {
                    target.setWalkSpeed(speedType.getSpeed());
                    break;
                }
                case FLYING: {
                    target.setFlySpeed(speedType.getSpeed());
                    break;
                }
            }

            if (silent == null || !silent) {
                target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eYour §d" + speedType.getDisplay() + " §espeed has been set to §d" + speedType.getSpeed() + "§e!");
            }
        });

        if (others) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet §6" + speedType.getDisplay() + " §espeed to §b" + speedType.getSpeed() + " §espeed for §d" + targets.size() + " §eplayers!");
        } else if (!(sender instanceof Player) || targets.doesNotContain((Player) sender)) {
            targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet §6" + speedType.getDisplay() + " §espeed to §b" + speedType.getSpeed() + " §efor §d" + target.getName() + "§e!"));
        }
    }

    @Suggestions("speedTypes")
    public List<String> speedTypes(CommandContext<CommandSender> context, String current) {
        return Stream.of("walking", "flying")
                .filter(it -> it.toLowerCase().startsWith(current.toLowerCase()))
                .collect(Collectors.toList());
    }


}
