package me.rexe0.bettersurvival.farming.alcohol;

import org.bukkit.Color;
import org.bukkit.Material;

public enum WineType {
    SWEET_BERRY("Sweet Berry Wine", Material.SWEET_BERRIES, 8),
    GLOW_BERRY("Glow Berry Wine", Material.GLOW_BERRIES, 4),
    MELON("Melon Wine", Material.GLOW_BERRIES, 4),
    CHORUS_FRUIT("Chorus Wine", Material.CHORUS_FRUIT, 8),
    APPLE("Apple Cider", Material.APPLE, 4),
    GOLDEN_APPLE("Golden Apple Cider", Material.GOLDEN_APPLE, 2),
    BEER("Beer", Material.WHEAT, 16),
    SUGAR_WASH("Sugar Wash", Material.SUGAR, 2);



    private String name;
    private Material fruit;
    private int fruitCost; // How many fruits are needed to make 1% equivalent
    private Color color;

    WineType(String name, Material fruit, int fruitCost) {
        this(name, fruit, fruitCost, Color.fromRGB(173, 14, 40));
    }

    WineType(String name, Material fruit, int fruitCost, Color color) {
        this.name = name;
        this.fruit = fruit;
        this.fruitCost = fruitCost;
        this.color = color;
    }

    public String getName() {
        return name;
    }
    public Material getFruit() {
        return fruit;
    }

    public Color getColor() {
        return color;
    }

    public int getFruitCost() {
        return fruitCost;
    }

    public static WineType getWineType(Material fruit) {
        for (WineType type : values())
            if (type.getFruit() == fruit)
                return type;

        return null;
    }
}
