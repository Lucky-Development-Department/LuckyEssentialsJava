package id.luckynetwork.dev.lyrams.lej.managers.whitelist;

import id.luckynetwork.dev.lyrams.lej.enums.settings.WhitelistCheckMode;
import lombok.Builder;
import lombok.Data;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@Data
@Builder(builderMethodName = "newBuilder")
public class PermissionWhitelistData extends WhitelistData {

    private final String permission;

    @Override
    public boolean check(OfflinePlayer player, WhitelistCheckMode checkMode) {
        if (!(player instanceof Player)) {
            return false;
        }

        return ((Player) player).hasPermission(this.permission);
    }
}
