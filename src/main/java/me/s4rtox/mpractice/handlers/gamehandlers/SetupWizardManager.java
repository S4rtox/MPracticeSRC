package me.s4rtox.mpractice.handlers.gamehandlers;

import de.tr7zw.changeme.nbtapi.NBTItem;
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
        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.BLAZE_ROD), "&4SetSpawns", true, "SetArenaSpawns", "Right click to set spawn"));
        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.SIGN), "&5SetArenaName", true, "SetArenaName", "Right click to set name"));
        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.EMERALD_BLOCK), "&SaveArena", true, "SaveArena", "Right click to save arena"));
        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.BLAZE_ROD), "&cCancel", true, "CancelArena", "Right click to set cancel the setup"));
        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.BLAZE_ROD), "&4SetSpawns", true, "SetArenaSpawns", "Right click to set spawn"));
        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.BLAZE_ROD), "&4SetSpawns", true, "SetArenaSpawns", "Right click to set spawn"));
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
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(!event.getItem().hasItemMeta()) return;

        NBTItem itemFlag = new NBTItem(event.getItem());

        TemporaryArena arena = inWizard.get(player.getUniqueId());

        if(itemFlag.getBoolean("SetArenaSpawns")){
            event.setCancelled(true);
            if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
                arena.addSpawnLocation(event.getClickedBlock().getLocation());
                player.sendMessage("This executed because righhtclickblock");
            }
            else{
                arena.addSpawnLocation(player.getLocation());
            }
            player.sendMessage("Set the spawn for the player number: " + arena.spawnLocations().size());

        }else if (itemFlag.getBoolean("SetArenaName")){
            event.setCancelled(true);
            new AnvilGUI.Builder()
                    .title("Enter the arena name")
                    .itemLeft(new ItemStack(Material.PAPER))
                    .plugin(gameManager.plugin())
                    .onComplete((player1, text) -> {
                        if(gameManager.arenaManager().getArenas().stream().anyMatch(allArenas -> allArenas.displayName().equalsIgnoreCase(text))){
                            return AnvilGUI.Response.text("An arena with that name already exists!");
                        }
                        arena.displayName(text);
                        return AnvilGUI.Response.close();
                    }).open(player);
        }else if (itemFlag.getBoolean("SaveArena")){
            event.setCancelled(true);
            if(arena.displayName() == null || arena.displayName().isEmpty()){
                player.sendMessage("Please set the displayname of the arena");
                player.sendMessage("Should be always as didnt made a setter for it yet.");
            }
            Arena saved = arena.toArena();
            gameManager.arenaManager().addArena(saved);
            gameManager.configManager().saveArena(saved);
            stopWizard(player);
        }else if (itemFlag.getBoolean("CancelArena")){
            event.setCancelled(true);
            stopWizard(player);
        }
    }

}
