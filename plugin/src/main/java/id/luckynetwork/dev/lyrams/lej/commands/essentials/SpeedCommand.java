package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.enums.SpeedType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpeedCommand extends CommandClass {

    public SpeedCommand() {
        super("speed");
    }

    public void speedCommand(CommandSender sender, String targetName, String typeOrSpeed, Float speed, Boolean silent) {
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
        plugin.getConfirmationManager().requestConfirmation(() -> {
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
                    target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eYour §d" + speedType.getDisplay() + " §espeed has been set to §d" + speedType.getSpeed() + "§e.");
                }
            });

            if (others) {
                if (targets.size() == 1) {
                    targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet §6" + speedType.getDisplay() + " §espeed to §b" + speedType.getSpeed() + " §efor §d" + target.getName() + "§e."));
                } else {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet §6" + speedType.getDisplay() + " §espeed to §b" + speedType.getSpeed() + " §espeed for §d" + targets.size() + " §eplayers.");
                }
            } else if (!(sender instanceof Player) || targets.doesNotContain((Player) sender)) {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet §6" + speedType.getDisplay() + " §espeed to §b" + speedType.getSpeed() + " §efor §d" + target.getName() + "§e."));
            }
        }, this.canSkip("set player speed", targets, sender));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "speed")) {
            return;
        }

        if (args.length == 0) {
            this.sendDefaultMessage(sender);
            return;
        }

        String targetName = "self";
        String typeOrSpeed = "walking";
        Float speed = null;
        boolean silent = String.join(" ", args).contains("-s");

        if (args.length >= 1) {
            typeOrSpeed = args[0];
        }

        if (args.length >= 2) {
            SpeedType speedType = SpeedType.getType(sender, typeOrSpeed);
            if (speedType != null) {
                typeOrSpeed = args[1];
            } else {
                try {
                    speed = Float.parseFloat(args[1]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid speed!");
                    return;
                }
            }
        }

        if (args.length >= 3) {
            try {
                speed = Float.parseFloat(args[2]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid speed!");
                return;
            }
        }

        this.speedCommand(sender, targetName, typeOrSpeed, speed, silent);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eSpeed command:");
        sender.sendMessage("§8└─ §e/speed <target> <typeOrSpeed> [speed] §8- §7Sets walking or flying speed");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "speed")) {
            return null;
        }

        if (args.length == 1) {
            return this.players(args[0]);
        } else if (args.length == 2) {
            return Stream.of("walking", "flying").filter(it -> it.toLowerCase().startsWith(args[1])).collect(Collectors.toList());
        }

        return null;
    }
}
