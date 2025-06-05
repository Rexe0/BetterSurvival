package me.rexe0.bettersurvival.farming.alcohol;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.item.drugs.ReinforcedBarrel;
import me.rexe0.bettersurvival.item.drugs.Wine;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlcoholListener implements Listener {
    public static final NamespacedKey BARREL_TYPE_KEY = new NamespacedKey(BetterSurvival.getInstance(), "BARREL_TYPE");
    // Stores the last time the barrel had an action
    public static final NamespacedKey BARREL_AGE_KEY = new NamespacedKey(BetterSurvival.getInstance(), "BARREL_AGE");
    private static final int FERMENT_TIME = 1000 * 60 * 10; // It takes 10 minutes to ferment

    @EventHandler
    public void onHarvest(BlockBreakEvent e) {
        Block block = e.getBlock();

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
        if (data.has(BARREL_TYPE_KEY, PersistentDataType.STRING)) {
            e.setDropItems(false);
            BarrelType type = BarrelType.valueOf(data.get(BARREL_TYPE_KEY, PersistentDataType.STRING));

            data.remove(BARREL_TYPE_KEY);
            data.remove(BARREL_AGE_KEY);
            ItemStack item = new ReinforcedBarrel(type).getItem();
            block.getWorld().dropItemNaturally(block.getLocation(), item);
        }
    }
    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getClickedBlock() == null) return;
        Block block = e.getClickedBlock();

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
        if (data.has(BARREL_AGE_KEY, PersistentDataType.LONG) && block.getType() == Material.BARREL) {
            long lastAction = data.get(BARREL_AGE_KEY, PersistentDataType.LONG);
            long currentTime = System.currentTimeMillis();
            // If the last action was less than 5 seconds ago, ignore the click
            if (currentTime - lastAction < FERMENT_TIME) return;

            float actions = (float) (currentTime - lastAction) / FERMENT_TIME;
            while (actions >= 1) {
                ferment(((Barrel) block.getState()).getInventory());
                actions--;
            }
            e.getPlayer().playSound(block.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1f, 0f);

            // Update the last action time
            data.set(BARREL_AGE_KEY, PersistentDataType.LONG, (long) (currentTime-(FERMENT_TIME*actions)));
        }
    }

    private List<ItemStack> getContainers(Inventory inventory) {
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack item : inventory.getContents()) {
            if (item == null) continue;
            if (item.getType() != Material.POTION) continue;
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            if (!meta.hasCustomEffects() || meta.getBasePotionType() == PotionType.WATER) {
                items.add(item);
                continue;
            }
        }
        return items;
    }
    private List<ItemStack> getFermentableItems(Inventory inventory, Material requiredMaterial) {
        List<ItemStack> fermentableItems = new ArrayList<>();
        for (ItemStack item : inventory.getContents()) {
            if (item == null) continue;

            boolean useable = requiredMaterial != null && item.getType() == requiredMaterial;
            if (requiredMaterial == null)
                for (WineType wineType : WineType.values())
                    if (item.getType() == wineType.getFruit()) {
                        useable = true;
                        requiredMaterial = wineType.getFruit();
                        break;
                    }


            if (useable)
                fermentableItems.add(item);
        }
        return fermentableItems;
    }
    private void ferment(Inventory inventory) {
        int yeast = Arrays.stream(inventory.getContents())
                .filter(item -> item != null && ItemDataUtil.isItem(item, ItemType.YEAST.getItem().getID()))
                .mapToInt(ItemStack::getAmount)
                .sum();

        for (ItemStack container : getContainers(inventory)) {
            WineType type = null;
            try {
                type = WineType.valueOf(ItemDataUtil.getStringValue(container, "wineType"));
            } catch (Exception ignored) { // If it is a water bottle
            }

            Material requiredMaterial = null;
            if (type != null) requiredMaterial = type.getFruit();

            List<ItemStack> fermentableItems = getFermentableItems(inventory, requiredMaterial);
            if (fermentableItems.isEmpty()) continue;
            type = WineType.getWineType(fermentableItems.get(0).getType());

            // Can only increase AVC by 1% per ferment tick. When foodCount == type.getFruitCost(), the concentration increases by 1%. Yeast with an amount equal to the foodCount is also required
            int foodCount = Math.min(Math.min(type.getFruitCost(),
                    fermentableItems.stream()
                            .mapToInt(ItemStack::getAmount)
                            .sum()), yeast);

            double concentration = ItemDataUtil.getDoubleValue(container, "concentration");
            concentration += (double) foodCount / type.getFruitCost();

            // Remove the used items from the inventory
            for (ItemStack item : fermentableItems) {
                if (item.getAmount() > foodCount) {
                    item.setAmount(item.getAmount() - foodCount);
                    break;
                } else {
                    foodCount -= item.getAmount();
                    item.setAmount(0);
                }
            }

            // Modify the ItemStack object in the inventory directly
            ItemStack item = new Wine(concentration, type).getItem();
            container.setType(item.getType());
            container.setItemMeta(item.getItemMeta());


        }

    }
}
