package id.luckynetwork.dev.lyrams.lej.commands.main;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ConfirmCommand extends CommandClass {

    public ConfirmCommand() {
        super("confirm");
        this.registerCommandInfo("confirm", "Confirms the confirmation");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.getConfirmationManager().confirm((Player) sender);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        return null;
    }
}
