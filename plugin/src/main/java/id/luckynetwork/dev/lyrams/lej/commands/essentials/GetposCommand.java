package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.config.Config;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class GetposCommand extends CommandClass {

    @CommandMethod("getpos [target]")
    @CommandDescription("Gets the location of target")
    public void getposCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName
    ) {
        if (!Utils.checkPermission(sender, "getpos")) {
            return;
        }

        TargetsCallback targets = this.getTargets(sender, targetName);
        if (targets.notifyIfEmpty()) {
            sender.sendMessage(Config.PREFIX + "§cNo targets found!");
            return;
        }
        if (targets.size() > 1) {
            sender.sendMessage(Config.PREFIX + "§cYou can only check one player at a time!");
            return;
        }

        targets.stream().findFirst().ifPresent(target -> {
            Location location = target.getLocation();
            sender.sendMessage("§eLocation of §d" + target.getName() + "§e:");
            sender.sendMessage("§8├─ §eWorld: §a" + location.getWorld().getName());
            sender.sendMessage("§8├─ §eX: §a" + location.getX());
            sender.sendMessage("§8├─ §eY: §a" + location.getY());
            sender.sendMessage("§8├─ §eZ: §a" + location.getZ());
            sender.sendMessage("§8├─ §eYaw: §a" + location.getYaw());
            if (sender instanceof Player) {
                if (((Player) sender).getWorld() == target.getWorld()) {
                    sender.sendMessage("§8├─ §ePitch: §a" + location.getPitch());
                    sender.sendMessage("§8└─ §eDistance: §a" + location.distanceSquared(((Player) sender).getLocation()));
                } else {
                    sender.sendMessage("§8└─ §ePitch: §a" + location.getPitch());
                }

                sender.sendMessage("");
                ComponentBuilder textBuilder = new ComponentBuilder("        §7(( Click to teleport ))")
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to teleport to " + target.getName()).create()))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + target.getName()));
                BaseComponent[] text = textBuilder.create();
                ((Player) sender).spigot().sendMessage(text);
                sender.sendMessage("");
            } else {
                sender.sendMessage("§8└─ §ePitch: §a" + location.getPitch());
            }
        });
    }
}
