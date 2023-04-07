package id.luckynetwork.dev.lyrams.lej.commands.essentials;

import id.luckynetwork.dev.lyrams.lej.commands.api.CommandClass;
import id.luckynetwork.dev.lyrams.lej.enums.InventoryScope;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClearCommand extends CommandClass {

    public ClearCommand() {
        super("clear", Arrays.asList("clearinventory", "ci"));
    }

    public void clearCommand(CommandSender sender, String targetName, String type, Boolean silent) {
        TargetsCallback targets;
        InventoryScope inventoryScope;
        if (!InventoryScope.getType(targetName).equals(InventoryScope.UNKNOWN) && sender instanceof Player) {
            // the sender wants to clear their own inventory
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
        if (others && !Utils.checkPermission(sender, true, "clear")) {
            return;
        }

        plugin.getConfirmationManager().requestConfirmation(() -> {
            targets.forEach(target -> {
                switch (inventoryScope) {
                    case ALL: {
                        target.getInventory().clear();
                        target.getInventory().setArmorContents(null);
                        target.updateInventory();

                        if (silent == null || !silent) {
                            target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eYour inventory has been cleared!");
                        }
                        break;
                    }
                    case HAND: {
                        target.getInventory().clear(target.getInventory().getHeldItemSlot());
                        target.updateInventory();

                        if (silent == null || !silent) {
                            target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eYour item in hand has been cleared!");
                        }
                        break;
                    }
                    case ARMOR: {
                        target.getInventory().setArmorContents(null);
                        target.updateInventory();

                        if (silent == null || !silent) {
                            target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eYour armor has been cleared!");
                        }
                        break;
                    }
                    case SPECIFIC: {
                        target.getInventory().remove(inventoryScope.getItemStack().getType());
                        target.updateInventory();

                        if (silent == null || !silent) {
                            target.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eCleared all §d" + inventoryScope.getItemStack().getType() + " §efrom your inventory.");
                        }
                        break;
                    }
                }
            });

            if (others) {
                if (targets.size() == 1) {
                    targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eCleared §6" + inventoryScope.getDisplay() + " §efor §d" + target.getName() + "§e."));
                } else {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eCleared §6" + inventoryScope.getDisplay() + " §efor §d" + targets.size() + " §eplayers.");
                }
            } else if ((!(sender instanceof Player)) || (targets.doesNotContain((Player) sender) && !targetName.equals("self"))) {
                targets.stream().findFirst().ifPresent(target -> sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eCleared §6" + inventoryScope.getDisplay() + " §efor §d" + target.getName() + "§e."));
            }
        }, this.canSkip("clear player inventory", targets, sender));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkPermission(sender, "clear")) {
            return;
        }

        if (args.length == 0 && !(sender instanceof Player)) {
            sender.sendMessage("§cPlease specify a player!");
            return;
        }

        if (args.length == 0) {
            this.clearCommand(sender, "self", "all", null);
            return;
        }

        String targetName = args[0];
        String type = "all";
        boolean silent = args[args.length - 1].equalsIgnoreCase("-s");

        if (args.length >= 2) {
            type = args[1];
        }

        this.clearCommand(sender, targetName, type, silent);
    }

    @Override
    public void sendDefaultMessage(CommandSender sender) {
        sender.sendMessage("§eClear command:");
        sender.sendMessage("§8└─ §e/clear [<all/hand/armor>] §8- §7Clear your inventory");
        sender.sendMessage("§8└─ §e/clear <player> [<all/hand/armor>] [<silent>] §8- §7Clear a player's inventory");
    }

    @Override
    public List<String> getTabSuggestions(CommandSender sender, String alias, String[] args) {
        if (!Utils.checkPermission(sender, "clear")) {
            return null;
        }

        switch (args.length) {
            case 1:
                return this.players(args[0]);
            case 2:
                return Stream.of("all", "hand", "armor")
                        .filter(it -> it.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            case 3:
                return Stream.of("true", "false")
                        .filter(it -> it.toLowerCase().startsWith(args[2].toLowerCase()))
                        .collect(Collectors.toList());
        }

        return null;
    }
}
