package id.luckynetwork.dev.lyrams.lej.callbacks;

import lombok.Data;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Data
public class CanSkipCallback {
    private final CommandSender sender;
    private final boolean canSkip;

    @Nullable
    private final List<String> reason;
}
