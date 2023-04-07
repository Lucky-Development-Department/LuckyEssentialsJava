package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import id.luckynetwork.dev.lyrams.lej.callbacks.IsIntegerCallback;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpawnMobCommand extends CommandClass {

    public SpawnMobCommand() {
        super("spawnmob");
        this.registerCommandInfo("spawnmob", "Spawn a mob");
    }

    public void spawnMobCommand(CommandSender sender, String targetOrMob, String mobOrAmount, Integer amount) {
        int spawnAmount = 1;
        EntityType entityType = null;
        List<Location> locations = new ArrayList<>();

        for (EntityType type : EntityType.values()) {
            if (type.name().equalsIgnoreCase(targetOrMob)) {
                entityType = type;
            }
        }

        if (!(sender instanceof Player) || entityType == null) {
            // /spawnmob LyraMS zombie [1]
            TargetsCallback targets = this.getTargets(sender, targetOrMob);
            if (targets.notifyIfEmpty()) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
                return;
            }

            locations.addAll(targets.stream().map(Player::getLocation).collect(Collectors.toList()));

            if (mobOrAmount == null) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlease enter the mob type!");
                return;
            }

            for (EntityType type : EntityType.values()) {
                if (type.name().equalsIgnoreCase(mobOrAmount)) {
                    entityType = type;
                }
            }

            if (entityType == null) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown mob: §l" + mobOrAmount + "§c!");
                return;
            }

            if (amount != null) {
                spawnAmount = amount;
            }
        } else {
            // /spawnmob zombie [1]
            locations.add(((Player) sender).getLocation());
            if (mobOrAmount != null) {
                try {
                    spawnAmount = Integer.parseInt(mobOrAmount);
                } catch (Exception ignored) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid number: §l" + mobOrAmount + "§c!");
                    return;
                }
            }
        }

        EntityType finalEntityType = entityType;
        for (int i = 0; i < spawnAmount; i++) {
            try {
                locations.forEach(location -> location.getWorld().spawn(location, finalEntityType.getEntityClass()));
            } catch (Exception ignored) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cFailed summoning §l" + entityType.name() + "§c!");
                return;
            }
        }

        if (locations.size() > 1) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSummoned §6" + spawnAmount + "x " + entityType.name() + " §eat " + locations.size() + " locations §e.");
        } else {
            if (sender instanceof Player) {
                if (locations.get(0).equals(((Player) sender).getLocation())) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSummoned §d" + spawnAmount + "x " + entityType.name() + " §eat your location.");
                } else {
                    IsIntegerCallback isInteger = Utils.isInteger(mobOrAmount);
                    String targetName = isInteger.isInteger() ? mobOrAmount : targetOrMob;
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSummoned §6" + spawnAmount + "x " + entityType.name() + " §eat " + targetName + "§e.");
                }
            } else {
                IsIntegerCallback isInteger = Utils.isInteger(mobOrAmount);
                String targetName = isInteger.isInteger() ? mobOrAmount : targetOrMob;
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSummoned §6" + spawnAmount + "x " + entityType.name() + " §eat " + targetName + "§e.");
            }
        }
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "spawnmob")) {
            return;
        }

        if (args.length == 0) {
            this.sendDefaultMessage(sender);
            return;
        }

        String targetOrMob = args[0];
        String mobOrAmount = args.length > 1 ? args[1] : null;
        int amount = args.length > 2 ? Integer.parseInt(args[2]) : 1;

        this.spawnMobCommand(sender, targetOrMob, mobOrAmount, amount);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eSpawnmob command:");
        sender.sendMessage("§8└─ §e/spawnmob <mob> §8- §7Summons an entity at your location");
        sender.sendMessage("§8└─ §e/spawnmob <mob> <amount> §8- §7Summons an entity at your location with a set amount");
        sender.sendMessage("§8└─ §e/spawnmob <target> <mob> §8- §7Summons an entity at other player's location");
        sender.sendMessage("§8└─ §e/spawnmob <target> <mob> <amount> §8- §7Summons an entity at other player's location with a set amount");
    }


    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        return null;
    }
}
