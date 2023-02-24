package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states;

import me.s4rtox.mmhunt.MMHunt;
import me.s4rtox.mmhunt.handlers.gamehandlers.GameManager;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mmhunt.util.Colorize;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

public class InitArenaState extends ArenaState {
    private final Arena arena;
    public InitArenaState(GameManager gameManager, Arena arena) {
        super(gameManager, arena);
        this.arena = arena;
    }

    @Override
    public void onEnable(MMHunt plugin) {
        super.onEnable(plugin);
        WorldBorder arenaBorder = arena.getWorld().getWorldBorder();
        arenaBorder.setCenter(arena.getCenterLocation());
        arenaBorder.setSize(arena.getWorldBorderRadius());
        arena.setRunner(null);
        //Code for startup, only executed once.
        arena.setArenaState(new WaitingArenaState(gameManager, arena));
    }

    @Override
    public void onPlayerJoin(Player player) {
        player.sendMessage(Colorize.format("&cError sending you to the game!, this arena has already started!"));
    }
    @Override
    public void onSpectatorJoin(Player player) {
        player.sendMessage(Colorize.format("&cError sending you to the game!, this arena hasn't started yet!"));
    }

    @Override
    public GameState getGameStateEnum() {
        return GameState.SETUP;
    }

    @Override
    public void setDefaultPlayersStates() {

    }

    @Override
    public void setDefaultPlayerState(Player player) {

    }
}
