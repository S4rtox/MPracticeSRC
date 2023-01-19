package me.s4rtox.mpractice.handlers.gamehandlers.arena.worldmanagers;

import com.grinderwolf.swm.api.world.SlimeWorld;
import org.bukkit.Bukkit;

import java.io.File;

public class LocalGameMap implements GameMap{
    private final File sourceWorldFolder;
    private File activeWorldFolder;

    private SlimeWorld slimeWorld;

    public LocalGameMap(File worldFolder, String worldName, boolean loadOnInit) {
        this.sourceWorldFolder = new File(worldFolder, worldName);
        if(loadOnInit) load();
    }

    @Override
    public boolean load() {
        if (isLoaded()) return true;
        this.activeWorldFolder = new File(Bukkit.getWorldContainer().getParent());
        return false;
    }

    @Override
    public void unload() {

    }

    @Override
    public boolean restoreFromSource() {
        return false;
    }

    @Override
    public boolean isLoaded() {
        return false;
    }

    @Override
    public SlimeWorld getWorld() {
        return null;
    }
}
