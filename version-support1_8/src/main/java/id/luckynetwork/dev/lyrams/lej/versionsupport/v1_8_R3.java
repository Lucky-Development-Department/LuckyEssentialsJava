package id.luckynetwork.dev.lyrams.lej.versionsupport;

import net.minecraft.server.v1_8_R3.DamageSource;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class v1_8_R3 extends VersionSupport {

    public v1_8_R3(Plugin plugin, String versionName) {
        super(plugin, versionName);
    }

    @Override
    public ItemStack getItemInHand(Player player) {
        return player.getItemInHand();
    }

    @Override
    public void kill(Player player) {
        ((CraftPlayer) player).getHandle().damageEntity(DamageSource.OUT_OF_WORLD, 1000);
    }

}
