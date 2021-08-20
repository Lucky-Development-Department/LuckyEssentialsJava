package id.luckynetwork.dev.lyrams.lej.versionsupport.v1_16_R1;

import id.luckynetwork.dev.lyrams.lej.versionsupport.BukkitCommandWrap;
import id.luckynetwork.dev.lyrams.lej.versionsupport.VersionSupport;
import id.luckynetwork.dev.lyrams.lej.versionsupport.v1_16_R1.enums.LEnchants;
import id.luckynetwork.dev.lyrams.lej.versionsupport.v1_16_R1.enums.LItemStack;
import net.minecraft.server.v1_16_R3.DamageSource;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class v1_16_R1 extends VersionSupport {

    private final CommandWarp commandWrap;

    public v1_16_R1(Plugin plugin) {
        super(plugin);
        this.commandWrap = new CommandWarp();
    }

    @Override
    public ItemStack getItemInHand(Player player) {
        return player.getInventory().getItemInMainHand();
    }

    @Override
    public void kill(Player player) {
        ((CraftPlayer) player).getHandle().damageEntity(DamageSource.OUT_OF_WORLD, 1000);
    }

    @Override
    public double getMaxHealth(Player player) {
        return player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
    }

    @Override
    public ItemStack getItemByName(String name, int amount, int damage) {
        name = name.toUpperCase();
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

            if (damage != 0) {
                itemStack.setDurability((short) damage);
            }
        }

        return itemStack;
    }

    @Override
    public List<String> getEnchantments() {
        return Arrays.stream(LEnchants.values()).map(Enum::name).collect(Collectors.toList());
    }

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

    @Override
    public void reloadCommands(Player player) {
        player.updateCommands();
    }

    @Override
    public BukkitCommandWrap getCommandWrap() {
        return commandWrap;
    }

}
