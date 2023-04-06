package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class EditSignCommand extends CommandClass {

    public EditSignCommand() {
        super("editsign");
    }

    public void setCommand(Player sender, Integer line, String text) {
        Sign sign = this.getSign(sender);
        if (sign == null) {
            return;
        }

        if (line > 3 || line < 0) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid sign line: §l" + line + "§c!");
            return;
        }

        sign.setLine(line, Utils.colorize(text));
        sign.update();
        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eLine §d" + line + " §eupdated.");
    }

    public void clearCommand(Player sender, Integer line) {
        Sign sign = this.getSign(sender);
        if (sign == null) {
            return;
        }

        if (line > 3 || line < -1) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid sign line: §l" + line + "§c!");
            return;
        }

        if (line == -1) {
            for (int i = 0; i < 3; i++) {
                sign.setLine(i, "");
            }
            sign.update();
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSign cleared.");
        } else {
            sign.setLine(line, "");
            sign.update();
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eLine §d" + line + " §ecleared.");
        }
    }

    private Sign getSign(Player player) {
        Block targetBlock = player.getTargetBlock(null, 7);
        if (!(targetBlock instanceof Sign)) {
            player.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cSign not found!");
            return null;
        }

        return (Sign) targetBlock;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "editsign")) {
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cOnly players can use this command!");
            return;
        }

        if (args.length == 0) {
            this.sendDefaultMessage(sender);
            return;
        }

        Player player = (Player) sender;
        Sign sign = this.getSign(player);
        if (sign == null) {
            return;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 3) {
                this.sendDefaultMessage(sender);
                return;
            }

            this.setCommand(player, Integer.parseInt(args[1]), args[2]);
        } else if (args[0].equalsIgnoreCase("clear")) {
            if (args.length < 2) {
                this.sendDefaultMessage(sender);
                return;
            }

            this.clearCommand(player, Integer.parseInt(args[1]));
        }
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eEditSign command:");
        sender.sendMessage("§8└─ §e/editsign set <line> <text> §8- §7Sets the text of a line in a sign");
        sender.sendMessage("§8└─ §e/editsign clear [line] §8- §7Clears the text of a line or all lines in a sign");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        return null;
    }
}
