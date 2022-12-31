package me.s4rtox.mpractice.handlers.gamehandlers.tasks;

import me.s4rtox.mpractice.handlers.gamehandlers.arena.Arena;
import org.bukkit.scheduler.BukkitRunnable;

public class ActiveArenaEvents extends BukkitRunnable {
    private final Arena arena;
    private final Runnable onFinish;
    private int matchDuration;
    private final int refillIntervals;
    private final int refillAmounts;
    private int currentRefill = 0;
    private int activeTime = 0;
    private boolean firstRun = true;

    public ActiveArenaEvents(Arena arena, Runnable onFinish, int matchDuration, int refillIntervals) {
        this.arena = arena;
        this.onFinish = onFinish;
        this.matchDuration = matchDuration;
        this.refillIntervals = refillIntervals;
        this.refillAmounts = matchDuration / refillIntervals;
    }

    public ActiveArenaEvents(Arena arena, Runnable onFinish, int matchDuration, int refillIntervals, int refillAmounts) {
        this.arena = arena;
        this.onFinish = onFinish;
        this.matchDuration = matchDuration;
        this.refillIntervals = refillIntervals;
        this.refillAmounts = refillAmounts;
    }

    @Override
    public void run() {
        if (firstRun) {
            //Code to be executed only ONCE
            firstRun = false;
        }else{
            //Code to be executed EVERY SECOND except on start
            //Condition to be executed every time the interval is met
            if((activeTime % refillIntervals) == 0 && currentRefill != refillAmounts){
                arena.fillChests();
                arena.sendArenaTitle("&a&lChests refilled!", "", 1, 1, 1);
                arena.sendPlayersMessage("&a&lChest refilled!");
                currentRefill++;
            }
        }
        //Code to be executed every second
        if (matchDuration <= 0) {
            onFinish.run();
            cancel();
            return;
        }
        //Messages to be broadcasted each x seconds
        if (matchDuration == 10 || matchDuration <= 5) {
            arena.sendPlayersMessage("&aGame ending in " + matchDuration + "...");
        }
        matchDuration--;
        activeTime++;
    }
}
