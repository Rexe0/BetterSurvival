package me.rexe0.bettersurvival.item.golf;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.golf.GolfBallEntity;
import me.rexe0.bettersurvival.golf.GolfClubLogic;
import me.rexe0.bettersurvival.item.Item;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GolfHorn extends Item {
    public GolfHorn() {
        super(Material.GOAT_HORN, ChatColor.GREEN+"Golf Horn", "GOLF_HORN");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"A horn used to return");
        lore.add(ChatColor.GRAY+"your golf ball to its");
        lore.add(ChatColor.GRAY+"last location.");
        lore.add(" ");
        lore.add(ChatColor.GRAY+"Use this to get your");
        lore.add(ChatColor.GRAY+"golf ball out of");
        lore.add(ChatColor.GRAY+"impossible spots at");
        lore.add(ChatColor.GRAY+"the cost of a stroke.");
        return lore;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.PROTECTION, 1, true);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public boolean onRightClick(Player player) {
        GolfBallEntity ball = GolfClubLogic.getInstance().getGolfBall(player);
        if (ball == null) return false;
        ball.returnToLastLocation();
        return false;
    }

    @Override
    public Recipe getRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), getItem());
        recipe.shape("#  ", "# #", "###");
        recipe.setIngredient('#', Material.COPPER_INGOT);
        recipe.setGroup("GOLF");
        return recipe;
    }
}
