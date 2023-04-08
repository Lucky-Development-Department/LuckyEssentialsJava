package id.luckynetwork.dev.lyrams.lej.commands.trolls;

import id.luckynetwork.dev.lyrams.lej.commands.trolls.api.TrollCommand;
import id.luckynetwork.dev.lyrams.lej.enums.ToggleType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LaunchCommand extends TrollCommand {

    public LaunchCommand() {
        super("launch");
        this.registerCommandInfo("launch", "Launches a player into the air");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            this.sendDefaultMessage(sender);
            return;
        }

        String targetName = args[0];
        Double power = null;
        Boolean damage = null;

        // Check if power flag is present
        // -p 10.0
        String allArgs = String.join(" ", args);
        if (allArgs.contains("-p")) {
            String[] split = allArgs.split("-p");
            if (split.length == 2) {
                String[] split2 = split[1].split(" ");
                if (split2.length > 0) {
                    try {
                        power = Double.parseDouble(split2[0]);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        // Check if damage flag is present
        // -d true
        if (allArgs.contains("-d")) {
            String[] split = allArgs.split("-d");
            if (split.length == 2) {
                String[] split2 = split[1].split(" ");
                if (split2.length > 0) {
                    damage = Boolean.parseBoolean(split2[0]);
                }
            }
        }

        if (!Utils.checkPermission(sender, "trolls.launch")) {
            return;
        }

        TargetsCallback targets;
        if (!ToggleType.getToggle(targetName).equals(ToggleType.UNKNOWN) && sender instanceof Player) {
            targets = this.getTargets(sender, "self");
        } else {
            targets = this.getTargets(sender, targetName);
        }

        if (targets.notifyIfEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        double finalPower = power == null ? 10.0 : power;
        boolean finalDamage = damage != null && damage;
        plugin.getConfirmationManager().requestConfirmation(() -> {
            targets.forEach(target -> {
                Location location = target.getLocation();
                target.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), (float) finalPower, finalDamage, finalDamage);
                if (finalDamage) {
                    target.getWorld().strikeLightning(location);
                    target.getWorld().strikeLightning(location);
                    target.getWorld().strikeLightning(location);
                } else {
                    target.getWorld().strikeLightningEffect(location);
                    target.getWorld().strikeLightningEffect(location);
                    target.getWorld().strikeLightningEffect(location);
                }
                target.setVelocity(target.getEyeLocation().getDirection().setY(finalPower));
            });

            if (targets.size() > 1) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eLaunched §d" + targets.size() + " §eplayers!");
            } else if (targets.size() == 1 || (!(sender instanceof Player)) || targets.doesNotContain((Player) sender)) {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eLaunched §d" + target.getName() + "§!"));
            }
        }, this.canSkip("troll-Launch", targets, sender));
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eLaunch command:");
        sender.sendMessage("§8└─ §e/launch §8- §7Shows this message");
        sender.sendMessage("§8└─ §e/launch <player> §8- §7Launches the specified player");
        sender.sendMessage("§8└─ §e/launch <player> -p <power> §8- §7Launches the specified player with the specified power");
        sender.sendMessage("§8└─ §e/launch <player> -d <true/false> §8- §7Launches the specified player with damage");
        sender.sendMessage("§8└─ §e/launch <player> -p <power> -d <true/false> §8- §7Launches the specified player with the specified power and damage");
    }
}
