package id.luckynetwork.dev.lyrams.lej.commands.main;

import cloud.commandframework.Command;
import cloud.commandframework.CommandTree;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.common.reflect.ClassPath;
import id.luckynetwork.dev.lyrams.lej.LuckyEssentials;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

public class LuckyEssentialsCommand {

    @Getter
    private PaperCommandManager<CommandSender> manager;
    @Getter
    private final LuckyEssentials plugin;
    @Getter
    private final Command.Builder<CommandSender> builder;
    @Getter
    private final MinecraftHelp<CommandSender> minecraftHelp;
    @Getter
    private final AnnotationParser<CommandSender> annotationParser;

    public LuckyEssentialsCommand(LuckyEssentials plugin) {
        this.plugin = plugin;

        Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction = CommandExecutionCoordinator.simpleCoordinator();
        Function<CommandSender, CommandSender> mapperFunction = Function.identity();
        try {
            this.manager = new PaperCommandManager<>(
                    plugin,
                    executionCoordinatorFunction,
                    mapperFunction,
                    mapperFunction
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.builder = this.manager.commandBuilder("luckyessentials", "luckyessential", "essentials", "essential", "ess", "less");

        // registers the custom help command
        BukkitAudiences bukkitAudiences = BukkitAudiences.create(plugin);
        this.minecraftHelp = new MinecraftHelp<>(
                "/luckyessentials help",
                bukkitAudiences::sender,
                this.manager
        );

        if (this.manager.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            this.manager.registerAsynchronousCompletions();
        }

        // registers the annotation parser
        Function<ParserParameters, CommandMeta> commandMetaFunction = it ->
                CommandMeta.simple()
                        .with(CommandMeta.DESCRIPTION, it.get(StandardParameters.DESCRIPTION, "No description"))
                        .build();

        this.annotationParser = new AnnotationParser<>(
                this.manager,
                CommandSender.class,
                commandMetaFunction
        );

        // initializes the default command
        manager.command(builder
                .meta(CommandMeta.DESCRIPTION, "The main command")
                .handler(commandContext -> {
                    CommandSender sender = commandContext.getSender();
                    String query = commandContext.getOrDefault("query", null);
                    if (query == null) {
                        sender.sendMessage(Utils.getPluginDescription());
                        return;
                    }

                    this.getMinecraftHelp().queryCommands(query, sender);
                }));

        this.initCommands();
    }

    @SuppressWarnings("UnstableApiUsage")
    private void initCommands() {
        plugin.getLogger().info("Loading and registering commands...");
        try {
            ClassPath classPath = ClassPath.from(plugin.getClass().getClassLoader());
            for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive("id.luckynetwork.dev.lyrams.lej.commands")) {
                if (classInfo.getName().endsWith("LuckyEssentialsCommand") || classInfo.getName().contains(".api")) {
                    continue;
                }

                try {
                    Class<?> commandClass = Class.forName(classInfo.getName());
                    this.parseAnnotationCommands(commandClass.newInstance());
                } catch (Exception e) {
                    plugin.getLogger().severe("Failed loading command class: " + classInfo.getName());
                    e.printStackTrace();
                }
            }

            plugin.getLogger().info("Registered " + manager.getCommands().size() + " commands!");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed loading command classes!");
            e.printStackTrace();
        }
    }

    private void parseAnnotationCommands(Object... clazz) {
        Arrays.stream(clazz).forEach(this.annotationParser::parse);
    }
}
