package id.luckynetwork.dev.lyrams.lej;

import com.google.common.base.Joiner;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import id.luckynetwork.dev.lyrams.lej.commands.main.LuckyEssentialsCommand;
import id.luckynetwork.dev.lyrams.lej.config.ConfigFile;
import id.luckynetwork.dev.lyrams.lej.listeners.*;
import id.luckynetwork.dev.lyrams.lej.listeners.trolls.TrollListeners;
import id.luckynetwork.dev.lyrams.lej.managers.ConfirmationManager;
import id.luckynetwork.dev.lyrams.lej.managers.InvseeManager;
import id.luckynetwork.dev.lyrams.lej.managers.MainConfigManager;
import id.luckynetwork.dev.lyrams.lej.managers.SlotsManager;
import id.luckynetwork.dev.lyrams.lej.managers.whitelist.WhitelistManager;
import id.luckynetwork.dev.lyrams.lej.utils.Utils;
import id.luckynetwork.dev.lyrams.lej.versionsupport.VersionSupport;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public class LuckyEssentials extends JavaPlugin {

    @Getter
    private static LuckyEssentials instance;

    @Setter
    private ConfigFile mainConfig, slotsConfig, whitelistConfig;

    private LuckyEssentialsCommand mainCommand;
    private VersionSupport versionSupport;

    private InvseeManager invseeManager;
    private MainConfigManager mainConfigManager;
    private WhitelistManager whitelistManager;
    private SlotsManager slotsManager;

    private ConfirmationManager confirmationManager;

    @Override
    public void onEnable() {
        long millis = System.currentTimeMillis();

        instance = this;
        this.loadDependencies();
        this.loadVersionSupport();

        this.loadConfigs();
        this.mainConfigManager = new MainConfigManager(this);
        this.slotsManager = new SlotsManager(this);
        this.whitelistManager = new WhitelistManager(this);

        this.confirmationManager = new ConfirmationManager(this);

        this.mainCommand = new LuckyEssentialsCommand(this);
        this.invseeManager = new InvseeManager(this);

        this.registerListeners(
                new ChatListener(this),
                new ConnectionListeners(this),
                new DamageListeners(),
                new DeathListeners(this)
        );
        new TrollListeners(this);
        new InvseeListeners(this);

        this.sendStuffToConsoleLmao();
        this.getLogger().info("LuckyEssentials v" + this.getDescription().getVersion() + " loaded in " + (System.currentTimeMillis() - millis) + "ms!");
    }

    @Override
    public void onDisable() {

    }

    private void registerListeners(Listener... listeners) {
        Arrays.stream(listeners).forEach(it -> Bukkit.getPluginManager().registerEvents(it, this));
    }

    public void loadConfigs() {
        this.mainConfig = new ConfigFile(this, "config.yml");
        this.slotsConfig = new ConfigFile(this, "slots.yml");
        this.whitelistConfig = new ConfigFile(this, "whitelist.yml");
    }

    /**
     * downloads and/or loads dependencies
     */
    private void loadDependencies() {
        this.getLogger().info("Loading and injecting dependencies...");
        Map<String, String> dependencyMap = new HashMap<>();

        InputStream stream = null;
        InputStreamReader reader = null;
        try {
            stream = LuckyEssentials.class.getClassLoader().getResourceAsStream("dependencies.json");

            assert stream != null;
            reader = new InputStreamReader(stream);

            JsonParser parser = new JsonParser();
            JsonArray dependencies = parser.parse(reader).getAsJsonArray();
            if (dependencies.size() == 0) {
                return;
            }

            for (JsonElement element : dependencies) {
                JsonObject dependency = element.getAsJsonObject();
                if (!dependency.get("name").getAsString().contains("adventure-api")) {
                    dependencyMap.put(
                            dependency.get("name").getAsString(),
                            dependency.get("url").getAsString()
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * loads the appropriate {@link VersionSupport}
     */
    private void loadVersionSupport() {
        this.getLogger().info("Loading version support...");

        String version = Utils.getNmsVersion();
        this.getLogger().info("Detected server version: " + version);
        try {
            Class<?> support;
            switch (version) {
                case "v1_8_R3": {
                    support = Class.forName("id.luckynetwork.dev.lyrams.lej.versionsupport.v1_8_R3.v1_8_R3");
                    this.getLogger().info("Loaded version support v1_8_R3");
                    break;
                }
                case "v1_9_R1":
                case "v1_9_R2":
                case "v1_10_R1":
                case "v1_11_R1":
                case "v1_12_R1": {
                    support = Class.forName("id.luckynetwork.dev.lyrams.lej.versionsupport.v1_12_R1.v1_12_R1");
                    this.getLogger().info("Loaded version support v1_12_R1");
                    break;
                }
                case "v1_13_R1": {
                    support = Class.forName("id.luckynetwork.dev.lyrams.lej.versionsupport.v1_13_R1.v1_13_R1");
                    this.getLogger().info("Loaded version support v1_13_R1");
                    break;
                }
                case "v1_14_R1":
                case "v1_15_R1":
                case "v1_16_R1":
                case "v1_16_R3": {
                    support = Class.forName("id.luckynetwork.dev.lyrams.lej.versionsupport.v1_16_R1.v1_16_R1");
                    this.getLogger().info("Loaded version support v1_16_R1");
                    break;
                }
                case "v1_17_R1": {
                    support = Class.forName("id.luckynetwork.dev.lyrams.lej.versionsupport.v1_17_R1.v1_17_R1");
                    this.getLogger().info("Loaded version support v1_17_R1");
                    break;
                }
                case "v1_18_R1":
                case "v1_18_R2": {
                    support = Class.forName("id.luckynetwork.dev.lyrams.lej.versionsupport.v1_18_R1.v1_18_R1");
                    this.getLogger().info("Loaded version support v1_18_R1");
                    break;
                }
                case "v1_19_R1":
                case "v1_19_R2": {
                    support = Class.forName("id.luckynetwork.dev.lyrams.lej.versionsupport.v1_19_R1.v1_19_R1");
                    this.getLogger().info("Loaded version support v1_19_R1");
                    break;
                }
                default: {
                    this.getLogger().severe("Unsupported server version!");
                    Bukkit.getPluginManager().disablePlugin(this);
                    return;
                }
            }

            versionSupport = (VersionSupport) support.getConstructor(Class.forName("org.bukkit.plugin.Plugin")).newInstance(this);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            e.printStackTrace();
            this.getLogger().severe("Unsupported server version!");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    /**
     * I was bored...
     */
    private void sendStuffToConsoleLmao() {
        String luckyEssentials =
                "\n" +
                        "§e  _                _            ______                    _   _       _              \n" +
                        "§e | |              | |          |  ____|                  | | (_)     | |             \n" +
                        "§e | |    _   _  ___| | ___   _  | |__   ___ ___  ___ _ __ | |_ _  __ _| |___          \n" +
                        "§e | |   | | | |/ __| |/ / | | | |  __| / __/ __|/ _ \\ '_ \\| __| |/ _` | / __|       \n" +
                        "§e | |___| |_| | (__|   <| |_| | | |____\\__ \\__ \\  __/ | | | |_| | (_| | \\__ \\    \n" +
                        "§e |______\\__,_|\\___|_|\\_\\\\__, | |______|___/___/\\___|_| |_|\\__|_|\\__,_|_|___/ \n" +
                        "§e                         __/ |                                                       \n" +
                        "§e                        |___/                                                        \n\n" +
                        "                  §aLuckyEssentials §cv" + this.getDescription().getVersion() + " §eby §d" + Joiner.on(", ").join(this.getDescription().getAuthors()) + "\n" +
                        " ";

        for (String s : luckyEssentials.split("\n")) {
            Bukkit.getConsoleSender().sendMessage(s);
        }
    }
}
