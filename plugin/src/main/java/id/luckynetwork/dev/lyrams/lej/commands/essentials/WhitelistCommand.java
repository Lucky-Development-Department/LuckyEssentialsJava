package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.config.Config;
import id.luckynetwork.dev.lyrams.lej.config.WhitelistConfig;
import id.luckynetwork.dev.lyrams.lej.enums.ToggleType;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class WhitelistCommand extends CommandClass {

    @CommandMethod("whitelist|ewl|wl info|i|check|c")
    @CommandDescription("Gets the information about the current LuckyEssentials whitelist system configuration")
    public void whitelistInfoCommand(
            final @NonNull CommandSender sender
    ) {
        if (!Utils.checkPermission(sender, "whitelist")) {
            return;
        }

        sender.sendMessage("§eWhitelist system info:");
        sender.sendMessage("§8├─ §eState: " + Utils.colorizeTrueFalse(WhitelistConfig.enabled, TrueFalseType.ON_OFF));

        ComponentBuilder textBuilder = new ComponentBuilder("")
                .append("§8├─ §eWhitelisted Players: §a" + WhitelistConfig.whitelistedList.size())
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to run /whitelist list").create()))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/whitelist list"));
        BaseComponent[] text = textBuilder.create();
        if (sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(text);
        } else {
            sender.sendMessage(BaseComponent.toLegacyText(text));
        }

        sender.sendMessage("§8└─ §eCheck Mode: §a" + WhitelistConfig.checkMode.toString());
    }

    @CommandMethod("whitelist list [page]")
    @CommandDescription("Lists all whitelisted players")
    public void whitelistListCommand(
            final @NonNull CommandSender sender,
            @NonNull @Argument(value = "page", description = "The page", defaultValue = "1") Integer page
    ) {
        if (!Utils.checkPermission(sender, "whitelist")) {
            return;
        }

        List<WhitelistConfig.WhitelistData> whitelistedPlayers = WhitelistConfig.whitelistedList;
        if (whitelistedPlayers.size() > 5) {
            int maxPage = (int) Math.ceil(whitelistedPlayers.size() / 5.0);
            page = Math.min(Math.max(page, 1), maxPage);

            int from = page > 1 ? 5 * page - 5 : 0;
            int to = page > 0 ? 5 * page : 5;
            if (to > whitelistedPlayers.size()) {
                to -= (to - whitelistedPlayers.size());
            }

            sender.sendMessage("§6§m------------§a Whitelisted Players §e(§7" + page + "§e/§7" + maxPage + "§e) §6§m------------");

            List<WhitelistConfig.WhitelistData> pagedWhitelistedPlayers = whitelistedPlayers.subList(from, to);
            int i = from;
            for (WhitelistConfig.WhitelistData data : pagedWhitelistedPlayers) {
                sender.sendMessage("§7Player §a#" + ++i);
                sender.sendMessage("§8├─ §eUUID: §a" + data.getUuid());
                sender.sendMessage("§8└─ §eName: §a" + data.getName());
                sender.sendMessage(" ");
            }

            boolean lastPage = (page == maxPage);
            ComponentBuilder textBuilder = new ComponentBuilder("§6§m-----------------------§8 ");
            if (lastPage) {
                textBuilder.append("§8[§e←§8]")
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click for previous page").create()))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/whitelist list " + (page - 1)));
            } else {
                textBuilder.append("§8[§e→§8]")
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click for next page").create()))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/whitelist list " + (page + 1)));
            }
            textBuilder.append(" §6§m-----------------------");

            BaseComponent[] text = textBuilder.create();
            if (sender instanceof Player) {
                ((Player) sender).spigot().sendMessage(text);
            } else {
                sender.sendMessage(BaseComponent.toLegacyText(text));
            }
        } else {
            sender.sendMessage("§6§m------------§a Whitelisted Players §6§m------------");
            int i = 0;
            for (WhitelistConfig.WhitelistData data : WhitelistConfig.whitelistedList) {
                sender.sendMessage("§7Player §a#" + ++i);
                sender.sendMessage("§8├─ §eUUID: §a" + data.getUuid());
                sender.sendMessage("§8└─ §eName: §a" + data.getName());
                sender.sendMessage(" ");
            }
            sender.sendMessage("§6§m---------------------------------------------");
        }
    }

    @CommandMethod("whitelist add <target>")
    @CommandDescription("Adds a player to the LuckyEssentials whitelist system")
    public void whitelistAddCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName
    ) {
        if (!Utils.checkPermission(sender, "whitelist")) {
            return;
        }

        OfflineTargetsCallback targets = this.getTargetsOffline(sender, targetName);
        if (targets.isEmpty()) {
            sender.sendMessage(Config.PREFIX + "§cNo targets found!");
            return;
        }

        targets.forEach(target -> {
            WhitelistConfig.WhitelistData data = WhitelistConfig.WhitelistData.newBuilder()
                    .uuid(target.getUniqueId().toString())
                    .name(target.getName())
                    .build();

            if (WhitelistConfig.whitelistedList.contains(data)) {
                sender.sendMessage(Config.PREFIX + "§c§l" + data.getName() + " §cis already whitelisted.");
            } else {
                WhitelistConfig.whitelistedList.add(data);
                sender.sendMessage(Config.PREFIX + "§eAdded §d" + data.getName() + " §eto the whitelist.");
            }
        });

        WhitelistConfig.save();
    }

    @CommandMethod("whitelist remove <target>")
    @CommandDescription("Removes a player to the LuckyEssentials whitelist system")
    public void whitelistRemoveCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName
    ) {
        if (!Utils.checkPermission(sender, "whitelist")) {
            return;
        }

        OfflineTargetsCallback targets = this.getTargetsOffline(sender, targetName);
        if (targets.isEmpty()) {
            sender.sendMessage(Config.PREFIX + "§cNo targets found!");
            return;
        }

        targets.forEach(target -> {
            WhitelistConfig.WhitelistData data = WhitelistConfig.WhitelistData.newBuilder()
                    .uuid(target.getUniqueId().toString())
                    .name(target.getName())
                    .build();

            if (WhitelistConfig.whitelistedList.contains(data)) {
                WhitelistConfig.whitelistedList.remove(data);
                sender.sendMessage(Config.PREFIX + "§eRemoved §d" + data.getName() + " §efrom the whitelist.");
            } else {
                boolean removed = WhitelistConfig.whitelistedList.removeIf(it -> it.getName().equals(target.getName()) || it.getUuid().equals(target.getUniqueId().toString()));
                if (removed) {
                    sender.sendMessage(Config.PREFIX + "§eRemoved §d" + data.getName() + " §efrom the whitelist.");
                } else {
                    sender.sendMessage(Config.PREFIX + "§c§l" + data.getName() + " §cis already not whitelisted.");
                }
            }
        });

        WhitelistConfig.save();
    }

    @CommandMethod("whitelist toggle [toggle]")
    @CommandDescription("Toggles on or off the LuckyEssentials whitelist system")
    public void whitelistToggleCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        if (!Utils.checkPermission(sender, "whitelist")) {
            return;
        }

        ToggleType toggleType = ToggleType.getToggle(toggle);
        if (toggleType.equals(ToggleType.UNKNOWN)) {
            sender.sendMessage(Config.PREFIX + "§cUnknown toggle type §l" + toggle + "§c!");
            return;
        }

        switch (toggleType) {
            case ON: {
                WhitelistConfig.enabled = true;
                break;
            }
            case OFF: {
                WhitelistConfig.enabled = false;
                break;
            }
            case TOGGLE: {
                WhitelistConfig.enabled = !WhitelistConfig.enabled;
                break;
            }
        }

        WhitelistConfig.save();
        sender.sendMessage(Config.PREFIX + "§eToggled whitelist system " + Utils.colorizeTrueFalse(WhitelistConfig.enabled, TrueFalseType.ON_OFF) + "§e!");
    }

    @CommandMethod("whitelist reload")
    @CommandDescription("Reloads the LuckyEssentials whitelist system")
    public void whitelistReloadCommand(
            final @NonNull CommandSender sender
    ) {
        if (!Utils.checkPermission(sender, "whitelist")) {
            return;
        }

        WhitelistConfig.reload();
        sender.sendMessage(Config.PREFIX + "§eReloaded the whitelist system!");
    }
}
