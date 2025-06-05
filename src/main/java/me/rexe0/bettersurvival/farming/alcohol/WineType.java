package me.rexe0.bettersurvival.farming.alcohol;

import org.bukkit.Color;
import org.bukkit.Material;

public enum WineType {
    SWEET_BERRY("Sweet Berry Wine", Material.SWEET_BERRIES),
    CHORUS_FRUIT("Chorus Wine", Material.CHORUS_FRUIT),
    APPLE("Apple Cider", Material.APPLE),
    GOLDEN_APPLE("Golden Apple Cider", Material.GOLDEN_APPLE);



    private String name;
    private Material fruit;
    private Color color;

    WineType(String name, Material fruit) {
        this(name, fruit, Color.fromRGB(173, 14, 40));
    }

    WineType(String name, Material fruit, Color color) {
        this.name = name;
        this.fruit = fruit;
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
}
