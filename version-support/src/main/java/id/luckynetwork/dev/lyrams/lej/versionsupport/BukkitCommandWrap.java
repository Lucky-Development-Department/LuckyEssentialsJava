package id.luckynetwork.dev.lyrams.lej.versionsupport;

import org.bukkit.command.Command;

public abstract class BukkitCommandWrap {

    public abstract void wrap(Command command, String alias);

    public abstract void unwrap(String command);

    public abstract boolean isUsed();

}
