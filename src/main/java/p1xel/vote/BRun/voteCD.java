package p1xel.vote.BRun;

import org.bukkit.scheduler.BukkitRunnable;
import p1xel.vote.Command.Cmd;
import p1xel.vote.Storage.Config;

public class voteCD extends BukkitRunnable {

    @Override
    public void run() {

        if (Cmd.voteCD <= 0) {
            Cmd.voteCD = Config.getInt("vote-cd");
            Cmd.canVote = true;
            cancel();
        }

        Cmd.voteCDID = getTaskId();
        Cmd.voteCD--;





    }

}
