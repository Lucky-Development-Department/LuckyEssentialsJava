package id.luckynetwork.dev.lyrams.lej.versionsupport;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
public abstract class VersionSupport {

    private final Plugin plugin;
    public final Cache<String, Enchantment> enchantmentCache;

    public VersionSupport(Plugin plugin) {
        this.plugin = plugin;
        this.enchantmentCache = CacheBuilder.newBuilder()
                .expireAfterAccess(1, TimeUnit.HOURS)
                .build();
    }

    public abstract ItemStack getItemInHand(Player player);

    public abstract void kill(Player player);

    public abstract double getMaxHealth(Player player);

    public abstract ItemStack getItemByName(String name, int amount, int damage);

    public abstract List<String> getEnchantments();

    public abstract Enchantment getEnchantName(String name);

}
