package id.luckynetwork.dev.lyrams.lej.commands.trolls;

import id.luckynetwork.dev.lyrams.lej.commands.trolls.api.TrollCommand;
import id.luckynetwork.dev.lyrams.lej.enums.TrollType;
import org.bukkit.command.CommandSender;

public class NoBreakCommand extends TrollCommand {

    public NoBreakCommand() {
        super("nobreak");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String[] targetToggle = this.getTargetToggle(sender, args);
        if (targetToggle == null) {
            return;
        }

        this.toggleTroll(sender, targetToggle[0], targetToggle[1], TrollType.NO_BREAK);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eNoBreak command:");
        sender.sendMessage("§8└─ §e/nobreak §8- §7Shows this message");
        sender.sendMessage("§8└─ §e/nobreak <player> §8- §7Toggles the nobreak troll for the specified player");
        sender.sendMessage("§8└─ §e/nobreak <player> <on/off/toggle> §8- §7Toggles the nobreak troll for the specified player");
    }

}
