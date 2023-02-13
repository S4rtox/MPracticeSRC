package me.s4rtox.mmhunt.util;

import com.google.common.base.Preconditions;
import de.tr7zw.changeme.nbtapi.NBT;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CItemBuilder {
    private final ItemStack item;
    public static CItemBuilder of(Material material) {
        return new CItemBuilder(material, 1);
    }

    public static CItemBuilder of(Material material, int amount) {
        return new CItemBuilder(material, amount);
    }
    public static CItemBuilder of(Material material, int amount, byte data) {
        return new CItemBuilder(material, amount,data);
    }

    public static CItemBuilder of(CItemBuilder builder) {
        return new CItemBuilder(builder.build());
    }

    public static CItemBuilder of(ItemStack item) {
        return new CItemBuilder(item);
    }

    public CItemBuilder(Material material, int amount) {
        Preconditions.checkArgument((amount > 0), "Amount cannot be lower than 0.");
        this.item = new ItemStack(material, amount);
    }

    public CItemBuilder(Material material, int amount, byte data){
        this(material,amount);
        addItemData(data);
    }

    private CItemBuilder(ItemStack item) {
        this.item = item;
    }

    public CItemBuilder amount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    public CItemBuilder data(short data) {
        this.item.setDurability(data);
        return this;
    }

    public CItemBuilder enchant(Enchantment enchantment, int level) {
        this.item.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public CItemBuilder dummyEnchant(){
        this.enchant(Enchantment.DURABILITY,1);
        this.hideEnchants();
        return this;
    }

    public CItemBuilder hideEnchants() {
        this.item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public CItemBuilder hideAttributes() {
        this.item.addItemFlags(ItemFlag.values());
        return this;
    }

    public CItemBuilder unenchant(Enchantment enchantment) {
        this.item.removeEnchantment(enchantment);
        return this;
    }

    public CItemBuilder name(String displayName) {
        ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName((displayName == null) ? null : ChatColor.translateAlternateColorCodes('&', displayName));
        this.item.setItemMeta(meta);
        return this;
    }

    public CItemBuilder addToLore(String... parts) {
        ItemMeta meta = this.item.getItemMeta();
        if (meta == null) {
            meta = Bukkit.getItemFactory().getItemMeta(this.item.getType());
        }
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.addAll(Arrays.stream(parts).map(part -> ChatColor.translateAlternateColorCodes('&', part)).toList());
        meta.setLore(lore);
        this.item.setItemMeta(meta);
        return this;
    }

    public CItemBuilder setLore(Collection<String> l) {
        ItemMeta meta = this.item.getItemMeta();
        List<String> lore = l.stream().map(part -> ChatColor.translateAlternateColorCodes('&', part)).toList();
        meta.setLore(lore);
        this.item.setItemMeta(meta);
        return this;
    }

    public CItemBuilder setLore(String... l) {
        ItemMeta meta = this.item.getItemMeta();
        List<String> lore = Arrays.stream(l).map(part -> ChatColor.translateAlternateColorCodes('&', part)).toList();
        meta.setLore(lore);
        this.item.setItemMeta(meta);
        return this;
    }


    public CItemBuilder color(Color color) {
        ItemMeta meta = this.item.getItemMeta();
        if (!(meta instanceof LeatherArmorMeta)) {
            throw new UnsupportedOperationException("Cannot set color of a non-leather armor item.");
        }
        ((LeatherArmorMeta) meta).setColor(color);
        this.item.setItemMeta(meta);
        return this;
    }

    public CItemBuilder addBooleanNbtData(String key, boolean state){
        NBT.modify(item, nbt -> {
            nbt.setBoolean(key, true);
        });
        return this;
    }

    public <E extends Enum<?>> CItemBuilder addEnumNbtData(String key, E value){
        NBT.modify(item, nbt -> {
            nbt.setEnum(key, value);
        });
        return this;
    }

    public CItemBuilder addStringNbtData(String key, String value){
        NBT.modify(item, nbt -> {
            nbt.setString(key, value);
        });
        return this;
    }

    public CItemBuilder addItemData(byte data){
        this.item.setData(new MaterialData(item.getType(),data));
        return this;
    }

    public GuiItem asGuiItem(){
        return new GuiItem(item);
    }

    public GuiItem asGuiItem(GuiAction<InventoryClickEvent> event){
        return new GuiItem(item, event);
    }

    public ItemStack build() {
        return this.item.clone();
    }
}
