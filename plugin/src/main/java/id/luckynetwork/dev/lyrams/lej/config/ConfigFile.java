package id.luckynetwork.dev.lyrams.lej.config;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigFile extends YamlConfiguration {

    @Getter
    private final File file;

    @SneakyThrows
    public ConfigFile(JavaPlugin plugin, String name) {
        this.file = new File(plugin.getDataFolder(), name);

        if (!this.file.exists()) {
            plugin.saveResource(name, false);
        }

        this.load(file);
    }

    public void save() {
        try {
            this.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
