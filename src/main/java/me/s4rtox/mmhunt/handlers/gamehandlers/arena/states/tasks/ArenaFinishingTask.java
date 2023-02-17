package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.tasks;

import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ArenaFinishingTask extends BukkitRunnable {
    private final Arena arena;
    private final Runnable onStart;
    private int duration;

    //Also called VICTORY ROYALE
    public ArenaFinishingTask(Arena arena, Runnable onStart, int duration) {
        this.arena = arena;
        this.onStart = onStart;
        this.duration = duration;
    }


    @Override
    public void run() {
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
