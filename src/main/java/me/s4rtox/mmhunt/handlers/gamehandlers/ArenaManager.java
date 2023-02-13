package me.s4rtox.mmhunt.handlers.gamehandlers;

import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import org.bukkit.entity.Player;

import java.util.*;

public class ArenaManager {

    private final List<Arena> arenalist;
    private final Map<UUID, Arena> playerArena = new HashMap<>();

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

    public void setPlayerArena(Player player, Arena arena){
        playerArena.put(player.getUniqueId(),arena);
    }

    public void removeFromArena(Player player){
        playerArena.remove(player.getUniqueId());
    }
    public Optional<Arena> findArena(String name) {
        if(arenalist.isEmpty() || name == null){
            return Optional.empty();
        }
        return arenalist.stream().filter(arena -> arena.getName().equals(name)).findAny();
    }

    public Optional<Arena> findPlayerArena(Player player) {
        return Optional.ofNullable(playerArena.get(player.getUniqueId()));
        // return arenalist.stream().filter(arena -> arena.isPlaying(player)).findAny();
    }

    public Optional<Arena> findSpectatingArena(Player player){
        return arenalist.stream().filter(arena -> arena.isSpectating(player)).findAny();
    }

    public boolean isInArena(Player player){
        return playerArena.containsKey(player.getUniqueId());
    }

    public void sortArenas(){
        arenalist.sort(Comparator.comparing((Arena a) -> a.getArenaState().getGameStateEnum()).reversed().thenComparingInt((Arena a) -> a.getHunters().size()).reversed());
    }

}
