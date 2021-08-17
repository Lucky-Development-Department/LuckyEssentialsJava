package id.luckynetwork.dev.lyrams.lej.config;

import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class WhitelistConfig {

    private final LuckyEssentials plugin = LuckyEssentials.instance;
    public boolean enabled = false;
    public String denyMessage = "You are not whitelisted!";
    public CheckMode checkMode = CheckMode.NAME;
    public List<WhitelistData> whitelistedList = new ArrayList<>();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void reload() {
        File configFile = new File(plugin.getDataFolder(), "whitelist.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource("whitelist.yml", false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        enabled = config.getBoolean("toggled", false);
        denyMessage = Utils.colorize(config.getString("deny-message", "You are not whitelisted!"));
        checkMode = CheckMode.valueOf(config.getString("check-mode", "NAME").toUpperCase());

        whitelistedList.clear();
        config.getConfigurationSection("whitelisted").getKeys(false).stream()
                .map(it -> config.getConfigurationSection("whitelisted." + it))
                .forEach(it -> {
                    try {
                        WhitelistData data = WhitelistData.newBuilder()
                                .name(it.getString("name"))
                                .uuid(it.getString("uuid"))
                                .build();

                        whitelistedList.add(data);
                    } catch (Exception ignored) {
                        plugin.getLogger().severe("There is something wrong in your whitelist.yml configuration!");
                    }
                });
    }

    @SneakyThrows
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void save() {
        File configFile = new File(plugin.getDataFolder(), "whitelist.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource("whitelist.yml", false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        config.set("toggled", enabled);
        config.set("deny-message", denyMessage);
        config.set("check-mode", checkMode.toString());
        whitelistedList.forEach(it -> {
            String name = it.getName();
            config.set("whitelisted." + name + ".name", name);
            config.set("whitelisted." + name + ".uuid", it.getUuid());
        });

        config.save(configFile);
    }

    public enum CheckMode {
        UUID,
        NAME,
        BOTH
    }

    @Data
    @Builder(builderMethodName = "newBuilder")
    public class WhitelistData {
        private final String uuid, name;
    }
}
