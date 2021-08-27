package id.luckynetwork.dev.lyrams.lej.managers.whitelist;

import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.config.ConfigFile;
import id.luckynetwork.dev.lyrams.lej.enums.WhitelistCheckMode;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class WhitelistManager {

    private final LuckyEssentials plugin;

    private boolean enabled = false;
    private String denyMessage = "§cYou are not whitelisted!";
    private WhitelistCheckMode checkMode = WhitelistCheckMode.NAME;
    private List<WhitelistData> whitelistedList = new ArrayList<>();

    public WhitelistManager(LuckyEssentials plugin) {
        this.plugin = plugin;
        this.reload();
    }

    public void reload() {
        plugin.setMainConfig(new ConfigFile(plugin, "whitelist.yml"));

        this.enabled = plugin.getWhitelistConfig().getBoolean("toggled", false);
        this.denyMessage = Utils.colorize(plugin.getWhitelistConfig().getString("deny-message", "§cYou are not whitelisted!"));
        this.checkMode = WhitelistCheckMode.valueOf(plugin.getWhitelistConfig().getString("check-mode", "NAME").toUpperCase());

        this.whitelistedList.clear();
        plugin.getWhitelistConfig().getConfigurationSection("whitelisted").getKeys(false).stream()
                .map(it -> plugin.getWhitelistConfig().getConfigurationSection("whitelisted." + it))
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

    public void save() {
        plugin.getWhitelistConfig().set("toggled", this.enabled);
        plugin.getWhitelistConfig().set("deny-message", this.denyMessage);
        plugin.getWhitelistConfig().set("check-mode", this.checkMode.toString());
        plugin.getWhitelistConfig().set("whitelisted", "");

        this.whitelistedList.forEach(it -> {
            StringBuilder name = new StringBuilder(it.getName());
            int i = 1;
            while (plugin.getWhitelistConfig().get("whitelisted." + name) != null) {
                if (plugin.getWhitelistConfig().getString("whitelisted." + name + ".name").equals(it.getName()) && plugin.getWhitelistConfig().getString("whitelisted." + name + ".uuid").equals(it.getUuid())) {
                    break;
                } else {
                    int number = i++;
                    name.append(number);
                }
            }

            plugin.getWhitelistConfig().set("whitelisted." + name + ".name", it.getName());
            plugin.getWhitelistConfig().set("whitelisted." + name + ".uuid", it.getUuid());
        });

        plugin.getWhitelistConfig().save();
    }
}
