package id.luckynetwork.dev.lyrams.lej.utils.pluginmanager;

import com.google.common.base.Joiner;
import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@UtilityClass
public class PluginManagerUtils {

    private final LuckyEssentials luckyEssentials = LuckyEssentials.getInstance();

    /**
     * Enable a plugin.
     *
     * @param plugin the plugin to enable
     */
    public boolean enable(Plugin plugin) {
        if (plugin != null && !plugin.isEnabled()) {
            try {
                Bukkit.getPluginManager().enablePlugin(plugin);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    /**
     * Disable a plugin.
     *
     * @param plugin the plugin to disable
     */
    public boolean disable(Plugin plugin) {
        if (plugin != null && plugin.isEnabled()) {
            try {
                Bukkit.getPluginManager().disablePlugin(plugin);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    public boolean restart(Plugin plugin) {
        if (plugin.isEnabled()) {
            return PluginManagerUtils.disable(plugin) && PluginManagerUtils.enable(plugin);
        }

        return PluginManagerUtils.enable(plugin);
    }


    /**
     * Returns a List of plugin names.
     *
     * @return list of plugin names
     */
    public List<String> getPluginNames(boolean fullName) {
        List<String> plugins = new ArrayList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (fullName) {
                plugins.add(plugin.getDescription().getFullName());
            } else {
                plugins.add(plugin.getName());
            }
        }

        return plugins;
    }

    /**
     * gets a plugin by its name
     *
     * @param name the name
     * @return the plugin, null if not found
     */
    @Nullable
    public Plugin getPluginByName(String name) {
        return Arrays.stream(Bukkit.getPluginManager().getPlugins()).filter(it -> it.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Returns the formatted name of the plugin.
     *
     * @param plugin the plugin to format
     * @return the formatted name
     */
    public String getFormattedName(@NotNull Plugin plugin, boolean includeVersion) {
        TrueFalseType trueFalseType = TrueFalseType.DEFAULT;
        trueFalseType.setIfTrue(plugin.getName());
        trueFalseType.setIfFalse(plugin.getName());

        String pluginName = Utils.colorizeTrueFalse(plugin.isEnabled(), trueFalseType);
        if (includeVersion) {
            pluginName += " (" + plugin.getDescription().getVersion() + ")";
        }

        return pluginName;
    }

    /**
     * gets all command usages for the plugin
     *
     * @param plugin the plugin
     * @return the command usages
     */
    public String getUsages(@NotNull Plugin plugin) {
        List<String> parsedCommands = getKnownCommands().keySet().stream()
                .filter(s -> s.toLowerCase().startsWith(plugin.getName().toLowerCase() + ":"))
                .map(s -> s.substring(plugin.getName().length() + ":".length()))
                .collect(Collectors.toList());

        if (parsedCommands.isEmpty()) {
            return "§cNo commands registered.";
        }

        return Joiner.on(", ").join(parsedCommands);
    }

    /**
     * Find which plugin has a given command registered.
     *
     * @param command the command.
     * @return the plugin.
     */
    public static List<String> findByCommand(String command) {
        List<String> plugins = new ArrayList<>();

        List<String> pls = new ArrayList<>();
        for (String s : PluginManagerUtils.getKnownCommands().keySet()) {
            if (s.contains(":")) {
                if (!s.equalsIgnoreCase("minecraft:/")) {
                    if (s.split(":")[1].equalsIgnoreCase(command)) {
                        String substring = s.substring(0, s.lastIndexOf(":"));
                        pls.add(substring);
                    }
                }
            }
        }

        for (String plugin : pls) {
            Plugin pl = Bukkit.getPluginManager().getPlugin(plugin);
            if (pl != null) {
                plugins.add(pl.getName());
            } else {
                plugins.add(plugin);
            }
        }

        return plugins;
    }

    private Field commandMapField;
    private Field knownCommandsField;

    public Map<String, Command> getKnownCommands() {
        if (commandMapField == null) try {
            commandMapField = Class.forName("org.bukkit.craftbukkit." + Utils.getNmsVersion() + ".CraftServer").getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        SimpleCommandMap commandMap;
        try {
            commandMap = (SimpleCommandMap) commandMapField.get(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (knownCommandsField == null) try {
            knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }

        Map<String, Command> knownCommands;

        try {
            knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        return knownCommands;
    }

    /**
     * Loads and enables a plugin.
     *
     * @param plugin plugin to load
     * @return status message
     */
    private boolean load(Plugin plugin) {
        return PluginManagerUtils.load(plugin.getName());
    }

    /**
     * Loads and enables a plugin.
     *
     * @param pluginName plugin's name
     * @return status message
     */
    public boolean load(String pluginName) {
        Plugin plugin;
        File pluginDir = new File("plugins");
        if (!pluginDir.isDirectory()) {
            return false;
        }

        File pluginFile = new File(pluginDir, pluginName + ".jar");
        if (!pluginFile.isFile()) {
            for (File file : Objects.requireNonNull(pluginDir.listFiles())) {
                if (file.toPath().endsWith(".jar")) {
                    try {
                        PluginDescriptionFile description = LuckyEssentials.instance.getPluginLoader().getPluginDescription(file);
                        if (description.getName().equalsIgnoreCase(pluginName)) {
                            pluginFile = file;
                            break;
                        }
                    } catch (InvalidDescriptionException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
        }

        try {
            plugin = Bukkit.getPluginManager().loadPlugin(pluginFile);
        } catch (InvalidPluginException | InvalidDescriptionException e) {
            e.printStackTrace();
            return false;
        }

        if (luckyEssentials.getVersionSupport().getCommandWrap().isUsed()) {
            Bukkit.getScheduler().runTaskLater(LuckyEssentials.getInstance(), () -> {
                Map<String, Command> knownCommands = getKnownCommands();

                for (Map.Entry<String, Command> entry : knownCommands.entrySet().stream().filter(stringCommandEntry -> stringCommandEntry.getValue() instanceof PluginIdentifiableCommand).filter(stringCommandEntry -> {
                    PluginIdentifiableCommand command = (PluginIdentifiableCommand) stringCommandEntry.getValue();
                    return command.getPlugin().getName().equalsIgnoreCase(plugin.getName());
                }).collect(Collectors.toList())) {
                    String alias = entry.getKey();
                    Command command = entry.getValue();
                    luckyEssentials.getVersionSupport().getCommandWrap().wrap(command, alias);
                }

                if (Bukkit.getOnlinePlayers().size() >= 1)
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        luckyEssentials.getVersionSupport().reloadCommands(player);
                    }
            }, 10L);
        }

        plugin.onLoad();
        Bukkit.getPluginManager().enablePlugin(plugin);
        return true;
    }

    /**
     * Unload a plugin.
     *
     * @param plugin the plugin to unload
     * @return the message to send to the user.
     */
    public boolean unload(Plugin plugin) {
        if (luckyEssentials.getVersionSupport().getCommandWrap().isUsed()) {
            Map<String, Command> knownCommands = getKnownCommands();

            for (Map.Entry<String, Command> entry : knownCommands.entrySet().stream().filter(stringCommandEntry -> stringCommandEntry.getValue() instanceof PluginIdentifiableCommand).filter(stringCommandEntry -> {
                PluginIdentifiableCommand command = (PluginIdentifiableCommand) stringCommandEntry.getValue();
                return command.getPlugin().getName().equalsIgnoreCase(plugin.getName());
            }).collect(Collectors.toList())) {
                String alias = entry.getKey();
                luckyEssentials.getVersionSupport().getCommandWrap().unwrap(alias);
            }

            if (Bukkit.getOnlinePlayers().size() >= 1) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    luckyEssentials.getVersionSupport().reloadCommands(player);
                }
            }
        }

        String name = plugin.getName();

        PluginManager pluginManager = Bukkit.getPluginManager();

        SimpleCommandMap commandMap = null;

        List<Plugin> plugins = null;

        Map<String, Plugin> names = null;
        Map<String, Command> commands = null;
        Map<Event, SortedSet<RegisteredListener>> listeners = null;

        boolean reloadlisteners = true;
        if (pluginManager != null) {
            pluginManager.disablePlugin(plugin);
            try {
                Field pluginsField = Bukkit.getPluginManager().getClass().getDeclaredField("plugins");
                pluginsField.setAccessible(true);
                plugins = (List<Plugin>) pluginsField.get(pluginManager);

                Field lookupNamesField = Bukkit.getPluginManager().getClass().getDeclaredField("lookupNames");
                lookupNamesField.setAccessible(true);
                names = (Map<String, Plugin>) lookupNamesField.get(pluginManager);

                try {
                    Field listenersField = Bukkit.getPluginManager().getClass().getDeclaredField("listeners");
                    listenersField.setAccessible(true);
                    listeners = (Map<Event, SortedSet<RegisteredListener>>) listenersField.get(pluginManager);
                } catch (Exception e) {
                    reloadlisteners = false;
                }

                Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                commandMap = (SimpleCommandMap) commandMapField.get(pluginManager);

                Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
                knownCommandsField.setAccessible(true);
                commands = (Map<String, Command>) knownCommandsField.get(commandMap);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }

        }

        pluginManager.disablePlugin(plugin);

        if (plugins != null && plugins.contains(plugin)) {
            plugins.remove(plugin);
        }

        if (names != null && names.containsKey(name)) {
            names.remove(name);
        }

        if (listeners != null && reloadlisteners) for (SortedSet<RegisteredListener> set : listeners.values())
            set.removeIf(value -> value.getPlugin() == plugin);

        if (commandMap != null)
            for (Iterator<Map.Entry<String, Command>> it = commands.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Command> entry = it.next();
                if (entry.getValue() instanceof PluginCommand) {
                    PluginCommand c = (PluginCommand) entry.getValue();
                    if (c.getPlugin() == plugin) {
                        c.unregister(commandMap);
                        it.remove();
                    }
                }
            }

        // Attempt to close the classloader to unlock any handles on the plugin's jar file.
        ClassLoader classLoader = plugin.getClass().getClassLoader();
        if (classLoader instanceof URLClassLoader) {
            try {
                Field pluginField = classLoader.getClass().getDeclaredField("plugin");
                pluginField.setAccessible(true);
                pluginField.set(classLoader, null);

                Field pluginInitField = classLoader.getClass().getDeclaredField("pluginInit");
                pluginInitField.setAccessible(true);
                pluginInitField.set(classLoader, null);

            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(PluginManagerUtils.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {

                ((URLClassLoader) classLoader).close();
            } catch (IOException ex) {
                Logger.getLogger(PluginManagerUtils.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        // Will not work on processes started with the -XX:+DisableExplicitGC flag, but lets try it anyway.
        // This tries to get around the issue where Windows refuses to unlock jar files that were previously loaded into the JVM.
        System.gc();

        return true;
    }

    /**
     * Reload a plugin.
     *
     * @param plugin the plugin to reload
     */
    public boolean reload(Plugin plugin) {
        if (plugin != null) {
            boolean unloaded = unload(plugin);
            boolean loaded = load(plugin);

            return unloaded && loaded;
        }

        return false;
    }
}
