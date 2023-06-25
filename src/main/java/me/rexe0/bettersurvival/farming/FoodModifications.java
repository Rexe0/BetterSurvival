package me.rexe0.bettersurvival.farming;

import me.rexe0.bettersurvival.BetterSurvival;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FoodModifications implements Listener {
    @EventHandler
    public void onDrink(PlayerItemConsumeEvent e) {
        if (e.getItem().getType() != Material.HONEY_BOTTLE) return;
        e.getPlayer().removePotionEffect(PotionEffectType.WITHER);
    }
    public static ShapelessRecipe getSuspiciousStewRecipe() {
        ItemStack result = new ItemStack(Material.SUSPICIOUS_STEW);
        SuspiciousStewMeta meta = (SuspiciousStewMeta) result.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.LUCK, 6000, 0), true);
        result.setItemMeta(meta);

        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(BetterSurvival.getInstance(), "pitcher_suspicious_stew"), result);

        recipe.addIngredient(Material.PITCHER_PLANT);
        recipe.addIngredient(Material.RED_MUSHROOM);
        recipe.addIngredient(Material.BROWN_MUSHROOM);
        recipe.addIngredient(Material.BOWL);
        return recipe;
    }
}
