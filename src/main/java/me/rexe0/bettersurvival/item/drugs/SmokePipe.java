package me.rexe0.bettersurvival.item.drugs;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class SmokePipe extends Item {
    public SmokePipe() {
        super(Material.STICK, ChatColor.GREEN+"Smoke Pipe", "SMOKE_PIPE");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = super.getLore();
        lore.add(ChatColor.GRAY+"A small wooden pipe, allowing you");
        lore.add(ChatColor.GRAY+"to smoke cannabis to suppress");
        lore.add(ChatColor.GRAY+"appetite and hunger.");
        return lore;
    }

    @Override
    public void onRightClick(Player player) {
        if (!ItemDataUtil.hasItem(ItemType.CANNABIS.getItem().getID(), 1, player)) {
            player.sendMessage(ChatColor.RED+"You have nothing to smoke.");
            return;
        }
        ItemStack cannabis = ItemDataUtil.removeItems(ItemType.CANNABIS.getItem().getID(), 1, player);
        int potency = ItemDataUtil.getIntegerValue(cannabis, "potency");

        for (int i = 0; i < Math.max(1, potency); i++)
            player.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, player.getEyeLocation(), 0, 1, 1, 1, 0.01);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BREATH, 0.5f, 0.5f);


        int nauseaLevel = player.hasPotionEffect(PotionEffectType.NAUSEA) ? player.getPotionEffect(PotionEffectType.NAUSEA).getAmplifier() : -1;
        nauseaLevel++;

        player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, (24*potency), nauseaLevel, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, (24*potency), 0, true, false));

        if (player.getFoodLevel() < potency/6)
            player.setFoodLevel(Math.min(potency/6, player.getFoodLevel()+3));

        if (nauseaLevel == 5) {
            player.damage(8);
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 600, 3, true, false));
        }

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
