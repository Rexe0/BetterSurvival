package me.rexe0.bettersurvival.constructs;

import org.bukkit.Material;
import org.bukkit.inventory.RecipeChoice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Modification {
    private final String name;
    private final Material icon;
    private final double health;
    private final double armor;
    private final double speed;
    private final double acceleration;

    private final Map<RecipeChoice, Integer> craftCost;
    private final Map<RecipeChoice, Integer> researchCost;

    protected int id;

    public Modification(String name, Material icon, double health, double armor, double speed, double acceleration) {
        this.name = name;
        this.icon = icon;
        this.health = health;
        this.armor = armor;
        this.speed = speed;
        this.acceleration = acceleration;
        this.craftCost = new HashMap<>();
        this.researchCost = new HashMap<>();
    }
    public abstract List<String> getDescription();

    public String getName() {
        return name;
    }
    public Material getIcon() {
        return icon;
    }

    public double getHealth() {
        return health;
    }

    public double getArmor() {
        return armor;
    }

    public double getSpeed() {
        return speed;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public int getId() {
        return id;
    }

    public void addResearchCost(RecipeChoice choice, int amount) {
        researchCost.put(choice, amount);
    }
    public void addResearchCost(Material material, int amount) {
        addResearchCost(new RecipeChoice.MaterialChoice(material), amount);
    }
    public Map<RecipeChoice, Integer> getResearchCost() {
        return researchCost;
    }

    public void addCraftCost(RecipeChoice choice, int amount) {
        craftCost.put(choice, amount);
    }
    public void addCraftCost(Material material, int amount) {
        addCraftCost(new RecipeChoice.MaterialChoice(material), amount);
    }
    public Map<RecipeChoice, Integer> getCraftCost() {
        return craftCost;
    }
}
