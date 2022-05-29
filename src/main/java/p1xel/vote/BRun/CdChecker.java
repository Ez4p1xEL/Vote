package p1xel.vote.BRun;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import p1xel.vote.Command.Cmd;
import p1xel.vote.Storage.Config;
import p1xel.vote.Storage.Data;
import p1xel.vote.Storage.Locale;
import p1xel.vote.Vote;

public class CdChecker extends BukkitRunnable {

    @Override
    public void run() {

        if (!Cmd.isVoting) {
            cancel();
        }
        if (Cmd.voteTimeLeft >= Config.getInt("vote-time")) {
            Cmd.isVoting = false;
            Cmd.voteTimeLeft = 0;
            String m = Locale.getMessage("vote-end").replaceAll("%vote%", Cmd.voteName).replaceAll("%yes%", String.valueOf(Cmd.voteYes)).replaceAll("%no%", String.valueOf(Cmd.voteNo));
            for (Player allp : Bukkit.getOnlinePlayers()) {
                allp.sendMessage(m);
            }
            if (Cmd.voteYes > Cmd.voteNo) {
                for (String cmds : Data.getCommands(Cmd.voteName)) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmds);
                }
                for (Player allp : Bukkit.getOnlinePlayers()) {
                    allp.sendMessage(Locale.getMessage("vote-result-yes").replaceAll("%vote%", Cmd.voteName));
                }
            } else {
                for (Player allp : Bukkit.getOnlinePlayers()) {
                    allp.sendMessage(Locale.getMessage("vote-result-no").replaceAll("%vote%", Cmd.voteName));
                }
            }


            Cmd.vote().clear();
            Cmd.voteYes = 0;
            Cmd.voteNo = 0;
            Cmd.votePlayersAmount = 0;
            cancel();
        } else {
            Cmd.voteTimeLeft++;
            Cmd.realTimeLeft--;
        }

        Cmd.cdCheckerID = getTaskId();
    }

}
