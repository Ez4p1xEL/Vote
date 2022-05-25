package p1xel.vote;

import org.bukkit.plugin.java.JavaPlugin;
import p1xel.vote.Command.Cmd;
import p1xel.vote.Storage.Data;
import p1xel.vote.Storage.Locale;

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

        getLogger().info("Plugin loaded!");
    }

}
