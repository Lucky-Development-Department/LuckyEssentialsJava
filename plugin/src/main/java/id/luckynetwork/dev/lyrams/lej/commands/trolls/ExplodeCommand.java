package id.luckynetwork.dev.lyrams.lej.commands.trolls;

import id.luckynetwork.dev.lyrams.lej.commands.trolls.api.TrollCommand;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ExplodeCommand extends TrollCommand {

    public ExplodeCommand() {
        super("explode");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            this.sendDefaultMessage(sender);
            return;
        }

        String targetName = args[0];
        Float power = null;
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
                        power = Float.parseFloat(split2[0]);
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
        if (!Utils.checkPermission(sender, "trolls.explode")) {
            return;
        }

        Set<Location> locations = new HashSet<>();
        TargetsCallback targets = new TargetsCallback();
        if (targetName.equals("self") && sender instanceof Player) {
            locations.add(((Player) sender).getTargetBlock(null, 120).getLocation());
        } else {
            targets = this.getTargets(sender, targetName);
        }

        if (!targets.isEmpty()) {
            locations.addAll(targets.stream().map(Player::getLocation).collect(Collectors.toList()));
        }

        float finalPower = power == null ? 4.0F : power;
        boolean finalDamage = damage == null || damage;
        TargetsCallback finalTargets = targets;
        plugin.getConfirmationManager().requestConfirmation(() -> {
            locations.forEach(location -> location.getWorld().createExplosion(location, finalPower, finalDamage));

            boolean others = !finalTargets.isEmpty() && finalTargets.size() > 1;
            if (others) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eExploded §d" + finalTargets.size() + " §eplayers.");
            } else if ((!(sender instanceof Player)) || (finalTargets.doesNotContain((Player) sender) && !targetName.equals("self"))) {
                finalTargets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eExploded §d" + target.getName() + "§e."));
            }
        }, this.canSkip("troll-Explode", targets, sender));
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eExplode command:");
        sender.sendMessage("§8└─ §e/explode §8- §7Shows this message");
        sender.sendMessage("§8└─ §e/explode <player> §8- §7Explodes the specified player");
        sender.sendMessage("§8└─ §e/explode <player> -p <power> §8- §7Explodes the specified player with the specified power");
        sender.sendMessage("§8└─ §e/explode <player> -d <true/false> §8- §7Explodes the specified player with damage");
        sender.sendMessage("§8└─ §e/explode <player> -p <power> -d <true/false> §8- §7Explodes the specified player with the specified power and damage");
    }
}
