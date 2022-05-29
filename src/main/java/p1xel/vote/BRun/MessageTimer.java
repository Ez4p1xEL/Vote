package p1xel.vote.BRun;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import p1xel.vote.Command.Cmd;
import p1xel.vote.Storage.Config;
import p1xel.vote.Storage.Data;
import p1xel.vote.Storage.Locale;

public class MessageTimer extends BukkitRunnable {

    @Override
    public void run() {

        if (!Cmd.isVoting) {
            this.cancel();
        }

        if (Cmd.voteTimeLeft == Config.getInt("vote-time")) {
            this.cancel();
        }

        if (Cmd.voteTimeLeft != 0) {
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
        }

        Cmd.messageTimerID = getTaskId();


    }

}
