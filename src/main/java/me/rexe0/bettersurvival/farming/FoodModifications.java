package me.rexe0.bettersurvival.farming;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class FoodModifications implements Listener {
    private final NamespacedKey SOUP_INGREDIENTS_KEY = new NamespacedKey(BetterSurvival.getInstance(), "SOUP_INGREDIENTS");
    private final NamespacedKey SOUP_TYPE_KEY = new NamespacedKey(BetterSurvival.getInstance(), "SOUP_TYPE");
    private final Material[] soupIngredients = new Material[]{
            Material.CARROT, Material.BAKED_POTATO, Material.COOKED_RABBIT,
            Material.BROWN_MUSHROOM, Material.RED_MUSHROOM,
            Material.BEETROOT
    };
    private final Map<Material, Material[]> soups;

    public FoodModifications() {
        soups = new HashMap<>();
        soups.put(Material.BEETROOT_SOUP, new Material[]{Material.BEETROOT, Material.BEETROOT, Material.BEETROOT,
                Material.BEETROOT, Material.BEETROOT, Material.BEETROOT});
        soups.put(Material.MUSHROOM_STEW, new Material[]{Material.BROWN_MUSHROOM, Material.RED_MUSHROOM});
        soups.put(Material.RABBIT_STEW, new Material[]{Material.CARROT, Material.BAKED_POTATO, Material.COOKED_RABBIT
                , Material.RED_MUSHROOM, Material.BROWN_MUSHROOM});
    }


    @EventHandler
    public void onDrink(PlayerItemConsumeEvent e) {
        if (e.getItem().getType() != Material.HONEY_BOTTLE) return;
        e.getPlayer().removePotionEffect(PotionEffectType.WITHER);
    }
    public static ShapelessRecipe getSuspiciousStewRecipe() {
        ItemStack result = new ItemStack(Material.SUSPICIOUS_STEW);
        SuspiciousStewMeta meta = (SuspiciousStewMeta) result.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.LUCK, 6000, 0), true);
        result.setItemMeta(meta);

        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(BetterSurvival.getInstance(), "recipe_pitcher_suspicious_stew"), result);

        recipe.addIngredient(Material.PITCHER_PLANT);
        recipe.addIngredient(Material.RED_MUSHROOM);
        recipe.addIngredient(Material.BROWN_MUSHROOM);
        recipe.addIngredient(Material.BOWL);
        return recipe;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (block.getType() != Material.WATER_CAULDRON) return;

        // Remove any data if the cauldron is broken
        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
        data.remove(SOUP_TYPE_KEY);
        data.remove(SOUP_INGREDIENTS_KEY);
    }
    @EventHandler
    private void onLevelChange(CauldronLevelChangeEvent e) {
        Block block = e.getBlock();
        if (block.getType() != Material.WATER_CAULDRON) return;
        // Remove any data if the cauldron is emptied
        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
        data.remove(SOUP_TYPE_KEY);
        data.remove(SOUP_INGREDIENTS_KEY);
    }

    @EventHandler
    private void onPickupSoup(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getHand() != EquipmentSlot.HAND) return;
        Block block = e.getClickedBlock();
        if (block == null) return;
        if (block.getType() != Material.WATER_CAULDRON) return;
        if (e.getItem() == null || e.getItem().getType() != Material.BOWL) return;
        Player player = e.getPlayer();

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());

        if (!data.has(SOUP_TYPE_KEY, PersistentDataType.STRING)) return;
        Material soupType = Material.valueOf(data.get(SOUP_TYPE_KEY, PersistentDataType.STRING));

        e.getItem().setAmount(e.getItem().getAmount()-1);
        if (player.getInventory().firstEmpty() == -1)
            // If the player doesn't have space, drop it instead
            player.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(soupType));
        else
            // Otherwise, add it to their inventory directly
            player.getInventory().addItem(new ItemStack(soupType));

        e.getPlayer().playSound(block.getLocation(), Sound.ITEM_BUCKET_FILL, 1, 1.2f);

        Levelled levelled = (Levelled) block.getBlockData();
        int level = levelled.getLevel()-1;
        if (level <= 0) {
            block.setType(Material.CAULDRON);
            data.remove(SOUP_TYPE_KEY);
        } else {
            levelled.setLevel(level);
            block.setBlockData(levelled);
        }
    }
    @EventHandler
    public void onAddIngredient(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getHand() != EquipmentSlot.HAND) return;
        Block block = e.getClickedBlock();
        if (block == null) return;
        if (block.getType() != Material.WATER_CAULDRON) return;
        Block under = block.getLocation().subtract(0, 1, 0).getBlock();
        if (under.getType() != Material.FIRE
                && under.getType() != Material.CAMPFIRE
                && under.getType() != Material.SOUL_CAMPFIRE) return;
        if (e.getItem() == null) return;
        if (Arrays.stream(soupIngredients).noneMatch(m -> m == e.getItem().getType())) return;

        Levelled levelled = (Levelled) block.getBlockData();
        if (levelled.getLevel() < levelled.getMaximumLevel()) return;

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());

        if (data.has(SOUP_TYPE_KEY, PersistentDataType.STRING)) return;

        String ingredients = e.getItem().getType().toString();
        if (data.has(SOUP_INGREDIENTS_KEY, PersistentDataType.STRING)) {
            ingredients = data.get(SOUP_INGREDIENTS_KEY, PersistentDataType.STRING);
            ingredients += " " + e.getItem().getType();
        }

        data.set(SOUP_INGREDIENTS_KEY, PersistentDataType.STRING, ingredients);

        e.getPlayer().playSound(block.getLocation(), Sound.ITEM_BUCKET_EMPTY_FISH, 1, 1.2f);

        List<Material> currentIngredients = new ArrayList<>();
        for (String str : ingredients.split(" "))
            currentIngredients.add(Material.valueOf(str));

        currentIngredients.sort(Enum::compareTo);
        // Check if current ingredients in the cauldron match the ingredients of an actual soup item
        for (Map.Entry<Material, Material[]> entry : soups.entrySet()) {
            if (entry.getValue().length != currentIngredients.size()) continue;
            List<Material> requiredIngredients = Arrays.asList(entry.getValue());
            requiredIngredients.sort(Enum::compareTo);

            if (!requiredIngredients.equals(currentIngredients)) continue;
            // All ingredients match a soup

            data.remove(SOUP_INGREDIENTS_KEY);
            data.set(SOUP_TYPE_KEY, PersistentDataType.STRING, entry.getKey().toString());
            e.getPlayer().playSound(block.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
            break;
        }

        e.getItem().setAmount(e.getItem().getAmount()-1);
    }
}
