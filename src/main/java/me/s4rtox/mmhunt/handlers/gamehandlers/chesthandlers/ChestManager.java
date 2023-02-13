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
    private final HashMap<String, List<LootItem>> chestItems = new HashMap<>();
    //TODO: reload for chests

    public ChestManager(MMHunt plugin) {
        this.chestConfig = plugin.getChestConfig();
        loadChests();
    }

    public void loadChests(){
        this.chestItems.clear();
        Section itemConfig = chestConfig.getSection("chestItems");
        for (String chestType : itemConfig.getRoutesAsStrings(false)) {
            this.chestItems.put(chestType, new ArrayList<>());
            Section chestSection = itemConfig.getSection(chestType);
            for (String itemSection : chestSection.getRoutesAsStrings(false)) {
                Section itemChests = chestSection.getSection(itemSection);
                this.chestItems.get(chestType).add(new LootItem(itemChests));
            }
        }
    }


    public void fillChest(Inventory inventory, String chestType, boolean clearChest){
        if(!chestItems.containsKey(chestType)){
            Bukkit.getLogger().warning("Chest item type " + chestType + "doesn't exist.");
            return;
        }
        if(clearChest){
            inventory.clear();
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Set<LootItem> usedItem = new HashSet<>();
        for (int slotIndex = 0; slotIndex < inventory.getSize() ; slotIndex++) {
            ItemStack previousItem = inventory.getItem(slotIndex);
            //Checks if there is an item in the slot already, if there is it continues
            if(previousItem != null && previousItem.getType() != Material.AIR) continue;
            LootItem randomItem = chestItems.get(chestType).get(random.nextInt(chestItems.get(chestType).size()));
            //Checks if the item has already been put
            if(usedItem.contains(randomItem)) continue;
            if(randomItem.shouldFill(random)){
                if(!randomItem.allowDuplicates()) usedItem.add(randomItem);
                inventory.setItem(slotIndex,randomItem.toItemStack(random));
            }
        }
    }

    //TODO: Config implementation
    public void fillChestLocations(List<Location> chestLocations,String chestType, boolean clearChestsInventories){
        if(chestLocations.isEmpty()) return;
        for (Location chestLocation : chestLocations){
            if(chestLocation.getBlock().getType() != Material.CHEST) continue;
            Chest chest = (Chest) chestLocation.getBlock().getState();
            fillChest(chest.getInventory(), chestType, clearChestsInventories);
        }
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
