package me.rexe0.bettersurvival.item.golf;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.SkullUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GolfBall extends Item {
    public GolfBall() {
        super(Material.PLAYER_HEAD, ChatColor.GREEN+"Golf Ball", "GOLF_BALL");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"A small ball used for playing golf.");
        lore.add(ChatColor.GRAY+"Right click a golf tee to place it.");
        return lore;
    }

    @Override
    public ItemStack getItem() {
        return SkullUtil.getCustomSkull(super.getItem(), "http://textures.minecraft.net/texture/b4936a032c688050a36d33a4c3f0d56a4a705d8a89dfdded1472438ec000c9d0"
                , UUID.fromString("b520e1b1-5662-42e3-af5e-93280ffe0574"));
    }

    @Override
    public boolean onRightClick(Player player) {
        return true;
    }

    @Override
    public Recipe getRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), getItem());
        recipe.shape("###", "#$#", "###");
        recipe.setIngredient('#', Material.COPPER_INGOT);
        recipe.setIngredient('$', Material.COAL_BLOCK);
        recipe.setGroup("GOLF");
        return recipe;
    }
}
