package id.luckynetwork.dev.lyrams.lej.managers;

import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.config.ConfigFile;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SlotsManager {

    private final LuckyEssentials plugin;

    private boolean enabled = false;
    private int maxPlayers = 1000;
    private String denyMessage = "§cServer is full!";

    public SlotsManager(LuckyEssentials plugin) {
        this.plugin = plugin;
        this.reload();
    }

    public void reload() {
        plugin.setSlotsConfig(new ConfigFile(plugin, "slots.yml"));

        this.enabled = plugin.getSlotsConfig().getBoolean("toggled", false);
        this.maxPlayers = plugin.getSlotsConfig().getInt("max-players", 1000);
        this.denyMessage = Utils.colorize(plugin.getSlotsConfig().getString("deny-message", "§cServer is full!"));
    }

    public void save() {
        plugin.getSlotsConfig().set("toggled", this.enabled);
        plugin.getSlotsConfig().set("max-players", this.maxPlayers);
        plugin.getSlotsConfig().set("deny-message", this.denyMessage);

        plugin.getSlotsConfig().save();
    }
}
