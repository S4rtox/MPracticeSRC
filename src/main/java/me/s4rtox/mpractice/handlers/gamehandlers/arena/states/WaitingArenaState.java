package me.s4rtox.mpractice.handlers.gamehandlers.arena.states;

import me.s4rtox.mpractice.handlers.gamehandlers.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class WaitingArenaState extends ArenaState {

    private final Arena arena;

    public WaitingArenaState(Arena arena) {
        this.arena = arena;
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event){
         if(arena.isPlaying(event.getPlayer())){
             if(arena.isPlaying(event.getPlayer())){
                 arena.removePlayer(event.getPlayer());
             }
         }
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            if(arena.isPlaying(player)){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        if(arena.isPlaying(player)){
            event.setCancelled(true);
        }

    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        if(arena.isPlaying(player)){
            event.setCancelled(true);
        }

    }


}
