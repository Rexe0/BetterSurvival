package me.rexe0.bettersurvival.fishing;

import org.bukkit.ChatColor;
import org.bukkit.block.Biome;
import org.bukkit.potion.PotionEffectType;

public enum BiomeGroup {
    OCEAN(ChatColor.BLUE+"Ocean", new Biome[]{Biome.OCEAN, Biome.DEEP_OCEAN, Biome.COLD_OCEAN, Biome.DEEP_COLD_OCEAN}, PotionEffectType.DOLPHINS_GRACE, 50),
    WARM_OCEAN(ChatColor.BLUE+"Warm Ocean", new Biome[]{Biome.WARM_OCEAN, Biome.LUKEWARM_OCEAN, Biome.DEEP_LUKEWARM_OCEAN}, PotionEffectType.WATER_BREATHING, 50),
    FROZEN_OCEAN(ChatColor.AQUA+"Frozen Ocean",new Biome[]{Biome.FROZEN_OCEAN, Biome.DEEP_FROZEN_OCEAN}, PotionEffectType.ABSORPTION, 7),
    RIVER(ChatColor.BLUE+"River", new Biome[]{Biome.RIVER, Biome.FROZEN_RIVER}, PotionEffectType.HUNGER, 4),
    JUNGLE(ChatColor.DARK_GREEN+"Jungle", new Biome[]{Biome.JUNGLE, Biome.SPARSE_JUNGLE, Biome.BAMBOO_JUNGLE}, PotionEffectType.SATURATION, 5),
    CAVERNS(ChatColor.DARK_GRAY+"Caverns", new Biome[]{}, PotionEffectType.DAMAGE_RESISTANCE, 16),
    FOREST(ChatColor.GREEN+"Forest", new Biome[]{}, PotionEffectType.REGENERATION, 20);

    private final String name;
    private final Biome[] biomes;
    private final PotionEffectType effect;
    private final int effectDivisor; // The weight of the fish is divided by the effect divisor and rounded down to get the amplifier of the effect


    BiomeGroup(String name, Biome[] biomes, PotionEffectType effect, int effectDivisor) {
        this.name = name;
        this.biomes = biomes;
        this.effect = effect;
        this.effectDivisor = effectDivisor;
    }

    public String getName() {
        return name;
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
