package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.tasks;

import me.s4rtox.mmhunt.handlers.gamehandlers.GameManager;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.ActiveArenaState;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.DeathmatchArenaState;
import me.s4rtox.mmhunt.util.CItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ActiveArenaEvents extends BukkitRunnable {
    private final Arena arena;
    private final GameManager gameManager;
    private int matchDuration = 0;
    private int timeUntill = 0;
    private final WorldBorder worldBorder;
    private String nextEvent;
    private boolean firstRun = false;

    public ActiveArenaEvents(Arena arena, GameManager gameManager) {
        this.arena = arena;
        this.gameManager = gameManager;
        this.worldBorder = arena.getWorld().getWorldBorder();
        nextEvent = "Remove Strength";
        timeUntill = 5*60;
    }

    //Eventos:
    // Empieza con fuerza 2, resistencia 2, velocidad y salto 2.
    // Al minuto 5 se discipa la fuerza
    // Minuto 7 se discipa la resistencia
    // Minuto 8 se le quita el salto
    // Minuto 10 se reduce el borde por 70 bloques
    // Minuto 15 se le entrega un item a todos los cazadores
    // Minuto 20 se reduce el borde por otros 70 bloques
    // Minuto 25 se le da velocidad a todos
    // Minuto 30 se reduce el borde por 70 bloques
    // Minuto 35 se le da armadura de hierro encantada a todos los cazadores
    // Minuto 40 Se teletransporta todos al centro
    // Minuto 50 Deathmatch-ish
    @Override
    public void run() {
        if (!firstRun){
            arena.doRunnerAction(player -> {
                EntityEquipment equipment = player.getEquipment();
                equipment.setArmorContents(new ItemStack[]{
                        CItemBuilder.of(Material.IRON_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
                        CItemBuilder.of(Material.IRON_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
                        CItemBuilder.of(Material.IRON_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
                        CItemBuilder.of(Material.IRON_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build()
                });
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,5*60*20,2,false,false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,7*60*20,2,false,false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,99999999*20,2,false,false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,8*60*20,2,false,false));
            });
            firstRun = true;
        }
        switch (matchDuration) {
            case 5 * 60 -> {
                arena.doRunnerAction(player -> player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE));
                nextEvent = "Remove Resistance";
                timeUntill = 7 * 60 - matchDuration;
            }
            case 7 * 60 -> {
                arena.doRunnerAction(player -> player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE));
                nextEvent = "Remove Jump";
                timeUntill = 8 * 60 - matchDuration;
            }
            case 8 * 60 -> {
                arena.doRunnerAction(player -> player.removePotionEffect(PotionEffectType.JUMP));
                nextEvent = "Border Shrink";
                timeUntill = 10 * 60 - matchDuration;
            }
            case 10 * 60 -> {
                worldBorder.setSize(worldBorder.getSize() - 70, 10);
                nextEvent = "Hunter Item";
                timeUntill = 15 * 60 - matchDuration;
            }
            case 15 * 60 -> {
                arena.doHunterAction(player -> player.getInventory().addItem(CItemBuilder.of(Material.GOLDEN_APPLE).name("uwu").build()));
                nextEvent = "Border Shrink";
                timeUntill = 20 * 60 - matchDuration;
            }
            case 20 * 60 -> {
                worldBorder.setSize(worldBorder.getSize() - 70, 10);
                nextEvent = "Hunters Speed";
                timeUntill = 25 * 60 - matchDuration;
            }
            case 25 * 60 -> {
                arena.doHunterAction(player -> player.addPotionEffect(PotionEffectType.SPEED.createEffect(9999999, 1)));
                nextEvent = "Border Shrink";
                timeUntill = 30 * 60 - matchDuration;
            }
            case 30 * 60 -> {
                worldBorder.setSize(worldBorder.getSize() - 70, 10);
                nextEvent = "Hunters Armor";
                timeUntill = 35 * 60 - matchDuration;
            }
            case 35 * 60 -> {
                arena.doHunterAction(player -> {
                    Inventory inv = player.getInventory();
                    CItemBuilder.tryAdding(player, inv.addItem(
                            CItemBuilder.of(Material.IRON_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
                            CItemBuilder.of(Material.IRON_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
                            CItemBuilder.of(Material.IRON_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
                            CItemBuilder.of(Material.IRON_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
                            CItemBuilder.of(Material.IRON_AXE).enchant(Enchantment.DURABILITY, 1).build(),
                            CItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).build()
                    ).values());
                });
                nextEvent = "Hunters trackers";
                timeUntill = 40 * 60 - matchDuration;
            }
            case 40 * 60 -> {
                arena.doHunterAction(player -> {
                    CItemBuilder.tryAdding(player, player.getInventory().addItem(
                            CItemBuilder.of(Material.COMPASS).name("&bRunner Tracker").build()
                    ).values());
                });
                nextEvent = "DeathMatch";
                timeUntill = 50 * 60 - matchDuration;
            }
            case 50 * 60 -> {
                arena.setArenaState(new DeathmatchArenaState(gameManager, arena));
                cancel();
            }
        }
        //Code to be executed every second
        arena.updateAllScoreboardsLine(3,"&fNext Event: &a" + nextEvent + " &7&l→&a " + (timeUntill < 60 ? timeUntill : timeUntill / 60 + ":" + timeUntill % 60));
        arena.updateAllScoreboardsLine(5, "&fMatch duration: &a" + (matchDuration < 60 ? matchDuration : matchDuration / 60 + ":" + matchDuration % 60));

        //Messages to be broadcasted each x seconds
        if(timeUntill > 0){
            timeUntill--;
        }
        matchDuration++;
    }
}
