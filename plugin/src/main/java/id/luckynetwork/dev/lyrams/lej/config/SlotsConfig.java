package id.luckynetwork.dev.lyrams.lej.config;

import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@UtilityClass
public class SlotsConfig {


    private final LuckyEssentials plugin = LuckyEssentials.instance;
    public boolean enabled = false;
    public int maxPlayers = 1000;
    public String denyMessage = "Server is full!";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void reload() {
        File configFile = new File(plugin.getDataFolder(), "slots.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource("slots.yml", false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        enabled = config.getBoolean("slots", false);
        denyMessage = Utils.colorize(config.getString("deny-message", "You are not whitelisted!"));
        maxPlayers = config.getInt("max-players", 1000);
    }
}
