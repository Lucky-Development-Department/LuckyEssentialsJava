package id.luckynetwork.dev.lyrams.lej.commands.main;

import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandClass {

    @Getter
    protected LuckyEssentials plugin = LuckyEssentials.instance;

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
