package id.luckynetwork.dev.lyrams.lej.commands.trolls;

import id.luckynetwork.dev.lyrams.lej.commands.trolls.api.TrollCommand;
import id.luckynetwork.dev.lyrams.lej.enums.TrollType;
import org.bukkit.command.CommandSender;

public class FakePlaceCommand extends TrollCommand {
    public FakePlaceCommand() {
        super("fakeplace");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String[] targetToggle = this.getTargetToggle(sender, args);
        if (targetToggle == null) {
            return;
        }

        this.toggleTroll(sender, targetToggle[0], targetToggle[1], TrollType.FAKE_PLACE);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eFakePlace command:");
        sender.sendMessage("§8└─ §e/fakeplace §8- §7Shows this message");
        sender.sendMessage("§8└─ §e/fakeplace <player> §8- §7Toggles the fakeplace troll for the specified player");
        sender.sendMessage("§8└─ §e/fakeplace <player> <on/off/toggle> §8- §7Toggles the fakeplace troll for the specified player");
    }
}
