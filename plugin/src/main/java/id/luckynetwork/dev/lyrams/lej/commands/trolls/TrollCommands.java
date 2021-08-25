package id.luckynetwork.dev.lyrams.lej.commands.trolls;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.ProxiedBy;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.config.Config;
import id.luckynetwork.dev.lyrams.lej.enums.ToggleType;
import id.luckynetwork.dev.lyrams.lej.enums.TrollType;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class TrollCommands extends CommandClass {

    @ProxiedBy("fakebreak")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt fakebreak [target] [toggle]")
    @CommandDescription("Broken blocks will shortly reappear")
    public void onFakeBreakCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        this.toggleTroll(sender, targetName, toggle, TrollType.FAKE_BREAK);
    }

    @ProxiedBy("fakeplace")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt fakeplace [target] [toggle]")
    @CommandDescription("Placed blocks will shortly disappear")
    public void onFakePlaceCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        this.toggleTroll(sender, targetName, toggle, TrollType.FAKE_PLACE);
    }

    @ProxiedBy("lagback")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt lagback [target] [toggle]")
    @CommandDescription("Simulates 1000ms movement lag")
    public void onLagBackCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        this.toggleTroll(sender, targetName, toggle, TrollType.LAGBACK);
    }

    @ProxiedBy("nointeract")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt nointeract [target] [toggle]")
    @CommandDescription("Disables interacting with other entities and blocks")
    public void onNoInteractCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        this.toggleTroll(sender, targetName, toggle, TrollType.NO_INTERACT);
    }

    @ProxiedBy("nopickup")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt nopickup [target] [toggle]")
    @CommandDescription("Disables picking items up")
    public void onNoPickupCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        this.toggleTroll(sender, targetName, toggle, TrollType.NO_PICKUP);
    }

    @ProxiedBy("onetap")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt onetap [target] [toggle]")
    @CommandDescription("Sets health to 0 whenever damaged")
    public void onOneTapCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        this.toggleTroll(sender, targetName, toggle, TrollType.ONE_TAP);
    }

    @ProxiedBy("sticky")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt sticky [target] [toggle]")
    @CommandDescription("Teleports to the highest block in their current location")
    public void onStickyCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        this.toggleTroll(sender, targetName, toggle, TrollType.STICKY);
    }

    @ProxiedBy("nodamage")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt nodamage [target] [toggle]")
    @CommandDescription("Disables damaging other entities")
    public void onNoDamageCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        this.toggleTroll(sender, targetName, toggle, TrollType.NO_DAMAGE);
    }

    @ProxiedBy("nohit")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt nohit [target] [toggle]")
    @CommandDescription("Disables hitting other entities")
    public void onNoHitCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        this.toggleTroll(sender, targetName, toggle, TrollType.NO_HIT);
    }

    @ProxiedBy("noplace")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt noplace [target] [toggle]")
    @CommandDescription("Disables placing blocks")
    public void onNoPlaceCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        this.toggleTroll(sender, targetName, toggle, TrollType.NO_PLACE);
    }

    @ProxiedBy("nobreak")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt nobreak [target] [toggle]")
    @CommandDescription("Disables breaking blocks")
    public void onNoBreakCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        this.toggleTroll(sender, targetName, toggle, TrollType.NO_BREAK);
    }

    /**
     * toggles a troll for a player
     *
     * @param sender     the command sender
     * @param targetName the targetName
     * @param toggle     the toggle
     * @param trollType  the {@link TrollType}
     */
    private void toggleTroll(CommandSender sender, String targetName, String toggle, TrollType trollType) {
        if (!Utils.checkPermission(sender, "trolls." + trollType.getDisplay())) {
            return;
        }

        TargetsCallback targets;
        ToggleType toggleType;
        if (!ToggleType.getToggle(targetName).equals(ToggleType.UNKNOWN) && sender instanceof Player) {
            // the sender wants to change their own troll state
            targets = this.getTargets(sender, "self");
            toggleType = ToggleType.getToggle(targetName);
        } else {
            targets = this.getTargets(sender, targetName);
            toggleType = ToggleType.getToggle(toggle);
        }

        if (targets.notifyIfEmpty()) {
            sender.sendMessage(Config.PREFIX + "§cNo targets found!");
            return;
        }

        if (toggleType.equals(ToggleType.UNKNOWN)) {
            sender.sendMessage(Config.PREFIX + "§cUnknown toggle type §l" + toggle + "§c!");
            return;
        }

        targets.forEach(target -> {
            switch (toggleType) {
                case ON: {
                    Utils.applyMetadata(target, trollType.getMetadataKey(), true);
                    break;
                }
                case OFF: {
                    Utils.removeMetadata(target, trollType.getMetadataKey());
                    break;
                }
                case TOGGLE: {
                    if (target.hasMetadata(trollType.getMetadataKey())) {
                        Utils.removeMetadata(target, trollType.getMetadataKey());
                    } else {
                        Utils.applyMetadata(target, trollType.getMetadataKey(), true);
                    }
                    break;
                }
            }
        });

        if (targets.size() > 1) {
            sender.sendMessage(Config.PREFIX + "§eToggled §6" + trollType.getDisplay() + " §efor §d" + targets.size() + " §eplayers!");
        } else if (targets.size() == 1 || (!(sender instanceof Player) || targets.doesNotContain((Player) sender))) {
            targets.stream().findFirst().ifPresent(target -> sender.sendMessage(Config.PREFIX + "§eToggled §6" + trollType.getDisplay() + " §efor §d" + target.getName() + "§e: " + Utils.colorizeTrueFalse(target.hasMetadata(trollType.getMetadataKey()), TrueFalseType.ON_OFF)));
        }
    }

}
