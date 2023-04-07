package id.luckynetwork.dev.lyrams.lej.commands.pluginmanager;

import com.google.common.base.Joiner;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import id.luckynetwork.dev.lyrams.lej.utils.pluginmanager.PluginManagerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PluginManagerCommand extends CommandClass {

    public PluginManagerCommand() {
        super("pluginmanager", Arrays.asList("lpm"));
    }

    public void listCommand(CommandSender sender) {
        if (!Utils.checkPermission(sender, "pluginmanager.list")) {
            return;
        }

        List<PluginManagerUtils.PluginInfo> pluginInfos = Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(PluginManagerUtils::getPluginInfo).sorted(Comparator.comparing(PluginManagerUtils.PluginInfo::getRawName)).collect(Collectors.toCollection(ArrayList::new));

        final int[] size = {pluginInfos.size()};
        sender.sendMessage("§ePlugins §d(" + size[0] + ")§e: ");
        pluginInfos.forEach(it -> {
            boolean last = (--size[0] < 1);
            String message;
            if (last) {
                message = "§8└─ ";
            } else {
                message = "§8├─ ";
            }

            message += it.getName() + " §7(§6v§e" + it.getVersion() + "§7)";
            sender.sendMessage(message);
        });
    }

    public void infoCommand(CommandSender sender, String pluginName) {
        if (!Utils.checkPermission(sender, "pluginmanager.info")) {
            return;
        }

        Plugin pluginByName = PluginManagerUtils.getPluginByName(pluginName);
        if (pluginByName == null) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlugin not found!");
            return;
        }

        sender.sendMessage("§e" + pluginByName.getName() + " info:");
        sender.sendMessage("§8├─ §eVersion: §a" + pluginByName.getDescription().getVersion());
        sender.sendMessage("§8├─ §eAuthor(s): §a" + Joiner.on(", ").join(pluginByName.getDescription().getAuthors()));
        sender.sendMessage("§8├─ §eState: " + Utils.colorizeTrueFalse(pluginByName.isEnabled(), TrueFalseType.ENABLED));
        sender.sendMessage("§8├─ §eMain Class: §a" + pluginByName.getDescription().getMain());
        sender.sendMessage("§8├─ §eDepends: §a" + Joiner.on(", ").join(pluginByName.getDescription().getDepend()));
        sender.sendMessage("§8└─ §eSoft Depends: §a" + Joiner.on(", ").join(pluginByName.getDescription().getSoftDepend()));
    }

    public void loadCommand(CommandSender sender, String pluginName) {
        if (!Utils.checkPermission(sender, "pluginmanager.load")) {
            return;
        }

        Plugin pluginByName = PluginManagerUtils.getPluginByName(pluginName);
        if (pluginByName != null) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlugin is already loaded!");
            return;
        }

        boolean loaded = PluginManagerUtils.load(pluginName);
        if (loaded) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSuccessfully loaded §d" + pluginName + "§e.");
        } else {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cFailed loading §l" + pluginName + "§c! §7§oCheck console for more details!");
        }
    }

    public void unLoadCommand(CommandSender sender, String pluginName) {
        if (!Utils.checkPermission(sender, "pluginmanager.unload")) {
            return;
        }

        Plugin pluginByName = PluginManagerUtils.getPluginByName(pluginName);
        if (pluginByName == null) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlugin is not loaded!");
            return;
        }

        boolean loaded = PluginManagerUtils.unload(pluginByName);
        if (loaded) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSuccessfully unloaded §d" + pluginName + "§e.");
        } else {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cFailed unloading §l" + pluginName + "§c! §7§oCheck console for more details!");
        }
    }

    public void reloadCommand(CommandSender sender, String pluginName) {
        if (!Utils.checkPermission(sender, "pluginmanager.reload")) {
            return;
        }

        Plugin pluginByName = PluginManagerUtils.getPluginByName(pluginName);
        if (pluginByName == null) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlugin not found!");
            return;
        }

        boolean reloaded = PluginManagerUtils.reload(pluginByName);
        if (reloaded) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSuccessfully reloaded §d" + pluginName + "§e.");
        } else {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cFailed reloading §l" + pluginName + "§c! §7§oCheck console for more details!");
        }
    }

    public void enableCommand(CommandSender sender, String pluginName) {
        if (!Utils.checkPermission(sender, "pluginmanager.enable")) {
            return;
        }

        Plugin pluginByName = PluginManagerUtils.getPluginByName(pluginName);
        if (pluginByName == null) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlugin not found!");
            return;
        }
        if (pluginByName.isEnabled()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlugin is already enabled!");
            return;
        }

        boolean enabled = PluginManagerUtils.enable(pluginByName);
        if (enabled) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSuccessfully enabled §d" + pluginName + "§e.");
        } else {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cFailed enabling §l" + pluginName + "§c! §7§oCheck console for more details!");
        }
    }

    public void disableCommand(CommandSender sender, String pluginName) {
        if (!Utils.checkPermission(sender, "pluginmanager.disable")) {
            return;
        }

        Plugin pluginByName = PluginManagerUtils.getPluginByName(pluginName);
        if (pluginByName == null) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlugin not found!");
            return;
        }
        if (!pluginByName.isEnabled()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlugin is already disabled!");
            return;
        }

        boolean disabled = PluginManagerUtils.disable(pluginByName);
        if (disabled) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSuccessfully disabled §d" + pluginName + "§e.");
        } else {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cFailed disabling §l" + pluginName + "§c! §7§oCheck console for more details!");
        }
    }


    public void restartCommand(CommandSender sender, String pluginName) {
        if (!Utils.checkPermission(sender, "pluginmanager.restart")) {
            return;
        }

        Plugin pluginByName = PluginManagerUtils.getPluginByName(pluginName);
        if (pluginByName == null) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlugin not found!");
            return;
        }

        boolean restarted = PluginManagerUtils.restart(pluginByName);
        if (restarted) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSuccessfully restarted §d" + pluginName + "§e.");
        } else {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cFailed restarting §l" + pluginName + "§c! §7§oCheck console for more details!");
        }
    }

    public void usageCommand(CommandSender sender, String pluginName) {
        if (!Utils.checkPermission(sender, "pluginmanager.usage")) {
            return;
        }

        Plugin pluginByName = PluginManagerUtils.getPluginByName(pluginName);
        if (pluginByName == null) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlugin not found!");
            return;
        }

        String usages = PluginManagerUtils.getUsages(pluginByName);
        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eCommands: §a" + usages);
    }

    public void lookupCommand(CommandSender sender, String command) {
        if (!Utils.checkPermission(sender, "pluginmanager.lookup")) {
            return;
        }

        List<String> pluginByCommands = PluginManagerUtils.findByCommand(command);
        if (pluginByCommands.isEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo plugins found!");
            return;
        }

        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§d" + command + " §eis registered to: §a" + Joiner.on(", ").join(pluginByCommands));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "pluginmanager")) {
            return;
        }

        if (args.length == 0) {
            this.sendDefaultMessage(sender);
            return;
        }

        String subCommand = args[0];
        switch (subCommand.toLowerCase()) {
            case "enable": {
                if (args.length < 2) {
                    this.sendDefaultMessage(sender);
                    return;
                }

                String pluginName = args[1];
                this.enableCommand(sender, pluginName);
                break;
            }
            case "disable": {
                if (args.length < 2) {
                    this.sendDefaultMessage(sender);
                    return;
                }

                String pluginName = args[1];
                this.disableCommand(sender, pluginName);
                break;
            }
            case "restart": {
                if (args.length < 2) {
                    this.sendDefaultMessage(sender);
                    return;
                }

                String pluginName = args[1];
                this.restartCommand(sender, pluginName);
                break;
            }
            case "usage":
            case "uses":
            case "help": {
                if (args.length < 2) {
                    this.sendDefaultMessage(sender);
                    return;
                }

                String pluginName = args[1];
                this.usageCommand(sender, pluginName);
                break;
            }
            case "lookup": {
                if (args.length < 2) {
                    this.sendDefaultMessage(sender);
                    return;
                }

                String pluginName = args[1];
                this.lookupCommand(sender, pluginName);
                break;
            }
            case "load": {
                if (args.length < 2) {
                    this.sendDefaultMessage(sender);
                    return;
                }

                String pluginName = args[1];
                this.loadCommand(sender, pluginName);
                break;
            }
            case "unload": {
                if (args.length < 2) {
                    this.sendDefaultMessage(sender);
                    return;
                }

                String pluginName = args[1];
                this.unLoadCommand(sender, pluginName);
                break;
            }
            case "reload": {
                if (args.length < 2) {
                    this.sendDefaultMessage(sender);
                    return;
                }

                String pluginName = args[1];
                this.reloadCommand(sender, pluginName);
                break;
            }
            case "info": {
                if (args.length < 2) {
                    this.sendDefaultMessage(sender);
                    return;
                }

                String pluginName = args[1];
                this.infoCommand(sender, pluginName);
                break;
            }
            case "list": {
                this.listCommand(sender);
                break;
            }
            default:
                this.sendDefaultMessage(sender);
                break;
        }
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§ePluginManager command:");
        sender.sendMessage("§8└─ §e/pluginmanager §8- §7Shows this message");
        sender.sendMessage("§8└─ §e/pluginmanager enable <pluginName> §8- §7Enables a plugin");
        sender.sendMessage("§8└─ §e/pluginmanager disable <pluginName> §8- §7Disables a plugin");
        sender.sendMessage("§8└─ §e/pluginmanager restart <pluginName> §8- §7Restarts a plugin");
        sender.sendMessage("§8└─ §e/pluginmanager usage <pluginName> §8- §7Gets the command usages of a plugin");
        sender.sendMessage("§8└─ §e/pluginmanager lookup <command> §8- §7Looks up for the plugin of the command");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "pluginmanager")) {
            return null;
        }

        if (args.length == 1) {
            return Stream.of("enable", "disable", "restart", "usage", "uses", "help", "lookup").filter(it -> it.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        } else if (args.length == 2) {
            return Stream.of(Bukkit.getPluginManager().getPlugins()).map(Plugin::getName).filter(it -> it.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
        }

        return null;
    }
}
