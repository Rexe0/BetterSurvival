package me.rexe0.bettersurvival.fishing;

import org.bukkit.block.Biome;

public enum BiomeGroup {
    OCEAN(new Biome[]{Biome.OCEAN, Biome.DEEP_OCEAN, Biome.COLD_OCEAN, Biome.DEEP_COLD_OCEAN}),
    WARM_OCEAN(new Biome[]{Biome.WARM_OCEAN, Biome.LUKEWARM_OCEAN, Biome.DEEP_LUKEWARM_OCEAN}),
    FROZEN_OCEAN(new Biome[]{Biome.FROZEN_OCEAN, Biome.DEEP_FROZEN_OCEAN}),
    RIVER(new Biome[]{Biome.RIVER, Biome.FROZEN_RIVER}),
    JUNGLE(new Biome[]{Biome.JUNGLE, Biome.SPARSE_JUNGLE, Biome.BAMBOO_JUNGLE}),
    CAVERNS(new Biome[]{}),
    FOREST(new Biome[]{});

    private final Biome[] biomes;

    BiomeGroup(Biome[] biomes) {
        this.biomes = biomes;
    }


    public Biome[] getBiomes() {
        return biomes;
    }
}
