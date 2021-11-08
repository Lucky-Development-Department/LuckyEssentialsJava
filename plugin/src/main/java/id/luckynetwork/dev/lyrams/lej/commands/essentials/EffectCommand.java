package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.Flag;
import id.luckynetwork.dev.lyrams.lej.callbacks.IsIntegerCallback;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EffectCommand extends CommandClass {

    @CommandMethod("effect <target> <effect> [amplifier] [duration]")
    @CommandDescription("Applies a potion effect for you or other player")
    public void effectCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "effect", description = "The potion effect name or 'clear'", defaultValue = "health") String effectName,
            final @NonNull @Argument(value = "amplifier", description = "The effect amplifier", defaultValue = "-1") Integer amplifier,
            final @NonNull @Argument(value = "duration", description = "The effect duration in seconds", defaultValue = "-1") Integer duration,
            final @Nullable @Flag(value = "silent", aliases = "s", description = "Should the target not be notified?") Boolean silent
    ) {
        if (!Utils.checkPermission(sender, "effect")) {
            return;
        }

        TargetsCallback targets = this.getTargets(sender, targetName);
        if (targets.notifyIfEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        boolean others = targets.size() > 1 || (sender instanceof Player && targets.doesNotContain((Player) sender));
        if (others && !Utils.checkPermission(sender, true, "effect")) {
            return;
        }

        if (effectName.equalsIgnoreCase("clear")) {
            plugin.getConfirmationManager().requestConfirmation(() -> {
                targets.forEach(target -> {
                    target.getActivePotionEffects().forEach(it -> target.removePotionEffect(it.getType()));
                    if (silent == null || !silent) {
                        target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eRemoved all potion effects.");
                    }
                });

                if (others) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eRemoved all potion effects from §d" + targets.size() + " §eplayers.");
                } else if ((!(sender instanceof Player)) || (targets.doesNotContain((Player) sender) && !targetName.equals("self"))) {
                    targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eRemoved all potion effects from §d" + target.getName() + "§e."));
                }
            }, this.canSkip("clear all active potion effect", targets, sender));
            return;
        }

        List<PotionEffect> potionEffectList = this.parseEffects(sender, effectName);
        if (potionEffectList.isEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo valid effects found!");
            return;
        }

        plugin.getConfirmationManager().requestConfirmation(() -> {
            targets.forEach(target ->
                    potionEffectList.forEach(it -> {
                        int finalDuration = duration == -1 ? it.getDuration() : (duration * 20);
                        int finalAmplifier = amplifier == -1 ? it.getAmplifier() : amplifier;

                        // remove the effect
                        boolean isRemove = finalDuration == 0;
                        if (target.hasPotionEffect(it.getType()) || isRemove) {
                            target.removePotionEffect(it.getType());
                            if (isRemove) {
                                return;
                            }
                        }

                        PotionEffect potionEffect = new PotionEffect(it.getType(), finalDuration, finalAmplifier);
                        target.addPotionEffect(potionEffect);
                        if (silent == null || !silent) {
                            String durationSeconds = String.valueOf(duration == -1 ? (it.getDuration() / 20) : duration);
                            target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eApplied §6" + potionEffect.getType().getName() + ":" + finalAmplifier + " §efor §b" + durationSeconds + " seconds§e!");
                        }
                    }));

            if (potionEffectList.size() == 1) {
                PotionEffect potionEffect = potionEffectList.get(0);
                int finalAmplifier = amplifier == -1 ? potionEffect.getAmplifier() : amplifier;
                String durationSeconds = String.valueOf(duration == -1 ? (potionEffect.getDuration() / 20) : duration);
                if (others) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eApplied §6" + potionEffect.getType().getName() + ":" + finalAmplifier + " §efor §b" + durationSeconds + " seconds §eto §d" + targets.size() + " §eplayers.");
                } else if ((!(sender instanceof Player)) || (targets.doesNotContain((Player) sender) && !targetName.equals("self"))) {
                    targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eApplied §6" + potionEffect.getType().getName() + ":" + finalAmplifier + " §efor §b" + durationSeconds + " seconds §eto §d" + target.getName() + "§e."));
                }
            } else {
                if (others) {
                    if (targets.size() == 1) {
                        targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eApplied all potion effects to §d" + target.getName() + "!"));
                    } else {
                        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eApplied all potion effects to §d" + targets.size() + " §eplayers");
                    }
                } else if ((!(sender instanceof Player)) || (targets.doesNotContain((Player) sender) && !targetName.equals("self"))) {
                    targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eApplied all potion effects to §d" + target.getName() + "!"));
                }
            }
        }, this.canSkip("apply potion effect", targets, sender));
    }

    private List<PotionEffect> parseEffects(CommandSender sender, String effects) {
        List<PotionEffect> potionEffectList = new ArrayList<>();
        if (effects.contains(",")) {
            String[] split = effects.split(",");
            for (String effect : split) {
                if (!effect.contains(":")) {
                    PotionEffectType effectType = plugin.getVersionSupport().getPotionEffectByName(effect);
                    if (effectType == null) {
                        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown potion effect type §l" + effect + "§c!");
                        continue;
                    }

                    PotionEffect potionEffect = new PotionEffect(effectType, 600, 0);
                    potionEffectList.add(potionEffect);
                    continue;
                }

                String[] strings = effect.split(":");
                if (strings.length == 2) {
                    PotionEffectType effectType = plugin.getVersionSupport().getPotionEffectByName(strings[0]);
                    if (effectType == null) {
                        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown potion effect type §l" + strings[0] + "§c!");
                        continue;
                    }

                    IsIntegerCallback isIntegerCallback = Utils.isInteger(strings[1]);
                    if (isIntegerCallback.isInteger()) {
                        int amplifier = isIntegerCallback.getValue();
                        PotionEffect potionEffect = new PotionEffect(effectType, 600, amplifier);
                        potionEffectList.add(potionEffect);
                    } else {
                        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid potion effect level: §l" + strings[0] + "§c!");
                    }
                } else if (strings.length == 3) {
                    PotionEffectType effectType = plugin.getVersionSupport().getPotionEffectByName(strings[0]);
                    if (effectType == null) {
                        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown potion effect type §l" + strings[0] + "§c!");
                        continue;
                    }

                    IsIntegerCallback isIntegerCallback = Utils.isInteger(strings[1]);
                    if (isIntegerCallback.isInteger()) {
                        int amplifier = isIntegerCallback.getValue();
                        IsIntegerCallback isIntegerCallback1 = Utils.isInteger(strings[2]);
                        if (isIntegerCallback1.isInteger()) {
                            int duration = isIntegerCallback1.getValue();
                            PotionEffect potionEffect = new PotionEffect(effectType, duration, amplifier);
                            potionEffectList.add(potionEffect);
                        } else {
                            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid potion effect level: §l" + strings[0] + "§c!");
                        }
                    } else {
                        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid potion effect level: §l" + strings[0] + "§c!");
                    }
                }
            }
        } else {
            if (!effects.contains(":")) {
                PotionEffectType effectType = plugin.getVersionSupport().getPotionEffectByName(effects);
                if (effectType == null) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown potion effect type §l" + effects + "§c!");
                    return potionEffectList;
                }

                PotionEffect potionEffect = new PotionEffect(effectType, 600, 0);
                potionEffectList.add(potionEffect);
                return potionEffectList;
            }

            String[] strings = effects.split(":");
            if (strings.length == 2) {
                PotionEffectType effectType = plugin.getVersionSupport().getPotionEffectByName(strings[0]);
                if (effectType == null) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown potion effect type §l" + strings[0] + "§c!");
                    return potionEffectList;
                }

                IsIntegerCallback isIntegerCallback = Utils.isInteger(strings[1]);
                if (isIntegerCallback.isInteger()) {
                    int amplifier = isIntegerCallback.getValue();
                    PotionEffect potionEffect = new PotionEffect(effectType, 600, amplifier);
                    potionEffectList.add(potionEffect);
                } else {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid potion effect level: §l" + strings[0] + "§c!");
                }
            } else if (strings.length == 3) {
                PotionEffectType effectType = plugin.getVersionSupport().getPotionEffectByName(strings[0]);
                if (effectType == null) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown potion effect type §l" + strings[0] + "§c!");
                    return potionEffectList;
                }

                IsIntegerCallback isIntegerCallback = Utils.isInteger(strings[1]);
                if (isIntegerCallback.isInteger()) {
                    int amplifier = isIntegerCallback.getValue();
                    IsIntegerCallback isIntegerCallback1 = Utils.isInteger(strings[2]);
                    if (isIntegerCallback1.isInteger()) {
                        int duration = isIntegerCallback1.getValue();
                        PotionEffect potionEffect = new PotionEffect(effectType, duration, amplifier);
                        potionEffectList.add(potionEffect);
                    } else {
                        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid potion effect level: §l" + strings[0] + "§c!");
                    }
                } else {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid potion effect level: §l" + strings[0] + "§c!");
                }
            }
        }

        return potionEffectList;
    }
}
