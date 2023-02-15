package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.tasks;

import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.ActiveArenaState;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class ActiveArenaEvents extends BukkitRunnable {
    private final ActiveArenaState state;
    private final Arena arena;
    private int matchDuration = 0;

    public ActiveArenaEvents(ActiveArenaState activeArenaState) {
        this.state = activeArenaState;
        this.arena = activeArenaState.getArena();
    }

    @Override
    public void run() {

        //Code to be executed every second
        arena.updateAllScoreboardsLine(4, "&fIn: &a" + (matchDuration < 60 ? matchDuration : matchDuration / 60 + ":" + matchDuration % 60));

        //Messages to be broadcasted each x seconds

        matchDuration++;
    }
}
