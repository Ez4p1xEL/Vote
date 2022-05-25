package p1xel.vote.Storage;

import p1xel.vote.Command.Cmd;
import p1xel.vote.Vote;

public class Config {

    public static String getLanguage() {
        return Vote.getInstance().getConfig().getString("Language");
    }

    public static int getInt(String path) { return Vote.getInstance().getConfig().getInt(path); }

    public static String getVersion() { return Vote.getInstance().getConfig().getString("Version"); }

    public static void reloadConfig() {
        Vote.getInstance().reloadConfig();
        Locale.language = Config.getLanguage();
    }

}
