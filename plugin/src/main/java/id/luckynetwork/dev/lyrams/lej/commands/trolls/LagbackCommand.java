package id.luckynetwork.dev.lyrams.lej.commands.trolls;

import id.luckynetwork.dev.lyrams.lej.commands.trolls.api.TrollCommand;
import id.luckynetwork.dev.lyrams.lej.enums.TrollType;
import org.bukkit.command.CommandSender;

public class LagbackCommand extends TrollCommand {

    public LagbackCommand() {
        super("lagback");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String[] targetToggle = this.getTargetToggle(sender, args);
        if (targetToggle == null) {
            return;
        }

        this.toggleTroll(sender, targetToggle[0], targetToggle[1], TrollType.LAGBACK);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eLagback command:");
        sender.sendMessage("§8└─ §e/lagback §8- §7Shows this message");
        sender.sendMessage("§8└─ §e/lagback <player> §8- §7Toggles the lagback troll for the specified player");
        sender.sendMessage("§8└─ §e/lagback <player> <on/off/toggle> §8- §7Toggles the lagback troll for the specified player");
    }
}
