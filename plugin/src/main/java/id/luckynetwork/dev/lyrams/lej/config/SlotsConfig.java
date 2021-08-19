package id.luckynetwork.dev.lyrams.lej.config;

import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@UtilityClass
public class SlotsConfig {


    private final LuckyEssentials plugin = LuckyEssentials.instance;
    public boolean enabled = false;
    public int maxPlayers = 1000;
    public String denyMessage = "§cServer is full!";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void reload() {
        File configFile = new File(plugin.getDataFolder(), "slots.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource("slots.yml", false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        enabled = config.getBoolean("toggled", false);
        denyMessage = Utils.colorize(config.getString("deny-message", "§cServer is full!"));
        maxPlayers = config.getInt("max-players", 1000);
    }

    @SneakyThrows
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void save() {
        File configFile = new File(plugin.getDataFolder(), "slots.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource("slots.yml", false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        config.set("toggled", enabled);
        config.set("deny-message", denyMessage);
        config.set("max-players", maxPlayers);

        config.save(configFile);
    }
}
