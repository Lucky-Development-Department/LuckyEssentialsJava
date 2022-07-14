package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import id.luckynetwork.dev.lyrams.lej.callbacks.IsIntegerCallback;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpawnMobCommand extends CommandClass {

    @CommandMethod("spawnmob <targetOrMob> [mobOrAmount] [amount]")
    @CommandDescription("Summons an entity you're looking at or at other player's location")
    public void spawnMobCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "targetOrMob", description = "The mob or the target player", suggestions = "players") String targetOrMob,
            final @Nullable @Argument(value = "mobOrAmount", description = "The mob or the amount of mobs to spawn") String mobOrAmount,
            final @Nullable @Argument(value = "amount", description = "The amount of mobs to spawn") Integer amount
    ) {
        if (!Utils.checkPermission(sender, "spawnmob")) {
            return;
        }

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

}
