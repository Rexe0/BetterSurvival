package me.rexe0.bettersurvival.worldgen;

import org.bukkit.HeightMap;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.Random;

public class CoalDeposit extends BlockPopulator {
    private SimplexOctaveGenerator generator;
    public SimplexOctaveGenerator getGenerator(long seed) {
        if (generator == null) {
            generator = new SimplexOctaveGenerator(new Random(seed), 2);
            generator.setScale(0.5);
        }
        return generator;
    }

    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion) {
        SimplexOctaveGenerator generator = getGenerator(worldInfo.getSeed());

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int dx = x + chunkX * 16;
                int dz = z + chunkZ * 16;

                boolean exposed = random.nextInt(0, 120) == 0; // 1 in 120 chance for the ore to be exposed to the surface
                int y = limitedRegion.getHighestBlockYAt(dx, dz, HeightMap.OCEAN_FLOOR_WG)- (exposed ? 2 : 6);

                for (int i = -2; i < 3; i++) {
                    if (!limitedRegion.isInRegion(dx, y+i, dz)) continue;

                    double noise = generator.noise(dx, dz, 0.01, 40, true);
                    noise = noise * noise;
                    if (noise <= 0.7+Math.abs(i/20f)) continue;

                    if (!validBiome(limitedRegion.getBiome(dx, y+i, dz))) continue;

                    // Only replace solid blocks to prevent floating ore deposits and other issues
                    if (!limitedRegion.getType(dx, y+i, dz).isSolid()) continue;

                    // Ensure that no adjacent blocks are exposed to air
                    if (!exposed)
                        if (limitedRegion.getType(dx+1, y+i, dz) == Material.AIR
                        || limitedRegion.getType(dx-1, y+i, dz) == Material.AIR
                        || limitedRegion.getType(dx, y+i, dz+1) == Material.AIR
                        || limitedRegion.getType(dx, y+i, dz-1) == Material.AIR) continue;

                    limitedRegion.setType(dx, y+i, dz, Material.COAL_ORE);
                }

            }
        }
    }

    private boolean validBiome(Biome biome) {
        return biome.toString().contains("BADLANDS");
    }
}
