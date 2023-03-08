package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.tasks;

import me.s4rtox.mmhunt.config.ConfigManager;
import me.s4rtox.mmhunt.handlers.gamehandlers.GameManager;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.ActiveArenaState;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.states.events.ReviveAllTask;
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
    private final ActiveArenaState state;
    private ReviveAllTask reviveAllTask;
    private final GameManager gameManager;
    private int matchDuration = 0;
    private int timeUntill = 0;
    private final WorldBorder worldBorder;
    private String nextEvent;
    private boolean firstRun = false;

    public ActiveArenaEvents(ActiveArenaState state, GameManager gameManager) {
        this.state = state;
        this.arena = state.getArena();
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
                        CItemBuilder.of(Material.DIAMOND_BOOTS)
                                .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                                .enchant(Enchantment.DURABILITY, 3)
                                .enchant(Enchantment.DEPTH_STRIDER, 2)
                                .build(),
                        CItemBuilder.of(Material.DIAMOND_LEGGINGS)
                                .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                                .enchant(Enchantment.DURABILITY, 3)
                                .build(),
                        CItemBuilder.of(Material.NETHERITE_CHESTPLATE)
                                .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                                .enchant(Enchantment.DURABILITY, 3)
                                .build(),
                        CItemBuilder.of(Material.NETHERITE_HELMET)
                                .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                                .enchant(Enchantment.DURABILITY, 3)
                                .enchant(Enchantment.WATER_WORKER, 2)
                                .enchant(Enchantment.OXYGEN, 3)
                                .build()
                });
                player.getInventory().addItem(
                        CItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL,3).enchant(Enchantment.DURABILITY,10).build(),
                        CItemBuilder.of(Material.BOW)
                                .enchant(Enchantment.ARROW_DAMAGE, 4)
                                .enchant(Enchantment.DURABILITY,10)
                                .enchant(Enchantment.ARROW_KNOCKBACK, 2)
                                .build(),
                        CItemBuilder.of(Material.STICK)
                                .enchant(Enchantment.KNOCKBACK, 4)
                                .build(),
                        CItemBuilder.of(Material.CROSSBOW)
                                .enchant(Enchantment.MULTISHOT,1)
                                .enchant(Enchantment.DURABILITY,10)
                                .enchant(Enchantment.QUICK_CHARGE,2)
                                .build(),
                        CItemBuilder.of(Material.NETHERITE_PICKAXE)
                                .enchant(Enchantment.DIG_SPEED,6)
                                .enchant(Enchantment.DURABILITY,10)
                                .build(),
                        CItemBuilder.of(Material.DIAMOND_AXE)
                                .enchant(Enchantment.DIG_SPEED,6)
                                .enchant(Enchantment.DAMAGE_ALL, 3)
                                .enchant(Enchantment.DURABILITY,10)
                                .build(),
                        CItemBuilder.of(Material.OAK_WOOD,64)
                                .build(),
                        CItemBuilder.of(Material.OAK_WOOD,64)
                                .build(),
                        CItemBuilder.of(Material.COBBLESTONE,64)
                                .build(),
                        CItemBuilder.of(Material.WATER_BUCKET,2)
                                .build(),
                        CItemBuilder.of(Material.LAVA_BUCKET,2)
                                .build(),
                        CItemBuilder.of(Material.FLINT_AND_STEEL,1).enchant(Enchantment.DURABILITY,10)
                                .build(),
                        CItemBuilder.of(Material.REDSTONE_BLOCK,64).build(),
                        CItemBuilder.of(Material.REPEATER,64).build(),
                        CItemBuilder.of(Material.TNT,10).build(),
                        CItemBuilder.of(Material.ENDER_PEARL,64).build(),
                        CItemBuilder.of(Material.ENCHANTED_GOLDEN_APPLE,3).build(),
                        CItemBuilder.of(Material.GOLDEN_APPLE,10).build(),
                        CItemBuilder.of(Material.COOKED_BEEF,64).build(),
                        CItemBuilder.of(Material.ELYTRA,1).build(),
                        CItemBuilder.of(Material.FIREWORK_ROCKET,5).build()
                );
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
                worldBorder.setSize(worldBorder.getSize() - 150, 10);
                nextEvent = "Refill Chests";
                timeUntill = 13 * 60 - matchDuration;
            }
            case 13 * 60 -> {
                arena.refillChests();
                arena.sendAllTitle("&aChest has been refilled","",20,20,20);
                arena.sendAllSound(Sound.BLOCK_CHEST_CLOSE,1f,1f);
                nextEvent = "Hunter Item";
                timeUntill = 15 * 60 - matchDuration;
            }
            case 15 * 60 -> {
                arena.doHunterAction(player -> player.getInventory().addItem(CItemBuilder.of(Material.GOLDEN_APPLE,20).name("uwu").build()));
                nextEvent = "Border Shrink";
                timeUntill = 20 * 60 - matchDuration;
            }
            case 20 * 60 -> {
                worldBorder.setSize(worldBorder.getSize() - 150, 10);
                nextEvent = "Hunters Speed";
                timeUntill = 25 * 60 - matchDuration;
            }
            case 25 * 60 -> {
                arena.doHunterAction(player -> player.addPotionEffect(PotionEffectType.SPEED.createEffect(9999999, 1)));
                nextEvent = "Refill Chests";
                timeUntill = 28 * 60 - matchDuration;
            }
            case 28 * 60 -> {
                arena.refillChests();
                arena.sendAllTitle("&aChest has been refilled","",20,20,20);
                arena.sendAllSound(Sound.BLOCK_CHEST_CLOSE,1f,1f);
                nextEvent = "Border Shrink";
                timeUntill = 30 * 60 - matchDuration;
            }
            case 30 * 60 -> {
                worldBorder.setSize(worldBorder.getSize() - 150, 10);
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
                nextEvent = "DeathMatch(Border Closer)";
                timeUntill = 60 * 60 - matchDuration;
            }
            case 60 * 60 -> {
                nextEvent = "&7-";
                worldBorder.setSize(worldBorder.getSize() - 300, 25);
                cancel();
            }
        }
        try{
            arena.doHunterAction(player -> gameManager.getTrackerHandler().setCompassToTarget(player));
        }catch (Exception ignored){

        }

        //Code to be executed every second
        arena.updateAllScoreboards("&e&lMMHunt",
                "",
                "&fAlive: &a" + state.getRespawningPlayers().size(),
                "",
                "&fNext Event: &a" + nextEvent + " &7&l→&a " + (timeUntill < 60 ? timeUntill : timeUntill / 60 + ":" + timeUntill % 60),
                "",
                "&fMatch duration: &a" + (matchDuration < 60 ? matchDuration : matchDuration / 60 + ":" + matchDuration % 60),
                "",
                "&f" + ConfigManager.serverIP
        );
        /*
        if(state.getRespawningPlayers().isEmpty()){
            reviveAllTask = new ReviveAllTask(state,10);
            reviveAllTask.run();
        }
         */
        //Messages to be broadcasted each x seconds
        if(timeUntill > 0){
            timeUntill--;
        }
        matchDuration++;
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        try{
            reviveAllTask.cancel();
        }catch (Exception ignored){}
    }
}
