package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states;

import me.s4rtox.mmhunt.handlers.gamehandlers.GameManager;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mmhunt.util.Colorize;
import org.bukkit.entity.Player;

public class RunnerOutArenaState extends ArenaState{
    protected RunnerOutArenaState(GameManager gameManager, Arena arena) {
        super(gameManager, arena);

    }

    @Override
    public void onPlayerJoin(Player player) {
        super.onPlayerJoin(player);
        arena.sendAllMessage("&7[&a+&7] &f" + player.getDisplayName());
        setDefaultPlayerState(player);
        arena.updateAllScoreboardsLine(1, "&fPlayers: &a" + arena.getCurrentPlayers() + "&7/&a");
        player.teleport(arena.getWaitingLobby());
    }

    @Override
    public void onSpectatorJoin(Player player) {
        player.sendMessage(Colorize.format("&cError sending you to the game!, this arena hasn't started yet!"));
    }

    @Override
    public GameState getGameStateEnum() {
        return GameState.ACTIVE;
    }

    @Override
    public void setDefaultPlayersStates() {

    }

    @Override
    public void setDefaultPlayerState(Player player) {

    }
}
