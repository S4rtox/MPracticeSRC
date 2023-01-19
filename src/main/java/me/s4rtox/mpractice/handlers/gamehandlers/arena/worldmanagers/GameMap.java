package me.s4rtox.mpractice.handlers.gamehandlers.arena.worldmanagers;

import com.grinderwolf.swm.api.world.SlimeWorld;

public interface GameMap {
    boolean load();
    void unload();
    boolean restoreFromSource();

    boolean isLoaded();
    SlimeWorld getWorld();
}
