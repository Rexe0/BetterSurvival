package me.rexe0.bettersurvival.smithing;

import org.bukkit.Material;

import java.awt.*;

public enum SmithingOre {
    COPPER(new Color(214, 109, 72), "Copper", 5, 1, 1, Material.RAW_COPPER, Material.COPPER_INGOT),
    IRON(new Color(198, 198, 198), "Iron", 10, 1.5f, 1.4f, Material.RAW_IRON, Material.IRON_INGOT),
    GOLD(new Color(233, 177, 21), "Gold", 6, 0.7f, 0.9f, Material.RAW_GOLD, Material.GOLD_INGOT),
    DIAMOND(new Color(74, 237, 217), "Diamond", 12, 3.1f, 8.3f, Material.DIAMOND),
    NETHERITE(new Color(93, 86, 93), "Netherite", 15, 3.5f, 10.8f, Material.NETHERITE_SCRAP, Material.ANCIENT_DEBRIS),
    AMETHYST(new Color(157, 111, 242), "Amethyst", 8, 1.5f, 4f, Material.AMETHYST_SHARD),
    QUARTZ(new Color(229, 223, 214), "Quartz", 7, 1.2f, 2.4f, Material.QUARTZ),
    EMERALD(new Color(14, 183, 9), "Emerald", 11, 1.7f, 3.5f, Material.EMERALD),
    PRISMARINE(new Color(91, 165, 140), "Prismarine", 10, 1.3f, 3.5f, Material.PRISMARINE_SHARD),
    SHULKER(new Color(151, 105, 151), "Shulker", 15, 3.5f, 10.8f, Material.SHULKER_SHELL),
    ;
    private final Color color;
    private final String name;
    private final Material[] materials;
    private final int time; // Time each ore takes to smelt in seconds
    private final float armorDurabilityMultiplier;
    private final float itemDurabilityMultiplier;

    SmithingOre(Color color, String name, int time, float armorDurabilityMultiplier, float itemDurabilityMultiplier, Material... materials) {
        this.color = color;
        this.name = name;
        this.time = time;
        this.armorDurabilityMultiplier = armorDurabilityMultiplier;
        this.itemDurabilityMultiplier = itemDurabilityMultiplier;
        this.materials = materials;
    }


    public String getName() {
        return name;
    }

    public int getTime() {
        return time;
    }

    public float getArmorDurabilityMultiplier() {
        return armorDurabilityMultiplier;
    }

    public float getItemDurabilityMultiplier() {
        return itemDurabilityMultiplier;
    }

    public Material[] getMaterials() {
        return materials;
    }

    public Color getColor() {
        return color;
    }

    public static SmithingOre getFromMaterial(Material material) {
        for (SmithingOre ore : values())
            for (Material mat : ore.getMaterials())
                if (mat == material) return ore;
        return null;
    }
}
