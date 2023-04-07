package id.luckynetwork.dev.lyrams.lej.commands.main;

import com.google.common.reflect.ClassPath;
import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import id.luckynetwork.lyrams.lyralibs.core.command.data.CommandInfo;
import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Utils.getPluginDescription());
            return;
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
                sender.sendMessage(Utils.getPluginDescription());
                sender.sendMessage(" ");
                sender.sendMessage("§e§lLuckyEssentials Commands:");
                for (CommandInfo commandInfo : commands) {
                    sender.sendMessage("§7- §e/" + commandInfo.getCommand() + " §7- §f" + commandInfo.getDescription());
                }
                break;
            }
            default: {
                sender.sendMessage(Utils.getPluginDescription());
                break;
            }
        }
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        return null;
    }
}
