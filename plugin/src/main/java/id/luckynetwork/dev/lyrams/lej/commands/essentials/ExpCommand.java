package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import id.luckynetwork.dev.lyrams.lej.callbacks.IsIntegerCallback;
import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExpCommand extends CommandClass {

    public ExpCommand() {
        super("exp", Collections.singletonList("xp"));
    }

    public void addCommand(CommandSender sender, String targetName, String amount, Boolean silent) {
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

    public void removeCommand(CommandSender sender, String targetName, String amount, Boolean silent) {
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
        plugin.getConfirmationManager().requestConfirmation(() -> {
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
        }, this.canSkip("change player exp", targets, sender));
    }

    public void setCommand(CommandSender sender, String targetName, String amount, Boolean silent) {
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

    public void checkCommand(CommandSender sender, String targetName) {
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

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "exp")) {
            return;
        }

        if (args.length == 0) {
            this.sendDefaultMessage(sender);
            return;
        }

        String arg = args[0];
        switch (arg.toLowerCase()) {
            case "add": {
                if (args.length < 3) {
                    this.sendDefaultMessage(sender);
                    return;
                }

                String targetName = args[1];
                String amount = args[2];
                boolean silent = args.length == 4 && args[3].equalsIgnoreCase("-s");

                this.addCommand(sender, targetName, amount, silent);
                break;
            }

            case "remove": {
                if (args.length < 3) {
                    this.sendDefaultMessage(sender);
                    return;
                }

                String targetName = args[1];
                String amount = args[2];
                boolean silent = args.length == 4 && args[3].equalsIgnoreCase("-s");

                this.removeCommand(sender, targetName, amount, silent);
                break;
            }

            case "set": {
                if (args.length < 3) {
                    this.sendDefaultMessage(sender);
                    return;
                }

                String targetName = args[1];
                String amount = args[2];
                boolean silent = args.length == 4 && args[3].equalsIgnoreCase("-s");

                this.setCommand(sender, targetName, amount, silent);
                break;
            }

            case "check": {
                if (args.length < 2) {
                    this.sendDefaultMessage(sender);
                    return;
                }

                String targetName = args[1];
                this.checkCommand(sender, targetName);
                break;
            }

            default: {
                this.sendDefaultMessage(sender);
            }
        }
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eExp command:");
        sender.sendMessage("§8└─ §e/exp add <player> <amount> [-s] §8- §7Adds experience to a player");
        sender.sendMessage("§8└─ §e/exp remove <player> <amount> [-s] §8- §7Removes experience from a player");
        sender.sendMessage("§8└─ §e/exp set <player> <amount> [-s] §8- §7Sets experience of a player");
        sender.sendMessage("§8└─ §e/exp check <player> §8- §7Checks experience of a player");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "exp")) {
            return null;
        }

        if (args.length == 0) {
            return Stream.of("add", "remove", "set", "check")
                    .filter(it -> it.toLowerCase().startsWith(args[0]))
                    .collect(Collectors.toList());
        } else if (args.length == 0) {
            return this.players(args[0]);
        } else if (args.length == 0 && !args[0].equalsIgnoreCase("check")) {
            return Stream.of("-s")
                    .filter(it -> it.toLowerCase().startsWith(args[0]))
                    .collect(Collectors.toList());
        }

        return null;
    }
}
