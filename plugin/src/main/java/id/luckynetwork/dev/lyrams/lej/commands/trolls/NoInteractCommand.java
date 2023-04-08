package id.luckynetwork.dev.lyrams.lej.commands.trolls;

import id.luckynetwork.dev.lyrams.lej.commands.trolls.api.TrollCommand;
import id.luckynetwork.dev.lyrams.lej.enums.TrollType;
import org.bukkit.command.CommandSender;

public class NoInteractCommand extends TrollCommand {

    public NoInteractCommand() {
        super("nointeract");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String[] targetToggle = this.getTargetToggle(sender, args);
        if (targetToggle == null) {
            return;
        }

        this.toggleTroll(sender, targetToggle[0], targetToggle[1], TrollType.NO_INTERACT);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eNoInteract command:");
        sender.sendMessage("§8└─ §e/nointeract §8- §7Shows this message");
        sender.sendMessage("§8└─ §e/nointeract <player> §8- §7Toggles the nointeract troll for the specified player");
        sender.sendMessage("§8└─ §e/nointeract <player> <on/off/toggle> §8- §7Toggles the nointeract troll for the specified player");
    }
}
