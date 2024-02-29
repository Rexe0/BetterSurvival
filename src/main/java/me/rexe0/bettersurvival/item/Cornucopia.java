package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.BetterSurvival;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Cornucopia extends Item {
    private final int FOOD_INCREASE = 4;
    private final int SATURATION_INCREASE = 6;
    public Cornucopia() {
        super(Material.GOLDEN_CARROT, ChatColor.GREEN+"Cornucopia", "CORNUCOPIA");
    }

    @Override
    public void onConsume(Player player) {
        player.setFoodLevel(Math.min(20, player.getFoodLevel()+FOOD_INCREASE));
        player.setSaturation(Math.min(player.getFoodLevel(), player.getSaturation()+SATURATION_INCREASE));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 0, true, false));
    }

    @Override
    public Recipe getRecipe() {
        ItemStack item = ItemType.CORNUCOPIA.getItem().getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), item);
        recipe.shape(" % ", "#@#", "&%&");
        recipe.setIngredient('#', Material.GOLDEN_CARROT);
        recipe.setIngredient('@', Material.HAY_BLOCK);
        recipe.setIngredient('&', Material.BEETROOT);
        recipe.setIngredient('%', Material.BAKED_POTATO);

        return recipe;
    }
}
