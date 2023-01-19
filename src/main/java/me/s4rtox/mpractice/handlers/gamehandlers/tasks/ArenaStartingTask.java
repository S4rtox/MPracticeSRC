package me.s4rtox.mpractice.handlers.gamehandlers.tasks;

import lombok.Getter;
import me.s4rtox.mpractice.handlers.gamehandlers.arena.Arena;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class ArenaStartingTask extends BukkitRunnable {
    private final Arena arena;
    @Getter
    private final Runnable onStart;
    private int timeUntilStart;
    private boolean firstRun = false;

    //Handles the countdown, when it finishes it starts the runnable given in the constructor
    public ArenaStartingTask(Arena arena, Runnable onStart, int timeUntilStart) {
        this.arena = arena;
        this.onStart = onStart;
        this.timeUntilStart = timeUntilStart;
    }


    @Override
    public void run() {
        if(!firstRun){
            arena.updateAllScoreboardsLine(5, "&fStatus: &6&lSTARTING!");
            firstRun = true;
        }
        if (timeUntilStart <= 0) {
            arena.sendAllSound(Sound.ENDERDRAGON_GROWL,1,1);
            arena.sendAllTitle("&aSTARTING!", "", 1, 1 , 1);
            onStart.run();
            cancel();
            return;
        }
        arena.updateAllScoreboardsLine(3, "&fStarting in: &a" + timeUntilStart);

        if (timeUntilStart == 30 || timeUntilStart == 10 || timeUntilStart <= 5) {
            arena.sendPlayersMessage("&aStarting in " + timeUntilStart + "...");
            arena.sendAllSound(Sound.NOTE_PLING, 1, 1);
            if(timeUntilStart <= 5){
                arena.sendAllTitle("&a" + timeUntilStart + "...", "", 1, 1 , 1);
            }
        }
        timeUntilStart--;
    }
}
