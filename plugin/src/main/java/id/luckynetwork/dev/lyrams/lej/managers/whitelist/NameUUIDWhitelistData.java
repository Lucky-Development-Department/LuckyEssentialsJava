package id.luckynetwork.dev.lyrams.lej.managers.whitelist;

import id.luckynetwork.dev.lyrams.lej.enums.settings.WhitelistCheckMode;
import lombok.Builder;
import lombok.Data;
import org.bukkit.OfflinePlayer;

@Data
@Builder(builderMethodName = "newBuilder")
public class NameUUIDWhitelistData extends WhitelistData {
    private final String uuid, name;

    @Override
    public boolean check(OfflinePlayer player, WhitelistCheckMode checkMode) {
        switch (checkMode) {
            case NAME:
                return player.getName().equals(this.name);
            case UUID:
                return player.getUniqueId().toString().equals(this.uuid);
            default:
                return player.getName().equals(this.name) && player.getUniqueId().toString().equals(this.uuid);
        }
    }
}
