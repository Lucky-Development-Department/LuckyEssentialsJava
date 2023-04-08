package id.luckynetwork.dev.lyrams.lej.commands.trolls;

import id.luckynetwork.dev.lyrams.lej.commands.trolls.api.TrollCommand;
import id.luckynetwork.dev.lyrams.lej.enums.TrollType;
import org.bukkit.command.CommandSender;

public class NoHitCommand extends TrollCommand {

    public NoHitCommand() {
        super("nohit");
        this.registerCommandInfo("nohit", "Toggles the nohit troll for a player");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String[] targetToggle = this.getTargetToggle(sender, args);
        if (targetToggle == null) {
            return;
        }

        this.toggleTroll(sender, targetToggle[0], targetToggle[1], TrollType.NO_HIT);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eNoHit command:");
        sender.sendMessage("§8└─ §e/nohit §8- §7Shows this message");
        sender.sendMessage("§8└─ §e/nohit <player> §8- §7Toggles the nohit troll for the specified player");
        sender.sendMessage("§8└─ §e/nohit <player> <on/off/toggle> §8- §7Toggles the nohit troll for the specified player");
    }

}
