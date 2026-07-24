package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.item.ItemType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import java.util.ArrayList;
import java.util.List;

public class TungstenFishingRod extends Item {
    public TungstenFishingRod() {
        super(Material.FISHING_ROD, ChatColor.GREEN+"Tungsten Fishing Rod", "TUNGSTEN_FISHING_ROD");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"Can fish in "+ChatColor.GOLD+"Lava"+ChatColor.GRAY+".");
        lore.add(ChatColor.GRAY+"Can use Bait and Tackle when fishing.");
        return lore;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        Damageable damageable = (Damageable) item.getItemMeta();
        damageable.setMaxDamage(150);
        damageable.setFireResistant(true);
        item.setItemMeta(damageable);
        return item;
    }
    public ShapedRecipe getRecipe() {
        ItemStack item = ItemType.TUNGSTEN_FISHING_ROD.getItem().getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), item);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        recipe.shape("  #", " #$", "# $");
        recipe.setIngredient('#', new RecipeChoice.ExactChoice(ItemType.TUNGSTEN_CRYSTAL.getItem().getItem()));
        recipe.setIngredient('$', Material.STRING);
        return recipe;
    }
}