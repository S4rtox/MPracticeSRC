package me.s4rtox.mpractice.handlers.gamehandlers.arena.states;

import lombok.Getter;
import me.s4rtox.mpractice.MPractice;
import me.s4rtox.mpractice.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mpractice.handlers.gamehandlers.tasks.ArenaStartingTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class StartingArenaState extends ArenaState {
    private final Arena arena;
    @Getter
    private ArenaStartingTask arenaStartingTask;

    public StartingArenaState(Arena arena) {
        this.arena = arena;
    }

    @Override
    public void onEnable(MPractice plugin) {
        super.onEnable(plugin);
         arenaStartingTask = new ArenaStartingTask(arena, () -> arena.arenaState(new ActiveArenaState(arena)), 10);
        arenaStartingTask.runTaskTimer(plugin,0, 20);
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
