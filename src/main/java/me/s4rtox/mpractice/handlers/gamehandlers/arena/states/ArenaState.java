package me.s4rtox.mpractice.handlers.gamehandlers.arena.states;

import me.s4rtox.mpractice.MPractice;
import me.s4rtox.mpractice.handlers.gamehandlers.GameManager;
import me.s4rtox.mpractice.handlers.gamehandlers.arena.Arena;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class ArenaState implements Listener {
    protected final GameManager gameManager;
    protected final Arena arena;

    protected ArenaState(GameManager gameManager, Arena arena) {
        this.gameManager = gameManager;
        this.arena = arena;
    }

    public void onEnable(MPractice plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void onDisable(MPractice plugin) {
        HandlerList.unregisterAll(this);
    }


}
