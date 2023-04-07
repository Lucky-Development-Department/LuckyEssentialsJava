package id.luckynetwork.dev.lyrams.lej.commands.essentials.gamemode;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GamemodeCommand extends CommandClass {

    public GamemodeCommand() {
        super("gamemode", Collections.singletonList("gm"));
    }

    public void gamemodeCommand(CommandSender sender, String mode, String targetName) {
        GameMode gameMode = this.getModeFromString(mode);
        if (gameMode == null) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown gamemode: §l" + mode + "§c!");
            return;
        }

        if (!Utils.checkPermission(sender, "gamemode." + gameMode)) {
            return;
        }

        TargetsCallback targets = this.getTargets(sender, targetName);
        if (targets.notifyIfEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        boolean others = targets.size() > 1 || (sender instanceof Player && targets.doesNotContain((Player) sender));
        if (others && !Utils.checkPermission(sender, true, "gamemode." + gameMode)) {
            return;
        }

        plugin.getConfirmationManager().requestConfirmation(() -> {
            targets.forEach(target -> {
                target.setGameMode(gameMode);
                target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eYour gamemode has been set to §d" + gameMode + "§e.");
            });

            if (others) {
                if (targets.size() == 1) {
                    targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet gamemode to §6" + gameMode + " §efor §d" + target.getName() + " §e!"));
                } else {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet gamemode to §6" + gameMode + " §efor §d" + targets.size() + " §eplayers.");
                }
            } else if (!(sender instanceof Player) || targets.doesNotContain((Player) sender)) {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet gamemode to §6" + gameMode + " §efor §d" + target.getName() + " §e!"));
            }
        }, this.canSkip("gamemode change", targets, sender));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "gamemode")) {
            return;
        }

        if (args.length < 1) {
            this.sendDefaultMessage(sender);
            return;
        }

        String gamemode = "survival";
        String targetName = "self";

        if (args.length == 1) {
            gamemode = args[0];
        } else if (args.length == 2) {
            gamemode = args[0];
            targetName = args[1];
        }

        this.gamemodeCommand(sender, gamemode, targetName);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eGamemode command:");
        sender.sendMessage("§8└─ §e/gm <gamemode> <player> §8- §7Set your gamemode to the specified one");
        sender.sendMessage("§8└─ §e/gms <player> §8- §7Set your gamemode to survival");
        sender.sendMessage("§8└─ §e/gmc <player> §8- §7Set your gamemode to creative");
        sender.sendMessage("§8└─ §e/gma <player> §8- §7Set your gamemode to adventure");
        sender.sendMessage("§8└─ §e/gmsp <player> §8- §7Set your gamemode to spectator");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "gamemode")) {
            return null;
        }

        if (args.length == 1) {
            return this.gamemodes(args[0]);
        } else if (args.length == 2) {
            return this.players(args[1]);
        }

        return null;
    }


    private List<String> gamemodes(String current) {
        return Stream.of("survival", "sv", "s", "creative", "ctv", "c", "adventure", "adv", "a", "spectator", "spec", "sp").filter(it -> it.toLowerCase().startsWith(current.toLowerCase())).collect(Collectors.toList());
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
