package id.luckynetwork.dev.lyrams.lej.commands.trolls;

import id.luckynetwork.dev.lyrams.lej.commands.trolls.api.TrollCommand;
import id.luckynetwork.dev.lyrams.lej.enums.TrollType;
import org.bukkit.command.CommandSender;

public class StickyCommand extends TrollCommand {

    public StickyCommand() {
        super("sticky");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String[] targetToggle = this.getTargetToggle(sender, args);
        if (targetToggle == null) {
            return;
        }

        this.toggleTroll(sender, targetToggle[0], targetToggle[1], TrollType.STICKY);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eSticky command:");
        sender.sendMessage("§8└─ §e/sticky §8- §7Shows this message");
        sender.sendMessage("§8└─ §e/sticky <player> §8- §7Toggles the sticky troll for the specified player");
        sender.sendMessage("§8└─ §e/sticky <player> <on/off/toggle> §8- §7Toggles the sticky troll for the specified player");
    }
}
