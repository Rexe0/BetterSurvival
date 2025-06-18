package me.rexe0.bettersurvival.worldgen;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.List;
import java.util.Random;

public class DeepOceanGenerator extends ChunkGenerator {
    private static final List<Biome> OCEAN_BIOMES = List.of(
            Biome.DEEP_OCEAN,
            Biome.OCEAN,
            Biome.DEEP_COLD_OCEAN,
            Biome.COLD_OCEAN,
            Biome.DEEP_FROZEN_OCEAN,
            Biome.FROZEN_OCEAN,
            Biome.DEEP_LUKEWARM_OCEAN,
            Biome.LUKEWARM_OCEAN,
            Biome.WARM_OCEAN
    );

    private SimplexOctaveGenerator generator;
    public SimplexOctaveGenerator getGenerator(long seed) {
        if (generator == null) {
            generator = new SimplexOctaveGenerator(new Random(seed), 6);
            generator.setScale(0.008);
        }
        return generator;
    }

    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        if (worldInfo.getEnvironment() != World.Environment.NORMAL) return;
        SimplexOctaveGenerator generator = getGenerator(worldInfo.getSeed());

        int worldX = chunkX * 16;
        int worldZ = chunkZ * 16;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (!OCEAN_BIOMES.contains(chunkData.getBiome(x, 62, z))) continue;

                int floorY = getSeaFloorY(chunkData, x, z);

                int delta = 45 - floorY;
                if (delta <= 0) continue;

                double noise = generator.noise(worldX + x, worldZ + z, 1, 5, true);
                noise = (noise+1)/2; // Normalize the noise to be between 0 and 1

                double totalChange = (delta*noise);

                // Secondary noise to create deep abysses in the oceans
                noise = generator.noise(worldX + x, worldZ + z, 0.7, 5, true);
                noise = 1+((noise+1)/2); // Normalize the noise to be between 1 and 2

                if (noise > 1.7 && delta > 5)
                    totalChange *= noise;


                for (int y = floorY; y > Math.max(25, floorY-(totalChange)); y--) {
                    chunkData.setBlock(x, y, z, Material.WATER);
                }
            }
        }

    }

    @Override
    public void generateSurface(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        if (worldInfo.getEnvironment() != World.Environment.NORMAL) return;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (!OCEAN_BIOMES.contains(chunkData.getBiome(x, 62, z))) continue;
                int floorY = getSeaFloorY(chunkData, x, z);

                if (!chunkData.getBiome(x, 62, z).toString().contains("WARM_OCEAN")) continue;

                if (chunkData.getType(x, floorY, z) != Material.SAND) continue;
                chunkData.setBlock(x, floorY-1, z, Material.SAND);
            }
        }
    }

    private int getSeaFloorY(ChunkData data, int x, int z) {
        int y = 62;
        while (data.getType(x, y, z) == Material.WATER && y > data.getMinHeight())
            y--;
        return y;
    }

    @Override
    public boolean shouldGenerateNoise() {
        return true;
    }

    @Override
    public boolean shouldGenerateSurface() {
        return true;
    }

    @Override
    public boolean shouldGenerateCaves() {
        return true;
    }

    @Override
    public boolean shouldGenerateMobs() {
        return true;
    }

    @Override
    public boolean shouldGenerateDecorations() {
        return true;
    }

    @Override
    public boolean shouldGenerateStructures() {
        return true;
    }
}
