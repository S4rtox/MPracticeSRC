package me.s4rtox.mpractice.handlers.gamehandlers;

import java.util.List;

public class ArenaManager {

    private final List<Arena> arenalist;

    public ArenaManager(List<Arena> arenalist){
        this.arenalist = arenalist;
    }

    public List<Arena> getArenas(){
        return arenalist;
    }
    public void addArena(Arena arena){
        this.arenalist.add(arena);
    }

    public void removeArena(Arena arena){
        this.arenalist.remove(arena);
    }

}
