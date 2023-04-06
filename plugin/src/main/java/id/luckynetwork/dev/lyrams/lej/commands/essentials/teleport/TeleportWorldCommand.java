package id.luckynetwork.dev.lyrams.lej.commands.essentials.teleport;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TeleportWorldCommand extends CommandClass {

    public TeleportWorldCommand() {
        super("tpworld", Collections.singletonList("tpw"));
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

    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        return null;
    }
}
