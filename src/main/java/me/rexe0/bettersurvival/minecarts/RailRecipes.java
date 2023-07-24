package me.rexe0.bettersurvival.minecarts;

import me.rexe0.bettersurvival.BetterSurvival;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class RailRecipes {
    public static ShapedRecipe getRailRecipe() {
        ItemStack item = new ItemStack(Material.RAIL, 4);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), "rail"), item);
        recipe.shape("# #", "#@#", "# #");
        recipe.setIngredient('#', Material.IRON_NUGGET);
        recipe.setIngredient('@', Material.STICK);

        return recipe;
    }
}
