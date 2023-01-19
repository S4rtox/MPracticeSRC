package me.s4rtox.mpractice.handlers.gamehandlers.tasks;

import me.s4rtox.mpractice.handlers.gamehandlers.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ArenaFinishingTask extends BukkitRunnable {
    private final Arena arena;
    private final Runnable onStart;
    private int duration;
    private final Player winner;
    private boolean firstRun = true;

    //Also called VICTORY ROYALE
    public ArenaFinishingTask(Arena arena, Runnable onStart, int duration, Player winner) {
        this.arena = arena;
        this.onStart = onStart;
        this.duration = duration;
        this.winner = winner;
    }


    @Override
    public void run() {
        if (winner != null) {
            //It hurts me to deal with this this way but I think better than making another runnable.
            if (firstRun) {
                //Code to be executed only ONCE with a winner
                arena.sendAllMessage("&a&l" + winner.getName() + "&6&l has won!!");
                firstRun = false;
            }
            //Code to be executed EVERY SECOND with a winner
        } else {
            if (firstRun) {
                //Code to be executed only ONCE without a winner(Tie)
                firstRun = false;
            }
            //Code to be executed EVERY SECOND without a winner(Tie)
        }
        arena.updateAllScoreboardsLine(3,  "&fGoing back in: &a" + duration);
        //Code to be executed allways every second
        if (duration <= 0) {
            onStart.run();
            cancel();
            return;
        }

        if (duration == 10 || duration <= 5) {
            arena.sendPlayersMessage("&aSending to lobby in " + duration + "...");
        }
        duration--;
    }
}
