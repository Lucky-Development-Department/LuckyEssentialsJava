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
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ExpCommand extends CommandClass {

    @CommandMethod("exp|xp add <target> <amount>")
    @CommandDescription("Adds exp or level for target")
    public void addCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "amount", description = "The amount") String amount,
            final @Nullable @Flag(value = "silent", aliases = "s", description = "Should the target not be notified?") Boolean silent
    ) {
        if (!Utils.checkPermission(sender, "exp")) {
            return;
        }

        boolean isLevel = false;
        int finalAmount = 0;

        TargetsCallback targets = this.getTargets(sender, targetName);
        if (targets.notifyIfEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        boolean others = targets.size() > 1 || (sender instanceof Player && targets.doesNotContain((Player) sender));
        if (others && !Utils.checkPermission(sender, true, "exp")) {
            return;
        }

        IsIntegerCallback callback = Utils.isInteger(amount);
        if (amount.endsWith("L")) {
            String level = amount.split("L")[0];
            IsIntegerCallback isIntegerCallback = Utils.isInteger(level);
            if (isIntegerCallback.isInteger()) {
                isLevel = true;
                finalAmount = isIntegerCallback.getValue();
            }
        } else if (callback.isInteger()) {
            finalAmount = callback.getValue();
        } else {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown level: §l" + amount + "§c!");
            return;
        }

        boolean finalIsLevel = isLevel;
        int finalAmount1 = finalAmount;
        targets.forEach(target -> {
            if (finalIsLevel) {
                target.giveExpLevels(finalAmount1);
                if (silent == null || !silent) {
                    target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eAdded §d" + finalAmount1 + " §eto your player level.");
                }
            } else {
                target.giveExp(finalAmount1);
                if (silent == null || !silent) {
                    target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eAdded §d" + finalAmount1 + " §eto your player experience.");
                }
            }
        });

        if (others) {
            if (targets.size() == 1) {
                if (finalIsLevel) {
                    targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eAdded §6" + finalAmount1 + " §elevel to §d" + target.getName() + "§e."));
                } else {
                    targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eAdded §6" + finalAmount1 + " §eexperience to §d" + target.getName() + "§e."));
                }
            } else {
                if (finalIsLevel) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eAdded §6" + finalAmount1 + " §elevel for §d" + targets.size() + " §eplayers.");
                } else {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eAdded §6" + finalAmount1 + " §eexperience for §d" + targets.size() + " §eplayers.");
                }
            }
        } else if ((!(sender instanceof Player)) || (targets.doesNotContain((Player) sender) && !targetName.equals("self"))) {
            if (finalIsLevel) {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eAdded §6" + finalAmount1 + " §elevel to §d" + target.getName() + "§e."));
            } else {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eAdded §6" + finalAmount1 + " §eexperience to §d" + target.getName() + "§e."));
            }
        }
    }

    @CommandMethod("exp|xp remove <target> <amount>")
    @CommandDescription("Removes exp or level for target")
    public void removeCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "amount", description = "The amount") String amount,
            final @Nullable @Flag(value = "silent", aliases = "s", description = "Should the target not be notified?") Boolean silent
    ) {
        if (!Utils.checkPermission(sender, "exp")) {
            return;
        }

        boolean isLevel = false;
        int finalAmount = 0;

        TargetsCallback targets = this.getTargets(sender, targetName);
        if (targets.notifyIfEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        boolean others = targets.size() > 1 || (sender instanceof Player && targets.doesNotContain((Player) sender));
        if (others && !Utils.checkPermission(sender, true, "exp")) {
            return;
        }

        IsIntegerCallback callback = Utils.isInteger(amount);
        if (amount.endsWith("L")) {
            String level = amount.split("L")[0];
            IsIntegerCallback isIntegerCallback = Utils.isInteger(level);
            if (isIntegerCallback.isInteger()) {
                isLevel = true;
                finalAmount = isIntegerCallback.getValue();
            }
        } else if (callback.isInteger()) {
            finalAmount = callback.getValue();
        } else {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown level: §l" + amount + "§c!");
            return;
        }

        boolean finalIsLevel = isLevel;
        int finalAmount1 = finalAmount;
        targets.forEach(target -> {
            if (finalIsLevel) {
                target.giveExpLevels(-finalAmount1);
                if (silent == null || !silent) {
                    target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eRemoved §d" + finalAmount1 + " §efrom your player level.");
                }
            } else {
                target.giveExp(-finalAmount1);
                if (silent == null || !silent) {
                    target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eRemoved §d" + finalAmount1 + " §efrom your player experience.");
                }
            }
        });

        if (others) {
            if (targets.size() == 1) {
                if (finalIsLevel) {
                    targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eRemoved §6" + finalAmount1 + " §elevel from §d" + target.getName() + "§e."));
                } else {
                    targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eRemoved §6" + finalAmount1 + " §eexperience from §d" + target.getName() + "§e."));
                }
            } else {
                if (finalIsLevel) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eRemoved §6" + finalAmount1 + " §elevel from §d" + targets.size() + " §eplayers.");
                } else {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eRemoved §6" + finalAmount1 + " §eexperience from §d" + targets.size() + " §eplayers.");
                }
            }
        } else if ((!(sender instanceof Player)) || (targets.doesNotContain((Player) sender) && !targetName.equals("self"))) {
            if (finalIsLevel) {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eRemoved §6" + finalAmount1 + " §elevel from §d" + target.getName() + "§e."));
            } else {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eRemoved §6" + finalAmount1 + " §eexperience from §d" + target.getName() + "§e."));
            }
        }
    }

    @CommandMethod("exp|xp set <target> <amount>")
    @CommandDescription("Sets exp or level for target")
    public void setCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName,
            final @NonNull @Argument(value = "amount", description = "The amount") String amount,
            final @Nullable @Flag(value = "silent", aliases = "s", description = "Should the target not be notified?") Boolean silent
    ) {
        if (!Utils.checkPermission(sender, "exp")) {
            return;
        }

        boolean isLevel = false;
        int finalAmount = 0;

        TargetsCallback targets = this.getTargets(sender, targetName);
        if (targets.notifyIfEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        boolean others = targets.size() > 1 || (sender instanceof Player && targets.doesNotContain((Player) sender));
        if (others && !Utils.checkPermission(sender, true, "exp")) {
            return;
        }

        IsIntegerCallback callback = Utils.isInteger(amount);
        if (amount.endsWith("L")) {
            String level = amount.split("L")[0];
            IsIntegerCallback isIntegerCallback = Utils.isInteger(level);
            if (isIntegerCallback.isInteger()) {
                isLevel = true;
                finalAmount = isIntegerCallback.getValue();
            }
        } else if (callback.isInteger()) {
            finalAmount = callback.getValue();
        } else {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown level: §l" + amount + "§c!");
            return;
        }

        boolean finalIsLevel = isLevel;
        int finalAmount1 = finalAmount;
        targets.forEach(target -> {
            if (finalIsLevel) {
                target.setLevel(finalAmount1);
                if (silent == null || !silent) {
                    target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet §d" + finalAmount1 + " §eas your player level.");
                }
            } else {
                target.setExp(finalAmount1);
                if (silent == null || !silent) {
                    target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet §d" + finalAmount1 + " §eas your player experience.");
                }
            }
        });

        if (others) {
            if (targets.size() == 1) {
                if (finalIsLevel) {
                    targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet level to §6" + finalAmount1 + " §efor §d" + target.getName() + "§e."));
                } else {
                    targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet experience to §6" + finalAmount1 + " §efor §d" + target.getName() + "§e."));
                }
            } else {
                if (finalIsLevel) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet level to §6" + finalAmount1 + " §efor §d" + targets.size() + " §eplayers.");
                } else {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet experience to §6" + finalAmount1 + " §efor §d" + targets.size() + " §eplayers.");
                }
            }
        } else if ((!(sender instanceof Player)) || (targets.doesNotContain((Player) sender) && !targetName.equals("self"))) {
            if (finalIsLevel) {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet level to §6" + finalAmount1 + " §efor §d" + target.getName() + "§e."));
            } else {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eSet experience to §6" + finalAmount1 + " §efor §d" + target.getName() + "§e."));
            }
        }
    }

    @CommandMethod("exp|xp check <target>")
    @CommandDescription("Checks exp and level of target")
    public void checkCommand(
            final @NonNull CommandSender sender,
            final @NonNull @Argument(value = "target", description = "The target player", defaultValue = "self", suggestions = "players") String targetName
    ) {
        if (!Utils.checkPermission(sender, "exp")) {
            return;
        }

        TargetsCallback targets = this.getTargets(sender, targetName);
        if (targets.notifyIfEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }
        if (targets.size() > 1) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cYou can only check one player at a time!");
            return;
        }

        targets.stream().findFirst().ifPresent(target -> {
            sender.sendMessage("§ePlayer experience info of §d" + target.getName() + "§e:");
            sender.sendMessage("§8├─ §eExp: §a" + target.getExp());
            sender.sendMessage("§8├─ §eTotal Exp: §a" + target.getTotalExperience());
            sender.sendMessage("§8├─ §eExp To Level: §a" + target.getExpToLevel());
            sender.sendMessage("§8└─ §eLevel: §a" + target.getLevel());
        });

    }

}
