package me.rexe0.bettersurvival.item.smithing;

import me.rexe0.bettersurvival.smithing.SmithingOre;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public enum SmithingType {
    SWORD("Sword", 95),
    SPEAR("Spear", 190),
    AXE("Axe", 64),
    PICKAXE("Pickaxe", 64),
    SHOVEL("Shovel", 190),
    HOE("Hoe", 95),
    HELMET("Helmet", 25),
    CHESTPLATE("Chestplate", 22),
    LEGGINGS("Leggings", 24),
    BOOTS("Boots", 36),
    ;

    private final String name;
    private final int durabilityPerMaterial;

    SmithingType(String name, int durabilityPerMaterial) {
        this.name = name;
        this.durabilityPerMaterial = durabilityPerMaterial;
    }

    public String getName() {
        return name;
    }

    public boolean isArmor() {
        return this == HELMET || this == CHESTPLATE || this == LEGGINGS || this == BOOTS;
    }

    public ItemStack getItem(SmithingOre ore) {
        return switch (this) {
            case SWORD -> switch (ore) {
                case COPPER -> new ItemStack(Material.COPPER_SWORD);
                case IRON -> new ItemStack(Material.IRON_SWORD);
                case GOLD -> new ItemStack(Material.GOLDEN_SWORD);
                case NETHERITE -> new ItemStack(Material.NETHERITE_SWORD);
                default -> new ItemStack(Material.DIAMOND_SWORD);
            };
            case SPEAR -> switch (ore) {
                case COPPER -> new ItemStack(Material.COPPER_SPEAR);
                case IRON -> new ItemStack(Material.IRON_SPEAR);
                case GOLD -> new ItemStack(Material.GOLDEN_SPEAR);
                case NETHERITE -> new ItemStack(Material.NETHERITE_SPEAR);
                default -> new ItemStack(Material.DIAMOND_SPEAR);
            };
            case AXE -> switch (ore) {
                case COPPER -> new ItemStack(Material.COPPER_AXE);
                case IRON -> new ItemStack(Material.IRON_AXE);
                case GOLD -> new ItemStack(Material.GOLDEN_AXE);
                case NETHERITE -> new ItemStack(Material.NETHERITE_AXE);
                default -> new ItemStack(Material.DIAMOND_AXE);
            };
            case PICKAXE -> switch (ore) {
                case COPPER -> new ItemStack(Material.COPPER_PICKAXE);
                case IRON -> new ItemStack(Material.IRON_PICKAXE);
                case GOLD -> new ItemStack(Material.GOLDEN_PICKAXE);
                case NETHERITE -> new ItemStack(Material.NETHERITE_PICKAXE);
                default -> new ItemStack(Material.DIAMOND_PICKAXE);
            };
            case SHOVEL -> switch (ore) {
                case COPPER -> new ItemStack(Material.COPPER_SHOVEL);
                case IRON -> new ItemStack(Material.IRON_SHOVEL);
                case GOLD -> new ItemStack(Material.GOLDEN_SHOVEL);
                case NETHERITE -> new ItemStack(Material.NETHERITE_SHOVEL);
                default -> new ItemStack(Material.DIAMOND_SHOVEL);
            };
            case HOE -> switch (ore) {
                case COPPER -> new ItemStack(Material.COPPER_HOE);
                case IRON -> new ItemStack(Material.IRON_HOE);
                case GOLD -> new ItemStack(Material.GOLDEN_HOE);
                case NETHERITE -> new ItemStack(Material.NETHERITE_HOE);
                default -> new ItemStack(Material.DIAMOND_HOE);
            };
            case HELMET -> switch (ore) {
                case COPPER -> new ItemStack(Material.COPPER_HELMET);
                case IRON -> new ItemStack(Material.IRON_HELMET);
                case GOLD -> new ItemStack(Material.GOLDEN_HELMET);
                case DIAMOND -> new ItemStack(Material.DIAMOND_HELMET);
                case NETHERITE -> new ItemStack(Material.NETHERITE_HELMET);
                case AMETHYST -> getColoredArmor(Material.LEATHER_HELMET, SmithingOre.AMETHYST.getColor());
                case QUARTZ -> getColoredArmor(Material.LEATHER_HELMET, SmithingOre.QUARTZ.getColor());
                case EMERALD -> getColoredArmor(Material.LEATHER_HELMET, SmithingOre.EMERALD.getColor());
                case PRISMARINE -> getColoredArmor(Material.LEATHER_HELMET, SmithingOre.PRISMARINE.getColor());
                case SHULKER -> getColoredArmor(Material.LEATHER_HELMET, SmithingOre.SHULKER.getColor());
            };
            case CHESTPLATE -> switch (ore) {
                case COPPER -> new ItemStack(Material.COPPER_CHESTPLATE);
                case IRON -> new ItemStack(Material.IRON_CHESTPLATE);
                case GOLD -> new ItemStack(Material.GOLDEN_CHESTPLATE);
                case DIAMOND -> new ItemStack(Material.DIAMOND_CHESTPLATE);
                case NETHERITE -> new ItemStack(Material.NETHERITE_CHESTPLATE);
                case AMETHYST -> getColoredArmor(Material.LEATHER_CHESTPLATE, SmithingOre.AMETHYST.getColor());
                case QUARTZ -> getColoredArmor(Material.LEATHER_CHESTPLATE, SmithingOre.QUARTZ.getColor());
                case EMERALD -> getColoredArmor(Material.LEATHER_CHESTPLATE, SmithingOre.EMERALD.getColor());
                case PRISMARINE -> getColoredArmor(Material.LEATHER_CHESTPLATE, SmithingOre.PRISMARINE.getColor());
                case SHULKER -> getColoredArmor(Material.LEATHER_CHESTPLATE, SmithingOre.SHULKER.getColor());
            };
            case LEGGINGS -> switch (ore) {
                case COPPER -> new ItemStack(Material.COPPER_LEGGINGS);
                case IRON -> new ItemStack(Material.IRON_LEGGINGS);
                case GOLD -> new ItemStack(Material.GOLDEN_LEGGINGS);
                case DIAMOND -> new ItemStack(Material.DIAMOND_LEGGINGS);
                case NETHERITE -> new ItemStack(Material.NETHERITE_LEGGINGS);
                case AMETHYST -> getColoredArmor(Material.LEATHER_LEGGINGS, SmithingOre.AMETHYST.getColor());
                case QUARTZ -> getColoredArmor(Material.LEATHER_LEGGINGS, SmithingOre.QUARTZ.getColor());
                case EMERALD -> getColoredArmor(Material.LEATHER_LEGGINGS, SmithingOre.EMERALD.getColor());
                case PRISMARINE -> getColoredArmor(Material.LEATHER_LEGGINGS, SmithingOre.PRISMARINE.getColor());
                case SHULKER -> getColoredArmor(Material.LEATHER_LEGGINGS, SmithingOre.SHULKER.getColor());
            };
            case BOOTS -> switch (ore) {
                case COPPER -> new ItemStack(Material.COPPER_BOOTS);
                case IRON -> new ItemStack(Material.IRON_BOOTS);
                case GOLD -> new ItemStack(Material.GOLDEN_BOOTS);
                case DIAMOND -> new ItemStack(Material.DIAMOND_BOOTS);
                case NETHERITE -> new ItemStack(Material.NETHERITE_BOOTS);
                case AMETHYST -> getColoredArmor(Material.LEATHER_BOOTS, SmithingOre.AMETHYST.getColor());
                case QUARTZ -> getColoredArmor(Material.LEATHER_BOOTS, SmithingOre.QUARTZ.getColor());
                case EMERALD -> getColoredArmor(Material.LEATHER_BOOTS, SmithingOre.EMERALD.getColor());
                case PRISMARINE -> getColoredArmor(Material.LEATHER_BOOTS, SmithingOre.PRISMARINE.getColor());
                case SHULKER -> getColoredArmor(Material.LEATHER_BOOTS, SmithingOre.SHULKER.getColor());
            };
        };
    }

    public ItemStack getItem(Map<SmithingOre, Integer> oreAmount) {
        // Compare ores by amount, get the one with the highest amount. If two or more ores have the same amount, get the one with the highest SmithingOre ordinal()
        SmithingOre dominantOre = oreAmount.entrySet()
                .stream()
                .max(Comparator
                        .comparing(Map.Entry<SmithingOre, Integer>::getValue)
                        .thenComparing(e -> e.getKey().ordinal()))
                .map(Map.Entry::getKey)
                .orElse(null);
        if (dominantOre == null) return null;

        ItemStack item = getItem(dominantOre);
        Damageable meta = (Damageable) item.getItemMeta();

        // Display
        meta.setDisplayName(ChatColor.WHITE + dominantOre.getName() + " " + getName());
        List<String> lore = new ArrayList<>();
        for (SmithingOre ore : oreAmount.entrySet().stream()
                .sorted(Map.Entry.<SmithingOre, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList()) {
            lore.add(ChatColor.GRAY + ore.getName() + ": " + net.md_5.bungee.api.ChatColor.of(ore.getColor()) + oreAmount.get(ore)*10 +"%");
        }
        meta.setLore(lore);

        float durability = 0;
        boolean isArmor = isArmor();
        for (Map.Entry<SmithingOre, Integer> entry : oreAmount.entrySet()) {
            durability += (isArmor ? entry.getKey().getArmorDurabilityMultiplier() : entry.getKey().getItemDurabilityMultiplier()) * entry.getValue() * durabilityPerMaterial;
        }
        meta.setMaxDamage(Math.round(durability));


        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getColoredArmor(Material material, Color color) {
        ItemStack item = new ItemStack(material);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(org.bukkit.Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()));
        item.setItemMeta(meta);
        return item;
    }
}
