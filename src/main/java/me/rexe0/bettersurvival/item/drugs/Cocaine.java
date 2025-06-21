package me.rexe0.bettersurvival.item.drugs;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cocaine extends Item {

    private final int potency;
    public Cocaine(int potency) {
        super(Material.SUGAR, ChatColor.DARK_GREEN+"Cocaine", "COCAINE");
        this.potency = potency;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        item.setItemMeta(ItemDataUtil.setIntegerValue(item, "potency", potency));
        return item;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = super.getLore();
        lore.add(ChatColor.GRAY+"A white powder with strong stimulating");
        lore.add(ChatColor.GRAY+"psychoactive effects.");
        lore.add(" ");
        lore.add(ChatColor.GRAY+"Potency: "+(ItemDataUtil.getFormattedColorString(potency+"", potency, 100))+"%");
        return lore;
    }

    @Override
    public boolean onRightClick(Player player) {
        ItemStack cocaine = player.getEquipment().getItemInMainHand();
        int potency = ItemDataUtil.getIntegerValue(cocaine, "potency");

        cocaine.setAmount(cocaine.getAmount()-1);
        player.getEquipment().setItemInMainHand(cocaine);

        player.playSound(player.getLocation(), Sound.ENTITY_HORSE_BREATHE, 1, 1.2f);

        int nauseaLevel = player.hasPotionEffect(PotionEffectType.NAUSEA) ? player.getPotionEffect(PotionEffectType.NAUSEA).getAmplifier() : -1;
        nauseaLevel++;

        player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, (12*potency), nauseaLevel, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, (6*potency), potency/50, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (6*potency), potency/50, true, false));

        if (nauseaLevel == 5) {
            player.damage(8);
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 600, 3, true, false));
        }
        return false;
    }

    @Override
    public Map<NamespacedKey, Recipe> getRecipes() {
        Map<NamespacedKey, Recipe> recipes = new HashMap<>();

        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), ItemType.COCAINE.getItem().getItem());

        List<ItemStack> ingredients = new ArrayList<>();
        for (int i = 0; i <= 100; i++)
            ingredients.add(new CocaLeaves(i).getItem());

        for (int i = 0; i < 5; i++)
            recipe.addIngredient(new RecipeChoice.ExactChoice(ingredients));
        recipes.put(new NamespacedKey(BetterSurvival.getInstance(), getID()), recipe);

        recipes.put(new NamespacedKey(BetterSurvival.getInstance(), getID()+"_block"), getBlockRecipe());
        return recipes;
    }

    private ShapelessRecipe getBlockRecipe() {
        ItemStack item = ItemType.COCAINE.getItem().getItem();
        item.setAmount(9);
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()+"_block"), item);

        List<ItemStack> ingredients = new ArrayList<>();
        for (int i = 0; i <= 100; i++)
            ingredients.add(new BlockOfCocaine(i).getItem());
        recipe.addIngredient(new RecipeChoice.ExactChoice(ingredients));

        return recipe;
    }
}