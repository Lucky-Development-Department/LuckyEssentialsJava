package id.luckynetwork.dev.lyrams.lej.versionsupport;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public abstract class VersionSupport {

    @Getter
    private final Plugin plugin;
    @Getter
    private final String versionName;

    public VersionSupport(Plugin plugin, String versionName) {
        this.plugin = plugin;
        this.versionName = versionName;
    }

    public abstract ItemStack getItemInHand(Player player);

    public abstract void kill(Player player);

    public abstract double getMaxHealth(Player player);

}
