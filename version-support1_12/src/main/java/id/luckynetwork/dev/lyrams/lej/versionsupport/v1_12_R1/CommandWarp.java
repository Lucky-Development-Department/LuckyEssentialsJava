package id.luckynetwork.dev.lyrams.lej.versionsupport.v1_12_R1;

import id.luckynetwork.dev.lyrams.lej.versionsupport.BukkitCommandWrap;
import org.bukkit.command.Command;

public class CommandWarp extends BukkitCommandWrap {

    @Override
    public void wrap(Command command, String alias) {
    }

    @Override
    public void unwrap(String command) {
    }

    @Override
    public boolean isUsed() {
        return false;
    }
}
