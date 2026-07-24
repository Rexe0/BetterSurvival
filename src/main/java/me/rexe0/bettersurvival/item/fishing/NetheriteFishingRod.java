package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.item.ItemType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.List;

public class NetheriteFishingRod extends Item {
    public NetheriteFishingRod() {
        super(Material.FISHING_ROD, ChatColor.BLUE+"Netherite Fishing Rod", "NETHERITE_FISHING_ROD");
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
        damageable.setMaxDamage(200);
        damageable.setFireResistant(true);
        item.setItemMeta(damageable);
        return item;
    }

    public SmithingTransformRecipe getRecipe() {
        ItemStack item = ItemType.NETHERITE_FISHING_ROD.getItem().getItem();
        return new SmithingTransformRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()),
                item,
                new RecipeChoice.MaterialChoice(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                new RecipeChoice.ExactChoice(ItemType.TUNGSTEN_FISHING_ROD.getItem().getItem()),
                new RecipeChoice.MaterialChoice(Material.NETHERITE_INGOT));
    }
}