package me.s4rtox.mpractice.util;

import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    public static ItemStack getSpecialItem(ItemStack item, String name, boolean isEnchanted, String customFlag,String... lore){
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(Colorize.format( name));
        if(isEnchanted) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        List<String> lores = new ArrayList<>();
        for (String s : lore){
            lores.add(Colorize.format( s));
        }
        meta.setLore(lores);
        item.setItemMeta(meta);
        NBT.modify(item, nbt -> {
            nbt.setBoolean(customFlag, true);
                });
        return item;
    }
    public static ItemStack getItem(ItemStack item, String name, boolean isEnchanted, String... lore){
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(Colorize.format( name));
        if(isEnchanted) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        List<String> lores = new ArrayList<>();
        for (String s : lore){
            lores.add(Colorize.format( s));
        }
        meta.setLore(lores);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getItem(ItemStack item, String name, boolean isEnchanted, List<String> lore){
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(Colorize.format( name));
        if(isEnchanted) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        List<String> lores = new ArrayList<>();
        for(String s : lore){
            lores.add(Colorize.format(s));
        }
        meta.setLore(lores);
        item.setItemMeta(meta);
        return item;
    }


}
