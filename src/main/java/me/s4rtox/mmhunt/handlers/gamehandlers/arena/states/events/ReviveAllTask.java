package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.events;

import lombok.Getter;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.ActiveArenaState;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class ReviveAllTask extends BukkitRunnable {
    private final Arena arena;
    private final ActiveArenaState state;
    private int timeUntilStart;
    private boolean firstRun = false;

    //Handles the countdown, when it finishes it starts the runnable given in the constructor
    public ReviveAllTask(ActiveArenaState state, int timeUntilStart) {
        this.arena = state.getArena();
        this.state = state;
        this.timeUntilStart = timeUntilStart;
    }


    @Override

    public void run() {
        if(!firstRun){
            firstRun = true;
            arena.sendPlayersMessage("&aEveryone is dead! Reviving everyone in " + timeUntilStart + "...");
        }


        if (timeUntilStart <= 0) {
            arena.sendAllSound(Sound.ENTITY_ENDER_DRAGON_GROWL,1,1);
            arena.sendAllTitle("&aREVIVED!", "", 20, 20 , 20);
            arena.doPlayerAction(state::revivePlayer);
            cancel();
            return;
        }

        if (timeUntilStart == 30 || timeUntilStart == 10 || timeUntilStart <= 5) {
            arena.sendPlayersMessage("&aReviving everyone in " + timeUntilStart + "...");
            arena.sendAllSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        }
        timeUntilStart--;
    }
}