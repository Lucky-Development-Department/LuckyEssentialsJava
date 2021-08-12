package id.luckynetwork.dev.lyrams.lej.versionsupport.v1_8_R3;

import id.luckynetwork.dev.lyrams.lej.versionsupport.v1_8_R3.enums.LEnchants;
import id.luckynetwork.dev.lyrams.lej.versionsupport.VersionSupport;
import id.luckynetwork.dev.lyrams.lej.versionsupport.v1_8_R3.enums.LItemStack;
import net.minecraft.server.v1_8_R3.DamageSource;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class v1_8_R3 extends VersionSupport {

    public v1_8_R3(Plugin plugin) {
        super(plugin);
    }

    @Override
    public ItemStack getItemInHand(Player player) {
        return player.getItemInHand();
    }

    @Override
    public void kill(Player player) {
        ((CraftPlayer) player).getHandle().damageEntity(DamageSource.OUT_OF_WORLD, 1000);
    }

    @Override
    public double getMaxHealth(Player player) {
        return player.getMaxHealth();
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public ItemStack getItemByName(String name, int amount, int damage) {
        name = name.toUpperCase();
        ItemStack cachedItem = materialCache.getIfPresent(name);
        if (cachedItem != null) {
            return cachedItem;
        }

        ItemStack itemStack = null;
        try {
            itemStack = LItemStack.valueOf(name).getItemStack();
        } catch (Exception ignored) {
        }

        if (itemStack == null) {
            Material material = Material.getMaterial(name);
            if (material != null) {
                itemStack = new ItemStack(material);
            }
        }

        if (itemStack != null) {
            if (amount == -1) {
                itemStack.setAmount(Math.max(itemStack.getMaxStackSize(), 1));
            } else {
                itemStack.setAmount(amount);
            }

            if (itemStack.getDurability() != (short) 0) {
                itemStack.setDurability((short) damage);
            }

            materialCache.put(name, itemStack);
        }

        return itemStack;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public Enchantment getEnchantName(String name) {
        name = name.toUpperCase();
        Enchantment cachedEnchant = enchantmentCache.getIfPresent(name);
        if (cachedEnchant != null) {
            return cachedEnchant;
        }

        Enchantment enchantment = null;
        try {
            enchantment = LEnchants.valueOf(name).getEnchantment();
        } catch (Exception ignored) {
        }

        if (enchantment == null) {
            if (Enchantment.getByName(name) != null) {
                enchantment = Enchantment.getByName(name);
            }
        }

        if (enchantment != null) {
            enchantmentCache.put(name, enchantment);
        }

        return enchantment;
    }

}
