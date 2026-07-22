package me.rexe0.bettersurvival.item.basketball;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.item.ItemType;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import java.util.ArrayList;
import java.util.List;

public class AirJordans extends Item {
    public AirJordans() {
        super(Material.LEATHER_BOOTS, ChatColor.GREEN+"Air Jordans", "AIR_JORDANS");
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(Color.fromRGB(255, 28, 43));
        meta.addAttributeModifier(Attribute.SAFE_FALL_DISTANCE, new AttributeModifier(new NamespacedKey(BetterSurvival.getInstance(), "air_jordans"), 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET));
        meta.setUnbreakable(true);

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"Allows you to perform a");
        lore.add(ChatColor.GRAY+"super jump by sneaking.");
        return lore;
    }

    @Override
    public Recipe getRecipe() {
        ItemStack item = ItemType.AIR_JORDANS.getItem().getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), item);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        recipe.setGroup("BASKETBALL");
        recipe.shape("# #", "# #", "& &");
        recipe.setIngredient('#', Material.LEATHER);
        recipe.setIngredient('&', Material.WIND_CHARGE);

        return recipe;
    }
}
