package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.events.animations;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FireworksDownAnimation extends BukkitRunnable {
    private final Plugin plugin;
    private final Location animLocation;
    private final Runnable onEnd;
    private final int duration;
    private final List<Object[]> frames = new ArrayList<>();
    private int currentFrame = 0;
    private int totalFrames = 0;

    public FireworksDownAnimation(Plugin plugin, Location animLocation, int duration, Runnable onEnd){
        this.plugin = plugin;
        this.animLocation = animLocation.clone();
        this.duration = duration;
        this.onEnd = onEnd;
        frames.add(new Object[] { Color.RED, Color.RED, FireworkEffect.Type.BALL_LARGE });
        frames.add(new Object[] { Color.BLUE, Color.PURPLE, FireworkEffect.Type.BALL });
        frames.add(new Object[] { Color.GREEN, Color.YELLOW, FireworkEffect.Type.STAR });
        frames.add(new Object[] { Color.ORANGE, Color.YELLOW, FireworkEffect.Type.BURST });

    }
    @Override
    public void run() {
        if(totalFrames > duration){
            Bukkit.getScheduler().runTask(plugin,onEnd);
            this.stop();
        }
        if (currentFrame >= frames.size()) {
            // Reset the animation when it reaches the end
            currentFrame = 0;
        }

        Object[] frame = frames.get(currentFrame);
        Color primaryColor = (Color) frame[0];
        Color secondaryColor = (Color) frame[1];
        FireworkEffect.Type type = (FireworkEffect.Type) frame[2];
        spawnFirework(animLocation, primaryColor, secondaryColor, type);
        animLocation.add(0,-1,0);
        currentFrame++;
        totalFrames++;
    }

    private void spawnFirework(Location location, Color primaryColor, Color secondaryColor, FireworkEffect.Type type) {
        World world = location.getWorld();

        Firework firework = (Firework) world.spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();

        // Set the firework's colors and type
        FireworkEffect effect = FireworkEffect.builder()
                .withColor(primaryColor, secondaryColor)
                .with(type)
                .build();
        meta.addEffect(effect);

        // Set the firework's power and randomize it
        meta.setPower(0);
        firework.setFireworkMeta(meta);
        firework.detonate();
        // Spawn the firewor
    }

    public void start() {
        // Schedule the animation to run every 5 ticks (0.25 seconds)
        this.runTaskTimer(plugin, 0, 5);
    }

    public void stop() {
        // Stop the animation
        try{
            this.cancel();
        }catch (IllegalStateException ignored){
        }

    }
}
