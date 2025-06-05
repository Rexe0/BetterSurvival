package me.rexe0.bettersurvival.farming.alcohol;

import org.bukkit.Material;

public enum BarrelType {
    OAK("Oak", Material.OAK_PLANKS),
    SPRUCE("Spruce", Material.SPRUCE_PLANKS),
    BIRCH("Birch", Material.BIRCH_PLANKS),
    JUNGLE("Jungle", Material.JUNGLE_PLANKS),
    ACACIA("Acacia", Material.ACACIA_PLANKS),
    DARK_OAK("Dark Oak", Material.DARK_OAK_PLANKS),
    MANGROVE("Mangrove", Material.MANGROVE_PLANKS),
    CHERRY("Cherry", Material.CHERRY_PLANKS),
    PALE_OAK("Pale Oak", Material.PALE_OAK_PLANKS),
    BAMBOO("Bamboo", Material.BAMBOO_PLANKS),
    CRIMSON("Crimson", Material.CRIMSON_PLANKS),
    WARPED("Warped", Material.WARPED_PLANKS);

    private String name;
    private Material planks;

    BarrelType(String name, Material planks) {
        this.name = name;
        this.planks = planks;
    }

    public String getName() {
        return name;
    }
    public Material getPlanks() {
        return planks;
    }
}
