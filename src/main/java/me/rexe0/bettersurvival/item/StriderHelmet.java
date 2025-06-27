package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.BetterSurvival;
import net.minecraft.tags.FluidTags;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class StriderHelmet extends Item {
    public StriderHelmet() {
        super(Material.TURTLE_HELMET, ChatColor.GREEN+"Strider Helmet", "STRIDER_HELMET");
    }


    @Override
    public void armorEquipped(Player player) {
        // 20 seconds of water breathing
        if (!((CraftPlayer)player).getHandle().isEyeInFluid(FluidTags.WATER))
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 400, 0));
        // 5 Seconds of Fire Resistance
        if (player.getFireTicks() <= -20)
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100, 0));
    }

    public SmithingTransformRecipe getRecipe() {
        ItemStack item = ItemType.STRIDER_HELMET.getItem().getItem();
        return new SmithingTransformRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()),
                item,
                new RecipeChoice.MaterialChoice(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                new RecipeChoice.MaterialChoice(Material.TURTLE_HELMET),
                new RecipeChoice.MaterialChoice(Material.NETHERITE_INGOT));
    }
}
