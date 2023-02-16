package me.s4rtox.mmhunt.handlers.gamehandlers.chesthandlers;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import me.s4rtox.mmhunt.MMHunt;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ChestManager {
    private final YamlDocument chestConfig;
    private final HashMap<String, ChestLoot> chestItems = new HashMap<>();
    //TODO: reload for chests

    public ChestManager(MMHunt plugin) {
        this.chestConfig = plugin.getChestConfig();
        loadChests();
    }

    public void loadChests(){
        this.chestItems.clear();
        Section itemConfig = chestConfig.getSection("chestItems");
        for (String chestType : itemConfig.getRoutesAsStrings(false)) {
            this.chestItems.put(chestType, new ChestLoot());
            Section chestSection = itemConfig.getSection(chestType);
            for (String itemSection : chestSection.getRoutesAsStrings(false)) {
                Section itemChests = chestSection.getSection(itemSection);
                this.chestItems.get(chestType).getItems().add(new LootItem(itemChests));
            }
        }
    }




    public void fillChest(Inventory inventory, String chestType, boolean clearChest,ThreadLocalRandom random){
        if(!chestItems.containsKey(chestType)){
            Bukkit.getLogger().warning("Chest item type " + chestType + "doesn't exist.");
            return;
        }
        if(clearChest){
            inventory.clear();
        }
        Set<LootItem> usedItem = new HashSet<>();
        for (int slotIndex = 0; slotIndex < inventory.getSize() ; slotIndex++) {
            ItemStack previousItem = inventory.getItem(slotIndex);
            //Checks if there is an item in the slot already, if there is it continues
            if(previousItem != null && previousItem.getType() != Material.AIR) continue;
            LootItem randomItem = chestItems.get(chestType).getRandomItem(random);
            //Checks if the item has already been put
            if(usedItem.contains(randomItem)) continue;
            if(!randomItem.allowDuplicates()) usedItem.add(randomItem);
            inventory.setItem(slotIndex,randomItem.toItemStack(random));
        }
    }

    public void firstFillChests(List<Location> chestLocations,String chestType, boolean clearChestsInventories){
        if(chestLocations.isEmpty()) return;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (Location chestLocation : chestLocations){
            if(filterChest(chestConfig.getInt("chest-appearance-chance",20),chestLocation,random)) continue;
            if(chestLocation.getBlock().getType() != Material.CHEST) continue;
            Chest chest = (Chest) chestLocation.getBlock().getState();
            fillChest(chest.getInventory(), chestType, clearChestsInventories,random);
        }
    }

    public void fillChestLocations(List<Location> chestLocations,String chestType, boolean clearChestsInventories){
        if(chestLocations.isEmpty()) return;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (Location chestLocation : chestLocations){
            if(chestLocation.getBlock().getType() != Material.CHEST) continue;
            Chest chest = (Chest) chestLocation.getBlock().getState();
            fillChest(chest.getInventory(), chestType, clearChestsInventories,random);
        }
    }

    public void filterAllChests(List<Location> chestLocations){
        if(chestLocations.isEmpty()) return;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (Location chestLocation : chestLocations){
            filterChest(chestConfig.getInt("chest-appearance-chance",20),chestLocation,random);
        }
    }



    /**
     * Filters the chests by chance
     * @param chance Chance for the chest to appear(0-100)
     * @param chestLocation Location for the chest
     * @return false if it wasn't removed, true if it was.
     */
    private boolean filterChest(int chance, Location chestLocation, ThreadLocalRandom random){
        if(random.nextInt(0,100) >= chance){
            chestLocation.getBlock().setType(Material.AIR);
            return true;
        }
        return false;
    }

    public void clearChestsInventories(List<Location> chestLocations){
        if(chestLocations.isEmpty()) return;
        for (Location chestLocation : chestLocations){
            if(chestLocation.getBlock().getType() != Material.CHEST) continue;
            Chest chest = (Chest) chestLocation.getBlock().getState();
            chest.getInventory().clear();
        }
    }


}
