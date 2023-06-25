package me.rexe0.bettersurvival.worldgen;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.BrushableBlock;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class DesertGeneration extends BlockPopulator {
    private static final double BRUSHABLE_BLOCK_MULTIPLIER = 0.01;
    private Material[] junk = new Material[]{
            Material.DEAD_BUSH,
            Material.STICK,
            Material.SAND,
            Material.CACTUS,
            Material.RABBIT_HIDE
    };
    private Material[] treasure = new Material[]{
            Material.DIAMOND,
            Material.EMERALD,
    };


    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion) {
        for (int x = 0; x < 16; x++) {
            for (int y = 60; y < 77; y++) {
                for (int z = 0; z < 16; z++) {
                    if (limitedRegion.getBiome(x + chunkX * 16, y, z + chunkZ * 16) != Biome.DESERT) continue;

                    if (limitedRegion.getType(x + chunkX * 16, y, z + chunkZ * 16) != Material.SAND) continue;

                    if (random.nextDouble() > BRUSHABLE_BLOCK_MULTIPLIER) continue;
                    limitedRegion.setType(x + chunkX * 16, y, z + chunkZ * 16, Material.SUSPICIOUS_SAND);

                    BrushableBlock brushableBlock = (BrushableBlock) limitedRegion.getBlockState(x + chunkX * 16, y, z + chunkZ * 16);

                    // 10% chance for treasure, 90% chance for junk
                    double rand = random.nextDouble();
                    Material drop = rand < 0.9 ? junk[random.nextInt(junk.length)] : treasure[random.nextInt(treasure.length)];
                    brushableBlock.setItem(new ItemStack(drop));
                    brushableBlock.update();
                }
            }
        }
    }
}
