package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.*;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collections;
import java.util.List;

public class TeleportCommand extends CommandClass {

    @CommandMethod("tp <player> [target]")
    @CommandDescription("Teleports you or other player to another player")
    public void teleportCommand(final @NonNull CommandSender sender, final @NonNull @Argument(value = "player", description = "The target player", defaultValue = "self", suggestions = "players") String player, final @NonNull @Argument(value = "target", description = "The second target player", defaultValue = "undefinedTarget2Placeholder", suggestions = "players") String target, final @Nullable @Flag(value = "silent", aliases = "s", description = "Should the target not be notified?") Boolean silent) {
        if (!Utils.checkPermission(sender, "teleport")) {
            return;
        }

        TargetsCallback players = this.getTargets(sender, player);
        if (players.notifyIfEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        if (target.equals("undefinedTarget2Placeholder")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid usage: §l" + player + "§c!");
                return;
            }

            // the player wants to teleport themselves to another player
            if (players.size() > 1) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid usage: §l" + player + "§c!");
                return;
            }

            players.stream().findFirst().ifPresent(destination -> {
                ((Player) sender).teleport(destination);
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported you to §d" + destination.getName() + "§e.");
            });
        } else {
            TargetsCallback targets = this.getTargets(sender, target);
            if (targets.isEmpty()) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
                return;
            }

            if (targets.size() > 1) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid usage: §l" + target + "§c!");
                return;
            }

            if (!Utils.checkPermission(sender, true, "teleport")) {
                return;
            }

            // the sender wants to teleport other player to another player
            targets.stream().findFirst().ifPresent(destination -> {
                players.forEach(toTeleport -> {
                    toTeleport.teleport(destination);
                    if (silent == null || !silent) {
                        toTeleport.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported you to §d" + destination.getName() + "§e.");
                    }
                });

                if (players.size() > 1) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported §d" + players.size() + " §eplayers to §6" + destination.getName() + "§e.");
                } else {
                    players.stream().findFirst().ifPresent(it -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported §d" + it.getName() + " §eto §6" + destination.getName() + "§e."));
                }
            });
        }
    }

    @CommandMethod("tpall [target]")
    @CommandDescription("Teleports all player to you or another player")
    public void teleportAllCommand(final @NonNull CommandSender sender, final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String target, final @Nullable @Flag(value = "silent", aliases = "s", description = "Should the target not be notified?") Boolean silent) {
        if (!Utils.checkPermission(sender, "teleport.all")) {
            return;
        }

        TargetsCallback targets = this.getTargets(sender, target);
        if (targets.isEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        if (targets.size() > 1) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid usage: §l" + target + "§c!");
            return;
        }

        // the sender wants to teleport other player to another player
        plugin.getConfirmationManager().requestConfirmation(() -> targets.stream().findFirst().ifPresent(destination -> {
            Bukkit.getOnlinePlayers().forEach(toTeleport -> {
                if (sender instanceof Player && toTeleport.equals(sender)) {
                    return;
                }

                toTeleport.teleport(destination);
                if (silent == null || !silent) {
                    toTeleport.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported you to §d" + destination.getName() + "§e.");
                }
            });

            if (destination.getName().equalsIgnoreCase(sender.getName())) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported §d" + Bukkit.getOnlinePlayers().size() + " §eplayers to §6you§e.");
            } else {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported §d" + Bukkit.getOnlinePlayers().size() + " §eplayers to §6" + destination.getName() + "§e.");
            }
        }), sender, Collections.singletonList(plugin.getMainConfigManager().getPrefix() + "§eAre you sure you want to execute §dteleport all player§e?"));
    }

    @CommandMethod("tppos <location>")
    @CommandDescription("Teleports you to a certain location")
    public void teleportPositionCommand(final @NonNull Player sender, final @NonNull @Argument(value = "location", description = "The target location") Location location, final @Nullable @Flag(value = "world", aliases = "w", description = "The target location world") String worldName, final @Nullable @Flag(value = "yaw", aliases = "y", description = "The target location yaw") Float yaw, final @Nullable @Flag(value = "pitch", aliases = "p", description = "The target location pitch") Float pitch) {
        if (!Utils.checkPermission(sender, "teleport.location")) {
            return;
        }

        plugin.getMainCommand().getManager().taskRecipe().begin(location).synchronous(destination -> {
            Location clone = destination.clone();

            if (worldName != null) {
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown world: §l" + worldName + "§c!");
                    return;
                }
                clone.setWorld(world);
            }
            if (yaw != null) {
                clone.setYaw(yaw);
            }
            if (pitch != null) {
                clone.setPitch(pitch);
            }

            sender.teleport(clone);
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported you to §d" + this.beautifyLocation(sender.getLocation()) + "§e.");
        }).execute();
    }

    private String beautifyLocation(Location location) {
        return "(" + location.getWorld().getName() + " | X:" + location.getX() + " Y:" + location.getY() + " Z:" + location.getZ() + " Yaw:" + location.getY() + " Pitch:" + location.getPitch() + ")";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eTeleport command:");
        sender.sendMessage("§8└─ §e/tp <player> §8- §7Teleport to a player");
        sender.sendMessage("§8└─ §e/tp <player> <player> §8- §7Teleport a player to another player");
        sender.sendMessage("§8└─ §e/tp <player> <x> <y> <z> §8- §7Teleport a player to a location");
        sender.sendMessage("§8└─ §e/tp <player> <x> <y> <z> <world> §8- §7Teleport a player to a location in a world");
        sender.sendMessage("§8└─ §e/tp <player> <x> <y> <z> <world> -y <yaw> -p <pitch> §8- §7Teleport a player to a location in a world with a yaw and pitch");
        sender.sendMessage("§8└─ §e/tpw <world> §8- §7Teleport to a world");
        sender.sendMessage("§8└─ §e/tphere <player> §8- §7Teleport a player to you");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        return null;
    }
}
