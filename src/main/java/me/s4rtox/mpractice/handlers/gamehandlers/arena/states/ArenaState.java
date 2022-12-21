package me.s4rtox.mpractice.handlers.gamehandlers.arena.states;

import me.s4rtox.mpractice.MPractice;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class ArenaState implements Listener {

    public void onEnable(MPractice plugin) {
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    public void onDisable(MPractice plugin) {
        HandlerList.unregisterAll(this);
    }

}
