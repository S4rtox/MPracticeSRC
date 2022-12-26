package me.s4rtox.mpractice.handlers.gamehandlers;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessageManager {
    public Map<String, ArrayList<Player>> channels = new HashMap<>();
    public Map<Player, String> playerChannel = new HashMap<>();

    public void joinChannel(Player player, String messageChannel){
        if(playerChannel.get(player) != null){
            String previusChannel = playerChannel.get(player);
            leaveChannel(player, previusChannel);
        }
        ArrayList<Player> playersInChannel = channels.get(messageChannel);
        if(playersInChannel == null){
            playersInChannel = new ArrayList<>();
        }
        if(!playersInChannel.contains(player)){
            playersInChannel.add(player);
            channels.put(messageChannel,playersInChannel);
            playerChannel.put(player, messageChannel);
        }
    }

    public void leaveChannel(Player player, String messageChannel){
        ArrayList<Player> playersInChannel = channels.get(messageChannel);
        playersInChannel.remove(player);
        channels.put(messageChannel,playersInChannel);
        playerChannel.remove(player);
    }

    //return the list of the channel wich a player is in
    public ArrayList<Player> getChannel(Player player){
        String channelName = playerChannel.get(player);
        return channels.get(channelName);
    }
}
