package me.s4rtox.mmhunt.handlers.gamehandlers.chesthandlers;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import me.s4rtox.mmhunt.util.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class LootItem {
    private final Material material;
    private final String customName;
    private final Map<Enchantment, Integer> enchantementToLevelMap = new HashMap<>();
    private final double chance;
    private final int minAmount;
    private final int maxAmount;
    private final boolean allowDuplicates;

    public LootItem(Section section){
        material = Material.valueOf(section.getString("material", "AIR").toUpperCase().replace('-','_'));
        this.customName = section.getString("name", "");
        if(section.isList("enchantments")){
            List<String> enchantmentList = section.getStringList("enchantments");
            for (String enchantment : enchantmentList){
                String[] fullench = enchantment.split(":",2);
                Enchantment enchant = Enchantment.getByName(fullench[0].toUpperCase().replace('-','_'));
                if(enchant != null){
                    int level;
                    try{
                        level = Integer.parseInt(fullench[1]);
                    }catch (NumberFormatException exception){
                        level = 1;
                    }
                    enchantementToLevelMap.put(enchant,level);
                }
            }
        }

        this.chance = section.getDouble("chance", 0.1);
        this.minAmount = section.getInt("minAmount", 1);
        this.maxAmount =  section.getInt("maxAmount", 1);
        this.allowDuplicates = section.getBoolean("allowDuplicates", true);
    }

    public boolean shouldFill(Random random){
        return random.nextDouble() < chance;
    }

    public boolean allowDuplicates(){
        return allowDuplicates;
    }

    public ItemStack toItemStack(ThreadLocalRandom random) {
        int amount;
        if (minAmount != maxAmount) {
            amount = random.nextInt(minAmount, maxAmount);
        } else {
            amount = minAmount;
        }
        ItemStack item = new ItemStack(material, amount);
        item.editMeta(itemMeta -> {
            itemMeta.setDisplayName(Colorize.format(customName));

            if (!enchantementToLevelMap.isEmpty()) {
                for (Map.Entry<Enchantment, Integer> enchantEntry : enchantementToLevelMap.entrySet()) {
                    itemMeta.addEnchant(enchantEntry.getKey(), enchantEntry.getValue(), true);
                }
            }
        });

        return item;
    }

    public double getChance(){
        return chance;
    }

}

