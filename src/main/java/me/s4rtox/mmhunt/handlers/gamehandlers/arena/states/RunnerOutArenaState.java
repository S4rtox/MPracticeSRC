package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states;

import me.s4rtox.mmhunt.handlers.gamehandlers.GameManager;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.events.RunnerOutOfCageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class RunnerOutArenaState extends PlayableArenaState{
    private RunnerOutOfCageEvent event;
    public RunnerOutArenaState(GameManager gameManager, Arena arena) {
        super(gameManager, arena);
        arena.doHunterAction(player -> {
            player.getInventory().clear();
            player.getActivePotionEffects().clear();
            player.teleport(arena.getSpawnLocation());
        });
        event = new RunnerOutOfCageEvent(gameManager,this,20);
        event.runTaskTimer(gameManager.getPlugin(),0,20);
    }

    @Override
    public void onPlayerJoin(Player player) {
        super.onPlayerJoin(player);
    }

    @Override
    public void cancelArenaEvents() {
        event.cancel();
    }

    @EventHandler
    public void freezePlayersInplace(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!arena.isPlaying(player)) return;
        if(arena.isRunner(player)) return;
        if (event.getFrom().getX() == event.getTo().getX() &&
                event.getFrom().getY() == event.getTo().getY() &&
                event.getFrom().getZ() == event.getTo().getZ()) return;
        event.setTo(event.getFrom());
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (arena.isPlaying(player)) {
                if(arena.isRunner(player)) return;
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (arena.isPlaying(player)) {
            if(arena.isRunner(player)) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (arena.isPlaying(player)) {
            if(arena.isRunner(player)) return;
            event.setCancelled(true);
        }

    }

    @EventHandler
    private void onHungerChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (arena.isPlaying(player)) {
                if(arena.isRunner(player)) return;
                event.setCancelled(true);
            }
        }
    }

}
