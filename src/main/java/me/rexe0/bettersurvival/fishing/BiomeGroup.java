package me.rexe0.bettersurvival.fishing;

import org.bukkit.ChatColor;
import org.bukkit.block.Biome;
import org.bukkit.potion.PotionEffectType;

import java.awt.*;

public enum BiomeGroup {
    OCEAN(ChatColor.BLUE+"Ocean", new Biome[]{Biome.OCEAN, Biome.DEEP_OCEAN, Biome.COLD_OCEAN, Biome.DEEP_COLD_OCEAN}, PotionEffectType.DOLPHINS_GRACE, 50),
    WARM_OCEAN(ChatColor.BLUE+"Warm Ocean", new Biome[]{Biome.WARM_OCEAN, Biome.LUKEWARM_OCEAN, Biome.DEEP_LUKEWARM_OCEAN}, PotionEffectType.HASTE, 20),
    FROZEN_OCEAN(ChatColor.AQUA+"Frozen Ocean",new Biome[]{Biome.FROZEN_OCEAN, Biome.DEEP_FROZEN_OCEAN}, PotionEffectType.ABSORPTION, 7),
    RIVER(ChatColor.BLUE+"River", new Biome[]{Biome.RIVER, Biome.FROZEN_RIVER}, PotionEffectType.HUNGER, 4),
    JUNGLE(ChatColor.DARK_GREEN+"Jungle", new Biome[]{Biome.JUNGLE, Biome.SPARSE_JUNGLE, Biome.BAMBOO_JUNGLE}, PotionEffectType.SATURATION, 5),
    CAVERNS(ChatColor.DARK_GRAY+"Caverns", new Biome[]{}, PotionEffectType.RESISTANCE, 16),
    FOREST(ChatColor.GREEN+"Forest", new Biome[]{}, PotionEffectType.REGENERATION, 20),

    LAVA(ChatColor.GOLD+"Lava", new Biome[]{}, PotionEffectType.FIRE_RESISTANCE, 200),
    NETHER_WASTES(net.md_5.bungee.api.ChatColor.of(new Color(184, 38, 28))+"Nether Wastes", new Biome[]{Biome.NETHER_WASTES}, PotionEffectType.FIRE_RESISTANCE, 200),
    SOUL_SAND_VALLEY(net.md_5.bungee.api.ChatColor.of(new Color(39, 130, 130))+"Soul Sand Valley", new Biome[]{Biome.SOUL_SAND_VALLEY}, PotionEffectType.FIRE_RESISTANCE, 200),
    CRIMSON_FOREST(net.md_5.bungee.api.ChatColor.of(new Color(220, 20, 60))+"Crimson Forest", new Biome[]{Biome.CRIMSON_FOREST}, PotionEffectType.FIRE_RESISTANCE, 200),
    WARPED_FOREST(net.md_5.bungee.api.ChatColor.of(new Color(29, 173, 147))+"Warped Forest", new Biome[]{Biome.WARPED_FOREST}, PotionEffectType.FIRE_RESISTANCE, 200),
    BASALT_DELTAS(net.md_5.bungee.api.ChatColor.of(new Color(181, 165, 183))+"Basalt Deltas", new Biome[]{Biome.BASALT_DELTAS}, PotionEffectType.FIRE_RESISTANCE, 200),
    THE_NETHER(net.md_5.bungee.api.ChatColor.of(new Color(191, 32, 17))+"The Nether", new Biome[]{Biome.NETHER_WASTES, Biome.SOUL_SAND_VALLEY, Biome.CRIMSON_FOREST, Biome.WARPED_FOREST, Biome.BASALT_DELTAS}, PotionEffectType.FIRE_RESISTANCE, 200),

    ANY_OCEAN(ChatColor.BLUE+"Any Ocean", new Biome[]{Biome.OCEAN, Biome.DEEP_OCEAN, Biome.COLD_OCEAN, Biome.DEEP_COLD_OCEAN,
            Biome.WARM_OCEAN, Biome.LUKEWARM_OCEAN, Biome.DEEP_LUKEWARM_OCEAN,
            Biome.FROZEN_OCEAN, Biome.DEEP_FROZEN_OCEAN}, PotionEffectType.HEALTH_BOOST, 45);

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
