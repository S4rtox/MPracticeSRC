package me.s4rtox.mpractice.util;

import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

public class Cooldown {
    private final HashMap<UUID, Double> cooldown;

    public Cooldown(){
        this.cooldown = new HashMap<>();
    }

    public boolean PlayerInCooldown(Player player, Double timeSeconds){
        if(this.cooldown.containsKey(player.getUniqueId())){
            if(this.cooldown.get(player.getUniqueId()) > System.currentTimeMillis()){
                return true;
            }
        }
        DecimalFormat newFormat = new DecimalFormat("#.##");
        timeSeconds = Double.valueOf(newFormat.format(timeSeconds));
        this.cooldown.put(player.getUniqueId(), System.currentTimeMillis() + ( timeSeconds * 1000));
        return false;
    }


    public double getCooldown(Player player){
        return (this.cooldown.get(player.getUniqueId()) - System.currentTimeMillis())/1000f;
    }
}
