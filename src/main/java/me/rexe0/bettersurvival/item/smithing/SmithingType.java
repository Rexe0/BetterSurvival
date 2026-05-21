package me.rexe0.bettersurvival.item.smithing;

import me.rexe0.bettersurvival.smithing.SmithingOre;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.awt.*;

public enum SmithingType {
    SWORD("Sword"),
    SPEAR("Spear"),
    AXE("Axe"),
    PICKAXE("Pickaxe"),
    SHOVEL("Shovel"),
    HOE("Hoe"),
    HELMET("Helmet"),
    CHESTPLATE("Chestplate"),
    LEGGINGS("Leggings"),
    BOOTS("Boots"),
    HORSE_ARMOR("Horse Armor"),
    NAUTILUS_ARMOR("Nautilus Armor"),
    ;

    private final String name;

    SmithingType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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
            case NAUTILUS_ARMOR -> switch (ore) {
                case COPPER -> new ItemStack(Material.COPPER_NAUTILUS_ARMOR);
                case IRON -> new ItemStack(Material.IRON_NAUTILUS_ARMOR);
                case GOLD -> new ItemStack(Material.GOLDEN_NAUTILUS_ARMOR);
                case NETHERITE -> new ItemStack(Material.NETHERITE_NAUTILUS_ARMOR);
                default -> new ItemStack(Material.DIAMOND_NAUTILUS_ARMOR);
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
            case HORSE_ARMOR -> switch (ore) {
                case COPPER -> new ItemStack(Material.COPPER_HORSE_ARMOR);
                case IRON -> new ItemStack(Material.IRON_HORSE_ARMOR);
                case GOLD -> new ItemStack(Material.GOLDEN_HORSE_ARMOR);
                case DIAMOND -> new ItemStack(Material.DIAMOND_HORSE_ARMOR);
                case NETHERITE -> new ItemStack(Material.NETHERITE_HORSE_ARMOR);
                case AMETHYST -> getColoredArmor(Material.LEATHER_HORSE_ARMOR, SmithingOre.AMETHYST.getColor());
                case QUARTZ -> getColoredArmor(Material.LEATHER_HORSE_ARMOR, SmithingOre.QUARTZ.getColor());
                case EMERALD -> getColoredArmor(Material.LEATHER_HORSE_ARMOR, SmithingOre.EMERALD.getColor());
                case PRISMARINE -> getColoredArmor(Material.LEATHER_HORSE_ARMOR, SmithingOre.PRISMARINE.getColor());
                case SHULKER -> getColoredArmor(Material.LEATHER_HORSE_ARMOR, SmithingOre.SHULKER.getColor());
            };
        };
    }

    private static ItemStack getColoredArmor(Material material, Color color) {
        ItemStack item = new ItemStack(material);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(org.bukkit.Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()));
        item.setItemMeta(meta);
        return item;
    }
}
