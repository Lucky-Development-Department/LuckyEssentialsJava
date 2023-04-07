package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.enums.InventoryScope;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FixCommand extends CommandClass {

    public FixCommand() {
        super("fix", Collections.singletonList("repair"));
        this.registerCommandInfo("fix", "Repairs your items");
    }

    public void fixCommand(CommandSender sender, String targetName, String type, Boolean silent) {
        TargetsCallback targets;
        InventoryScope inventoryScope;
        if (!InventoryScope.getType(targetName).equals(InventoryScope.UNKNOWN) && sender instanceof Player) {
            // the sender wants to fix their own items
            targets = this.getTargets(sender, "self");
            inventoryScope = InventoryScope.getType(targetName);
        } else {
            targets = this.getTargets(sender, targetName);
            inventoryScope = InventoryScope.getType(type);
        }

        if (targets.notifyIfEmpty()) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cNo targets found!");
            return;
        }

        if (inventoryScope.equals(InventoryScope.UNKNOWN)) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cUnknown inventory scope §l" + type + "§c!");
            return;
        }

        boolean others = targets.size() > 1 || (sender instanceof Player && targets.doesNotContain((Player) sender));
        if (others && !Utils.checkPermission(sender, true, "fix")) {
            return;
        }

        plugin.getConfirmationManager().requestConfirmation(() -> {
            targets.forEach(target -> {
                switch (inventoryScope) {
                    case ALL: {
                        for (ItemStack content : target.getInventory().getContents()) {
                            if (content == null || content.getType().isBlock() || content.getDurability() == 0 || content.getType().getMaxDurability() < 1) {
                                continue;
                            }

                            content.setDurability((short) 0);
                        }
                        fixArmorContents(target);

                        if (silent == null || !silent) {
                            target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eYour items have been repaired.");
                        }
                        break;
                    }
                    case HAND: {
                        ItemStack itemInHand = plugin.getVersionSupport().getItemInHand(target);
                        if (itemInHand == null || itemInHand.getType().isBlock() || itemInHand.getDurability() == 0 || itemInHand.getType().getMaxDurability() < 1) {
                            return;
                        }

                        itemInHand.setDurability((short) 0);
                        target.updateInventory();

                        if (silent == null || !silent) {
                            target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eYour item in hand has been repaired.");
                        }
                        break;
                    }
                    case ARMOR: {
                        fixArmorContents(target);

                        if (silent == null || !silent) {
                            target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eYour armor has been repaired.");
                        }
                        break;
                    }
                    case SPECIFIC: {
                        for (ItemStack content : target.getInventory().getContents()) {
                            this.validateInventoryContent(inventoryScope, content);
                        }
                        for (ItemStack content : target.getInventory().getArmorContents()) {
                            this.validateInventoryContent(inventoryScope, content);
                        }

                        target.updateInventory();

                        if (silent == null || !silent) {
                            target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eRepaired all §d" + inventoryScope.getItemStack().getType() + " §ein your inventory.");
                        }
                        break;
                    }
                }
            });

            if (others) {
                if (targets.size() == 1) {
                    targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eRepaired §6" + inventoryScope.getDisplay() + " §efor §d" + target.getName() + "§e."));
                } else {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eRepaired §6" + inventoryScope.getDisplay() + " §efor §d" + targets.size() + " §eplayers.");
                }
            } else if ((!(sender instanceof Player)) || (targets.doesNotContain((Player) sender) && !targetName.equals("self"))) {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eRepaired §6" + inventoryScope.getDisplay() + " §efor §d" + target.getName() + "§e."));
            }
        }, this.canSkip("repair", targets, sender));
    }

    private void validateInventoryContent(InventoryScope inventoryScope, ItemStack content) {
        if (content == null || content.getType().isBlock() || content.getDurability() == 0 || content.getType().getMaxDurability() < 1) {
            return;
        }

        if (content.getType() != inventoryScope.getItemStack().getType()) {
            return;
        }

        content.setDurability((short) 0);
    }

    private void fixArmorContents(Player target) {
        for (ItemStack content : target.getInventory().getArmorContents()) {
            if (content == null || content.getType().isBlock() || content.getDurability() == 0 || content.getType().getMaxDurability() < 1) {
                continue;
            }

            content.setDurability((short) 0);
        }
        target.updateInventory();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "fix")) {
            return;
        }

        if (args.length == 0 && !(sender instanceof Player)) {
            sender.sendMessage("§cPlease specify a player!");
            return;
        }

        String targetName = "self";
        String type = "hand";
        boolean silent = false;

        if (args.length == 0) {
            this.fixCommand(sender, targetName, type, silent);
            return;
        }

        // /fix [target/scope] [scope] -s [silent]
        if (args.length >= 1) {
            targetName = args[0];
        }

        if (args.length >= 2) {
            type = args[1];
        }

        if (args[args.length - 1].equalsIgnoreCase("-s")) {
            silent = true;
        }

        this.fixCommand(sender, targetName, type, silent);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eFix command:");
        sender.sendMessage("§8└─ §e/fix [<scope>] §8- §7Fix your item in hand");
        sender.sendMessage("§8└─ §e/fix <target> [<scope>] §8- §7Fix the target's inventory");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "fix")) {
            return null;
        }

        if (args.length == 1) {
            List<String> suggestions = Stream.of("all", "hand", "armor")
                    .filter(it -> it.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
            suggestions.addAll(this.players(args[0]));

            return suggestions;
        } else if (args.length == 2) {
            return Stream.of("all", "hand", "armor")
                    .filter(it -> it.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return null;
    }
}
