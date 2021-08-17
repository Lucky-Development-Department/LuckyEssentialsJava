package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.config.Config;
import id.luckynetwork.dev.lyrams.lej.config.WhitelistConfig;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Set;

public class WhitelistCommand extends CommandClass {

    @CommandMethod("whitelist list [page]")
    @CommandDescription("Lists all whitelisted players")
    public void whitelistListCommand(
            final @NonNull CommandSender sender,
            @NonNull @Argument(value = "page", description = "The page", defaultValue = "1") Integer page
    ) {
        List<WhitelistConfig.WhitelistData> whitelistedPlayers = WhitelistConfig.whitelistedList;
        if (whitelistedPlayers.size() > 5) {
            int maxPage = (int) Math.ceil(whitelistedPlayers.size() / 5.0);
            page = Math.min(Math.max(page, 1), maxPage);

            int from = page > 1 ? 5 * page - 5 : 0;
            int to = page > 0 ? 5 * page : 5;
            if (to > whitelistedPlayers.size()) {
                to -= (to - whitelistedPlayers.size());
            }

            sender.sendMessage("§6§m-----------------§a Whitelisted Players §6(" + page + "§6/" + maxPage + "§6) §6§m-----------------");

            List<WhitelistConfig.WhitelistData> pagedWhitelistedPlayers = whitelistedPlayers.subList(from, to);
            int i = from;
            for (WhitelistConfig.WhitelistData data : pagedWhitelistedPlayers) {
                sender.sendMessage("§7Player §a#" + ++i);
                sender.sendMessage("§8└─ §eUUID: §a" + data.getUuid());
                sender.sendMessage("§8└─ §eName: §a" + data.getName());
                sender.sendMessage(" ");
            }

            boolean lastPage = (page == maxPage);
            ComponentBuilder textBuilder = new ComponentBuilder("§6§m----------------------------§7 ");
            if (lastPage) {
                textBuilder.append("[§6←§7]")
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click for previous page").create()))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/whitelist list " + (page - 1)));
            } else {
                textBuilder.append("[§6→§7]")
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click for next page").create()))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/whitelist list " + (page + 1)));
            }
            textBuilder.append(" §6§m----------------------------");

            BaseComponent[] text = textBuilder.create();
            if (sender instanceof Player) {
                sender.spigot().sendMessage(text);
            } else {
                sender.sendMessage(BaseComponent.toLegacyText(text));
            }
        } else {
            sender.sendMessage("§6§m-----------------§a Whitelisted Players §6§m-----------------");
            int i = 0;
            for (WhitelistConfig.WhitelistData data : WhitelistConfig.whitelistedList) {
                sender.sendMessage("§7Player §a#" + ++i);
                sender.sendMessage("§8└─ §eUUID: §a" + data.getUuid());
                sender.sendMessage("§8└─ §eName: §a" + data.getName());
                sender.sendMessage(" ");
            }
            sender.sendMessage("§6§m-------------------------------------------------------");
        }
    }

    @CommandMethod("whitelist add <target>")
    @CommandDescription("Adds a player to the LuckyEssentials whitelist system")
    public void whitelistAddCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName
    ) {
        Set<OfflinePlayer> targets = this.getTargetsOffline(sender, targetName);
        if (targets.isEmpty()) {
            sender.sendMessage(Config.PREFIX + "§cNo targets found!");
            return;
        }

        targets.forEach(target -> {
            WhitelistConfig.WhitelistData data = WhitelistConfig.WhitelistData.newBuilder()
                    .uuid(target.getUniqueId().toString())
                    .name(target.getName())
                    .build();

            WhitelistConfig.whitelistedList.add(data);
        });

        WhitelistConfig.save();
    }
}
