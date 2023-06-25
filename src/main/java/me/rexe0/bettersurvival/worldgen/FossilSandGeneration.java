package me.rexe0.bettersurvival.worldgen;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.BrushableBlock;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class FossilSandGeneration extends BlockPopulator {
    private static final float diamondMultiplier = 0.1f;

    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int dx = x + chunkX * 16;
                int dz = z + chunkZ * 16;

                boolean foundFossil = false;
                for (int y = -63; y < 64; y++) {
                    if (limitedRegion.getBiome(dx, y, dz) != Biome.DESERT) continue;

                    if (limitedRegion.getType(dx, y, dz) != Material.BONE_BLOCK) continue;

                    // Increase diamond yield
                    if (y < 0 && random.nextDouble() <= diamondMultiplier) {
                        limitedRegion.setType(dx, y, dz, Material.DEEPSLATE_DIAMOND_ORE);
                    }
                    foundFossil = true;
                    break;
                }
                if (!foundFossil) continue;
                int y = 100;
                while (y > 30 && limitedRegion.getType(dx, y, dz) == Material.AIR)
                    y--;

                if (limitedRegion.getType(dx, y, dz) != Material.SAND) continue;

                limitedRegion.setType(dx, y, dz, Material.SUSPICIOUS_SAND);

                BrushableBlock brushableBlock = (BrushableBlock) limitedRegion.getBlockState(dx, y, dz);

                brushableBlock.setItem(new ItemStack(Material.BONE));
                brushableBlock.update();
            }
        }
    }
}
