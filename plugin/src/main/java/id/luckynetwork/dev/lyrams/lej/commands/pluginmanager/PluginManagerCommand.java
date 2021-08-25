package id.luckynetwork.dev.lyrams.lej.commands.pluginmanager;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.Flag;
import cloud.commandframework.annotations.specifier.Greedy;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import com.google.common.base.Joiner;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.config.Config;
import id.luckynetwork.dev.lyrams.lej.config.SlotsConfig;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import id.luckynetwork.dev.lyrams.lej.utils.pluginmanager.PluginManagerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PluginManagerCommand extends CommandClass {

    @CommandMethod("luckyessentials pluginmanager|pm list")
    @CommandDescription("Lists all loaded plugin")
    public void listCommand(
            final @NonNull CommandSender sender,
            final @Nullable @Flag(value = "fullname", aliases = "f", description = "Should it also show the plugin version?") Boolean fullName
    ) {
        if (!Utils.checkPermission(sender, "pluginmanager.list")) {
            return;
        }

        boolean includeName = fullName != null && fullName;
        List<String> pluginNames = Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .map(it -> PluginManagerUtils.getFormattedName(it, includeName))
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toCollection(ArrayList::new));

        sender.sendMessage(Config.PREFIX + "§ePlugins §d(" + pluginNames.size() + ")§e: " + Joiner.on(", ").join(pluginNames));
    }

    @CommandMethod("luckyessentials pluginmanager|pm info <pluginName>")
    @CommandDescription("Gets the info of a plugin")
    public void infoCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "pluginName", description = "The plugin name", suggestions = "plugins") @Greedy String pluginName
    ) {
        if (!Utils.checkPermission(sender, "pluginmanager.info")) {
            return;
        }

        Plugin plugin = PluginManagerUtils.getPluginByName(pluginName);
        if (plugin == null) {
            sender.sendMessage(Config.PREFIX + "§cPlugin not found!");
            return;
        }

        sender.sendMessage("§e" + plugin.getName() + " info:");
        sender.sendMessage("§8├─ §eVersion: §a" + plugin.getDescription().getVersion());
        sender.sendMessage("§8├─ §eAuthor(s): §a" + Joiner.on(", ").join(plugin.getDescription().getAuthors()));
        sender.sendMessage("§8├─ §eState: " + Utils.colorizeTrueFalse(SlotsConfig.enabled, TrueFalseType.ENABLED));
        sender.sendMessage("§8├─ §eMain Class: §a" + plugin.getDescription().getMain());
        sender.sendMessage("§8├─ §eDepends: §a" + Joiner.on(", ").join(plugin.getDescription().getDepend()));
        sender.sendMessage("§8└─ §eSoft Depends: §a" + Joiner.on(", ").join(plugin.getDescription().getSoftDepend()));
    }

    @CommandMethod("luckyessentials pluginmanager|pm load <pluginName>")
    @CommandDescription("Loads a plugin")
    public void loadCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "pluginName", description = "The plugin name", suggestions = "plugins") @Greedy String pluginName
    ) {
        if (!Utils.checkPermission(sender, "pluginmanager.load")) {
            return;
        }

        Plugin plugin = PluginManagerUtils.getPluginByName(pluginName);
        if (plugin != null) {
            sender.sendMessage(Config.PREFIX + "§cPlugin is already loaded!");
            return;
        }

        boolean loaded = PluginManagerUtils.load(pluginName);
        if (loaded) {
            sender.sendMessage(Config.PREFIX + "§eSuccessfully loaded §d" + pluginName + "§e!");
        } else {
            sender.sendMessage(Config.PREFIX + "§cFailed loading §l" + pluginName + "§c! §7§oCheck console for more details!");
        }
    }

    @CommandMethod("luckyessentials pluginmanager|pm unload <pluginName>")
    @CommandDescription("Unloads a plugin")
    public void unLoadCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "pluginName", description = "The plugin name", suggestions = "plugins") @Greedy String pluginName
    ) {
        if (!Utils.checkPermission(sender, "pluginmanager.unload")) {
            return;
        }

        Plugin plugin = PluginManagerUtils.getPluginByName(pluginName);
        if (plugin == null) {
            sender.sendMessage(Config.PREFIX + "§cPlugin is not loaded!");
            return;
        }

        boolean loaded = PluginManagerUtils.unload(plugin);
        if (loaded) {
            sender.sendMessage(Config.PREFIX + "§eSuccessfully unloaded §d" + pluginName + "§e!");
        } else {
            sender.sendMessage(Config.PREFIX + "§cFailed unloading §l" + pluginName + "§c! §7§oCheck console for more details!");
        }
    }

    @CommandMethod("luckyessentials pluginmanager|pm reload <pluginName>")
    @CommandDescription("Reloads a plugin")
    public void reloadCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "pluginName", description = "The plugin name", suggestions = "plugins") @Greedy String pluginName
    ) {
        if (!Utils.checkPermission(sender, "pluginmanager.reload")) {
            return;
        }

        Plugin plugin = PluginManagerUtils.getPluginByName(pluginName);
        if (plugin == null) {
            sender.sendMessage(Config.PREFIX + "§cPlugin not found!");
            return;
        }

        boolean reloaded = PluginManagerUtils.reload(plugin);
        if (reloaded) {
            sender.sendMessage(Config.PREFIX + "§eSuccessfully reloaded §d" + pluginName + "§e!");
        } else {
            sender.sendMessage(Config.PREFIX + "§cFailed reloading §l" + pluginName + "§c! §7§oCheck console for more details!");
        }
    }

    @CommandMethod("luckyessentials pluginmanager|pm enable <pluginName>")
    @CommandDescription("Enables a plugin")
    public void enableCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "pluginName", description = "The plugin name", suggestions = "plugins") @Greedy String pluginName
    ) {
        if (!Utils.checkPermission(sender, "pluginmanager.enable")) {
            return;
        }

        Plugin plugin = PluginManagerUtils.getPluginByName(pluginName);
        if (plugin == null) {
            sender.sendMessage(Config.PREFIX + "§cPlugin not found!");
            return;
        }
        if (plugin.isEnabled()) {
            sender.sendMessage(Config.PREFIX + "§cPlugin is already enabled!");
            return;
        }

        boolean enabled = PluginManagerUtils.enable(plugin);
        if (enabled) {
            sender.sendMessage(Config.PREFIX + "§eSuccessfully enabled §d" + pluginName + "§e!");
        } else {
            sender.sendMessage(Config.PREFIX + "§cFailed enabling §l" + pluginName + "§c! §7§oCheck console for more details!");
        }
    }

    @CommandMethod("luckyessentials pluginmanager|pm disable <pluginName>")
    @CommandDescription("Disables a plugin")
    public void disableCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "pluginName", description = "The plugin name", suggestions = "plugins") @Greedy String pluginName
    ) {
        if (!Utils.checkPermission(sender, "pluginmanager.disable")) {
            return;
        }

        Plugin plugin = PluginManagerUtils.getPluginByName(pluginName);
        if (plugin == null) {
            sender.sendMessage(Config.PREFIX + "§cPlugin not found!");
            return;
        }
        if (!plugin.isEnabled()) {
            sender.sendMessage(Config.PREFIX + "§cPlugin is already disabled!");
            return;
        }

        boolean disabled = PluginManagerUtils.disable(plugin);
        if (disabled) {
            sender.sendMessage(Config.PREFIX + "§eSuccessfully disabled §d" + pluginName + "§e!");
        } else {
            sender.sendMessage(Config.PREFIX + "§cFailed disabling §l" + pluginName + "§c! §7§oCheck console for more details!");
        }
    }

    @CommandMethod("luckyessentials pluginmanager|pm restart <pluginName>")
    @CommandDescription("Restarts a plugin")
    public void restartCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "pluginName", description = "The plugin name", suggestions = "plugins") @Greedy String pluginName
    ) {
        if (!Utils.checkPermission(sender, "pluginmanager.restart")) {
            return;
        }

        Plugin plugin = PluginManagerUtils.getPluginByName(pluginName);
        if (plugin == null) {
            sender.sendMessage(Config.PREFIX + "§cPlugin not found!");
            return;
        }

        boolean restarted = PluginManagerUtils.restart(plugin);
        if (restarted) {
            sender.sendMessage(Config.PREFIX + "§eSuccessfully restarted §d" + pluginName + "§e!");
        } else {
            sender.sendMessage(Config.PREFIX + "§cFailed restarting §l" + pluginName + "§c! §7§oCheck console for more details!");
        }
    }

    @CommandMethod("luckyessentials pluginmanager|pm usage|uses|help <pluginName>")
    @CommandDescription("Gets the command usages of a plugin")
    public void usageCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "pluginName", description = "The plugin name", suggestions = "plugins") @Greedy String pluginName
    ) {
        if (!Utils.checkPermission(sender, "pluginmanager.usage")) {
            return;
        }

        Plugin plugin = PluginManagerUtils.getPluginByName(pluginName);
        if (plugin == null) {
            sender.sendMessage(Config.PREFIX + "§cPlugin not found!");
            return;
        }

        String usages = PluginManagerUtils.getUsages(plugin);
        sender.sendMessage(Config.PREFIX + "§eCommands: §a" + usages);
    }

    @CommandMethod("luckyessentials pluginmanager|pm lookup <command>")
    @CommandDescription("Looks up for the plugin of the command")
    public void lookupCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "command", description = "The command") @Greedy String command
    ) {
        if (!Utils.checkPermission(sender, "pluginmanager.lookup")) {
            return;
        }

        List<String> pluginByCommands = PluginManagerUtils.findByCommand(command);
        if (pluginByCommands.isEmpty()) {
            sender.sendMessage(Config.PREFIX + "§cNo plugins found!");
            return;
        }

        sender.sendMessage(Config.PREFIX + "§d" + command + " §eis registered to: §a" + Joiner.on(", ").join(pluginByCommands));
    }

    @Suggestions("plugins")
    public List<String> plugins(CommandContext<CommandSender> context, String current) {
        return Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .map(Plugin::getName)
                .filter(it -> it.toLowerCase().startsWith(current.toLowerCase()))
                .collect(Collectors.toList());
    }
}
