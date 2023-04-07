package id.luckynetwork.dev.lyrams.lej.commands.essentials.teleport;

import com.google.common.base.Joiner;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TeleportPositionCommand extends CommandClass {

    public TeleportPositionCommand() {
        super("tppos");
        this.registerCommandInfo("tppos", "Teleports a player to a specific position");
    }

    public void teleportPositionCommand(Player sender, Location location, String worldName, Float yaw, Float pitch) {
        Location clone = location.clone();

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
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can execute this command!");
            return;
        }

        if (!Utils.checkPermission(sender, "teleport.position")) {
            return;
        }

        if (args.length < 3) {
            this.sendDefaultMessage(sender);
            return;
        }

        Player player = (Player) sender;
        String x = args[0];
        String y = args[1];
        String z = args[2];

        Location location;
        try {
            location = new Location(player.getWorld(), Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z));
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid location: §l" + x + " " + y + " " + z + "§c!");
            return;
        }


        String worldName = null;
        float yaw = 0.0f;
        float pitch = 0.0f;

        String allArgs = Joiner.on(" ").join(args);
        if (allArgs.contains("-w")) {
            String[] split = allArgs.split("-w");
            if (split[1].contains(" ")) {
                worldName = split[1].split(" ")[0];
            } else {
                worldName = split[1];
            }
        }

        if (allArgs.contains("-y")) {
            String[] split = allArgs.split("-y");
            try {
                if (split[1].contains(" ")) {
                    yaw = Float.parseFloat(split[1].split(" ")[0]);
                } else {
                    yaw = Float.parseFloat(split[1]);
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid yaw: §l" + split[1].split(" ")[0] + "§c!");
            }
        }

        if (allArgs.contains("-p")) {
            String[] split = allArgs.split("-p");
            try {
                if (split[1].contains(" ")) {
                    pitch = Float.parseFloat(split[1].split(" ")[0]);
                } else {
                    pitch = Float.parseFloat(split[1]);
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid pitch: §l" + split[1].split(" ")[0] + "§c!");
            }
        }


        this.teleportPositionCommand(player, location, worldName, yaw, pitch);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eTeleport command:");
        sender.sendMessage("§e└─ §d/tppos <x> <y> <z> §7- Teleport to a specific position");
        sender.sendMessage("§e└─ §d/tppos <x> <y> <z> -w <world> §7- Teleport to a specific position in a specific world");
        sender.sendMessage("§e└─ §d/tppos <x> <y> <z> -y <yaw> §7- Teleport to a specific position with a specific yaw");
        sender.sendMessage("§e└─ §d/tppos <x> <y> <z> -p <pitch> §7- Teleport to a specific position with a specific pitch");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "teleport.position")) {
            return null;
        }

        return this.players(args[0]);
    }

    private String beautifyLocation(Location location) {
        return "(" + location.getWorld().getName() + " | X:" + location.getX() + " Y:" + location.getY() + " Z:" + location.getZ() + " Yaw:" + location.getY() + " Pitch:" + location.getPitch() + ")";
    }
}
