package me.rexe0.bettersurvival.worldgen;

import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

import java.util.Random;

public class ReduceDiamonds extends BlockPopulator {
    private static final float diamondMultiplier = 0.5f;
    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion) {
        Material material;
        for (int x = 0; x < 16; x++) {
            for (int y = -63; y < 17; y++) {
                for (int z = 0; z < 16; z++) {
                    material = limitedRegion.getType(x + chunkX * 16, y, z + chunkZ * 16);
                    if (material == Material.DIAMOND_ORE) {
                        if (random.nextInt(Math.round(1/diamondMultiplier)) != 0) {
                            limitedRegion.setType(x + chunkX * 16, y, z + chunkZ * 16, Material.STONE);
                        }
                    } else if (material == Material.DEEPSLATE_DIAMOND_ORE) {
                        if (random.nextInt(Math.round(1/diamondMultiplier)) != 0) {
                            limitedRegion.setType(x + chunkX * 16, y, z + chunkZ * 16, Material.DEEPSLATE);
                        }
                    }
                }
            }
        }
    }
}
