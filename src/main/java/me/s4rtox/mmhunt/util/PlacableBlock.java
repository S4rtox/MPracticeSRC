package me.s4rtox.mmhunt.util;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;

import java.util.UUID;

/**
 * An arbitrary implementation for Workload that changes
 * a single Block to a given Material.
 */
@AllArgsConstructor
public class PlacableBlock implements Workload {

    private final Location location;
    private final Material material;

    @Override
    public void compute() {
        FastBlockPlacer.rapidSetBlock(((CraftWorld) location.getWorld()).getHandle(), FastBlockPlacer.fromMaterial(material), location.getBlockX(),location.getBlockY(), location.getBlockZ());
    }

}