package id.luckynetwork.dev.lyrams.lej.commands.trolls;

import id.luckynetwork.dev.lyrams.lej.commands.trolls.api.TrollCommand;
import id.luckynetwork.dev.lyrams.lej.enums.TrollType;
import org.bukkit.command.CommandSender;

public class NoPickupCommand extends TrollCommand {

    public NoPickupCommand() {
        super("nopickup");
        this.registerCommandInfo("nopickup", "Toggles the nopickup troll for a player");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String[] targetToggle = this.getTargetToggle(sender, args);
        if (targetToggle == null) {
            return;
        }

        this.toggleTroll(sender, targetToggle[0], targetToggle[1], TrollType.NO_PICKUP);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eNoPickup command:");
        sender.sendMessage("§8└─ §e/nopickup §8- §7Shows this message");
        sender.sendMessage("§8└─ §e/nopickup <player> §8- §7Toggles the nopickup troll for the specified player");
        sender.sendMessage("§8└─ §e/nopickup <player> <on/off/toggle> §8- §7Toggles the nopickup troll for the specified player");
    }
}
