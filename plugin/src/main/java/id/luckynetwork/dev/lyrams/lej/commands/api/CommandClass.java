package id.luckynetwork.dev.lyrams.lej.commands.api;

import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.callbacks.CanSkipCallback;
import id.luckynetwork.dev.lyrams.lej.callbacks.IsDoubleCallback;
import id.luckynetwork.dev.lyrams.lej.callbacks.IsIntegerCallback;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class CommandClass {

    protected LuckyEssentials plugin = LuckyEssentials.getInstance();

    /**
     * Gets a set of target player(s) from the input arg.
     * <p>
     * <p>
     * Setting arg to null or 'self' can be used to only add the sender to the target set.
     * <p>
     * Setting arg to '*' or '@a' can be used to add all online players to the target set.
     * <p>
     * Setting arg to '*[r=number]' or '@a[r=number]' can be used to add all online players in the range of number to the target set.
     * <p>
     * Setting arg to '*[r=number1,n=number2]' or '@a[r=number1,n=number2]' can be used to add number2 amount of online players in the range of number1 to the target set.
     * <p>
     * Setting arg to '@r' can be used to add one random online player to the target set.
     * <p>
     * Setting arg to '@r[r=number]' can be used to add one random online player in the range of number to the target set.
     * <p>
     * Setting arg to '@r[n=number]' can be used to add number amount of random online player to the target set.
     * <p>
     * Setting arg to '@r[r=number1,n=number2]' can be used to add number2 amount of random online players in the range of number1 to the target set.
     * <p>
     * Setting arg to 'player1,player2,player3,...' can be used to add player1,player2,player3,... to the target set.
     * <p>
     * <p>
     * Is this overengineered? maybe lol
     *
     * @param sender the sender
     * @param arg    the arg
     * @return a set of target(s)
     */
    protected TargetsCallback getTargets(CommandSender sender, @Nullable String arg) {
        TargetsCallback callback = new TargetsCallback();
        if (sender instanceof Player) {
            if (arg == null) {
                // self
                callback.add((Player) sender);
                return callback;
            }

            switch (arg.toLowerCase()) {
                case "self": {
                    // self
                    callback.add((Player) sender);
                    return callback;
                }
                case "*":
                case "@a": {
                    // all players
                    callback.addAll(Bukkit.getOnlinePlayers());
                    return callback;
                }
                case "@r": {
                    // random player
                    Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);

                    Random random = new Random();
                    Player randomPlayer = players[random.nextInt(players.length)];

                    callback.add(randomPlayer);
                    return callback;
                }
            }

            if ((arg.startsWith("*[r=") || arg.startsWith("@a[r=") && arg.endsWith("]"))) {
                IsDoubleCallback isDoubleCallback;
                IsIntegerCallback isIntegerCallback;
                if (arg.split("r=")[1].split("]")[0].contains(",n=")) {
                    // all players in a range with a set amount
                    isDoubleCallback = Utils.isDouble(arg.split("=")[1].split(",")[0]);
                    isIntegerCallback = Utils.isInteger(arg.split(",n=")[1].split("]")[0]);
                } else {
                    // all player in a range
                    isDoubleCallback = Utils.isDouble(arg.split("=")[1].split("]")[0]);
                    isIntegerCallback = new IsIntegerCallback(true, -1);
                }

                if (!isDoubleCallback.isDouble() || !isIntegerCallback.isInteger()) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid target range or amount value!");
                    callback.setNotified(true);
                    return callback;
                }

                List<Player> nearbyPlayers = ((Player) sender).getNearbyEntities(isDoubleCallback.getValue(), isDoubleCallback.getValue(), isDoubleCallback.getValue()).stream()
                        .filter(entity -> entity instanceof Player)
                        .map(entity -> (Player) entity)
                        .collect(Collectors.toCollection(ArrayList::new));

                if (!nearbyPlayers.contains((Player) sender)) {
                    nearbyPlayers.add((Player) sender);
                }

                if (isIntegerCallback.getValue() > 0) {
                    while (nearbyPlayers.size() > isIntegerCallback.getValue()) {
                        nearbyPlayers.remove(nearbyPlayers.size() - 1);
                    }
                } else {
                    callback.addAll(nearbyPlayers);
                }
                return callback;
            }

            if (arg.startsWith("@r[r=") && arg.endsWith("]")) {
                IsDoubleCallback isDoubleCallback;
                IsIntegerCallback isIntegerCallback;
                if (arg.split("r=")[1].split("]")[0].contains(",n=")) {
                    // random players in a range with a set amount
                    isDoubleCallback = Utils.isDouble(arg.split("=")[1].split(",")[0]);
                    isIntegerCallback = Utils.isInteger(arg.split(",n=")[1].split("]")[0]);
                } else {
                    // random player in a range
                    isDoubleCallback = Utils.isDouble(arg.split("=")[1].split("]")[0]);
                    isIntegerCallback = new IsIntegerCallback(true, -1);
                }

                if (!isDoubleCallback.isDouble() || !isIntegerCallback.isInteger()) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid target range or amount value!");
                    callback.setNotified(true);
                    return callback;
                }

                List<Player> nearbyPlayers = ((Player) sender).getNearbyEntities(isDoubleCallback.getValue(), isDoubleCallback.getValue(), isDoubleCallback.getValue()).stream()
                        .filter(entity -> entity instanceof Player)
                        .map(entity -> (Player) entity)
                        .collect(Collectors.toCollection(ArrayList::new));

                if (!nearbyPlayers.contains((Player) sender)) {
                    nearbyPlayers.add((Player) sender);
                }

                if (isIntegerCallback.getValue() >= nearbyPlayers.size()) {
                    callback.addAll(nearbyPlayers);
                } else {
                    Random random = new Random();
                    while (isIntegerCallback.getValue() > callback.size()) {
                        Player randomPlayer = nearbyPlayers.get(random.nextInt(nearbyPlayers.size()));

                        callback.add(randomPlayer);
                        nearbyPlayers.remove(randomPlayer);
                    }
                }
                return callback;
            }

            if (arg.startsWith("@r[n=") && arg.endsWith("]")) {
                // random players with a set amount
                IsIntegerCallback isIntegerCallback = Utils.isInteger(arg.split("=")[1].split("]")[0]);
                if (isIntegerCallback.isInteger()) {
                    int amount = Integer.parseInt(arg.split("=")[1].split("]")[0]);
                    List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

                    if (amount >= onlinePlayers.size()) {
                        callback.addAll(onlinePlayers);
                    } else {
                        Random random = new Random();
                        while (amount > callback.size()) {
                            Player randomPlayer = onlinePlayers.get(random.nextInt(onlinePlayers.size()));

                            callback.add(randomPlayer);
                            onlinePlayers.remove(randomPlayer);
                        }
                    }
                } else {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid amount value!");
                    callback.setNotified(true);
                }
                return callback;
            }

            if (arg.contains(",")) {
                // selected players
                for (String potTarget : arg.split(",")) {
                    Player potTargetPlayer = Bukkit.getPlayer(potTarget);
                    if (potTargetPlayer == null) {
                        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlayer §l" + potTarget + " §cnot found!");
                        continue;
                    }

                    callback.add(potTargetPlayer);
                }
                return callback;
            }

            // selected player
            Player targetPlayer = Bukkit.getPlayer(arg);
            if (targetPlayer == null) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlayer not found!");
                callback.setNotified(true);
                return callback;
            }

            callback.add(targetPlayer);
            return callback;
        }

        if (arg == null) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlease specify a target player!");
            callback.setNotified(true);
            return callback;
        }

        switch (arg.toLowerCase()) {
            case "self": {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlease specify a target player!");
                callback.setNotified(true);
                return callback;
            }
            case "*":
            case "@a": {
                // all players
                callback.addAll(Bukkit.getOnlinePlayers());
                return callback;
            }
            case "@r": {
                // random player
                Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);

                Random random = new Random();
                Player randomPlayer = players[random.nextInt(players.length)];

                callback.add(randomPlayer);
                return callback;
            }
        }

        if (arg.startsWith("@r[n=") && arg.endsWith("]")) {
            // random players with a set amount
            IsIntegerCallback isIntegerCallback = Utils.isInteger(arg.split("=")[1].split("]")[0]);
            if (isIntegerCallback.isInteger()) {
                int amount = Integer.parseInt(arg.split("=")[1].split("]")[0]);
                List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

                if (amount >= onlinePlayers.size()) {
                    callback.addAll(onlinePlayers);
                } else {
                    Random random = new Random();
                    while (amount > callback.size()) {
                        Player randomPlayer = onlinePlayers.get(random.nextInt(onlinePlayers.size()));

                        callback.add(randomPlayer);
                        onlinePlayers.remove(randomPlayer);
                    }
                }
            } else {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid amount value!");
                callback.setNotified(true);
            }
            return callback;
        }

        if (arg.contains(",")) {
            // selected players
            for (String potTarget : arg.split(",")) {
                Player potTargetPlayer = Bukkit.getPlayer(potTarget);
                if (potTargetPlayer == null) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlayer §l" + potTarget + " §cnot found!");
                    continue;
                }

                callback.add(potTargetPlayer);
            }
            return callback;
        }

        Player targetPlayer = Bukkit.getPlayer(arg);
        if (targetPlayer == null) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlayer not found!");
            callback.setNotified(true);
            return callback;
        }

        callback.add(targetPlayer);
        return callback;
    }

    /**
     * Gets a set of target offlinePlayer(s) from the input arg.
     * <p>
     * <p>
     * Setting arg to null or 'self' can be used to only add the sender to the target set.
     * <p>
     * Setting arg to '*' or '@a' can be used to add all online players to the target set.
     * <p>
     * Setting arg to '*[r=number]' or '@a[r=number]' can be used to add all online players in the range of number to the target set.
     * <p>
     * Setting arg to '*[r=number1,n=number2]' or '@a[r=number1,n=number2]' can be used to add number2 amount of online players in the range of number1 to the target set.
     * <p>
     * Setting arg to '@r' can be used to add one random online player to the target set.
     * <p>
     * Setting arg to '@r[r=number]' can be used to add one random online player in the range of number to the target set.
     * <p>
     * Setting arg to '@r[n=number]' can be used to add number amount of random online player to the target set.
     * <p>
     * Setting arg to '@r[r=number1,n=number2]' can be used to add number2 amount of random online players in the range of number1 to the target set.
     * <p>
     * Setting arg to 'player1,player2,player3,...' can be used to add player1,player2,player3,... to the target set.
     * <p>
     * <p>
     * Is this overengineered? maybe lol
     *
     * @param sender the sender
     * @param arg    the arg
     * @return a set of target(s)
     */
    @SuppressWarnings("deprecation")
    protected OfflineTargetsCallback getTargetsOffline(CommandSender sender, @Nullable String arg) {
        OfflineTargetsCallback callback = new OfflineTargetsCallback();
        if (sender instanceof Player) {
            if (arg == null) {
                // self
                callback.add((Player) sender);
                return callback;
            }

            switch (arg.toLowerCase()) {
                case "self": {
                    // self
                    callback.add((Player) sender);
                    return callback;
                }
                case "*":
                case "@a": {
                    // all players
                    callback.addAll(Bukkit.getOnlinePlayers());
                    return callback;
                }
                case "@r": {
                    // random player
                    Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);

                    Random random = new Random();
                    Player randomPlayer = players[random.nextInt(players.length)];

                    callback.add(randomPlayer);
                    return callback;
                }
            }

            if ((arg.startsWith("*[r=") || arg.startsWith("@a[r=") && arg.endsWith("]"))) {
                IsDoubleCallback isDoubleCallback;
                IsIntegerCallback isIntegerCallback;
                if (arg.split("r=")[1].split("]")[0].contains(",n=")) {
                    // all players in a range with a set amount
                    isDoubleCallback = Utils.isDouble(arg.split("=")[1].split(",")[0]);
                    isIntegerCallback = Utils.isInteger(arg.split(",n=")[1].split("]")[0]);
                } else {
                    // all player in a range
                    isDoubleCallback = Utils.isDouble(arg.split("=")[1].split("]")[0]);
                    isIntegerCallback = new IsIntegerCallback(true, -1);
                }

                if (!isDoubleCallback.isDouble() || !isIntegerCallback.isInteger()) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid target range or amount value!");
                    callback.setNotified(true);
                    return callback;
                }

                List<Player> nearbyPlayers = ((Player) sender).getNearbyEntities(isDoubleCallback.getValue(), isDoubleCallback.getValue(), isDoubleCallback.getValue()).stream()
                        .filter(entity -> entity instanceof Player)
                        .map(entity -> (Player) entity)
                        .collect(Collectors.toCollection(ArrayList::new));

                if (!nearbyPlayers.contains((Player) sender)) {
                    nearbyPlayers.add((Player) sender);
                }

                if (isIntegerCallback.getValue() > 0) {
                    while (nearbyPlayers.size() > isIntegerCallback.getValue()) {
                        nearbyPlayers.remove(nearbyPlayers.size() - 1);
                    }
                } else {
                    callback.addAll(nearbyPlayers);
                }
                return callback;
            }

            if (arg.startsWith("@r[r=") && arg.endsWith("]")) {
                IsDoubleCallback isDoubleCallback;
                IsIntegerCallback isIntegerCallback;
                if (arg.split("r=")[1].split("]")[0].contains(",n=")) {
                    // random players in a range with a set amount
                    isDoubleCallback = Utils.isDouble(arg.split("=")[1].split(",")[0]);
                    isIntegerCallback = Utils.isInteger(arg.split(",n=")[1].split("]")[0]);
                } else {
                    // random player in a range
                    isDoubleCallback = Utils.isDouble(arg.split("=")[1].split("]")[0]);
                    isIntegerCallback = new IsIntegerCallback(true, -1);
                }

                if (!isDoubleCallback.isDouble() || !isIntegerCallback.isInteger()) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid target range or amount value!");
                    callback.setNotified(true);
                    return callback;
                }

                List<Player> nearbyPlayers = ((Player) sender).getNearbyEntities(isDoubleCallback.getValue(), isDoubleCallback.getValue(), isDoubleCallback.getValue()).stream()
                        .filter(entity -> entity instanceof Player)
                        .map(entity -> (Player) entity)
                        .collect(Collectors.toCollection(ArrayList::new));

                if (!nearbyPlayers.contains((Player) sender)) {
                    nearbyPlayers.add((Player) sender);
                }

                if (isIntegerCallback.getValue() >= nearbyPlayers.size()) {
                    callback.addAll(nearbyPlayers);
                } else {
                    Random random = new Random();
                    while (isIntegerCallback.getValue() > callback.size()) {
                        Player randomPlayer = nearbyPlayers.get(random.nextInt(nearbyPlayers.size()));

                        callback.add(randomPlayer);
                        nearbyPlayers.remove(randomPlayer);
                    }
                }
                return callback;
            }

            if (arg.startsWith("@r[n=") && arg.endsWith("]")) {
                // random players with a set amount
                IsIntegerCallback isIntegerCallback = Utils.isInteger(arg.split("=")[1].split("]")[0]);
                if (isIntegerCallback.isInteger()) {
                    int amount = Integer.parseInt(arg.split("=")[1].split("]")[0]);
                    List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

                    if (amount >= onlinePlayers.size()) {
                        callback.addAll(onlinePlayers);
                    } else {
                        Random random = new Random();
                        while (amount > callback.size()) {
                            Player randomPlayer = onlinePlayers.get(random.nextInt(onlinePlayers.size()));

                            callback.add(randomPlayer);
                            onlinePlayers.remove(randomPlayer);
                        }
                    }
                } else {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid amount value!");
                    callback.setNotified(true);
                }
                return callback;
            }

            if (arg.contains(",")) {
                // selected players
                for (String potTarget : arg.split(",")) {
                    OfflinePlayer potTargetPlayer = Bukkit.getOfflinePlayer(potTarget);
                    if (potTargetPlayer == null) {
                        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlayer §l" + potTarget + " §cnot found!");
                        continue;
                    }

                    callback.add(potTargetPlayer);
                }
                return callback;
            }

            // selected player
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(arg);
            if (targetPlayer == null) {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlayer not found!");
                callback.setNotified(true);
                return callback;
            }

            callback.add(targetPlayer);
            return callback;
        }

        if (arg == null) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlease specify a target player!");
            callback.setNotified(true);
            return callback;
        }

        switch (arg.toLowerCase()) {
            case "self": {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlease specify a target player!");
                callback.setNotified(true);
                return callback;
            }
            case "*":
            case "@a": {
                // all players
                callback.addAll(Bukkit.getOnlinePlayers());
                return callback;
            }
            case "@r": {
                // random player
                Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);

                Random random = new Random();
                Player randomPlayer = players[random.nextInt(players.length)];

                callback.add(randomPlayer);
                return callback;
            }
        }

        if (arg.startsWith("@r[n=") && arg.endsWith("]")) {
            // random players with a set amount
            IsIntegerCallback isIntegerCallback = Utils.isInteger(arg.split("=")[1].split("]")[0]);
            if (isIntegerCallback.isInteger()) {
                int amount = Integer.parseInt(arg.split("=")[1].split("]")[0]);
                List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

                if (amount >= onlinePlayers.size()) {
                    callback.addAll(onlinePlayers);
                } else {
                    Random random = new Random();
                    while (amount > callback.size()) {
                        Player randomPlayer = onlinePlayers.get(random.nextInt(onlinePlayers.size()));

                        callback.add(randomPlayer);
                        onlinePlayers.remove(randomPlayer);
                    }
                }
            } else {
                sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid amount value!");
                callback.setNotified(true);
            }
            return callback;
        }

        if (arg.contains(",")) {
            // selected players
            for (String potTarget : arg.split(",")) {
                OfflinePlayer potTargetPlayer = Bukkit.getOfflinePlayer(potTarget);
                if (potTargetPlayer == null) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlayer §l" + potTarget + " §cnot found!");
                    continue;
                }

                callback.add(potTargetPlayer);
            }
            return callback;
        }

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(arg);
        if (targetPlayer == null) {
            sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cPlayer not found!");
            callback.setNotified(true);
            return callback;
        }

        callback.add(targetPlayer);
        return callback;
    }

    /**
     * Parses a string of enchants into a hashmap with the key as the enchantment
     * and the value as the enchantment level
     *
     * @param sender   the sender
     * @param enchants the enchantments
     * @return a HashMap with the key as the enchantment and the value as the enchantment level
     */
    public HashMap<Enchantment, Integer> parseEnchants(CommandSender sender, String enchants) {
        HashMap<Enchantment, Integer> enchantmentMap = new HashMap<>();
        if (enchants.contains(",")) {
            String[] split = enchants.split(",");
            for (String ench : split) {
                if (!ench.contains(":") || ench.split(":")[0] == null || ench.split(":")[1] == null) {
                    Enchantment enchantment = plugin.getVersionSupport().getEnchantName(ench);
                    if (enchantment == null) {
                        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid enchantment: §l" + ench + "§c!");
                        continue;
                    }

                    int level = 1;
                    enchantmentMap.put(enchantment, level);
                } else {
                    Enchantment enchantment = plugin.getVersionSupport().getEnchantName(ench.split(":")[0]);
                    if (enchantment == null) {
                        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid enchantment: §l" + ench + "§c!");
                        continue;
                    }

                    IsIntegerCallback isIntegerCallback = Utils.isInteger(ench.split(":")[1]);
                    if (isIntegerCallback.isInteger()) {
                        int level = isIntegerCallback.getValue();
                        enchantmentMap.put(enchantment, level);
                    } else {
                        sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid enchantment level: §l" + ench + "§c!");
                    }
                }
            }
        } else {
            if (!enchants.contains(":") || enchants.split(":")[0] == null || enchants.split(":")[1] == null) {
                Enchantment enchantment = plugin.getVersionSupport().getEnchantName(enchants);
                if (enchantment == null) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid enchantment: §l" + enchants + "§c!");
                    return enchantmentMap;
                }

                int level = 1;
                enchantmentMap.put(enchantment, level);
            } else {
                Enchantment enchantment = plugin.getVersionSupport().getEnchantName(enchants.split(":")[0]);
                if (enchantment == null) {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid enchantment: §l" + enchants + "§c!");
                    return enchantmentMap;
                }

                IsIntegerCallback isIntegerCallback = Utils.isInteger(enchants.split(":")[1]);
                if (isIntegerCallback.isInteger()) {
                    int level = isIntegerCallback.getValue();
                    enchantmentMap.put(enchantment, level);
                } else {
                    sender.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cInvalid enchantment level: §l" + enchants + "§c!");
                }
            }
        }

        return enchantmentMap;
    }

    public CanSkipCallback canSkip(String action, TargetsCallback targetsCallback, CommandSender sender) {
        if (!plugin.getMainConfigManager().isUseConfirmation()) {
            return new CanSkipCallback(sender, true, null);
        }

        if (targetsCallback.size() <= 4) {
            Player target = targetsCallback.getTargets().stream().findFirst().orElse(null);
            if (target != null && target.equals(sender)) {
                return new CanSkipCallback(sender, true, null);
            }
        }

        if (targetsCallback.size() >= 75) {
            return new CanSkipCallback(sender, false, Collections.singletonList(
                    plugin.getMainConfigManager().getPrefix() + "§eAre you sure you want to execute §d" + action + " §eon §b" + targetsCallback.size() + " §eplayers?"
            ));
        }

        boolean playerSender = sender instanceof Player;

        World world = null;
        for (Player target : targetsCallback.getTargets()) {
            if (world != null) {
                if (world != target.getWorld()) {
                    return new CanSkipCallback(sender, false, Arrays.asList(
                            plugin.getMainConfigManager().getPrefix() + "§eAre you sure you want to execute §d" + action + " §eon §b" + targetsCallback.size() + " §eplayers?",
                            plugin.getMainConfigManager().getPrefix() + "§eSome players are scattered across different worlds."
                    ));
                }

                if (playerSender) {
                    if (((Player) sender).getWorld() == target.getWorld() && ((Player) sender).getLocation().distanceSquared(target.getLocation()) >= 100) {
                        return new CanSkipCallback(sender, false, Arrays.asList(
                                plugin.getMainConfigManager().getPrefix() + "§eAre you sure you want to execute §d" + action + " §eon §b" + targetsCallback.size() + " §eplayers?",
                                plugin.getMainConfigManager().getPrefix() + "§eSome players are located really far away from you."
                        ));
                    }
                }

                continue;
            }

            world = target.getWorld();
        }

        return new CanSkipCallback(sender, true, null);
    }

    @Suggestions("players")
    public List<String> players(CommandContext<CommandSender> context, String current) {
        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName)
                .filter(it -> it.toLowerCase().startsWith(current.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Suggestions("toggles")
    public List<String> toggles(CommandContext<CommandSender> context, String current) {
        return Stream.of("on", "off", "toggle")
                .filter(it -> it.toLowerCase().startsWith(current.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Data
    protected static class TargetsCallback {
        private boolean notified = false;
        private Set<Player> targets = new HashSet<>();

        public void add(Player player) {
            this.targets.add(player);
        }

        public void addAll(Collection<? extends Player> player) {
            this.targets.addAll(player);
        }

        public int size() {
            return this.targets.size();
        }

        public boolean isEmpty() {
            return this.targets.isEmpty();
        }

        public boolean notifyIfEmpty() {
            return this.isEmpty() && !this.isNotified();
        }

        public boolean doesNotContain(Player player) {
            return !this.targets.contains(player);
        }

        public Stream<Player> stream() {
            return StreamSupport.stream(Spliterators.spliterator(targets, 0), false);
        }

        public void forEach(Consumer<? super Player> action) {
            for (Player target : targets) {
                action.accept(target);
            }
        }
    }

    @Data
    protected static class OfflineTargetsCallback {
        private boolean notified = false;
        private Set<OfflinePlayer> targets = new HashSet<>();

        public void add(OfflinePlayer player) {
            this.targets.add(player);
        }

        public void addAll(Collection<? extends OfflinePlayer> player) {
            this.targets.addAll(player);
        }

        public int size() {
            return this.targets.size();
        }

        public boolean isEmpty() {
            return this.targets.isEmpty();
        }

        public Stream<OfflinePlayer> stream() {
            return StreamSupport.stream(Spliterators.spliterator(targets, 0), false);
        }

        public void forEach(Consumer<? super OfflinePlayer> action) {
            for (OfflinePlayer target : targets) {
                action.accept(target);
            }
        }
    }

}
