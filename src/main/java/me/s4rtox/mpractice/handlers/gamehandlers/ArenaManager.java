package me.s4rtox.mpractice.handlers.gamehandlers;

import me.s4rtox.mpractice.handlers.gamehandlers.arena.Arena;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public class ArenaManager {

    private final List<Arena> arenalist;

    public ArenaManager(List<Arena> arenalist) {
        this.arenalist = arenalist;
    }

    public List<Arena> getArenas() {
        return arenalist;
    }

    public void addArena(Arena arena) {
        this.arenalist.add(arena);
    }

    public void deleteArena(Arena arena) {
        this.arenalist.remove(arena);
    }

    public Optional<Arena> findArena(String name) {
        if(arenalist.isEmpty() || name == null){
            return Optional.empty();
        }
        return arenalist.stream().filter(arena -> arena.name().equals(name)).findAny();
    }

    public Optional<Arena> findPlayerArena(Player player) {
        return arenalist.stream().filter(arena -> arena.isPlaying(player)).findAny();
    }

}
