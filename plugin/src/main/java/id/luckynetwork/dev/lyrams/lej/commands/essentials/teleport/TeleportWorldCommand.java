package id.luckynetwork.dev.lyrams.lej.commands.essentials.teleport;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TeleportWorldCommand extends CommandClass {

    public TeleportWorldCommand() {
        super("tpworld", Collections.singletonList("tpw"));
        this.registerCommandInfo("tpw", "Teleports a player to a specific world");
    }

    public void teleportWorldCommand(Player sender, String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown world: §l" + worldName + "§c!");
            return;
        }

        sender.teleport(world.getSpawnLocation());
        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eTeleported you to §d" + this.beautifyLocation(sender.getLocation()) + "§e.");
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can execute this command!");
            return;
        }

        if (!Utils.checkPermission(sender, "teleport.world")) {
            return;
        }

        if (args.length == 0) {
            this.sendDefaultMessage(sender);
            return;
        }

        String worldName = args[0];
        this.teleportWorldCommand((Player) sender, worldName);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eTeleport command:");
        sender.sendMessage("§8└─ §e/tpw <world> §8- §7Teleport to a world");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "teleport.world", true)) {
            return null;
        }

        if (args.length == 1) {
            return Bukkit.getWorlds().stream().map(World::getName)
                    .filter(worldName -> worldName.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return null;
    }


    private String beautifyLocation(Location location) {
        return "(" + location.getWorld().getName() + " | X:" + location.getX() + " Y:" + location.getY() + " Z:" + location.getZ() + " Yaw:" + location.getY() + " Pitch:" + location.getPitch() + ")";
    }
}
