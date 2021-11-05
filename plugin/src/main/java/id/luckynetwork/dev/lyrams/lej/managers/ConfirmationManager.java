package id.luckynetwork.dev.lyrams.lej.managers;

import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ConfirmationManager {

    private final LuckyEssentials plugin;
    private final Map<Player, Callable> confirmationMap = new HashMap<>();

    public void requestConfirmation(Callable callable, CommandSender sender, boolean skip, @Nullable List<String> warnings) {
        if (skip || sender instanceof ConsoleCommandSender) {
            callable.call();
            return;
        }

        Player player = (Player) sender;
        if (warnings != null && !warnings.isEmpty()) {
            warnings.forEach(player::sendMessage);
        }

        player.sendMessage(plugin.getMainConfigManager().getPrefix() + "§6Please type §l/less confirm §6to confirm your action.");
        this.confirmationMap.put(player, callable);
    }

    public void requestConfirmation(Callable callable, CommandSender sender, boolean skip) {
        this.requestConfirmation(callable, sender, skip, null);
    }

    public void requestConfirmation(Callable callable, CommandSender sender, @Nullable List<String> warnings) {
        this.requestConfirmation(callable, sender, false, warnings);
    }

    public void requestConfirmation(Callable callable, CommandSender sender) {
        this.requestConfirmation(callable, sender, false, null);
    }

    public void confirm(Player player) {
        if (!this.confirmationMap.containsKey(player)) {
            return;
        }

        this.confirmationMap.get(player).call();
    }

    public void deleteConfirmation(Player player) {
        this.confirmationMap.remove(player);
    }


    public interface Callable {
        void call();
    }
}
