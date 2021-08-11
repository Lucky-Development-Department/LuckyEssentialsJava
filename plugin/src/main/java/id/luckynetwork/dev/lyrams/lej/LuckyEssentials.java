package id.luckynetwork.dev.lyrams.lej;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import id.luckynetwork.dev.lyrams.lej.commands.main.LuckyEssentialsCommand;
import id.luckynetwork.dev.lyrams.lej.dependency.DependencyHelper;
import id.luckynetwork.dev.lyrams.lej.versionsupport.VersionSupport;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Getter
public class LuckyEssentials extends JavaPlugin {

    @Getter
    public static LuckyEssentials instance;
    private LuckyEssentialsCommand mainCommand;
    private VersionSupport versionSupport;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        this.loadDependencies();
        this.loadVersionSupport();

        this.mainCommand = new LuckyEssentialsCommand(this);
    }

    @Override
    public void onDisable() {

    }

    private void loadVersionSupport() {
        String version = Bukkit.getServer().getClass().getName().split("\\.")[3];
        try {
            try {
                Class<?> support = Class.forName("id.luckynetwork.dev.lyrams.lej.versionsupport." + version);
                versionSupport = (VersionSupport) support.getConstructor(Class.forName("org.bukkit.plugin.Plugin"), String.class).newInstance(this, version);
            } catch (ClassNotFoundException ignored) {
                Class<?> support = Class.forName("id.luckynetwork.dev.lyrams.lej.versionsupport.v1_12_R1");
                versionSupport = (VersionSupport) support.getConstructor(Class.forName("org.bukkit.plugin.Plugin"), String.class).newInstance(this, "v1_12_R1");
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void loadDependencies() {
        Map<String, String> dependencyMap = new HashMap<>();

        InputStream stream = null;
        InputStreamReader reader = null;
        try {
            stream = LuckyEssentials.class.getClassLoader().getResourceAsStream("commandframework.json");

            assert stream != null;
            reader = new InputStreamReader(stream);

            JsonParser parser = new JsonParser();
            JsonArray dependencies = parser.parse(reader).getAsJsonArray();
            if (dependencies.size() == 0) {
                return;
            }

            for (JsonElement element : dependencies) {
                JsonObject dependency = element.getAsJsonObject();
                dependencyMap.put(
                        dependency.get("name").getAsString(),
                        dependency.get("url").getAsString()
                );
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

        DependencyHelper helper = new DependencyHelper(LuckyEssentials.class.getClassLoader());
        File dir = new File("plugins/LuckyEssentials/libs");
        try {
            helper.download(dependencyMap, dir.toPath());
            helper.loadDir(dir.toPath());
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
