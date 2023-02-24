package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states;

import me.s4rtox.mmhunt.handlers.gamehandlers.GameManager;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class DeathmatchArenaState extends PlayableArenaState{
    public DeathmatchArenaState(GameManager gameManager, Arena arena, List<UUID> alivePlayers) {
        super(gameManager, arena, alivePlayers);
    }

    @Override
    public GameState getGameStateEnum() {
        return null;
    }

    @Override
    public void setDefaultPlayersStates() {

    }

    @Override
    public void cancelArenaEvents() {

    }

    @Override
    public void setDefaultPlayerState(Player player) {

    }
}
