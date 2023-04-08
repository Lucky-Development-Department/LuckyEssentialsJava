package id.luckynetwork.dev.lyrams.lej.commands.trolls;

import id.luckynetwork.dev.lyrams.lej.commands.trolls.api.TrollCommand;
import id.luckynetwork.dev.lyrams.lej.enums.TrollType;
import org.bukkit.command.CommandSender;

public class NoPlaceCommand extends TrollCommand {

    public NoPlaceCommand() {
        super("noplace");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String[] targetToggle = this.getTargetToggle(sender, args);
        if (targetToggle == null) {
            return;
        }

        this.toggleTroll(sender, targetToggle[0], targetToggle[1], TrollType.NO_PLACE);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eNoPlace command:");
        sender.sendMessage("§8└─ §e/noplace §8- §7Shows this message");
        sender.sendMessage("§8└─ §e/noplace <player> §8- §7Toggles the noplace troll for the specified player");
        sender.sendMessage("§8└─ §e/noplace <player> <on/off/toggle> §8- §7Toggles the noplace troll for the specified player");
    }

}
