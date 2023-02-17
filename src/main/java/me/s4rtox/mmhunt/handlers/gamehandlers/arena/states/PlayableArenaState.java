package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states;

import me.s4rtox.mmhunt.handlers.gamehandlers.GameManager;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mmhunt.util.Colorize;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public abstract class PlayableArenaState extends ArenaState{
    protected PlayableArenaState(GameManager gameManager, Arena arena) {
        super(gameManager, arena);
    }

    @Override
    public void setDefaultPlayerState(Player player) {
        arena.doHunterAction(p -> p.setPlayerListName(Colorize.format(ActiveArenaState.HUNTER + p.getName())));
    }

    //TODO: buscar como juntar los 3 tipos de tipos activos
    @EventHandler
    private void activeChatFormat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (arena.isInGame(player)) {
            event.getRecipients().clear();
            arena.doAllAction(player1 -> event.getRecipients().add(player1));
            if(arena.isSpectating(player)){
                event.setFormat(Colorize.format(ActiveArenaState.SPECTATOR + player.getDisplayName() + "&7:&7 ") + event.getMessage());
            }else if(!alivePlayers.contains(player.getUniqueId())){
                event.setFormat(Colorize.format(ActiveArenaState.DEAD + player.getDisplayName() + "&7:&7 ") + event.getMessage());

            }else if(arena.isRunner(player)){
                event.setFormat(Colorize.format(ActiveArenaState.RUNNER + player.getDisplayName() + "&7:&f ") + event.getMessage());
            }else{
                event.setFormat(Colorize.format(ActiveArenaState.HUNTER + player.getDisplayName() + "&7: ") + event.getMessage());
            }
        }
    }
}
