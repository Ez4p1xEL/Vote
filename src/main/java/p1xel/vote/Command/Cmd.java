package p1xel.vote.Command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import p1xel.vote.BRun.CdChecker;
import p1xel.vote.BRun.MessageTimer;
import p1xel.vote.BRun.voteCD;
import p1xel.vote.Storage.Config;
import p1xel.vote.Storage.Data;
import p1xel.vote.Storage.Locale;
import p1xel.vote.Vote;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;

public class Cmd implements CommandExecutor {

    public static boolean isVoting = false;
    public static int voteYes = 0;
    public static int voteNo = 0;
    public static int votePlayersAmount = 0;

    public static int voteTimeLeft = 0;

    public static String voteName = null;

    public static int realTimeLeft = Config.getInt("vote-time");

    static HashMap<String, String> votemap = new HashMap<>();

    public static HashMap<String, String> vote() {
        return votemap;
    }

    public static int cdCheckerID = 0;
    public static int messageTimerID = 0;
    public static int voteCDID = 0;

    HashMap<String, Integer> pcd = new HashMap<>();

    public static int voteCD = Config.getInt("vote-cd");
    public static boolean canVote = true;

    @ParametersAreNonnullByDefault
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        String senderName = sender.getName();

        if (args.length == 0) {
            sender.sendMessage(Locale.getMessage("commands.help"));
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(Locale.getMessage("commands.top"));
                sender.sendMessage(Locale.getMessage("commands.top-space"));
                sender.sendMessage(Locale.getMessage("commands.plugin").replaceAll("%version%", Config.getVersion()));
                sender.sendMessage(Locale.getMessage("commands.help"));
                if (sender.hasPermission("vote.start")) {
                    sender.sendMessage(Locale.getMessage("commands.start"));
                }
                sender.sendMessage(Locale.getMessage("commands.yes-1"));
                sender.sendMessage(Locale.getMessage("commands.no-1"));
                if (sender.hasPermission("vote.create")) {
                    sender.sendMessage(Locale.getMessage("commands.create"));
                }
                if (sender.hasPermission("vote.remove")) {
                    sender.sendMessage(Locale.getMessage("commands.remove"));
                }
                if (sender.hasPermission("vote.stop")) {
                    sender.sendMessage(Locale.getMessage("commands.stop"));
                }
                if (sender.hasPermission("vote.reload")) {
                    sender.sendMessage(Locale.getMessage("commands.reload"));
                }
                sender.sendMessage(Locale.getMessage("commands.bottom-space"));
                sender.sendMessage(Locale.getMessage("commands.bottom"));
                return true;
            }

            if (args[0].equalsIgnoreCase("stop")) {

                if (!sender.hasPermission("vote.stop")) {
                    sender.sendMessage(Locale.getMessage("no-perm"));
                    return true;
                }

                if (!isVoting) {
                    sender.sendMessage(Locale.getMessage("no-current-vote"));
                    return true;
                }

                isVoting = false;
                votePlayersAmount = 0;
                voteYes = 0;
                voteNo = 0;
                vote().clear();

                Bukkit.getServer().getScheduler().cancelTask(cdCheckerID);
                Bukkit.getServer().getScheduler().cancelTask(messageTimerID);
                Bukkit.getServer().getScheduler().cancelTask(voteCDID);
                canVote = true;

                sender.sendMessage(Locale.getMessage("stop-success").replaceAll("%vote%", voteName));
                return true;

            }

            if (args[0].equalsIgnoreCase("reload")) {

                if (!sender.hasPermission("vote.reload")) {
                    sender.sendMessage(Locale.getMessage("no-perm"));
                    return true;
                }

                Config.reloadConfig();
                sender.sendMessage(Locale.getMessage("reload-success"));
                return true;

            }

        }

        if (args.length == 2) {

            if (args[0].equalsIgnoreCase("start")) {

                if (!sender.hasPermission("vote.start")) {
                    sender.sendMessage(Locale.getMessage("no-perm"));
                    return true;
                }

                if (isVoting) {
                    sender.sendMessage(Locale.getMessage("current-vote"));
                    return true;
                }

                if (!Data.isContainThisVote(args[1])) {
                    sender.sendMessage(Locale.getMessage("vote-not-exists").replaceAll("%vote%", args[1]));
                    return true;
                }

                if (!canVote) {
                    sender.sendMessage(Locale.getMessage("cd-left").replaceAll("%time%", String.valueOf(voteCD)));
                    return true;
                }

                if (pcd.get(sender.getName()) != null) {
                    sender.sendMessage(Locale.getMessage("player-cd-left").replaceAll("%time%", String.valueOf(pcd.get(sender.getName()))));
                    return true;
                }

                sender.sendMessage(Locale.getMessage("start-success").replaceAll("%vote%", args[1]));
                isVoting = true;
                voteName = args[1];
                canVote = false;

                realTimeLeft = Config.getInt("vote-time");

                for (Player allp : Bukkit.getOnlinePlayers()) {
                    for (String m : Locale.get().getStringList("vote-start")) {
                        m = m.replaceAll("%vote%", Cmd.voteName);
                        m = m.replaceAll("%message%", Data.getVoteMessage(Cmd.voteName));
                        m = m.replaceAll("%time%", String.valueOf(Cmd.realTimeLeft));
                        m = m.replaceAll("%yes%", String.valueOf(Cmd.voteYes));
                        m = m.replaceAll("%no%", String.valueOf(Cmd.voteNo));
                        allp.sendMessage(Locale.translate(m));
                    }
                }

                new MessageTimer().runTaskTimer(Vote.getInstance(), 0L, Config.getInt("notice-time") * 20L);

                new CdChecker().runTaskTimer(Vote.getInstance(), 0L, 20L);

                pcd.put(sender.getName(), Config.getInt("player-vote-cd"));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (pcd.get(sender.getName()) <= 0) {
                            pcd.remove(sender.getName());
                            cancel();
                        }
                        pcd.put(sender.getName(), pcd.get(sender.getName()) - 1);
                    }
                }.runTaskTimer(Vote.getInstance(), 0L, 20L);

                new voteCD().runTaskTimer(Vote.getInstance(), Config.getInt("vote-time") * 20L, 20L);

                return true;

            }

            if (args[0].equalsIgnoreCase("remove")) {

                if (!sender.hasPermission("vote.remove")) {
                    sender.sendMessage(Locale.getMessage("no-perm"));
                    return true;
                }

                if (!Data.isContainThisVote(args[1])) {
                    sender.sendMessage(Locale.getMessage("vote-not-exists").replaceAll("%vote%", args[1]));
                    return true;
                }

                sender.sendMessage(Locale.getMessage("remove-success").replaceAll("%vote%", args[1]));
                Data.removeVote(args[1]);
                if (voteName.equalsIgnoreCase(args[1])) {
                    for (Player allp: Bukkit.getOnlinePlayers()) {
                        allp.sendMessage(Locale.getMessage("vote-force-end").replaceAll("%player%", sender.getName()));
                    }
                    isVoting = false;
                    votePlayersAmount = 0;
                    voteNo = 0;
                    voteYes = 0;
                    voteName = null;
                    voteTimeLeft = 0;
                    vote().clear();
                }
                return true;
            }

        }

        if (args.length <= 2) {
            if (args[0].equalsIgnoreCase("yes")) {

                if (isVoting) {
                    if (vote().get(senderName) != null) {
                        sender.sendMessage(Locale.getMessage("vote-already"));
                        return true;
                    }

                    voteYes++;
                    votePlayersAmount++;
                    vote().put(senderName, "YES");
                    sender.sendMessage(Locale.getMessage("yes-success"));

                    String name = sender.getName();

                    if (args.length == 2) {
                        if (args[1].equalsIgnoreCase("-a")) {
                            name = Locale.getMessage("anonymous");
                        }
                    }

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage(Locale.getMessage("vote-yes").replaceAll("%player%", name).replaceAll("%amount%", String.valueOf(votePlayersAmount)));
                    }

                    return true;

                } else {
                    sender.sendMessage(Locale.getMessage("no-current-vote"));
                    return true;
                }

            }

            if (args[0].equalsIgnoreCase("no")) {

                if (isVoting) {
                    if (vote().get(senderName) != null) {
                        sender.sendMessage(Locale.getMessage("vote-already"));
                        return true;
                    }

                    voteNo++;
                    votePlayersAmount++;
                    vote().put(senderName, "NO");
                    sender.sendMessage(Locale.getMessage("no-success"));

                    String name = sender.getName();

                    if (args.length == 2) {
                        if (args[1].equalsIgnoreCase("-a")) {
                            name = Locale.getMessage("anonymous");
                        }
                    }

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage(Locale.getMessage("vote-no").replaceAll("%player%", name).replaceAll("%amount%", String.valueOf(votePlayersAmount)));
                    }

                    return true;

                } else {
                    sender.sendMessage(Locale.getMessage("no-current-vote"));
                    return true;
                }

            }
        }

        if (args.length == 4) {

            if (args[0].equalsIgnoreCase("create")) {

                if (!sender.hasPermission("vote.create")) {
                    sender.sendMessage(Locale.getMessage("no-perm"));
                    return true;
                }

                if (Data.isContainThisVote(args[1])) {
                    sender.sendMessage(Locale.getMessage("vote-already-exists").replaceAll("%vote%", args[1]));
                    return true;
                }

                String command = args[3].replaceAll("_", " ").replaceAll("%under%", "_");
                Data.addVote(args[1], args[2], command);
                sender.sendMessage(Locale.getMessage("create-success").replaceAll("%vote%", args[1]));
                return true;

            }

        }

        sender.sendMessage(Locale.getMessage("commands.help"));
        return true;

    }

}
