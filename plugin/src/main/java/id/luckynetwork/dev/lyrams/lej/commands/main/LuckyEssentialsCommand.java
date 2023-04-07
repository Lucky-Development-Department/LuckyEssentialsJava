package id.luckynetwork.dev.lyrams.lej.commands.main;

import com.google.common.reflect.ClassPath;
import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
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
        if (args.length != 0 && args[0].equalsIgnoreCase("help")) {
            // todo
        } else {
            sender.sendMessage(Utils.getPluginDescription());
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
