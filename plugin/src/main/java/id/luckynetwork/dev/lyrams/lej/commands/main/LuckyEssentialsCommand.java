package id.luckynetwork.dev.lyrams.lej.commands.main;

import com.google.common.reflect.ClassPath;
import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import id.luckynetwork.lyrams.lyralibs.core.command.data.CommandInfo;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LuckyEssentialsCommand extends CommandClass {

    @Getter
    private final LuckyEssentials plugin;

    public LuckyEssentialsCommand(LuckyEssentials plugin) {
        super("luckyessentials", Arrays.asList("less", "lej"));
        this.plugin = plugin;
        this.registerCommandInfo("luckyessentials", "Shows the plugin description");

        this.initCommands();
    }

    @SuppressWarnings("UnstableApiUsage")
    private void initCommands() {
        plugin.getLogger().info("Loading and registering commands...");
        int count = 0;
        try {
            ClassPath classPath = ClassPath.from(plugin.getClass().getClassLoader());
            for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive("id.luckynetwork.dev.lyrams.lej.commands")) {
                if (classInfo.getName().endsWith("LuckyEssentialsCommand") || classInfo.getName().contains(".api")) {
                    continue;
                }

                try {
                    Class<?> commandClass = Class.forName(classInfo.getName());
                    commandClass.getConstructor().newInstance();
                    count++;
                } catch (Exception e) {
                    plugin.getLogger().severe("Failed loading command class: " + classInfo.getName());
                    e.printStackTrace();
                }
            }

            plugin.getLogger().info("Registered " + count + " commands!");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed loading command classes!");
            e.printStackTrace();
        }
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length == 0) {
            this.sendDefaultMessage(sender);
            return true;
        }

        String subCommand = args[0];
        switch (subCommand.toLowerCase()) {
            case "reload": {
                plugin.reloadConfig();
                sender.sendMessage("§aLuckyEssentials config has been reloaded!");
                break;
            }
            case "help":
            case "?":
            case "h": {
                this.sendHelpMessage(sender, args);
                break;
            }
            default: {
                this.sendDefaultMessage(sender);
                break;
            }
        }

        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage(Utils.getPluginDescription());
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (args.length == 1) {
            return Stream.of("reload", "help")
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return null;
    }

    private void sendHelpMessage(CommandSender sender, String[] args) {
        sender.sendMessage("§aLuckyEssentials §c" + plugin.getDescription().getVersion());
        // pagination
        int page = 1;
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {
            }
        }

        List<CommandInfo> commandsClone = commands;
        int maxPage = (int) Math.ceil(commandsClone.size() / 5.0);
        page = Math.min(Math.max(page, 1), maxPage);

        int from = page > 1 ? 5 * page - 5 : 0;
        int to = page > 0 ? 5 * page : 5;
        if (to > commandsClone.size()) {
            to -= (to - commandsClone.size());
        }

        sender.sendMessage("§6§m------------§a Available Commands  §e(§7" + page + "§e/§7" + maxPage + "§e) §6§m------------");

        commandsClone = commandsClone.subList(from, to);
        for (CommandInfo commandInfo : commandsClone) {
            sender.sendMessage("§7- §e/" + commandInfo.getCommand() + " §7- §f" + commandInfo.getDescription());
        }

        boolean lastPage = (page == maxPage);
        ComponentBuilder textBuilder = new ComponentBuilder("§6§m-----------------------§8 ");
        if (lastPage) {
            textBuilder.append("§8[§e←§8]").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click for previous page").create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/luckyessentials help " + (page - 1)));
        } else {
            textBuilder.append("§8[§e→§8]").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click for next page").create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/luckyessentials help " + (page + 1)));
        }
        textBuilder.append(" §6§m-----------------------");

        BaseComponent[] text = textBuilder.create();
        if (sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(text);
        } else {
            sender.sendMessage(BaseComponent.toLegacyText(text));
        }
    }
}
