package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.Flag;
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
    @CommandDescription("Give a potion effect for you or other player")
    public void effectCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "effect", description = "The potion effect name", defaultValue = "health") String effectName,
            final @NonNull @Argument(value = "amplifier", description = "The effect amplifier", defaultValue = "-1") Integer amplifier,
            final @NonNull @Argument(value = "duration", description = "The effect duration", defaultValue = "-1") Integer duration,
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

        List<PotionEffect> potionEffectList = this.parseEffects(sender, effectName);
        if (potionEffectList.isEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo valid effects found!");
            return;
        }

        targets.forEach(target ->
                potionEffectList.forEach(it -> {
                    int finalDuration = duration == -1 ? it.getDuration() : duration;
                    int finalAmplifier = amplifier == -1 ? it.getAmplifier() : amplifier;

                    PotionEffect potionEffect = new PotionEffect(it.getType(), finalDuration, finalAmplifier);
                    target.addPotionEffect(potionEffect);
                    if (silent == null || !silent) {
                        target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eApplied §d" + potionEffect.getType().getName() + ":" + potionEffect.getAmplifier() + " §efor §b" + potionEffect.getDuration() + " seconds§e!");
                    }
                }));

        if (potionEffectList.size() == 1) {
            PotionEffect potionEffect = potionEffectList.get(0);
            if (others) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eApplied §d" + potionEffect.getType().getName() + ":" + potionEffect.getAmplifier() + " §efor §b" + potionEffect.getDuration() + " seconds §eto §d" + targets.size() + " players!");
            } else if ((!(sender instanceof Player)) || (targets.doesNotContain((Player) sender) && !targetName.equals("self"))) {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eApplied §d" + potionEffect.getType().getName() + ":" + potionEffect.getAmplifier() + " §efor §b" + potionEffect.getDuration() + " seconds §eto §d" + target.getName() + "§e!"));
            }
        } else {
            if (others) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eApplied all potion effects to §d" + targets.size() + " players!");
            } else if ((!(sender instanceof Player)) || (targets.doesNotContain((Player) sender) && !targetName.equals("self"))) {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eApplied all potion effects to §d" + target.getName() + "!"));
            }
        }
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

                    PotionEffect potionEffect = new PotionEffect(effectType, 30, 0);
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

                    try {
                        int amplifier = Integer.parseInt(strings[1]);
                        PotionEffect potionEffect = new PotionEffect(effectType, 30, amplifier);
                        potionEffectList.add(potionEffect);
                    } catch (Exception ignored) {
                        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid enchantment level: §l " + strings[0] + "§c!");
                    }
                } else if (strings.length == 3) {
                    PotionEffectType effectType = plugin.getVersionSupport().getPotionEffectByName(strings[0]);
                    if (effectType == null) {
                        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown potion effect type §l" + strings[0] + "§c!");
                        continue;
                    }

                    try {
                        int amplifier = Integer.parseInt(strings[1]);
                        try {
                            int duration = Integer.parseInt(strings[2]);
                            PotionEffect potionEffect = new PotionEffect(effectType, duration, amplifier);
                            potionEffectList.add(potionEffect);
                        } catch (Exception ignored) {
                            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid enchantment level: §l " + strings[0] + "§c!");
                        }
                    } catch (Exception ignored) {
                        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid enchantment level: §l " + strings[0] + "§c!");
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

                PotionEffect potionEffect = new PotionEffect(effectType, 30, 0);
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

                try {
                    int amplifier = Integer.parseInt(strings[1]);
                    PotionEffect potionEffect = new PotionEffect(effectType, 30, amplifier);
                    potionEffectList.add(potionEffect);
                } catch (Exception ignored) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid enchantment level: §l " + strings[0] + "§c!");
                }
            } else if (strings.length == 3) {
                PotionEffectType effectType = plugin.getVersionSupport().getPotionEffectByName(strings[0]);
                if (effectType == null) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown potion effect type §l" + strings[0] + "§c!");
                    return potionEffectList;
                }

                try {
                    int amplifier = Integer.parseInt(strings[1]);
                    try {
                        int duration = Integer.parseInt(strings[2]);
                        PotionEffect potionEffect = new PotionEffect(effectType, duration, amplifier);
                        potionEffectList.add(potionEffect);
                    } catch (Exception ignored) {
                        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid enchantment level: §l " + strings[0] + "§c!");
                    }
                } catch (Exception ignored) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid enchantment level: §l " + strings[0] + "§c!");
                }
            }
        }

        return potionEffectList;
    }
}
