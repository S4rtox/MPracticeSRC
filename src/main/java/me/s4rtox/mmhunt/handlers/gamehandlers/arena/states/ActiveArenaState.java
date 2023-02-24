package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states;

import lombok.Getter;
import me.s4rtox.mmhunt.MMHunt;
import me.s4rtox.mmhunt.config.ConfigManager;
import me.s4rtox.mmhunt.handlers.gamehandlers.GameManager;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.tasks.ActiveArenaEvents;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.tasks.ReviveAllTask;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;
import java.util.UUID;

public class ActiveArenaState extends PlayableArenaState {
    private ActiveArenaEvents arenaEvents;

    public ActiveArenaState(GameManager gameManager, Arena arena, List<UUID> alivePlayers) {
        super(gameManager, arena, alivePlayers);
    }

    //TODO: arreglar que al morir no cancela el arenaevents
    //TODO: arreglar que al acabar no manda todos al lobby
    //TODO: Arreglar que todos los cofres desaparecen

    @Override
    public void onEnable(MMHunt plugin) {
        super.onEnable(plugin);
        // Stuff on start //
        arena.firstFillChests();
        setDefaultPlayersStates();
        // EVENTS //
        arenaEvents = new ActiveArenaEvents(this,gameManager);
        arenaEvents.runTaskTimer(gameManager.getPlugin(),0,20);;
    }


    public void cancelArenaEvents(){
        arenaEvents.cancel();
    }

    @Override
    public void setDefaultPlayersStates() {
        super.setDefaultPlayersStates();
        arena.updateAllScoreboards("&e&lMMHunt",
                "",
                "&fAlive: &a" + alivePlayers.size(),
                "",
                "&fNext Event:",
                "",
                "&fMatch duration: &a",
                "",
                "&f" + ConfigManager.serverIP
        );
    }

    @Override
    public void onPlayerJoin(Player player) {
        super.onPlayerJoin(player);
        player.teleport(arena.getSpawnLocation());
    }
}
