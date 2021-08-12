package id.luckynetwork.dev.lyrams.lej.commands.main;

import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.config.Config;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandClass {

    @Getter
    protected LuckyEssentials plugin = LuckyEssentials.instance;

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
    protected Set<Player> getTargets(CommandSender sender, @Nullable String arg) {
        Set<Player> targets = new HashSet<>();
        if (!(sender instanceof Player)) {
            if (arg == null || arg.equals("self")) {
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

}
