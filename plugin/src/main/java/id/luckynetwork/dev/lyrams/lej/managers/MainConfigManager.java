package id.luckynetwork.dev.lyrams.lej.managers;

import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.config.ConfigFile;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainConfigManager {

    private final LuckyEssentials plugin;

    private String prefix;
    private boolean chatLocked;
    private boolean oldInvsee;
    private boolean deathKick;
    private boolean rightClickInvsee;
    private boolean useConfirmation;

    public MainConfigManager(LuckyEssentials plugin) {
        this.plugin = plugin;
        this.reload();
    }

    public void reload() {
        plugin.setMainConfig(new ConfigFile(plugin, "config.yml"));

        this.prefix = Utils.colorize(plugin.getMainConfig().getString("prefix", "§e§lLUCKYESSENTIALS §a/ "));
        this.chatLocked = plugin.getMainConfig().getBoolean("chat-lock", false);
        this.oldInvsee = plugin.getMainConfig().getBoolean("old-invsee", false);
        this.deathKick = plugin.getMainConfig().getBoolean("death-kick", false);
        this.rightClickInvsee = plugin.getMainConfig().getBoolean("rightclick-invsee", true);
        this.useConfirmation = plugin.getMainConfig().getBoolean("use-confirmation", true);
    }

    public void save() {
        plugin.getMainConfig().set("prefix", this.prefix);
        plugin.getMainConfig().set("chat-lock", this.chatLocked);
        plugin.getMainConfig().set("death-kick", this.deathKick);

        plugin.getMainConfig().save();
    }
}
