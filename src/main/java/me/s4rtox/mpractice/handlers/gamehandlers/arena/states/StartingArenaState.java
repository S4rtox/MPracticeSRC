package me.s4rtox.mpractice.handlers.gamehandlers.arena.states;

import lombok.Getter;
import me.s4rtox.mpractice.MPractice;
import me.s4rtox.mpractice.handlers.gamehandlers.GameManager;
import me.s4rtox.mpractice.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mpractice.handlers.gamehandlers.tasks.ArenaStartingTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class StartingArenaState extends ArenaState {
    @Getter
    private ArenaStartingTask arenaStartingTask;

    public StartingArenaState(GameManager gameManager, Arena arena) {
        super(gameManager, arena);
    }

    @Override
    public void onEnable(MPractice plugin) {
        super.onEnable(plugin);
        arenaStartingTask = new ArenaStartingTask(arena, () -> arena.setArenaState(new ActiveArenaState(gameManager, arena)), 10);
        arenaStartingTask.runTaskTimer(plugin, 0, 20);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (arena.isPlaying(event.getPlayer())) {
            arena.sendToLobby(event.getPlayer());
        }
    }

    @EventHandler
    private void onWorldChange(PlayerChangedWorldEvent event) {
        if (arena.isPlaying(event.getPlayer())) {
            if (!event.getPlayer().getWorld().getName().equals(arena.world().getName())) {
                arena.removePlayer(event.getPlayer());
            }
        }
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (arena.isPlaying(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (arena.isPlaying(player)) {
            event.setCancelled(true);
            player.sendMessage("Starting state");
        }

    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (arena.isPlaying(player)) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    private void onHungerChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (arena.isPlaying(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void freezePlayersInplace(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!arena.isPlaying(player)) return;
        if (event.getFrom().getX() == event.getTo().getX() &&
                event.getFrom().getY() == event.getTo().getY() &&
                event.getFrom().getZ() == event.getTo().getZ()) return;
        event.setTo(event.getFrom());
    }
}
