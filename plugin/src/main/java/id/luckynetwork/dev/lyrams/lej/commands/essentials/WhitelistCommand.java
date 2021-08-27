package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.enums.ToggleType;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import id.luckynetwork.dev.lyrams.lej.managers.whitelist.WhitelistData;
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

    @CommandMethod("whitelist|ewl|wl info|i")
    @CommandDescription("Gets the information about the current LuckyEssentials whitelist system configuration")
    public void infoCommand(
            final @NonNull CommandSender sender
    ) {
        if (!Utils.checkPermission(sender, "whitelist")) {
            return;
        }

        sender.sendMessage("§eWhitelist system info:");
        sender.sendMessage("§8├─ §eState: " + Utils.colorizeTrueFalse(plugin.getWhitelistManager().isEnabled(), TrueFalseType.ON_OFF));

        ComponentBuilder textBuilder = new ComponentBuilder("")
                .append("§8├─ §eWhitelisted Players: §a" + plugin.getWhitelistManager().getWhitelistedList().size())
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to run /whitelist list").create()))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/whitelist list"));
        BaseComponent[] text = textBuilder.create();
        if (sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(text);
        } else {
            sender.sendMessage(BaseComponent.toLegacyText(text));
        }

        sender.sendMessage("§8└─ §eCheck Mode: §a" + plugin.getWhitelistManager().getCheckMode().toString());
    }

    @CommandMethod("whitelist|ewl|wl list [page]")
    @CommandDescription("Lists all whitelisted players")
    public void listCommand(
            final @NonNull CommandSender sender,
            @NonNull @Argument(value = "page", description = "The page", defaultValue = "1") Integer page
    ) {
        if (!Utils.checkPermission(sender, "whitelist")) {
            return;
        }

        List<WhitelistData> whitelistedPlayers = plugin.getWhitelistManager().getWhitelistedList();
        if (whitelistedPlayers.size() > 5) {
            int maxPage = (int) Math.ceil(whitelistedPlayers.size() / 5.0);
            page = Math.min(Math.max(page, 1), maxPage);

            int from = page > 1 ? 5 * page - 5 : 0;
            int to = page > 0 ? 5 * page : 5;
            if (to > whitelistedPlayers.size()) {
                to -= (to - whitelistedPlayers.size());
            }

            sender.sendMessage("§6§m------------§a Whitelisted Players §e(§7" + page + "§e/§7" + maxPage + "§e) §6§m------------");

            List<WhitelistData> pagedWhitelistedPlayers = whitelistedPlayers.subList(from, to);
            int i = from;
            for (WhitelistData data : pagedWhitelistedPlayers) {
                sender.sendMessage("§7Player §a#" + ++i);
                sender.sendMessage("§8├─ §eUUID: §a" + data.getUuid());
                sender.sendMessage("§8└─ §eName: §a" + data.getName());
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
            for (WhitelistData data : plugin.getWhitelistManager().getWhitelistedList()) {
                sender.sendMessage("§7Player §a#" + ++i);
                sender.sendMessage("§8├─ §eUUID: §a" + data.getUuid());
                sender.sendMessage("§8└─ §eName: §a" + data.getName());
            }
            sender.sendMessage("§6§m---------------------------------------------");
        }
    }

    @CommandMethod("whitelist|ewl|wl add <target>")
    @CommandDescription("Adds a player to the LuckyEssentials whitelist system")
    public void addCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName
    ) {
        if (!Utils.checkPermission(sender, "whitelist")) {
            return;
        }

        OfflineTargetsCallback targets = this.getTargetsOffline(sender, targetName);
        if (targets.isEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        targets.forEach(target -> {
            WhitelistData data = WhitelistData.newBuilder()
                    .uuid(target.getUniqueId().toString())
                    .name(target.getName())
                    .build();

            if (plugin.getWhitelistManager().getWhitelistedList().contains(data)) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§c§l" + data.getName() + " §cis already whitelisted.");
            } else {
                plugin.getWhitelistManager().getWhitelistedList().add(data);
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eAdded §d" + data.getName() + " §eto the whitelist.");
            }
        });

        plugin.getWhitelistManager().save();
    }

    @CommandMethod("whitelist|ewl|wl remove <target>")
    @CommandDescription("Removes a player to the LuckyEssentials whitelist system")
    public void removeCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName
    ) {
        if (!Utils.checkPermission(sender, "whitelist")) {
            return;
        }

        OfflineTargetsCallback targets = this.getTargetsOffline(sender, targetName);
        if (targets.isEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        targets.forEach(target -> {
            WhitelistData data = WhitelistData.newBuilder()
                    .uuid(target.getUniqueId().toString())
                    .name(target.getName())
                    .build();

            if (plugin.getWhitelistManager().getWhitelistedList().contains(data)) {
                plugin.getWhitelistManager().getWhitelistedList().remove(data);
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eRemoved §d" + data.getName() + " §efrom the whitelist.");
            } else {
                boolean removed = plugin.getWhitelistManager().getWhitelistedList().removeIf(it -> it.getName().equals(target.getName()) || it.getUuid().equals(target.getUniqueId().toString()));
                if (removed) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eRemoved §d" + data.getName() + " §efrom the whitelist.");
                } else {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§c§l" + data.getName() + " §cis already not whitelisted.");
                }
            }
        });

        plugin.getWhitelistManager().save();
    }

    @CommandMethod("whitelist|ewl|wl check <target>")
    @CommandDescription("Checks if a player is whitelisted in the LuckyEssentials whitelist system")
    public void checkCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName
    ) {
        if (!Utils.checkPermission(sender, "whitelist")) {
            return;
        }

        OfflineTargetsCallback targets = this.getTargetsOffline(sender, targetName);
        if (targets.isEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        targets.forEach(target -> {
            boolean whitelsited = false;
            switch (plugin.getWhitelistManager().getCheckMode()) {
                case UUID: {
                    whitelsited = plugin.getWhitelistManager().getWhitelistedList().stream().map(WhitelistData::getUuid).anyMatch(it -> it.equals(target.getUniqueId().toString()));
                    break;
                }
                case NAME: {
                    whitelsited = plugin.getWhitelistManager().getWhitelistedList().stream().map(WhitelistData::getName).anyMatch(it -> it.equals(target.getName()));
                    break;
                }
                case BOTH: {
                    whitelsited = plugin.getWhitelistManager().getWhitelistedList().stream().anyMatch(it -> it.getUuid().equals(target.getUniqueId().toString()) && it.getName().equals(target.getName()));
                    break;
                }
            }

            if (whitelsited) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§d" + target.getName() + " §eis §awhitelisted.");
            } else {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§d" + target.getName() + " §eis §cnot whitelisted.");
            }
        });
    }

    @CommandMethod("whitelist|ewl|wl toggle [toggle]")
    @CommandDescription("Toggles on or off the LuckyEssentials whitelist system")
    public void toggleCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        if (!Utils.checkPermission(sender, "whitelist")) {
            return;
        }

        ToggleType toggleType = ToggleType.getToggle(toggle);
        if (toggleType.equals(ToggleType.UNKNOWN)) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown toggle type §l" + toggle + "§c!");
            return;
        }

        switch (toggleType) {
            case ON: {
                plugin.getWhitelistManager().setEnabled(true);
                break;
            }
            case OFF: {
                plugin.getWhitelistManager().setEnabled(false);
                break;
            }
            case TOGGLE: {
                plugin.getWhitelistManager().setEnabled(!plugin.getWhitelistManager().isEnabled());
                break;
            }
        }

        plugin.getWhitelistManager().save();
        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eToggled whitelist system " + Utils.colorizeTrueFalse(plugin.getWhitelistManager().isEnabled(), TrueFalseType.ON_OFF) + "§e!");
    }

    @CommandMethod("whitelist|ewl|wl reload")
    @CommandDescription("Reloads the LuckyEssentials whitelist system")
    public void reloadCommand(
            final @NonNull CommandSender sender
    ) {
        if (!Utils.checkPermission(sender, "whitelist")) {
            return;
        }

        plugin.getWhitelistManager().reload();
        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eReloaded the whitelist system!");
    }
}
