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
    public String denyMessage = "§cYou are not whitelisted!";
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
        denyMessage = Utils.colorize(config.getString("deny-message", "§cYou are not whitelisted!"));
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
        config.set("whitelisted", "");
        whitelistedList.forEach(it -> {
            StringBuilder name = new StringBuilder(it.getName());
            int i = 1;
            while (config.get("whitelisted." + name) != null) {
                if (config.getString("whitelisted." + name + ".name").equals(it.getName()) && config.getString("whitelisted." + name + ".uuid").equals(it.getUuid())) {
                    break;
                } else {
                    int number = i++;
                    name.append(number);
                }
            }

            config.set("whitelisted." + name + ".name", it.getName());
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
