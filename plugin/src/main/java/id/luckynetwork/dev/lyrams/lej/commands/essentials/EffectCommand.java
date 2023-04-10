package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import id.luckynetwork.dev.lyrams.lej.callbacks.IsIntegerCallback;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EffectCommand extends CommandClass {

    public EffectCommand() {
        super("effect");
        this.registerCommandInfo("effect", "Gives a player a potion effect");
    }

    public void effectCommand(CommandSender sender, String targetName, String effectName, Integer amplifier, Integer duration, Boolean silent) {
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
            targets.forEach(target -> potionEffectList.forEach(it -> {
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
                    if (targets.size() == 1) {
                        targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eApplied §6" + potionEffect.getType().getName() + ":" + finalAmplifier + " §efor §b" + durationSeconds + " seconds §eto §d" + target.getName() + "§e."));
                    } else {
                        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eApplied §6" + potionEffect.getType().getName() + ":" + finalAmplifier + " §efor §b" + durationSeconds + " seconds §eto §d" + targets.size() + " §eplayers.");
                    }
                } else if ((!(sender instanceof Player)) || (targets.doesNotContain((Player) sender) && !targetName.equals("self"))) {
                    targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eApplied §6" + potionEffect.getType().getName() + ":" + finalAmplifier + " §efor §b" + durationSeconds + " seconds §eto §d" + target.getName() + "§e."));
                }
            } else {
                if (others) {
                    if (targets.size() == 1) {
                        targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eApplied all potion effects to §d" + target.getName() + "§e."));
                    } else {
                        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eApplied all potion effects to §d" + targets.size() + " §eplayers.");
                    }
                } else if ((!(sender instanceof Player)) || (targets.doesNotContain((Player) sender) && !targetName.equals("self"))) {
                    targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eApplied all potion effects to §d" + target.getName() + "§e."));
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

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "effect")) {
            return;
        }

        if (args.length == 0) {
            sendDefaultMessage(sender);
            return;
        }

        String targetName = args[0];
        String effectName = "health";
        int amplifier = -1;
        int duration = -1;
        boolean silent = false;

        if (args.length >= 2) {
            effectName = args[1];
        }

        if (args.length >= 3) {
            IsIntegerCallback isIntegerCallback = Utils.isInteger(args[2]);
            if (isIntegerCallback.isInteger()) {
                amplifier = isIntegerCallback.getValue();
            } else {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid amplifier: §l" + args[2] + "§c!");
                return;
            }
        }

        if (args.length >= 4) {
            IsIntegerCallback isIntegerCallback = Utils.isInteger(args[3]);
            if (isIntegerCallback.isInteger()) {
                duration = isIntegerCallback.getValue();
            } else {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid duration: §l" + args[3] + "§c!");
                return;
            }
        }

        if (args.length >= 5 && args[4].equalsIgnoreCase("-s")) {
            silent = true;
        }

        this.effectCommand(sender, targetName, effectName, amplifier, duration, silent);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eEffect command:");
        sender.sendMessage("§8└─ §e/effect <player> <effect> [amplifier] [duration] [-s] §8- §7Gives a player a potion effect");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "effect", true)) {
            return null;
        }

        if (args.length == 1) {
            return this.players(args[0]);
        } else if (args.length == 2) {
            List<String> suggestions = new ArrayList<>();
            for (PotionEffectType effectType : PotionEffectType.values()) {
                if (effectType == null) {
                    continue;
                }

                String name = effectType.getName();
                if (name == null) {
                    continue;
                }

                if (name.toLowerCase().startsWith(args[1].toLowerCase())) {
                    suggestions.add(name);
                }
            }

            return suggestions;
        } else if (args.length == 3) {
            return Stream.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
                    .filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 4) {
            return Stream.of("5", "20", "60", "120")
                    .filter(s -> s.toLowerCase().startsWith(args[3].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 5) {
            return Stream.of("-s")
                    .filter(s -> s.toLowerCase().startsWith(args[4].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return null;
    }

}
