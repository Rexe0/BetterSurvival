package me.rexe0.bettersurvival.worldgen;

import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

import java.util.Random;

public class ReduceIron extends BlockPopulator {
    private static final float ironMultiplier = 0.5f;
    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion) {
        Material material;
        for (int x = 0; x < 16; x++) {
            for (int y = -10; y < 60; y++) {
                for (int z = 0; z < 16; z++) {
                    material = limitedRegion.getType(x + chunkX * 16, y, z + chunkZ * 16);
                    if (material == Material.IRON_ORE) {
                        if (random.nextInt(Math.round(1/ironMultiplier)) != 0) {
                            limitedRegion.setType(x + chunkX * 16, y, z + chunkZ * 16, Material.STONE);
                        }
                    } else if (material == Material.DEEPSLATE_IRON_ORE) {
                        if (random.nextInt(Math.round(1/ironMultiplier)) != 0) {
                            limitedRegion.setType(x + chunkX * 16, y, z + chunkZ * 16, Material.DEEPSLATE);
                        }
                    }
                }
            }
        }
    }
}
