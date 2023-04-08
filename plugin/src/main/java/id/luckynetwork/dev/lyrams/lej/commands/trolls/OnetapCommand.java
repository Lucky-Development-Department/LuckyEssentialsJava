package id.luckynetwork.dev.lyrams.lej.commands.trolls;

import id.luckynetwork.dev.lyrams.lej.commands.trolls.api.TrollCommand;
import id.luckynetwork.dev.lyrams.lej.enums.TrollType;
import org.bukkit.command.CommandSender;

public class OnetapCommand extends TrollCommand {

    public OnetapCommand() {
        super("onetap");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String[] targetToggle = this.getTargetToggle(sender, args);
        if (targetToggle == null) {
            return;
        }

        this.toggleTroll(sender, targetToggle[0], targetToggle[1], TrollType.ONE_TAP);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eOnetap command:");
        sender.sendMessage("§8└─ §e/onetap §8- §7Shows this message");
        sender.sendMessage("§8└─ §e/onetap <player> §8- §7Toggles the onetap troll for the specified player");
        sender.sendMessage("§8└─ §e/onetap <player> <on/off/toggle> §8- §7Toggles the onetap troll for the specified player");
    }
}
