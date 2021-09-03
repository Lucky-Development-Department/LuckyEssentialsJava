package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.specifier.Greedy;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class EditSignCommand extends CommandClass {

    @CommandMethod("editsign|sign set <line> <text>")
    @CommandDescription("Sets the text of a line in a sign")
    public void setCommand(
            final @NonNull Player sender,
            final @NonNull @Argument(value = "line", description = "The line") Integer line,
            final @Nullable @Argument(value = "text", description = "The text") @Greedy String text
    ) {
        if (!Utils.checkPermission(sender, "editsign")) {
            return;
        }

        Sign sign = this.getSign(sender);
        if (sign == null) {
            return;
        }

        if (line > 3 || line < 0) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid sign line: §l" + line + "§c!");
            return;
        }

        sign.setLine(line, Utils.colorize(text));
        sign.update();
        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eLine §d" + line + " §eupdated.");
    }

    @CommandMethod("editsign|sign clear [line]")
    @CommandDescription("Clears the text of a line or all lines in a sign")
    public void clearCommand(
            final @NonNull Player sender,
            final @NonNull @Argument(value = "line", description = "The line", defaultValue = "-1") Integer line
    ) {
        if (!Utils.checkPermission(sender, "editsign")) {
            return;
        }

        Sign sign = this.getSign(sender);
        if (sign == null) {
            return;
        }

        if (line > 3 || line < -1) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid sign line: §l" + line + "§c!");
            return;
        }

        if (line == -1) {
            for (int i = 0; i < 3; i++) {
                sign.setLine(i, "");
            }
            sign.update();
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSign cleared.");
        } else {
            sign.setLine(line, "");
            sign.update();
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eLine §d" + line + " §ecleared.");
        }
    }

    @Nullable
    private Sign getSign(Player player) {
        Block targetBlock = player.getTargetBlock(null, 7);
        if (!(targetBlock instanceof Sign)) {
            player.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cSign not found!");
            return null;
        }

        return (Sign) targetBlock;
    }
}
