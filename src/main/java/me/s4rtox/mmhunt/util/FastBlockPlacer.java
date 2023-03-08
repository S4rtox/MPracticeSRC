package me.s4rtox.mmhunt.util;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R2.util.CraftMagicNumbers;

public class FastBlockPlacer {

    public static void rapidSetBlock(World world, IBlockData blockData, int x, int y, int z)  {
        final Chunk chunk = getChunkAt(world, x, z);
        final BlockPosition blockPosition = getBlockPosition(x,y,z);
        chunk.a(blockPosition, blockData,false);
        updateChange(world, blockPosition);
    }

    /**
     * Gets the chunk at a block's location.
     *
     * @param nmsWorld the handle of the target Bukkit world.
     * @param blockX the x location of the block (NOT chunk coordinates).
     * @param blockZ the z location of the block (NOT chunk coordinates).
     * @return the nms chunk.
     */
    public static Chunk getChunkAt(World nmsWorld, int blockX, int blockZ) {
        return nmsWorld.d(toChunkCoordinate(blockX), toChunkCoordinate(blockZ));
    }

    /**
     * Returns the chunk coordinate value of a block coordinate.
     *
     * @param blockCoordinate the block coordinate.
     * @return the chunk coordinate in respect to that block.
     */
    public static int toChunkCoordinate(final int blockCoordinate) {
        return blockCoordinate >> 4;
    }

    /**
     * Returns the block coordinate value that matches a chunk coordinate value.
     *
     * @param chunkCoordinate the chunk coordinate.
     * @return the respective block coordinate.
     */
    public static int toBlockCoordinate(final int chunkCoordinate) {
        return chunkCoordinate << 4;
    }

    /**
     * Gets the int required for knowing whether to apply physics or not.
     * @param applyPhysics true to apply physics.
     * @return an int that will enable or disable block physics when placed.
     */
    private static int getApplyPhysicsId(boolean applyPhysics) {
        return applyPhysics ? 3 : 2;
    }

    public static BlockPosition getBlockPosition(int x, int y, int z) {
        return new BlockPosition(x,y,z);
    }

    private static void updateChange(World world, BlockPosition blockPosition) {
        //world.c(EnumSkyBlock.BLOCK, blockPosition); //Fixes light but laggy.
        world.o(blockPosition);
    }

    public static IBlockData fromMaterial(Material m) {
        Block nmsBlock = CraftMagicNumbers.getBlock(m);
        return nmsBlock.n();
    }


}
