package id.luckynetwork.dev.lyrams.lej.utils;

import id.luckynetwork.dev.lyrams.lej.config.Config;
import id.luckynetwork.dev.lyrams.lej.enums.TrueFalseType;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public class Utils {

    public String colorize(String toColor) {
        return ChatColor.translateAlternateColorCodes('&', toColor);
    }

    /**
     * see {@link Utils#checkPermission(CommandSender, String, boolean, boolean, String)}
     */
    public boolean checkPermission(CommandSender sender, String permission) {
        return Utils.checkPermission(sender, permission, false, false, null);
    }

    /**
     * see {@link Utils#checkPermission(CommandSender, String, boolean, boolean, String)}
     */
    public boolean checkPermission(CommandSender sender, boolean others, String permission) {
        return Utils.checkPermission(sender, permission, others, false, null);
    }

    /**
     * checks if the command sender has a certain permission
     *
     * @param sender         the sender
     * @param permission     the permission
     * @param others         is the sender executing this to other player(s)?
     * @param showPermission should the permission deny message show the required permission?
     * @param command        the command, if set to null then the permission deny message will show the executed command.
     * @return see {@link CommandSender#hasPermission(String)}
     */
    public boolean checkPermission(CommandSender sender, String permission, boolean others, boolean showPermission, @Nullable String command) {
        permission = "luckyessentials." + permission.toLowerCase();
        if (others) {
            permission += ".others";
        }

        if (sender.hasPermission(permission)) {
            return true;
        }

        if (showPermission) {
            if (command != null) {
                sender.sendMessage(Config.PREFIX + "§cYou need to have the required permission §l" + permission + " to execute §l" + command + "§c!");
            } else {
                sender.sendMessage(Config.PREFIX + "§cYou need to have the required permission §l" + permission + " to do that!");
            }
        } else {
            if (command != null) {
                sender.sendMessage(Config.PREFIX + "§cYou don't have the required permission to execute §l" + command + "§c!");
            } else {
                sender.sendMessage(Config.PREFIX + "§cYou don't have the required permission to do that!");
            }
        }
        return false;
    }

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
     * Setting arg to '*[r=number1,n=number2]' can be used to add number2 amount of online players in the range of number1 to the target set.
     * <p>
     * Setting arg to 'player1,player2,player3,...' can be used to add player1,player2,player3,... to the target set.
     *
     * <p>
     * <p>
     * Is this overengineered? maybe lol
     *
     * @param sender the sender
     * @param arg    the arg
     * @return a set of target(s)
     */
    public Set<Player> getTargets(CommandSender sender, @Nullable String arg) {
        Set<Player> targets = new HashSet<>();
        if (!(sender instanceof Player)) {
            if (arg == null) {
                sender.sendMessage(Config.PREFIX + "§cPlease specify a target player!");
                return targets;
            }

            Player targetPlayer = Bukkit.getPlayer(arg);
            if (targetPlayer == null) {
                sender.sendMessage(Config.PREFIX + "§cPlayer not found!");
                return targets;
            }

            targets.add(targetPlayer);
            return targets;
        }

        if (arg != null) {
            if (arg.equals("*") || arg.equals("@a")) {
                // all players
                targets.addAll(Bukkit.getOnlinePlayers());
            } else if (arg.equals("self")) {
                // self
                targets.add((Player) sender);
            } else if (arg.equals("@r")) {
                // random player
                Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);

                Random random = new Random();
                Player randomPlayer = players[random.nextInt(players.length)];

                targets.add(randomPlayer);
            } else if ((arg.startsWith("*[r=") || arg.startsWith("@a[r=") && arg.endsWith("]"))) {
                try {
                    double range;
                    int amount;
                    if (arg.split("r=")[1].split("]")[0].contains(",n=")) {
                        // all players in a range with a set amount
                        range = Double.parseDouble(arg.split("=")[1].split(",")[0]);
                        amount = Integer.parseInt(arg.split(",n=")[1].split("]")[0]);
                    } else {
                        // all players in a range
                        range = Double.parseDouble(arg.split("=")[1].split("]")[0]);
                        amount = 0;
                    }

                    List<Player> nearbyPlayers = ((Player) sender).getNearbyEntities(range, range, range).stream()
                            .filter(entity -> entity instanceof Player)
                            .map(entity -> (Player) entity)
                            .collect(Collectors.toCollection(ArrayList::new));

                    if (!nearbyPlayers.contains((Player) sender)) {
                        nearbyPlayers.add((Player) sender);
                    }

                    if (amount > 0) {
                        while (nearbyPlayers.size() > amount) {
                            nearbyPlayers.remove(nearbyPlayers.size() - 1);
                        }
                    } else {
                        targets.addAll(nearbyPlayers);
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(Config.PREFIX + "§cInvalid target range or amount value!");
                    return targets;
                }
            } else if (arg.startsWith("@r[r=") && arg.endsWith("]")) {
                try {
                    double range;
                    int amount;
                    if (arg.split("r=")[1].split("]")[0].contains(",n=")) {
                        // random players in a range with a set amount
                        range = Double.parseDouble(arg.split("=")[1].split(",")[0]);
                        amount = Integer.parseInt(arg.split(",n=")[1].split("]")[0]);
                    } else {
                        // random player in a range
                        range = Double.parseDouble(arg.split("=")[1].split("]")[0]);
                        amount = 1;
                    }

                    List<Player> nearbyPlayers = ((Player) sender).getNearbyEntities(range, range, range).stream()
                            .filter(entity -> entity instanceof Player)
                            .map(entity -> (Player) entity)
                            .collect(Collectors.toCollection(ArrayList::new));

                    if (!nearbyPlayers.contains((Player) sender)) {
                        nearbyPlayers.add((Player) sender);
                    }

                    if (amount >= nearbyPlayers.size()) {
                        targets.addAll(nearbyPlayers);
                    } else {
                        Random random = new Random();
                        while (amount > targets.size()) {
                            Player randomPlayer = nearbyPlayers.get(random.nextInt(nearbyPlayers.size()));

                            targets.add(randomPlayer);
                            nearbyPlayers.remove(randomPlayer);
                        }
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(Config.PREFIX + "§cInvalid target range or amount value!");
                    return targets;
                }
            } else if (arg.startsWith("@r[n=") && arg.endsWith("]")) {
                try {
                    // random players with a set amount
                    int amount = Integer.parseInt(arg.split("=")[1].split("]")[0]);
                    List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

                    if (amount >= onlinePlayers.size()) {
                        targets.addAll(onlinePlayers);
                    } else {
                        Random random = new Random();
                        while (amount > targets.size()) {
                            Player randomPlayer = onlinePlayers.get(random.nextInt(onlinePlayers.size()));

                            targets.add(randomPlayer);
                            onlinePlayers.remove(randomPlayer);
                        }
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(Config.PREFIX + "§cInvalid amount value!");
                    return targets;
                }
            } else if (arg.contains(",")) {
                // selected players
                for (String potTarget : arg.split(",")) {
                    Player potTargetPlayer = Bukkit.getPlayer(potTarget);
                    if (potTargetPlayer == null) {
                        sender.sendMessage(Config.PREFIX + "§cPlayer §l" + potTarget + " §cnot found!");
                        continue;
                    }

                    targets.add(potTargetPlayer);
                }
            } else {
                // selected player
                Player targetPlayer = Bukkit.getPlayer(arg);
                if (targetPlayer == null) {
                    sender.sendMessage(Config.PREFIX + "§cPlayer not found!");
                    return targets;
                }

                targets.add(targetPlayer);
            }
        } else {
            // self
            targets.add((Player) sender);
        }

        return targets;
    }

    /**
     * colorizes a string from {@link TrueFalseType} to green if true and to red if false
     *
     * @param state         the state
     * @param trueFalseType see {@link TrueFalseType}
     * @return a green {@link TrueFalseType#getIfTrue()} if true and a red {@link TrueFalseType#getIfFalse()} if false
     */
    public String colorizeTrueFalse(boolean state, TrueFalseType trueFalseType) {
        if (state) {
            return "§a" + trueFalseType.getIfTrue();
        } else {
            return "§c" + trueFalseType.getIfFalse();
        }
    }
}
