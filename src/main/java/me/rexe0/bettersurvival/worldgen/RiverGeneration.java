package me.rexe0.bettersurvival.worldgen;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.BrushableBlock;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class RiverGeneration extends BlockPopulator {
    private static final float BRUSHABLE_BLOCK_MULTIPLIER = 0.1f;

    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion) {
        Material material;

        for (int x = 0; x < 16; x++) {
            for (int y = 50; y < 80; y++) {
                for (int z = 0; z < 16; z++) {
                    if (limitedRegion.getBiome(x + chunkX * 16, y, z + chunkZ * 16) != Biome.RIVER) continue;

                    material = limitedRegion.getType(x + chunkX * 16, y, z + chunkZ * 16);
                    if (material != Material.SAND && material != Material.GRAVEL) continue;

                    if (random.nextDouble() > BRUSHABLE_BLOCK_MULTIPLIER) continue;
                    limitedRegion.setType(x + chunkX * 16, y, z + chunkZ * 16, material == Material.SAND ? Material.SUSPICIOUS_SAND : Material.SUSPICIOUS_GRAVEL);

                    BrushableBlock brushableBlock = (BrushableBlock) limitedRegion.getBlockState(x + chunkX * 16, y, z + chunkZ * 16);
                    // 50% for junk (seagrass or clay), 40% for nugget, 8% for raw gold, 2% for raw gold block
                    double rand = random.nextDouble();
                    Material drop = rand < 0.5 ? (material == Material.SAND ? Material.SEAGRASS : Material.CLAY_BALL)
                            : rand < 0.9 ? Material.GOLD_NUGGET
                            : rand < 0.98 ? Material.RAW_GOLD
                            : Material.RAW_GOLD_BLOCK;
                    brushableBlock.setItem(new ItemStack(drop));
                    brushableBlock.update();
                }
            }
        }
    }
}
