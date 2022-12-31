package me.s4rtox.mpractice.handlers.gamehandlers;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.s4rtox.mpractice.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mpractice.handlers.gamehandlers.arena.TemporaryArena;
import me.s4rtox.mpractice.util.Colorize;
import me.s4rtox.mpractice.util.ItemBuilder;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetupWizardManager implements Listener {
    private final GameManager gameManager;
    private final Map<UUID, TemporaryArena> inWizard = new HashMap<>();

    public SetupWizardManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void startWizard(Player player, Arena arena) {
        if (gameManager.arenaManager().findPlayerArena(player).isPresent()) {
            player.sendMessage(Colorize.format("&4You cant start the arena setup ingame!"));
            return;
        }
        TemporaryArena temporaryArena;
        boolean existingArena = false;
        if (arena == null) {
            temporaryArena = new TemporaryArena(gameManager);
        } else {
            temporaryArena = new TemporaryArena(gameManager, arena);
            existingArena = true;
        }
        inWizard.put(player.getUniqueId(), temporaryArena);
        gameManager.rollbackManager().save(player);
        player.setGameMode(GameMode.CREATIVE);
        player.getInventory().clear();
        if (existingArena) {
            player.teleport(temporaryArena.centerLocation().clone().add(0, 1, 0));
        }
        setWizardItems(player);
    }

    private void setWizardItems(Player player) {
        Inventory inventory = player.getInventory();

        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.SIGN), "&5Set Name/DisplayName &7{Left Click | Right Click}", true, "SetArenaName", "&7Left click to set name", "&7Right Click to set displayname"));
        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.ANVIL), "&2Set ArenaCenter &7{Right Click}", true, "SetArenaCenter", "&7Right click to set the arena center"));
        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.DIAMOND_AXE), "&6Set ArenaCorners &7{Left Click | Right Click}", true, "SetArenaCorner", "&7Left click to set corner 1", "&7Right Click to set corner 2"));
        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.SKULL_ITEM), "&bSet SpectatorSpawns &7{Right Click}", true, "SetSpectatorSpawn", "&7Right click to set the spectator spawn"));
        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.ARROW), "&4Set Spawns &7{Left Click | Right Click}", true, "SetArenaSpawns", "&7Right click to &aADD &7 a spawn", "&7Left click to &cREMOVE &7 a spawn"));
        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.STICK), "&eSet IslandChests &7{Left Click | Right Click}", true, "SetIslandChests", "&7Right click to &aADD &7 a island chest", "&7Left click to &cREMOVE &7 a island chest"));
        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.BLAZE_ROD), "&eSet MiddleChests &7{Left Click | Right Click}", true, "SetMiddleChests", "&7Right click to &aADD &7 a middle chest", "&7Left click to &cREMOVE &7 a middle chest"));
        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.EMERALD_BLOCK), "&aSave Arena &7{Right Click}", true, "SaveArena", "&7Right click to &asave arena"));
        inventory.addItem(ItemBuilder.getSpecialItem(new ItemStack(Material.BARRIER), "&cCancel &7{Right Click}", true, "CancelArena", "&7Right click to &ccancel the setup"));

    }

    public void stopWizard(Player player) {
        inWizard.remove(player.getUniqueId());
        gameManager.rollbackManager().restore(player, false);
    }

    public boolean inWizard(Player player) {
        return inWizard.containsKey(player.getUniqueId());
    }

    @EventHandler
    public void onSetupModeInteract(PlayerInteractEvent event) {
        if (inWizard(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSetupModeDrop(PlayerDropItemEvent event) {
        if (inWizard(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSetupModeInventoryInteract(InventoryCreativeEvent event) {
        if (inWizard((Player) event.getWhoClicked())) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            player.updateInventory();
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void whileSetupModeDeath(PlayerDeathEvent event) {
        if (inWizard(event.getEntity())) {
            Player player = event.getEntity();
            stopWizard(player);
            player.sendMessage(Colorize.format("&cYou've died while in setup mode!, forcefully cancelled operation"));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void whileSetupChangeWorld(PlayerChangedWorldEvent event) {
        if (inWizard(event.getPlayer())) {
            Player player = event.getPlayer();
            stopWizard(player);
            player.sendMessage(Colorize.format("&cYou've changed worlds while in setup mode!, forcefully cancelled operation"));
        }
    }

    @EventHandler
    public void onSetupItemInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!inWizard(player)) return;
        if (!event.hasItem()) return;
        if (!event.getItem().hasItemMeta()) return;

        NBTItem itemFlag = new NBTItem(event.getItem());

        TemporaryArena arena = inWizard.get(player.getUniqueId());


        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Arena displayname setter 
            if (itemFlag.getBoolean("SetArenaName")) {
                new AnvilGUI.Builder()
                        .title("Enter the arena displayname")
                        .itemLeft(new ItemStack(Material.PAPER))
                        .plugin(gameManager.plugin())
                        .onComplete((player1, text) -> {
                            arena.displayName(text);
                            player.sendMessage(Colorize.format("&aSet the arena display name to: &e" + arena.displayName()));
                            return AnvilGUI.Response.close();
                        }).open(player);
                // Island Chest remover
            } else if (itemFlag.getBoolean("SetArenaCenter")) {

                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    arena.centerLocation(event.getClickedBlock().getLocation());
                } else {
                    arena.centerLocation(player.getLocation());
                }
                player.sendMessage(Colorize.format("&aSet the &ecenter &afor the arena"));

                // Arena Corner1 setter
            } else if (itemFlag.getBoolean("SetArenaCorner")) {

                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    arena.corner1Location(event.getClickedBlock().getLocation());
                } else {
                    arena.corner1Location(player.getLocation());
                }
                player.sendMessage(Colorize.format("&aSet the &ecorner1 &afor the arena"));

                // Arena Island Chests setters
            } else if (itemFlag.getBoolean("SetSpectatorSpawn")) {

                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    arena.spectatorSpawnLocation(event.getClickedBlock().getLocation());
                } else {
                    arena.spectatorSpawnLocation(player.getLocation());
                }
                player.sendMessage(Colorize.format("&aSet the &espectator spawn &afor the arena"));

                // Arena Island Chests setters
            } else if (itemFlag.getBoolean("SetArenaSpawns")) {
                Vector lookingAt = player.getLocation().getDirection();
                arena.addSpawnLocation(player.getLocation().clone().getBlock().getLocation().add(0.5,0,0.5).setDirection(lookingAt));
                player.sendMessage(Colorize.format("&aSet the &espawn &afor the player number: &6" + arena.spawnLocations().size()));

                // Arena Island Chests setters
            } else if (itemFlag.getBoolean("SetIslandChests")) {

                if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
                if ((event.getClickedBlock().getType() != Material.CHEST && event.getClickedBlock().getType() != Material.TRAPPED_CHEST))
                    return;
                arena.addIslandChest(event.getClickedBlock().getLocation());
                player.sendMessage(Colorize.format("&aAdded &eisland chest &anumber: &6" + arena.islandChests().size()));

                // Arena Middle Chests Setter
            } else if (itemFlag.getBoolean("SetMiddleChests")) {

                if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
                if (event.getClickedBlock().getType() != Material.CHEST && event.getClickedBlock().getType() != Material.TRAPPED_CHEST)
                    return;
                arena.addMiddleChest(event.getClickedBlock().getLocation());
                player.sendMessage(Colorize.format("&aAdded &emiddle chest &anumber: &6" + arena.middleChests().size()));

                // Arena saver
            } else if (itemFlag.getBoolean("SaveArena")) {

                if (arena.name() == null || arena.name().isEmpty()) {
                    player.sendMessage(Colorize.format("&cPlease set the name of the arena"));
                    return;
                }
                if (arena.displayName() == null || arena.displayName().isEmpty()) {
                    player.sendMessage(Colorize.format("&cPlease set the displayname of the arena"));
                    return;
                }
                if (arena.centerLocation() == null) {
                    player.sendMessage(Colorize.format("&cPlease set the center of the arena"));
                    return;
                }
                if (arena.corner1Location() == null || arena.corner2Location() == null) {
                    player.sendMessage(Colorize.format("&cPlease set the corners of the arena"));
                    return;
                }
                if (arena.spectatorSpawnLocation() == null) {
                    player.sendMessage(Colorize.format("&cPlease set the spectator spawn of the arena"));
                    return;
                }
                if (arena.spawnLocations().isEmpty() || arena.spawnLocations().size() < 2) {
                    player.sendMessage(Colorize.format("&cPlease set at least 2 player spawns on the arena"));
                    return;
                }
                if (arena.islandChests().isEmpty()) {
                    player.sendMessage(Colorize.format("&eThe current arena has no &6island chests&e, the arena will work but there wont be any island chests"));
                    player.sendMessage(Colorize.format("&eConsider editing the arena to add the chests, or just ignore this warning"));
                }
                if (arena.middleChests().isEmpty()) {
                    player.sendMessage(Colorize.format("&eThe current arena has no &6middle chests&e, the arena will work but there wont be any middle chests"));
                    player.sendMessage(Colorize.format("&eConsider editing the arena to add the chests, or just ignore this warning"));
                }
                Arena saved = arena.toArena();
                gameManager.arenaManager().addArena(saved);
                gameManager.configManager().saveArena(saved);
                player.sendMessage(Colorize.format("&a&lArena succesfully created!"));
                stopWizard(player);
            } else if (itemFlag.getBoolean("CancelArena")) {
                stopWizard(player);
            }
            //else if the action is any of the left clicks.
        } else if (event.getAction() != Action.PHYSICAL) {
            //  Removes the last Spawns/Chests if the item is left clicked instead of rightClicked
            if (itemFlag.getBoolean("SetArenaSpawns")) {

                arena.removeLastSpawnLocation();
                player.sendMessage(Colorize.format("&cRemoved the &espawn &cfor the player number: &6" + (arena.spawnLocations().size() + 1)));
                // displayname setter
            } else if (itemFlag.getBoolean("SetArenaName")) {

                new AnvilGUI.Builder()
                        .plugin(gameManager.plugin())
                        .itemLeft(new ItemStack(Material.PAPER))
                        .title("Enter the arena name")
                        .onComplete((player1, text) -> {
                            if (gameManager.arenaManager().getArenas().stream().anyMatch(allArenas -> allArenas.name().equalsIgnoreCase(text))) {
                                player.sendMessage(Colorize.format("&cAn arena with that name already exists!"));
                                return AnvilGUI.Response.text("Invalid name");
                            }
                            arena.name(text);
                            player.sendMessage(Colorize.format("&aSet the arena name to: &e") + arena.name());
                            return AnvilGUI.Response.close();
                        }).open(player);
                // Arena centerLocation
            } else if (itemFlag.getBoolean("SetIslandChests")) {

                arena.removeLastIslandChest();
                player.sendMessage(Colorize.format("&cRemoved &eisland chest &cnumber: &6" + (arena.islandChests().size() + 1)));
                // Middle Chest Remover
            } else if (itemFlag.getBoolean("SetMiddleChests")) {

                arena.removeLastMiddleChest();
                player.sendMessage(Colorize.format("&cRemoved &emiddle chest &cnumber: &6" + (arena.middleChests().size() + 1)));
                // Arena Corner2 Setter
            } else if (itemFlag.getBoolean("SetArenaCorner")) {

                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    arena.corner2Location(event.getClickedBlock().getLocation());
                } else {
                    arena.corner2Location(player.getLocation());
                }
                player.sendMessage(Colorize.format("&aSet the &ecorner2 &afor the arena"));

            }
        }

    }

}
