package me.s4rtox.mpractice.handlers.gamehandlers.tasks;

import me.s4rtox.mpractice.handlers.gamehandlers.arena.Arena;
import org.bukkit.scheduler.BukkitRunnable;

public class ArenaStartingTask extends BukkitRunnable {
    private final Arena arena;
    private final Runnable onStart;
    private int timeUntilStart;

    public ArenaStartingTask(Arena arena, Runnable onStart, int timeUntilStart) {
        this.arena = arena;
        this.onStart = onStart;
        this.timeUntilStart = timeUntilStart;
    }


    @Override
    public void run() {
        if(timeUntilStart <= 0){
            onStart.run();
            cancel();
            return;
        }

        timeUntilStart--;
        if(timeUntilStart == 10 || timeUntilStart <=5){{
           arena.sendArenaMessage("&aStarting in " + timeUntilStart + "...");

        }}
    }
}
