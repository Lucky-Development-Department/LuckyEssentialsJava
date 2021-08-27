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

    public MainConfigManager(LuckyEssentials plugin) {
        this.plugin = plugin;
        this.reload();
    }

    public void reload() {
        plugin.setMainConfig(new ConfigFile(plugin, "config.yml"));

        this.prefix = Utils.colorize(plugin.getMainConfig().getString("prefix", "§e§lLUCKYESSENTIALS §a/ "));
    }
}
