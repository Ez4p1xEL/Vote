package p1xel.vote.Storage;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import p1xel.vote.Vote;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Data {

    public static void createFile() {
        File file = new File(Vote.getInstance().getDataFolder(), "vote.yml");
        if (!file.exists()) {
            Vote.getInstance().saveResource("vote.yml", false);
        }
    }

    public static FileConfiguration get() {
        File file = new File(Vote.getInstance().getDataFolder(), "vote.yml");
        return YamlConfiguration.loadConfiguration(file);
    }

    public static void set(String path, Object value) {
        File file = new File(Vote.getInstance().getDataFolder(), "vote.yml");
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        yaml.set(path,value);
        try {
            yaml.save(file);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static boolean isContainThisVote(String name) {
        for (String key : get().getKeys(false)) {
            if (key.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public static void addVote(String name, String message, String command) {
        set(name + ".message", message.replaceAll("_", " ").replaceAll("%under%", "_"));
        set(name + ".commands", Collections.singletonList(command));
    }

    public static void removeVote(String name) {
        set(name, null);
    }

    public static boolean isVoteToAllPlayer(String name) {
        return get().getString(name + ".player").equalsIgnoreCase("@a");
    }

    public static String getPlayerName(String name) {
        return get().getString(name + ".player");
    }

    public static String getVoteMessage(String name) {
        return ChatColor.translateAlternateColorCodes('&', get().getString(name + ".message"));
    }

    public static List<String> getCommands(String name) {
        return get().getStringList(name + ".commands");
    }

}
