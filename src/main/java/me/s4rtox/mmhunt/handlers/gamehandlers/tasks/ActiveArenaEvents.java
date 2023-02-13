package me.s4rtox.mmhunt.handlers.gamehandlers.tasks;

import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class ActiveArenaEvents extends BukkitRunnable {
    private final Arena arena;
    private final Runnable onFinish;
    private int matchDuration;
    private final int refillIntervals;
    private final int refillAmounts;
    private int currentRefill = 0;
    private int timeUntilRefill;
    private boolean firstRun = true;
    private boolean doRefills = true;

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
        this.timeUntilRefill = refillIntervals;
        if(refillAmounts == 0){
            doRefills = false;
        }
    }

    @Override
    public void run() {
        if (matchDuration <= 0) {
            onFinish.run();
            cancel();
            return;
        }
        if (firstRun) {
            //Code to be executed only ONCE
            firstRun = false;
        }else{
            //Code to be executed EVERY SECOND except on start
            //Condition to be executed every time the interval is met
            if(timeUntilRefill == 0 && currentRefill != refillAmounts){
                arena.fillChests();
                arena.sendAllTitle("&a&lChests refilled!", "", 1, 1, 1);
                arena.sendPlayersMessage("&a&lChest refilled!");
                arena.sendPlayersSound(Sound.BLOCK_CHEST_CLOSE,1,1);
                timeUntilRefill = refillIntervals;
                currentRefill++;
                if(currentRefill == refillAmounts){
                    timeUntilRefill = -1;
                }
            }
        }
        //Code to be executed every second
        arena.updateAllScoreboardsLine(3,   "&fRefill in: &a" + ((timeUntilRefill < 0) ? "&7-" : (timeUntilRefill/60) + ":" + (timeUntilRefill%60)));

        //Messages to be broadcasted each x seconds
        if (matchDuration == 10 || matchDuration <= 5) {
            arena.sendAllMessage("&cGame ending in &6" + matchDuration + "&c...");
            arena.sendPlayersSound(Sound.UI_BUTTON_CLICK,1,1);
        }

        if(timeUntilRefill == 30 || timeUntilRefill == 60){
            arena.sendAllMessage("&aRefilling chests in " + timeUntilRefill);
            arena.sendPlayersSound(Sound.UI_BUTTON_CLICK,1,1);
        }
        matchDuration--;
        if(timeUntilRefill >= 0){
            timeUntilRefill--;
        }
    }
}
