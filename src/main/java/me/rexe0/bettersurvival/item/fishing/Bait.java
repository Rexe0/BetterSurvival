package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.Item;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmokingRecipe;

import java.util.ArrayList;
import java.util.List;

public class Bait extends Item {
    public Bait() {
        super(Material.PUMPKIN_SEEDS, ChatColor.GREEN+"Bait", "BAIT");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY+"Bait");
        return lore;
    }

    @Override
    public List<Recipe> getRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(new SmokingRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), getItem(), new RecipeChoice.MaterialChoice(Material.ROTTEN_FLESH), 20, 100));
        recipes.add(new FurnaceRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), getItem(), new RecipeChoice.MaterialChoice(Material.ROTTEN_FLESH), 20, 200));
        return recipes;
    }
}
