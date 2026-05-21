package me.rexe0.bettersurvival.item.smithing;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.smithing.SmithingOre;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CastingMold extends Item {
    private final SmithingType type;
    public CastingMold(SmithingType type) {
        super(Material.BOOK, ChatColor.GREEN+"Casting Mold", "CASTING_MOLD");
        this.type = type;
    }


    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        item.setItemMeta(ItemDataUtil.setStringValue(item, "smithingType", type.name()));
        return item;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = super.getLore();
        lore.add(ChatColor.GRAY+"A mold used for casting molten");
        lore.add(ChatColor.GRAY+"metals into specific shapes.");
        lore.add(" ");
        lore.add(ChatColor.GRAY+"Shape: "+ChatColor.GREEN+type.getName());
        return lore;
    }

    public SmithingType getType() {
        return type;
    }


    @Override
    public Map<NamespacedKey, Recipe> getRecipes() {
        Map<NamespacedKey, Recipe> recipes = new HashMap<>();

        for (SmithingType type : SmithingType.values()) {
            ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()+"_"+type.name()), new CastingMold(type).getItem());
            recipe.shape("###", "#@#", "###");
            recipe.setIngredient('@', type.getItem(SmithingOre.COPPER));
            recipe.setIngredient('#', Material.BRICK);
            recipe.setGroup("CASTING_MOLD");
            recipes.put(recipe.getKey(), recipe);
        }
        return recipes;
    }
}
