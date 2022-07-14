package id.luckynetwork.dev.lyrams.lej.commands.trolls;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.Flag;
import cloud.commandframework.annotations.ProxiedBy;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.enums.ToggleType;
import id.luckynetwork.dev.lyrams.lej.enums.TrollType;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TrollCommands extends CommandClass {

    @ProxiedBy("fakebreak")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt fakebreak [target] [toggle]")
    @CommandDescription("Broken blocks will shortly reappear")
    public void fakeBreakCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        this.toggleTroll(sender, targetName, toggle, TrollType.FAKE_BREAK);
    }

    @ProxiedBy("fakeplace")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt fakeplace [target] [toggle]")
    @CommandDescription("Placed blocks will shortly disappear")
    public void fakePlaceCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        this.toggleTroll(sender, targetName, toggle, TrollType.FAKE_PLACE);
    }

    @ProxiedBy("lagback")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt lagback [target] [toggle]")
    @CommandDescription("Simulates 1000ms movement lag")
    public void lagBackCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        this.toggleTroll(sender, targetName, toggle, TrollType.LAGBACK);
    }

    @ProxiedBy("nointeract")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt nointeract [target] [toggle]")
    @CommandDescription("Disables interacting with other entities and blocks")
    public void noInteractCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        this.toggleTroll(sender, targetName, toggle, TrollType.NO_INTERACT);
    }

    @ProxiedBy("nopickup")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt nopickup [target] [toggle]")
    @CommandDescription("Disables picking items up")
    public void noPickupCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        this.toggleTroll(sender, targetName, toggle, TrollType.NO_PICKUP);
    }

    @ProxiedBy("onetap")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt onetap [target] [toggle]")
    @CommandDescription("Sets health to 0 whenever damaged")
    public void oneTapCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        this.toggleTroll(sender, targetName, toggle, TrollType.ONE_TAP);
    }

    @ProxiedBy("sticky")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt sticky [target] [toggle]")
    @CommandDescription("Teleports to the highest block in their current location")
    public void stickyCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        this.toggleTroll(sender, targetName, toggle, TrollType.STICKY);
    }

    @ProxiedBy("nodamage")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt nodamage [target] [toggle]")
    @CommandDescription("Disables damaging other entities")
    public void noDamageCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        this.toggleTroll(sender, targetName, toggle, TrollType.NO_DAMAGE);
    }

    @ProxiedBy("nohit")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt nohit [target] [toggle]")
    @CommandDescription("Disables hitting other entities")
    public void noHitCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        this.toggleTroll(sender, targetName, toggle, TrollType.NO_HIT);
    }

    @ProxiedBy("noplace")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt noplace [target] [toggle]")
    @CommandDescription("Disables placing blocks")
    public void noPlaceCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        this.toggleTroll(sender, targetName, toggle, TrollType.NO_PLACE);
    }

    @ProxiedBy("nobreak")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt nobreak [target] [toggle]")
    @CommandDescription("Disables breaking blocks")
    public void noBreakCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "toggle", description = "on/off/toggle", defaultValue = "toggle", suggestions = "toggles") String toggle
    ) {
        this.toggleTroll(sender, targetName, toggle, TrollType.NO_BREAK);
    }

    @ProxiedBy("explode")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt explode [target]")
    @CommandDescription("Spawns an explosion on where you're looking at or at other player")
    public void explodeCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @Nullable @Flag(value = "power", description = "The explosion power") Float power,
            final @Nullable @Flag(value = "damage", description = "Should the explosion damage blocks") Boolean damage,
            final @Nullable @Flag(value = "silent", aliases = "s", description = "Should the target not be notified?") Boolean silent
    ) {
        if (!Utils.checkPermission(sender, "trolls.explode")) {
            return;
        }

        Set<Location> locations = new HashSet<>();
        TargetsCallback targets = new TargetsCallback();
        if (targetName.equals("self") && sender instanceof Player) {
            locations.add(((Player) sender).getTargetBlock(null, 120).getLocation());
        } else {
            targets = this.getTargets(sender, targetName);
        }

        if (!targets.isEmpty()) {
            locations.addAll(targets.stream().map(Player::getLocation).collect(Collectors.toList()));
        }

        float finalPower = power == null ? 4.0F : power;
        boolean finalDamage = damage == null || damage;
        TargetsCallback finalTargets = targets;
        plugin.getConfirmationManager().requestConfirmation(() -> {
            locations.forEach(location -> location.getWorld().createExplosion(location, finalPower, finalDamage));
            if (silent == null || !silent) {
                finalTargets.forEach(target -> target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eYou have been exploded!"));
            }

            boolean others = !finalTargets.isEmpty() && finalTargets.size() > 1;
            if (others) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eExploded §d" + finalTargets.size() + " §eplayers.");
            } else if ((!(sender instanceof Player)) || (finalTargets.doesNotContain((Player) sender) && !targetName.equals("self"))) {
                finalTargets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eExploded §d" + target.getName() + "§e."));
            }
        }, this.canSkip("troll-Explode", targets, sender));
    }

    @ProxiedBy("launch")
    @CommandMethod("luckytrolls|luckytroll|trolls|troll|lt launch [target]")
    @CommandDescription("Launches the target player")
    public void launchCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @Nullable @Flag(value = "power", description = "The power of the launch") Double power,
            final @Nullable @Flag(value = "damage", description = "Should the target take damage?") Boolean damage
    ) {
        if (!Utils.checkPermission(sender, "trolls.launch")) {
            return;
        }

        TargetsCallback targets;
        if (!ToggleType.getToggle(targetName).equals(ToggleType.UNKNOWN) && sender instanceof Player) {
            targets = this.getTargets(sender, "self");
        } else {
            targets = this.getTargets(sender, targetName);
        }

        if (targets.notifyIfEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        plugin.getConfirmationManager().requestConfirmation(() -> {
            double finalPower = power == null ? 10.0 : power;
            boolean finalDamage = damage != null && damage;
            targets.forEach(target -> {
                Location location = target.getLocation();
                if (finalDamage) {
                    target.getWorld().createExplosion(location, 4, true);
                    target.getWorld().strikeLightning(location);
                    target.getWorld().strikeLightning(location);
                    target.getWorld().strikeLightning(location);
                } else {
                    target.getWorld().createExplosion(location, 4, false);
                    target.getWorld().strikeLightningEffect(location);
                    target.getWorld().strikeLightningEffect(location);
                    target.getWorld().strikeLightningEffect(location);
                }
                target.setVelocity(target.getEyeLocation().getDirection().setY(finalPower));
            });

            if (targets.size() > 1) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eLaunched §d" + targets.size() + " §eplayers!");
            } else if (targets.size() == 1 || (!(sender instanceof Player)) || targets.doesNotContain((Player) sender)) {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eLaunched §d" + target.getName() + "§!"));
            }
        }, this.canSkip("troll-Launch", targets, sender));
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
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        if (toggleType.equals(ToggleType.UNKNOWN)) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown toggle type §l" + toggle + "§c!");
            return;
        }

        plugin.getConfirmationManager().requestConfirmation(() -> {
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
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eToggled §6" + trollType.getDisplay() + " §efor §d" + targets.size() + " §eplayers.");
            } else if (targets.size() == 1 || (!(sender instanceof Player) || targets.doesNotContain((Player) sender))) {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eToggled §6" + trollType.getDisplay() + " §efor §d" + target.getName() + "§e: " + Utils.colorizeTrueFalse(target.hasMetadata(trollType.getMetadataKey()), TrueFalseType.ON_OFF)));
            }
        }, this.canSkip("troll-" + trollType.getDisplay(), targets, sender));
    }

}
