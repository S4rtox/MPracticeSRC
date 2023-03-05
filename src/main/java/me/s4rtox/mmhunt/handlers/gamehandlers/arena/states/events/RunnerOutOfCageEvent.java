package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.events;

import lombok.Getter;
import me.s4rtox.mmhunt.handlers.gamehandlers.GameManager;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.ActiveArenaState;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.RunnerOutArenaState;
import me.s4rtox.mmhunt.util.Colorize;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class RunnerOutOfCageEvent extends BukkitRunnable {
    private final Arena arena;
    private final RunnerOutArenaState state;
    private final GameManager gameManager;
    private int timeUntilStart;

    //Handles the countdown, when it finishes it starts the runnable given in the constructor
    public RunnerOutOfCageEvent(GameManager gameManager,RunnerOutArenaState state, int timeUntilStart) {
        this.state = state;
        this.gameManager = gameManager;
        this.arena = state.getArena();
        this.timeUntilStart = timeUntilStart;
    }


    @Override
    public void run() {
        if (timeUntilStart <= 0) {
            arena.sendAllSound(Sound.ENTITY_ENDER_DRAGON_GROWL,1,1);
            arena.sendAllTitle(Colorize.format("&HUNTERS RELEASED!"), "", 20, 20 , 20);
            arena.setArenaState(new ActiveArenaState(gameManager,arena,state.getAlivePlayers()));
            cancel();
            return;
        }

        if (timeUntilStart == 30 || timeUntilStart == 10 || timeUntilStart <= 5) {
            arena.sendPlayersMessage("&aReleasing hunters in " + timeUntilStart + "...");
            arena.sendAllSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            if(timeUntilStart <= 5){
                arena.doHunterAction(player -> {
                    player.sendTitle(Colorize.format("&a" + timeUntilStart + "..."), "", 20, 20 , 20);
                });
            }
        }
        timeUntilStart--;
    }
}
