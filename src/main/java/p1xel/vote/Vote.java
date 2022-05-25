package p1xel.vote;

import org.bukkit.plugin.java.JavaPlugin;
import p1xel.vote.Command.Cmd;
import p1xel.vote.Storage.Config;
import p1xel.vote.Storage.Data;
import p1xel.vote.Storage.Locale;
import p1xel.vote.Storage.spigotmc.UpdateChecker;
import p1xel.vote.bStats.Metrics;

public class Vote extends JavaPlugin {

    private static Vote instance;
    public static Vote getInstance() {return instance;}

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Locale.createFile();
        Data.createFile();

        getServer().getPluginCommand("Vote").setExecutor(new Cmd());

        int pluginId = 15301;
        new Metrics(this, pluginId);

        if (Config.getBool("check-update")) {
            new UpdateChecker(this, 102221).getVersion(version -> {
                if (this.getDescription().getVersion().equals(version)) {
                    getLogger().info(Locale.getMessage("update-check.latest"));
                } else {
                    getLogger().info(Locale.getMessage("update-check.outdate"));
                }
            });
        }

        getLogger().info("Plugin loaded!");
    }

}
