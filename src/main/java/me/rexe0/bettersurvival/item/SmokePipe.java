package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SmokePipe extends Item {
    public SmokePipe() {
        super(Material.STICK, ChatColor.GREEN+"Smoke Pipe", "SMOKE_PIPE");
    }

    @Override
    public void onRightClick(Player player) {
        if (!ItemDataUtil.hasItem(ItemType.CANNABIS.getItem().getID(), 1, player)) {
            player.sendMessage(ChatColor.RED+"You have nothing to smoke.");
            return;
        }
        ItemStack cannabis = ItemDataUtil.removeItems(ItemType.CANNABIS.getItem().getID(), 1, player);
        int potency = ItemDataUtil.getIntegerValue(cannabis, "potency");

        for (int i = 0; i < Math.max(1, potency*10); i++)
            player.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, player.getEyeLocation(), 0, 1, 1, 1, 0.01);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BREATH, 0.5f, 0.5f);


        player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, (24*potency), 1, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, (24*potency), 0, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, (6*potency), 0, true, false));

    }
    public ShapedRecipe getRecipe() {
        ItemStack item = ItemType.SMOKE_PIPE.getItem().getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), item);
        recipe.shape("  #", " @$", "@  ");
        recipe.setIngredient('#', Material.COAL);
        recipe.setIngredient('@', Material.STICK);
        recipe.setIngredient('$', Material.TORCH);

        return recipe;
    }
}
