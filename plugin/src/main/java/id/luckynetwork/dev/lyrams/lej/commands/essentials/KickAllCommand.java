package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class KickAllCommand extends CommandClass {

    public KickAllCommand() {
        super("kickall");
    }

    public void kickAllCommand(CommandSender sender, String reason) {
        if (!Utils.checkPermission(sender, "kickall")) {
            return;
        }

        String message = reason == null ? "§cKicked by a staff member!" : Utils.colorize(reason);
        plugin.getConfirmationManager().requestConfirmation(() -> {
            Bukkit.getOnlinePlayers().forEach(target -> {
                if (target != sender && !Utils.checkPermission(target, "kickall.bypass", false, false, true, null)) {
                    target.kickPlayer(message);
                }
            });

            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eKicked all players.");
        }, sender, Collections.singletonList(plugin.getMainConfigManager().getPrefix() + "§eAre you sure you want to execute §dkick all§e?"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "kickall")) {
            return;
        }

        String reason = args.length == 0 ? null : String.join(" ", args);
        this.kickAllCommand(sender, reason);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eKickall command:");
        sender.sendMessage("§8└─ §e/kickall §8- §7Kick all players");
        sender.sendMessage("§8└─ §e/kickall <reason> §8- §7Kick all players with a custom reason");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        return null;
    }
}
