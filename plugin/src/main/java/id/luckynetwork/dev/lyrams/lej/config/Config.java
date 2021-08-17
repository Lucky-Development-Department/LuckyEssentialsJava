package id.luckynetwork.dev.lyrams.lej.config;

import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Config {

    private final LuckyEssentials plugin = LuckyEssentials.instance;
    public String PREFIX;

    public void reload() {
        plugin.saveDefaultConfig();

        Config.PREFIX = Utils.colorize(plugin.getConfig().getString("prefix", "§e§lLUCKYESSENTIALS §a/"));
    }

}
