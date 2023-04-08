package id.luckynetwork.dev.lyrams.lej.commands.trolls;

import id.luckynetwork.dev.lyrams.lej.commands.trolls.api.TrollCommand;
import id.luckynetwork.dev.lyrams.lej.enums.TrollType;
import org.bukkit.command.CommandSender;

public class NoDamageCommand extends TrollCommand {

    public NoDamageCommand() {
        super("nodamage");
        this.registerCommandInfo("nodamage", "Toggles the nodamage troll for a player");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String[] targetToggle = this.getTargetToggle(sender, args);
        if (targetToggle == null) {
            return;
        }

        this.toggleTroll(sender, targetToggle[0], targetToggle[1], TrollType.NO_DAMAGE);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eNoDamage command:");
        sender.sendMessage("§8└─ §e/nodamage §8- §7Shows this message");
        sender.sendMessage("§8└─ §e/nodamage <player> §8- §7Toggles the nodamage troll for the specified player");
        sender.sendMessage("§8└─ §e/nodamage <player> <on/off/toggle> §8- §7Toggles the nodamage troll for the specified player");
    }

}
