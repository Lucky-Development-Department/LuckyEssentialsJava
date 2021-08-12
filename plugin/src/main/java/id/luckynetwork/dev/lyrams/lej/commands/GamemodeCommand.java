package id.luckynetwork.dev.lyrams.lej.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.ProxiedBy;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import id.luckynetwork.dev.lyrams.lej.commands.main.CommandClass;
import id.luckynetwork.dev.lyrams.lej.config.Config;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GamemodeCommand extends CommandClass {

    @ProxiedBy("gm")
    @CommandMethod("gamemode <mode> [target]")
    @CommandDescription("Changes your gamemode or other player's gamemode")
    public void gamemodeCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "mode", description = "The gamemode", suggestions = "gamemodes") String mode,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName
    ) {
        if (!Utils.checkPermission(sender, "gamemode")) {
            return;
        }

        GameMode gameMode = this.getModeFromString(mode);
        if (gameMode == null) {
            sender.sendMessage(Config.PREFIX + "§cUnknown gamemode: §l" + mode + "§c!");
            return;
        }

        if (!Utils.checkPermission(sender, "gamemode." + gameMode)) {
            return;
        }

        Set<Player> targets = this.getTargets(sender, targetName);
        if (targets.isEmpty()) {
            sender.sendMessage(Config.PREFIX + "§cNo targets found!");
            return;
        }

        boolean others = targets.size() > 1;
        if (others && !Utils.checkPermission(sender, true, "gamemode." + gameMode)) {
            return;
        }

        targets.forEach(target -> {
            target.setGameMode(gameMode);
            target.sendMessage(Config.PREFIX + "§eYour gamemode has been set to §d" + gameMode + "§e!");
        });

        if (others) {
            sender.sendMessage(Config.PREFIX + "§eSet gamemode to §6" + gameMode + " §efor §d" + targets.size() + " §eplayers!");
        } else if (!(sender instanceof Player) || !targets.contains((Player) sender)) {
            targets.stream().findFirst().ifPresent(target -> sender.sendMessage(Config.PREFIX + "§eSet gamemode to §6" + gameMode + " §efor §d" + target.getName() + " §e!"));
        }
    }

    @CommandMethod("gms [target]")
    @CommandDescription("Changes your gamemode or other player's gamemode to survival")
    public void gmSurvivalCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName
    ) {
        if (!Utils.checkPermission(sender, "gamemode.survival")) {
            return;
        }

        Set<Player> targets = this.getTargets(sender, targetName);
        if (targets.isEmpty()) {
            sender.sendMessage(Config.PREFIX + "§cNo targets found!");
            return;
        }

        boolean others = targets.size() > 1;
        if (others && !Utils.checkPermission(sender, true, "gamemode.survival")) {
            return;
        }

        targets.forEach(target -> {
            target.setGameMode(GameMode.SURVIVAL);
            target.sendMessage(Config.PREFIX + "§eYour gamemode has been set to §dsurvival§e!");
        });

        if (others) {
            sender.sendMessage(Config.PREFIX + "§eSet gamemode to §6survival §efor §d" + targets.size() + " §eplayers!");
        } else if (!(sender instanceof Player) || !targets.contains((Player) sender)) {
            targets.stream().findFirst().ifPresent(target -> sender.sendMessage(Config.PREFIX + "§eSet gamemode to §6survival §efor §d" + target.getName() + " §e!"));
        }
    }

    @CommandMethod("gmc [target]")
    @CommandDescription("Changes your gamemode or other player's gamemode to creative")
    public void gmCreativeCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName
    ) {
        if (!Utils.checkPermission(sender, "gamemode.creative")) {
            return;
        }

        Set<Player> targets = this.getTargets(sender, targetName);
        if (targets.isEmpty()) {
            sender.sendMessage(Config.PREFIX + "§cNo targets found!");
            return;
        }

        boolean others = targets.size() > 1;
        if (others && !Utils.checkPermission(sender, true, "gamemode.creative")) {
            return;
        }

        targets.forEach(target -> {
            target.setGameMode(GameMode.CREATIVE);
            target.sendMessage(Config.PREFIX + "§eYour gamemode has been set to §dcreative§e!");
        });

        if (others) {
            sender.sendMessage(Config.PREFIX + "§eSet gamemode to §6creative §efor §d" + targets.size() + " §eplayers!");
        } else if (!(sender instanceof Player) || !targets.contains((Player) sender)) {
            targets.stream().findFirst().ifPresent(target -> sender.sendMessage(Config.PREFIX + "§eSet gamemode to §6creative §efor §d" + target.getName() + " §e!"));
        }
    }

    @CommandMethod("gma [target]")
    @CommandDescription("Changes your gamemode or other player's gamemode to adventure")
    public void gmAdventureCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName
    ) {
        if (!Utils.checkPermission(sender, "gamemode.adventure")) {
            return;
        }

        Set<Player> targets = this.getTargets(sender, targetName);
        if (targets.isEmpty()) {
            sender.sendMessage(Config.PREFIX + "§cNo targets found!");
            return;
        }

        boolean others = targets.size() > 1;
        if (others && !Utils.checkPermission(sender, true, "gamemode.adventure")) {
            return;
        }

        targets.forEach(target -> {
            target.setGameMode(GameMode.ADVENTURE);
            target.sendMessage(Config.PREFIX + "§eYour gamemode has been set to §dadventure§e!");
        });

        if (others) {
            sender.sendMessage(Config.PREFIX + "§eSet gamemode to §6adventure §efor §d" + targets.size() + " §eplayers!");
        } else if (!(sender instanceof Player) || !targets.contains((Player) sender)) {
            targets.stream().findFirst().ifPresent(target -> sender.sendMessage(Config.PREFIX + "§eSet gamemode to §6adventure §efor §d" + target.getName() + " §e!"));
        }
    }

    @CommandMethod("gmsp [target]")
    @CommandDescription("Changes your gamemode or other player's gamemode to spectator")
    public void gmSpectatorCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName
    ) {
        if (!Utils.checkPermission(sender, "gamemode.spectator")) {
            return;
        }

        Set<Player> targets = this.getTargets(sender, targetName);
        if (targets.isEmpty()) {
            sender.sendMessage(Config.PREFIX + "§cNo targets found!");
            return;
        }

        boolean others = targets.size() > 1;
        if (others && !Utils.checkPermission(sender, true, "gamemode.spectator")) {
            return;
        }

        targets.forEach(target -> {
            target.setGameMode(GameMode.SPECTATOR);
            target.sendMessage(Config.PREFIX + "§eYour gamemode has been set to §dspectator§e!");
        });

        if (others) {
            sender.sendMessage(Config.PREFIX + "§eSet gamemode to §6spectator §efor §d" + targets.size() + " §eplayers!");
        } else if (!(sender instanceof Player) || !targets.contains((Player) sender)) {
            targets.stream().findFirst().ifPresent(target -> sender.sendMessage(Config.PREFIX + "§eSet gamemode to §6spectator §efor §d" + target.getName() + " §e!"));
        }
    }

    @Suggestions("gamemodes")
    public List<String> gamemodes(CommandContext<CommandSender> context, String current) {
        return Stream.of(
                        "survival", "sv", "s",
                        "creative", "ctv", "c",
                        "adventure", "adv", "a",
                        "spectator", "spec", "sp"
                )
                .filter(it -> it.toLowerCase().startsWith(current.toLowerCase()))
                .collect(Collectors.toList());
    }

    private GameMode getModeFromString(String input) {
        switch (input.toLowerCase()) {
            case "survival":
            case "sv":
            case "s": {
                return GameMode.SURVIVAL;
            }

            case "creative":
            case "ctv":
            case "c": {
                return GameMode.CREATIVE;
            }

            case "adventure":
            case "adv":
            case "a": {
                return GameMode.ADVENTURE;
            }

            case "spectator":
            case "spec":
            case "sp": {
                return GameMode.SPECTATOR;
            }
        }

        return null;
    }
}
