package id.luckynetwork.dev.lyrams.lej.utils;

import com.google.common.base.Joiner;
import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.callbacks.IsDoubleCallback;
import id.luckynetwork.dev.lyrams.lej.callbacks.IsIntegerCallback;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class Utils {

    private final LuckyEssentials plugin;
    @Getter
    private final String nmsVersion;
    private String pluginDescription = null;

    static {
        plugin = LuckyEssentials.getInstance();
        nmsVersion = Bukkit.getServer().getClass().getName().split("\\.")[3];
    }

    /**
     * Gets the plugin description
     */
    public String getPluginDescription() {
        if (pluginDescription == null) {
            pluginDescription = plugin.getMainConfigManager().getPrefix() + "§eThis server is running §aLuckyEssentials §c" + plugin.getDescription().getVersion() + " §eby §d" + Joiner.on(",").join(plugin.getDescription().getAuthors());
        }

        return pluginDescription;
    }

    public String colorize(String toColor) {
        return ChatColor.translateAlternateColorCodes('&', toColor);
    }

    /**
     * see {@link Utils#checkPermission(CommandSender, String, boolean, boolean, boolean, String)}
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean checkPermission(CommandSender sender, String permission) {
        return Utils.checkPermission(sender, permission, false, false, false, null);
    }

    /**
     * see {@link Utils#checkPermission(CommandSender, String, boolean, boolean, boolean, String)}
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean checkPermission(CommandSender sender, boolean others, String permission) {
        return Utils.checkPermission(sender, permission, others, false, false, null);
    }

    /**
     * checks if the command target has a certain permission
     *
     * @param target         the target
     * @param permission     the permission
     * @param others         is the target executing this to other player(s)?
     * @param showPermission should the permission deny message show the required permission?
     * @param silent         should the target be notified of their lack of permission?
     * @param command        the command, if set to null then the permission deny message will show the executed command.
     * @return see {@link CommandSender#hasPermission(String)}
     */
    public boolean checkPermission(CommandSender target, String permission, boolean others, boolean showPermission, boolean silent, @Nullable String command) {
        permission = "luckyessentials." + permission.toLowerCase();
        if (others) {
            permission += ".others";
        }

        if (target.hasPermission(permission)) {
            return true;
        }

        if (!silent) {
            if (showPermission) {
                if (command != null) {
                    target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cYou don't have the required permission §l" + permission + " to do §l" + command + "§c!");
                } else {
                    target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cYou don't have the required permission §l" + permission + " to do that!");
                }
            } else {
                if (command != null) {
                    target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cYou don't have the required permission to do §l" + command + "§c!");
                } else {
                    target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cYou don't have the required permission to do that!");
                }
            }
        }
        return false;
    }

    /**
     * colorizes a string from {@link TrueFalseType} to green if true and to red if false
     *
     * @param state         the state
     * @param trueFalseType see {@link TrueFalseType}
     * @return a green {@link TrueFalseType#getIfTrue()} if true and a red {@link TrueFalseType#getIfFalse()} if false
     */
    public String colorizeTrueFalse(boolean state, TrueFalseType trueFalseType) {
        if (state) {
            return "§a" + trueFalseType.getIfTrue();
        }
        return "§c" + trueFalseType.getIfFalse();
    }

    /**
     * colorizes a string from {@link TrueFalseType} to green if true and to red if false
     *
     * @param state         the state
     * @param trueFalseType see {@link TrueFalseType}
     * @return a green {@link TrueFalseType#getIfTrue()} if true and a red {@link TrueFalseType#getIfFalse()} if false
     */
    public String colorizeTrueFalseBold(boolean state, TrueFalseType trueFalseType) {
        if (state) {
            return "§a§l" + trueFalseType.getIfTrue();
        }
        return "§c§l" + trueFalseType.getIfFalse();
    }

    /**
     * Applies metadata to player
     *
     * @param player   the player
     * @param metadata the metadata
     * @param value    the metadata value
     */
    public void applyMetadata(Player player, String metadata, Object value) {
        player.setMetadata(metadata, new FixedMetadataValue(LuckyEssentials.getInstance(), value));
    }

    /**
     * Removes metadata from player
     *
     * @param player   the player
     * @param metadata the metadata to remove
     */
    public void removeMetadata(Player player, String metadata) {
        player.removeMetadata(metadata, LuckyEssentials.getInstance());
    }

    /**
     * Checks if a string is parsable to an Integer
     *
     * @param s the string
     * @return {@link IsIntegerCallback}
     */
    public IsIntegerCallback isInteger(String s) {
        IsIntegerCallback callback = new IsIntegerCallback(false, 0);
        if (s == null) {
            return callback;
        }

        try {
            return callback.setInteger(true).setValue(Integer.parseInt(s));
        } catch (Exception ignored) {
            return callback;
        }
    }

    /**
     * Checks if a string is parsable to a double
     *
     * @param s the string
     * @return {@link IsDoubleCallback}
     */
    public IsDoubleCallback isDouble(String s) {
        IsDoubleCallback callback = new IsDoubleCallback(false, 0.0);
        if (s == null) {
            return callback;
        }

        try {
            return callback.setDouble(true).setValue(Double.parseDouble(s));
        } catch (Exception ignored) {
            return callback;
        }
    }
}
