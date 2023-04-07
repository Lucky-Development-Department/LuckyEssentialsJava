package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class RenameCommand extends CommandClass {

    public RenameCommand() {
        super("rename");
    }

    public void renameCommand(
            Player player,
            String name
    ) {
        ItemStack itemInHand = plugin.getVersionSupport().getItemInHand(player);
        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            player.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cYou are not holding anything!");
            return;
        }

        ItemMeta itemMeta = itemInHand.getItemMeta();
        itemMeta.setDisplayName(Utils.colorize(name));

        itemInHand.setItemMeta(itemMeta);

        player.updateInventory();
        player.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eItem successfully renamed.");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "rename")) {
            return;
        }

        if (args.length == 0) {
            this.sendDefaultMessage(sender);
            return;
        }

        String name = String.join(" ", args);

        this.renameCommand((Player) sender, name);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eRename command:");
        sender.sendMessage("§8└─ §e/rename <name> §8- §7Renames the currently held item");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        return null;
    }
}
