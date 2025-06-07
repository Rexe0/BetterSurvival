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
import org.bukkit.block.BlockFace;
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

public class FermentListener implements Listener {
    public static final NamespacedKey BARREL_TYPE_KEY = new NamespacedKey(BetterSurvival.getInstance(), "BARREL_TYPE");
    public static final NamespacedKey BARREL_PRODUCTS_KEY = new NamespacedKey(BetterSurvival.getInstance(), "BARREL_PRODUCTS");
    // Stores the last time the barrel had an action
    public static final NamespacedKey LAST_FERMENT_KEY = new NamespacedKey(BetterSurvival.getInstance(), "LAST_FERMENT");
    private static final int FERMENT_TIME = 1000 * 10; // It takes 10 minutes for one ferment tick

    @EventHandler
    public void onHarvest(BlockBreakEvent e) {
        Block block = e.getBlock();

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
        if (data.has(BARREL_TYPE_KEY, PersistentDataType.STRING)) {
            e.setDropItems(false);
            Barrel barrel = (Barrel) block.getState();
            List<ItemStack> items = new ArrayList<>(Arrays.asList(barrel.getInventory().getContents()));

            BarrelType type = BarrelType.valueOf(data.get(BARREL_TYPE_KEY, PersistentDataType.STRING));
            List<WineType> previousProducts = ReinforcedBarrel.decodePreviousProducts(data.get(BARREL_PRODUCTS_KEY, PersistentDataType.STRING));

            data.remove(BARREL_TYPE_KEY);
            data.remove(LAST_FERMENT_KEY);
            data.remove(BARREL_PRODUCTS_KEY);
            data.remove(AgingListener.BARREL_AGE_KEY);
            data.remove(DistillListener.LAST_DISTILL_KEY);

            items.add(new ReinforcedBarrel(type, previousProducts).getItem());
            items.forEach(item -> {
                if (item == null || item.getType() == Material.AIR) return;
                block.getWorld().dropItemNaturally(block.getLocation(), item);
            });
        }
    }
    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getClickedBlock() == null) return;
        Block block = e.getClickedBlock();
        if (block.getType() != Material.BARREL || block.getRelative(BlockFace.DOWN).getType() == Material.CAMPFIRE) return;

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
        if (data.has(LAST_FERMENT_KEY, PersistentDataType.LONG)) {
            long lastAction = data.get(LAST_FERMENT_KEY, PersistentDataType.LONG);
            long currentTime = System.currentTimeMillis();

            // Only ferment if the time has passed the threshold
            if (currentTime - lastAction < FERMENT_TIME) return;
            Inventory inventory = ((Barrel) block.getState()).getInventory();

            boolean fermented = false;
            double actions = Math.min(20, (double) (currentTime - lastAction) / FERMENT_TIME);
            while (actions >= 1) {
                if (ferment(inventory, data)) fermented = true;
                actions--;
            }
            if (fermented)
                e.getPlayer().playSound(block.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1f, 0f);

            // Update the last action time
            data.set(LAST_FERMENT_KEY, PersistentDataType.LONG, (long) (currentTime-(FERMENT_TIME*actions)));
        }
    }

    private List<ItemStack> getContainers(Inventory inventory) {
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack item : inventory.getContents()) {
            if (item == null) continue;
            if (item.getType() != Material.POTION) continue;
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            if (!meta.hasCustomEffects() && (!meta.hasBasePotionType() || meta.getBasePotionType() == PotionType.WATER)) {
                if (ItemDataUtil.getDoubleValue(item, "concentration") >= Wine.MAX_FERMENT_CONCENTRATION) continue; // Already at max concentration

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
            if (item.getType() == Material.FROGSPAWN || item.getType() == Material.AIR || item.getType() == Material.POTION) continue; // Ignore yeast, air and potions

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
    private boolean ferment(Inventory inventory, PersistentDataContainer data) {
        List<ItemStack> containers = getContainers(inventory);
        if (containers.isEmpty()) return false;

        int yeast = Arrays.stream(inventory.getContents())
                .filter(item -> item != null && ItemDataUtil.isItem(item, ItemType.YEAST.getItem().getID()))
                .mapToInt(ItemStack::getAmount)
                .sum();

        if (yeast <= 0) return false;

        int yeastShared = yeast;

        List<WineType> previousProducts = ReinforcedBarrel.decodePreviousProducts(data.get(BARREL_PRODUCTS_KEY, PersistentDataType.STRING));

        for (ItemStack container : containers) {
            if (yeastShared <= 0) break;
            double yeastPer = (double) yeast /containers.size();
            if (yeastPer % 1 != 0)
                yeastPer = Math.ceil(yeastPer);


            WineType type = null;
            double concentration = ItemDataUtil.getDoubleValue(container, "concentration");

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
                            .sum()), (int) yeastPer);

            concentration = Math.min(Wine.MAX_FERMENT_CONCENTRATION, concentration + (double) foodCount / type.getFruitCost());

            // Remove the used items from the inventory
            int n = foodCount;
            for (ItemStack item : fermentableItems) {
                if (item.getAmount() > n) {
                    item.setAmount(item.getAmount() - n);
                    break;
                } else {
                    n -= item.getAmount();
                    item.setAmount(0);
                }
            }

            // If the concentration is at max, kill off the yeast
            if (concentration >= Wine.MAX_FERMENT_CONCENTRATION) {
                n = Math.min(16, foodCount*2);
                for (ItemStack item : inventory.getContents()) {
                    if (item == null || !ItemDataUtil.isItem(item, ItemType.YEAST.getItem().getID())) continue;
                    if (item.getAmount() > n) {
                        item.setAmount(item.getAmount() - n);
                        break;
                    } else {
                        n -= item.getAmount();
                        item.setAmount(0);
                    }
                }
            } else {
                // If not, the yeast have a chance to multiply where the chance is based on the concentration of the wine
                double chance = (1-(concentration / Wine.MAX_FERMENT_CONCENTRATION))/10;
                for (ItemStack item : inventory.getContents()) {
                    if (item == null || !ItemDataUtil.isItem(item, ItemType.YEAST.getItem().getID())) continue;
                    if (Math.random() < chance)
                        item.setAmount(item.getAmount() + 1);
                }
            }

            // Each yeast can only contribute once per fermentation tick
            yeastShared -= foodCount;

            // Modify the ItemStack object in the inventory directly
            ItemStack item = new Wine(concentration, type, ItemDataUtil.getIntegerValue(container, "age")).getItem();
            container.setType(item.getType());
            container.setItemMeta(item.getItemMeta());

            previousProducts.add(type);
        }
        data.set(BARREL_PRODUCTS_KEY, PersistentDataType.STRING, ReinforcedBarrel.encodePreviousProducts(previousProducts));
        return true;
    }
}
