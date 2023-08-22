package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.item.ItemType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;

public class Magnet extends Item {
    public Magnet() {
        super(Material.IRON_NUGGET, ChatColor.GREEN+"Magnet", "MAGNET");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY+"Bait");
        lore.add(ChatColor.GRAY+"Increases the chance of catching");
        lore.add(ChatColor.GRAY+"treasure whilst fishing.");
        return lore;
    }

    public ShapedRecipe getRecipe() {
        ItemStack item = ItemType.MAGNET.getItem().getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), item);
        recipe.shape("###", "#@#", "###");
        recipe.setIngredient('#', Material.IRON_NUGGET);
        recipe.setIngredient('@', Material.COPPER_INGOT);

        return recipe;
    }
}
