package me.rexe0.bettersurvival.fishing;

import org.bukkit.block.Biome;
import org.bukkit.potion.PotionEffectType;

public enum BiomeGroup {
    OCEAN(new Biome[]{Biome.OCEAN, Biome.DEEP_OCEAN, Biome.COLD_OCEAN, Biome.DEEP_COLD_OCEAN}, PotionEffectType.DOLPHINS_GRACE, 50),
    WARM_OCEAN(new Biome[]{Biome.WARM_OCEAN, Biome.LUKEWARM_OCEAN, Biome.DEEP_LUKEWARM_OCEAN}, PotionEffectType.WATER_BREATHING, 50),
    FROZEN_OCEAN(new Biome[]{Biome.FROZEN_OCEAN, Biome.DEEP_FROZEN_OCEAN}, PotionEffectType.ABSORPTION, 7),
    RIVER(new Biome[]{Biome.RIVER, Biome.FROZEN_RIVER}, PotionEffectType.HUNGER, 4),
    JUNGLE(new Biome[]{Biome.JUNGLE, Biome.SPARSE_JUNGLE, Biome.BAMBOO_JUNGLE}, PotionEffectType.SATURATION, 5),
    CAVERNS(new Biome[]{}, PotionEffectType.DAMAGE_RESISTANCE, 16),
    FOREST(new Biome[]{}, PotionEffectType.REGENERATION, 20);

    private final Biome[] biomes;
    private final PotionEffectType effect;
    private final int effectDivisor; // The weight of the fish is divided by the effect divisor and rounded down to get the amplifier of the effect
    BiomeGroup(Biome[] biomes, PotionEffectType effect, int effectDivisor) {
        this.biomes = biomes;
        this.effect = effect;
        this.effectDivisor = effectDivisor;
    }

    public int getEffectDivisor() {
        return effectDivisor;
    }

    public PotionEffectType getEffect() {
        return effect;
    }

    public Biome[] getBiomes() {
        return biomes;
    }
}
