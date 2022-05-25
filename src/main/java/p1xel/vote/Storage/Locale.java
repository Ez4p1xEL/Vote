package p1xel.vote.Storage;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import p1xel.vote.Vote;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Locale {

    public static String language = Config.getLanguage();

    public static void createFile() {
        List<String> lang = Arrays.asList("en","zh_CN");
        System.out.println(Vote.getInstance().getDataFolder());
        for (String l : lang) {
            File file = new File(Vote.getInstance().getDataFolder(), l + ".yml");
            if (!file.exists()) {
                Vote.getInstance().saveResource(l + ".yml", false);
            }
        }
    }

    public static FileConfiguration get() {
        File file = new File(Vote.getInstance().getDataFolder(), Config.getLanguage() + ".yml");
        return YamlConfiguration.loadConfiguration(file);
    }

    public static void set(String path, Object value) {
        File file = new File(Vote.getInstance().getDataFolder(), Config.getLanguage() + ".yml");
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        yaml.set(path,value);
        try {
            yaml.save(file);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', get().getString(path).replaceAll("%plugin%", get().getString("Plugin")));
    }

    public static String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message.replaceAll("%plugin%", get().getString("Plugin")));
    }

}