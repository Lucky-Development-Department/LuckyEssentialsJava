package id.luckynetwork.dev.lyrams.lej.versionsupport;

import net.minecraft.server.v1_12_R1.DamageSource;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class v1_12_R1 extends VersionSupport {

    public v1_12_R1(Plugin plugin, String versionName) {
        super(plugin, versionName);
    }

    @Override
    public ItemStack getItemInHand(Player player) {
        return player.getInventory().getItemInMainHand();
    }

    @Override
    public void kill(Player player) {
        ((CraftPlayer) player).getHandle().damageEntity(DamageSource.OUT_OF_WORLD, 1000);
    }

}
