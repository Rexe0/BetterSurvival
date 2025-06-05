package me.rexe0.bettersurvival.item.drugs;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.farming.alcohol.BarrelType;
import me.rexe0.bettersurvival.item.Item;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReinforcedBarrel extends Item {
    private final BarrelType type;
    private List<ItemStack> previousContents;

    public ReinforcedBarrel(BarrelType type) {
        super(Material.BARREL, ChatColor.GREEN+"Reinforced Barrel", "REINFORCED_BARREL");
        this.type = type;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = super.getLore();
        lore.add(ChatColor.GRAY+"A special barrel able to ferment");
        lore.add(ChatColor.GRAY+"and age alcoholic beverages.");
        lore.add(ChatColor.GRAY+"The type of wood the barrel is made");
        lore.add(ChatColor.GRAY+"from affects the flavours of the product.");
        lore.add(" ");
        lore.add(ChatColor.GRAY+"Type: " + type.getName());
        return lore;
    }
    @Override
    public Map<NamespacedKey, Recipe> getRecipes() {
        Map<NamespacedKey, Recipe> recipes = new HashMap<>();

        for (BarrelType type : BarrelType.values()) {
            ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()+"_"+type.name()), new ReinforcedBarrel(type).getItem());
            recipe.shape("###", "#@#", "###");
            recipe.setIngredient('#', type.getPlanks());
            recipe.setIngredient('@', Material.BARREL);
        }

        return recipes;
    }
}
