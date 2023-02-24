package me.s4rtox.mmhunt.handlers.gamehandlers;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.Arena;
import me.s4rtox.mmhunt.handlers.gamehandlers.arena.TemporaryArena;
import me.s4rtox.mmhunt.util.CItemBuilder;
import me.s4rtox.mmhunt.util.Colorize;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.nio.Buffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetupWizardManager implements Listener {
    private final GameManager gameManager;
    private final Map<UUID, TemporaryArena> inWizard = new HashMap<>();

    public SetupWizardManager(GameManager gameManager) {
        this.gameManager = gameManager;
        gameManager.getPlugin().getServer().getPluginManager().registerEvents(this, gameManager.getPlugin());
    }

    public void startWizard(Player player, Arena arena) {
        if (gameManager.getArenaManager().isInArena(player)) {
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
        gameManager.getRollbackManager().save(player);
        player.setGameMode(GameMode.CREATIVE);
        player.getInventory().clear();
        if (existingArena) {
            player.teleport(temporaryArena.getCenterLocation().clone().add(0, 1, 0));
        }
        setWizardItems(player);
    }

    private void setWizardItems(Player player) {
        Inventory inventory = player.getInventory();

        inventory.addItem(CItemBuilder.of(Material.OAK_SIGN).name("&5Set Name/DisplayName &7{Left Click | Right Click}").setLore("&7Left click to set name", "&7Right Click to set displayname").dummyEnchant().addBooleanNbtData("SetArenaName", true).build());
        inventory.addItem(CItemBuilder.of(Material.ANVIL).name("&2Set ArenaCenter &7{Right Click}").setLore("&7Right click to set the arena center").dummyEnchant().addBooleanNbtData("SetArenaCenter", true).build());
        inventory.addItem(CItemBuilder.of(Material.DIAMOND_AXE).name("&5Set Arena Radius &7{Right Click}").setLore("&7Set the radius").dummyEnchant().addBooleanNbtData("SetRadius", true).build());
        inventory.addItem(CItemBuilder.of(Material.PLAYER_HEAD).name("&bSet SpectatorSpawn &7{Right Click}").setLore("&7Right click to set the spectator spawn").dummyEnchant().addBooleanNbtData("SetSpectatorSpawn", true).build());
        inventory.addItem(CItemBuilder.of(Material.BEACON).name("&bSet Spawn Location &7{Right Click}").setLore("&7Right click to set the main spawn").dummyEnchant().addBooleanNbtData("SetMainSpawn", true).build());
        inventory.addItem(CItemBuilder.of(Material.GLOWSTONE).name("&bSet Waiting Lobby Location &7{Right Click}").setLore("&7Right click to set the waiting lobby").dummyEnchant().addBooleanNbtData("SetWaitingLobby", true).build());
        inventory.addItem(CItemBuilder.of(Material.CHEST).name("&bSet Chests Lobby Location &7{Right Click | Right Click}").setLore("&7Right click to set the waiting lobby").dummyEnchant().addBooleanNbtData("SetChests", true).build());
        inventory.addItem(CItemBuilder.of(Material.EMERALD_BLOCK).name("&aSave Arena &7{Right Click}").setLore("&7Right click to &asave arena").dummyEnchant().addBooleanNbtData("SaveArena", true).build());
        inventory.addItem(CItemBuilder.of(Material.BARRIER).name("&cCancel &7{Right Click}").setLore("&7Right click to &ccancel the setup").dummyEnchant().addBooleanNbtData("CancelArena", true).build());

    }

    public void stopWizard(Player player) {
        inWizard.remove(player.getUniqueId());
        gameManager.getRollbackManager().restore(player, false);
    }

    public boolean inWizard(Player player) {
        return inWizard.containsKey(player.getUniqueId());
    }

    @EventHandler
    public void BlockBreak(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!inWizard(player)) return;
        if (!event.getItemInHand().hasItemMeta()) return;
        NBTItem itemFlag = new NBTItem(event.getItemInHand());
        TemporaryArena arena = inWizard.get(player.getUniqueId());
        if (itemFlag.getBoolean("SetChests")) {
            Location loc = event.getBlock().getLocation();
            arena.addchest(loc);
            loc.getBlock().setType(Material.CHEST);
            player.sendMessage(Colorize.format("&aAdded &eisland chest &anumber: &6" + arena.getChest().size()));
        }
    }

    @EventHandler
    public void BlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!inWizard(player)) return;
        Location loc = event.getBlock().getLocation();
        TemporaryArena arena = inWizard.get(player.getUniqueId());
        event.setCancelled(true);
            if(arena.getChest().contains(loc)){
                arena.getChest().remove(loc);
                loc.getBlock().setType(Material.AIR);
                player.sendMessage(Colorize.format("&cRemoved chest"));
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
                event.setCancelled(true);
                new AnvilGUI.Builder()
                        .plugin(gameManager.getPlugin())
                        .title("Enter the arena displayname")
                        .itemLeft(new ItemStack(Material.PAPER))
                        .onComplete((player1, text) -> {
                            arena.setDisplayName(text);
                            player.sendMessage(Colorize.format("&aSet the arena display name to: &e" + arena.getDisplayName()));
                            return AnvilGUI.Response.close();
                        }).open(player);
                // Island Chest remover
            } else if (itemFlag.getBoolean("SetArenaCenter")) {
                event.setCancelled(true);
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    arena.setCenterLocation(event.getClickedBlock().getLocation());
                } else {
                    arena.setCenterLocation(player.getLocation());
                }
                player.sendMessage(Colorize.format("&aSet the &ecenter &afor the arena"));

                // Arena Corner1 setter
            } else if (itemFlag.getBoolean("SetSpectatorSpawn")) {
                event.setCancelled(true);
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    arena.setSpectatorSpawnLocation(event.getClickedBlock().getLocation());
                } else {
                    arena.setSpectatorSpawnLocation(player.getLocation());
                }
                player.sendMessage(Colorize.format("&aSet the &espectator spawn &afor the arena"));

                // Arena Island Chests setters
            } else if (itemFlag.getBoolean("SetMainSpawn")) {
                event.setCancelled(true);
                Vector lookingAt = player.getLocation().getDirection();
                arena.setSpawnLocation(player.getLocation().clone().getBlock().getLocation().add(0.5, 0, 0.5).setDirection(lookingAt));
                player.sendMessage(Colorize.format("&aSet the &espawn"));

                // Arena Island Chests setters
            } else if (itemFlag.getBoolean("SetWaitingLobby")) {
                event.setCancelled(true);
                Vector lookingAt = player.getLocation().getDirection();
                arena.setWaitingLobby(player.getLocation().clone().getBlock().getLocation().add(0.5, 0, 0.5).setDirection(lookingAt));
                player.sendMessage(Colorize.format("&aSet the &espawn"));

                // Arena Island Chests setters
            } else if (itemFlag.getBoolean("SetRadius")) {
                event.setCancelled(true);
                arena.setWorldBorderRadius(1500);
                player.sendMessage(Colorize.format("&aSet the radius to 200"));

                // Arena Middle Chests Setter
            } else if (itemFlag.getBoolean("SaveArena")) {
                event.setCancelled(true);

                if (arena.getName() == null || arena.getName().isEmpty()) {
                    player.sendMessage(Colorize.format("&cPlease set the name of the arena"));
                    return;
                }
                if (arena.getDisplayName() == null || arena.getDisplayName().isEmpty()) {
                    player.sendMessage(Colorize.format("&cPlease set the displayname of the arena"));
                    return;
                }
                if (arena.getCenterLocation() == null) {
                    player.sendMessage(Colorize.format("&cPlease set the center of the arena"));
                    return;
                }
                if (arena.getWorldBorderRadius() == 0) {
                    player.sendMessage(Colorize.format("&cPlease set the worldborder of the arena"));
                    return;
                }
                if (arena.getSpectatorSpawnLocation() == null) {
                    player.sendMessage(Colorize.format("&cPlease set the spectator spawn of the arena"));
                    return;
                }
                if (arena.getWaitingLobby() == null) {
                    player.sendMessage(Colorize.format("&cPlease set the waiting spawn of the arena the arena"));
                    return;
                }
                Arena saved = arena.toArena();
                gameManager.getArenaManager().addArena(saved);
                gameManager.getConfigManager().saveArena(saved);
                player.sendMessage(Colorize.format("&a&lArena succesfully created!"));
                stopWizard(player);
            } else if (itemFlag.getBoolean("CancelArena")) {
                event.setCancelled(true);
                stopWizard(player);
            }
            //else if the action is any of the left clicks.
        } else if (event.getAction() != Action.PHYSICAL) {
            //  Removes the last Spawns/Chests if the item is left clicked instead of rightClicked
            if (itemFlag.getBoolean("SetArenaName")) {
                event.setCancelled(true);
                new AnvilGUI.Builder()
                        .plugin(gameManager.getPlugin())
                        .itemLeft(new ItemStack(Material.PAPER))
                        .title("Enter the arena name")
                        .onComplete((player1, text) -> {
                            if (gameManager.getArenaManager().getArenas().stream().anyMatch(allArenas -> allArenas.getName().equalsIgnoreCase(text))) {
                                player.sendMessage(Colorize.format("&cAn arena with that name already exists!"));
                                return AnvilGUI.Response.text("Invalid name");
                            }
                            arena.setName(text);
                            player.sendMessage(Colorize.format("&aSet the arena name to: &e") + arena.getName());
                            return AnvilGUI.Response.close();
                        }).open(player);
                // Arena centerLocation
            }


        }

    }


}
