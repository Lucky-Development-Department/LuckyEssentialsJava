package id.luckynetwork.dev.lyrams.lej.managers.whitelist;

import id.luckynetwork.dev.lyrams.lej.enums.settings.WhitelistCheckMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public abstract class WhitelistData {

    public abstract boolean check(OfflinePlayer player, WhitelistCheckMode checkMode);

}
