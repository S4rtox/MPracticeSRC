package me.s4rtox.mpractice.handlers.gamehandlers.tasks;

import me.s4rtox.mpractice.handlers.gamehandlers.arena.Arena;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class ArenaStartingTask extends BukkitRunnable {
    private final Arena arena;
    private final Runnable onStart;
    private int timeUntilStart;

    //Handles the countdown, when it finishes it starts the runnable given in the constructor
    public ArenaStartingTask(Arena arena, Runnable onStart, int timeUntilStart) {
        this.arena = arena;
        this.onStart = onStart;
        this.timeUntilStart = timeUntilStart;
    }


    @Override
    public void run() {
        if (timeUntilStart <= 0) {
            onStart.run();
            cancel();
            return;
        }


        if (timeUntilStart == 30 || timeUntilStart == 10 || timeUntilStart <= 5) {
            arena.sendPlayersMessage("&aStarting in " + timeUntilStart + "...");
            arena.sendPlayersSound(Sound.NOTE_PLING, 1, 1);
        }
        timeUntilStart--;
    }
}
