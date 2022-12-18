package me.s4rtox.mpractice.handlers.gamehandlers;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.s4rtox.mpractice.util.Colorize;
import me.s4rtox.mpractice.util.ItemBuilder;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetupWizardManager implements Listener {
    private final GameManager gameManager;
    public Map<UUID, TemporaryArena> inWizard = new HashMap<>();

   public SetupWizardManager(GameManager gameManager){
       this.gameManager = gameManager;
   }

    public void startWizard(Player player, Arena arena){
        TemporaryArena temporaryArena;
        if(arena == null){
            temporaryArena = new TemporaryArena();
        }else{
            temporaryArena = new TemporaryArena(arena);
        }
        inWizard.put(player.getUniqueId(), temporaryArena);
        player.setGameMode(GameMode.CREATIVE);
        player.getInventory().clear();

        Inventory inventory = player.getInventory();

        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.SIGN), "&5SetName &7{Right Click}", true, "SetArenaName", "Right click to set name"));
        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.PAPER), "&6SetDisplayName &7{Right Click}", true, "SetArenaDisplayName", "Right click to set spawn"));
        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.EMPTY_MAP), "&2SetArenaCenter &7{Right Click}", true, "SetArenaCenter", "Right click to set spawn"));
        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.DIAMOND_AXE), "&6SetArenaCorners &7{Left Click | Right Click}", true, "SetArenaCorner", "Right click to set spawn"));
        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.SKULL_ITEM), "&bSetSpectatorSpawns &7{Right Click}", true, "SetSpectatorSpawn", "Right click to set spawn"));
        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.ARROW), "&4SetSpawns &7{Left Click | Right Click}", true, "SetArenaSpawns", "Right click to set spawn"));
        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.STICK), "&eSetIslandChests &7{Left Click | Right Click}", true, "SetIslandChests", "Right click to set spawn"));
        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.BLAZE_ROD), "&eSetMiddleChests &7{Left Click | Right Click}", true, "SetMiddleChests", "Right click to set spawn"));
        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.EMERALD_BLOCK), "&aSaveArena &7{Right Click}", true, "SaveArena", "Right click to save arena"));
        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.BARRIER), "&cCancel &7{Right Click}", true, "CancelArena", "Right click to set cancel the setup"));
        
    }

    public void stopWizard(Player player){
        inWizard.remove(player.getUniqueId());
        player.setGameMode(GameMode.CREATIVE);
        player.getInventory().clear();

    }

    public boolean inWizard(Player player){
        return inWizard.containsKey(player.getUniqueId());
    }

    //TODO: Finish all the arena setters.
    @EventHandler
    public void onSetupItemInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(!inWizard(player)) return;
        if(!event.hasItem()) return;
        if(!event.getItem().hasItemMeta()) return;

        NBTItem itemFlag = new NBTItem(event.getItem());

        TemporaryArena arena = inWizard.get(player.getUniqueId());


        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
            // Arena name setter 
            if (itemFlag.getBoolean("SetArenaName")){
                event.setCancelled(true);
             new AnvilGUI.Builder()
                        .title("Enter the arena name")
                        .itemLeft(new ItemStack(Material.PAPER))
                        .plugin(gameManager.plugin())
                        .onComplete((player1, text) -> {
                            if(gameManager.arenaManager().getArenas().stream().anyMatch(allArenas -> allArenas.name().equalsIgnoreCase(text))){
                                return AnvilGUI.Response.text(Colorize.format("&cAn arena with that name already exists!"));
                            }
                            arena.displayName(text);
                            player.sendMessage(Colorize.format("&aSet the arena name to: &e") + arena.name());
                            return AnvilGUI.Response.close();
                        }).open(player);
            // Arena displayNameSetter        
            }else if (itemFlag.getBoolean("SetArenaDisplayName")){
                event.setCancelled(true);
                new AnvilGUI.Builder()
                        .title("Enter the arena displayname")
                        .itemLeft(new ItemStack(Material.PAPER))
                        .plugin(gameManager.plugin())
                        .onComplete((player1, text) -> {
                            arena.displayName(text);
                            player.sendMessage(Colorize.format("&aSet the arena name to: &e" + arena.displayName()));
                            return AnvilGUI.Response.close();
                        }).open(player);
            // Arena centerLocation setters                     
            }else if(itemFlag.getBoolean("SetArenaCenter")){
                event.setCancelled(true);
                if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
                    arena.centerLocation(event.getClickedBlock().getLocation());
                }else{
                    arena.centerLocation(player.getLocation());
                }
                player.sendMessage(Colorize.format("&aSet the &ecenter &afor the arena"));
            
            // Arena Corner1 setter 
            }else if(itemFlag.getBoolean("SetArenaCorner")){
                event.setCancelled(true);
                if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
                    arena.corner1Location(event.getClickedBlock().getLocation());
                }else{
                    arena.corner1Location(player.getLocation());
                }
                player.sendMessage(Colorize.format("&aSet the &ecorner1 &afor the arena"));
            
            // Arena Island Chests setters 
            }else if(itemFlag.getBoolean("SetSpectatorSpawn")){
                event.setCancelled(true);
                if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
                    arena.spectatorSpawnLocation(event.getClickedBlock().getLocation());
                }else{
                    arena.spectatorSpawnLocation(player.getLocation());
                }
                player.sendMessage(Colorize.format("&aSet the &espectator spawn &afor the arena"));
            
            // Arena Island Chests setters 
            }else if(itemFlag.getBoolean("SetArenaSpawns")){
                event.setCancelled(true);
                arena.addSpawnLocation(player.getLocation());
                player.sendMessage(Colorize.format("&aSet the &espawn &afor the player number: &6" + arena.spawnLocations().size()));
            
            // Arena Island Chests setters 
            }else if(itemFlag.getBoolean("SetIslandChests")){
                event.setCancelled(true);
                if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
                if((event.getClickedBlock().getType() != Material.CHEST && event.getClickedBlock().getType() != Material.TRAPPED_CHEST))  return;  
                arena.addIslandChest(event.getClickedBlock().getLocation());
                player.sendMessage(Colorize.format("&aAdded &eisland chest &anumber: &6" + arena.islandChests().size()));

            // Arena Middle Chests Setter 
            }else if(itemFlag.getBoolean("SetMiddleChests")){
                event.setCancelled(true);
                if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
                if(event.getClickedBlock().getType() != Material.CHEST && event.getClickedBlock().getType() != Material.TRAPPED_CHEST)  return;  
                arena.addMiddleChest(event.getClickedBlock().getLocation());
                player.sendMessage(Colorize.format("&aAdded &emiddle chest &anumber: &6" + arena.middleChests().size()));

            // Arena saver 
            }else if (itemFlag.getBoolean("SaveArena")){
                event.setCancelled(true);
                if(arena.name() == null || arena.name().isEmpty()){
                    player.sendMessage(Colorize.format("&cPlease set the name of the arena"));
                    return;
                }
                if(arena.displayName() == null || arena.displayName().isEmpty()){
                    player.sendMessage(Colorize.format("&cPlease set the displayname of the arena"));
                    return;
                }
                if(arena.centerLocation() == null){
                    player.sendMessage(Colorize.format("&cPlease set the center of the arena"));
                    return;
                }
                if(arena.corner1Location() == null || arena.corner2Location() == null){
                    player.sendMessage(Colorize.format("&cPlease set the corners of the arena"));
                    return;
                }
                if(arena.spectatorSpawnLocation() == null){
                    player.sendMessage(Colorize.format("&cPlease set the spectator spawn of the arena"));
                    return;
                }
                if(arena.spawnLocations().isEmpty() || arena.spawnLocations().size() < 2){
                    player.sendMessage(Colorize.format("&cPlease set at least 2 player spawns on the arena"));
                    return;
                }
                if(arena.islandChests().isEmpty()){
                    player.sendMessage(Colorize.format("&eThe current arena has no &6island chests&e, the arena will work but there wont be any island chests"));
                    player.sendMessage(Colorize.format("&eConsider editing the arena to add the chests, or just ignore this warning"));
                }
                if(arena.middleChests().isEmpty()){
                    player.sendMessage(Colorize.format("&eThe current arena has no &6middle chests&e, the arena will work but there wont be any middle chests"));
                    player.sendMessage(Colorize.format("&eConsider editing the arena to add the chests, or just ignore this warning"));
                }
                Arena saved = arena.toArena();
                gameManager.arenaManager().addArena(saved);
                gameManager.configManager().saveArena(saved);
                stopWizard(player);
            }else if (itemFlag.getBoolean("CancelArena")){
                event.setCancelled(true);
                stopWizard(player);
            }
            //else if the action is any of the left clicks.
        }else if(event.getAction() != Action.PHYSICAL){
            //  Removes the last Spawns/Chests if the item is left clicked instead of rightClicked
            if(itemFlag.getBoolean("SetArenaSpawns")){
                event.setCancelled(true);
                arena.removeLastSpawnLocation();
                player.sendMessage(Colorize.format("&cRemoved the &espawn &cfor the player number: &6" + (arena.spawnLocations().size() + 1)));
            // Island Chest remover
            }else if(itemFlag.getBoolean("SetIslandChests")){
                event.setCancelled(true); 
                arena.removeLastIslandChest();
                player.sendMessage(Colorize.format("&cRemoved &eisland chest &cnumber: &6" + (arena.islandChests().size() + 1 )));
            // Middle Chest Remover
            }else if(itemFlag.getBoolean("SetMiddleChests")){
                event.setCancelled(true); 
                arena.removeLastMiddleChest();
                player.sendMessage(Colorize.format("&cRemoved &emiddle chest &cnumber: &6" + (arena.middleChests().size() + 1 )));
            // Arena Corner2 Setter
            }else if(itemFlag.getBoolean("SetArenaCorner")){
                event.setCancelled(true);
                if(event.getAction() == Action.LEFT_CLICK_BLOCK){
                    arena.corner2Location(event.getClickedBlock().getLocation());
                }else{
                    arena.corner2Location(player.getLocation());
                }
                player.sendMessage(Colorize.format("&aSet the &ecorner2 &afor the arena"));
             
            }
        }

    }

}
