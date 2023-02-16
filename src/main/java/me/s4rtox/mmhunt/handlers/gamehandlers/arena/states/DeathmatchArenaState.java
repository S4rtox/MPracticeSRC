package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states;

import me.s4rtox.mmhunt.handlers.gamehandlers.GameManager;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import org.bukkit.entity.Player;

public class DeathmatchArenaState extends ArenaState{
    public DeathmatchArenaState(GameManager gameManager, Arena arena) {
        super(gameManager, arena);
    }

    @Override
    public GameState getGameStateEnum() {
        return null;
    }

    @Override
    public void setDefaultPlayersStates() {

    }

    @Override
    public void setDefaultPlayerState(Player player) {

    }
}
