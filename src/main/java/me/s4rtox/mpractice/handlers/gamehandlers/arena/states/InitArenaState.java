package me.s4rtox.mpractice.handlers.gamehandlers.arena.states;

import me.s4rtox.mpractice.MPractice;
import me.s4rtox.mpractice.handlers.gamehandlers.GameManager;
import me.s4rtox.mpractice.handlers.gamehandlers.arena.Arena;

public class InitArenaState extends ArenaState {
    public InitArenaState(GameManager gameManager, Arena arena) {
        super(gameManager, arena);
    }

    @Override
    public void onEnable(MPractice plugin) {
        super.onEnable(plugin);
        //Code for startup, only executed once.
        arena.setArenaState(new WaitingArenaState(gameManager, arena));
    }
}
