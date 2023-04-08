package id.luckynetwork.dev.lyrams.lej.commands.trolls;

import id.luckynetwork.dev.lyrams.lej.commands.trolls.api.TrollCommand;
import id.luckynetwork.dev.lyrams.lej.enums.TrollType;
import org.bukkit.command.CommandSender;

public class FakeBreakCommand extends TrollCommand {

    public FakeBreakCommand() {
        super("fakebreak");
        this.registerCommandInfo("fakebreak", "Toggles the fakebreak troll for a player");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String[] targetToggle = this.getTargetToggle(sender, args);
        if (targetToggle == null) {
            return;
        }

        this.toggleTroll(sender, targetToggle[0], targetToggle[1], TrollType.FAKE_BREAK);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eFakeBreak command:");
        sender.sendMessage("§8└─ §e/fakebreak §8- §7Shows this message");
        sender.sendMessage("§8└─ §e/fakebreak <player> §8- §7Toggles the fakebreak troll for the specified player");
        sender.sendMessage("§8└─ §e/fakebreak <player> <on/off/toggle> §8- §7Toggles the fakebreak troll for the specified player");
    }

}
