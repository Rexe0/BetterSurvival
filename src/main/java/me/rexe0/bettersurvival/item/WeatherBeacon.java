package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.weather.SeasonListener;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

public class WeatherBeacon extends Item {
    public WeatherBeacon() {
        super(Material.COMPASS, ChatColor.GREEN+"Weather Beacon", "WEATHER_BEACON");
    }

    @Override
    public boolean onRightClick(Player player) {
        if (SeasonListener.getCurrentWeather() == SeasonListener.Weather.CLEAR) return true;
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.sendMessage(ChatColor.GREEN+player.getName()+" cleared the weather using their Weather Beacon.");
            p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 1);
        });
        SeasonListener.setCurrentWeather(SeasonListener.Weather.CLEAR);
        return true;
    }

    @Override
    public Recipe getRecipe() {
        ItemStack item = ItemType.WEATHER_BEACON.getItem().getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), item);
        recipe.shape("&#&", "#@#", "&#&");
        recipe.setIngredient('#', new RecipeChoice.ExactChoice(ItemType.DRAGON_SCALE.getItem().getItem()));
        recipe.setIngredient('@', Material.NETHER_STAR);
        recipe.setIngredient('&', Material.REDSTONE_BLOCK);

        return recipe;
    }
}
