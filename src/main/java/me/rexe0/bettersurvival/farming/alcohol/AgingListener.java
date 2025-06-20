package me.rexe0.bettersurvival.farming.alcohol;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.item.drugs.ReinforcedBarrel;
import me.rexe0.bettersurvival.item.drugs.Spirit;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AgingListener implements Listener {
    // Stores the last time the barrel had an action
    public static final NamespacedKey BARREL_AGE_KEY = new NamespacedKey(BetterSurvival.getInstance(), "BARREL_AGE");
    private static final int AGE_TIME = 1000 * 60 * 60 * 10; // It takes 10 hours for one age tick

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getClickedBlock() == null) return;
        Block block = e.getClickedBlock();
        if (block.getType() != Material.BARREL || block.getRelative(BlockFace.DOWN).getType() == Material.CAMPFIRE) return;

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
        if (data.has(BARREL_AGE_KEY, PersistentDataType.LONG)) {
            long lastAction = data.get(BARREL_AGE_KEY, PersistentDataType.LONG);
            long currentTime = System.currentTimeMillis();

            // Only ferment if the time has passed the threshold
            if (currentTime - lastAction < AGE_TIME) return;
            Inventory inventory = ((Barrel) block.getState()).getInventory();

            boolean aged = false;
            double actions = Math.min(5, (double) (currentTime - lastAction) / AGE_TIME);
            while (actions >= 1) {
                if (age(inventory, data)) aged = true;
                actions--;
            }
            if (aged)
                e.getPlayer().playSound(block.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1f, 0f);

            // Update the last action time
            data.set(BARREL_AGE_KEY, PersistentDataType.LONG, (long) (currentTime-(AGE_TIME*actions)));
        }
    }

    private List<ItemStack> getContainers(Inventory inventory) {
        List<ItemStack> items = new ArrayList<>();
        WineType type = null;
        SpiritType type1 = null;
        for (ItemStack item : inventory.getContents()) {
            if (item == null) continue;
            if (item.getType() != Material.POTION) continue;
            if (ItemDataUtil.isItem(item, ItemType.WINE.getItem().getID()) && type1 == null) {
                if (type == null)
                    type = WineType.valueOf(ItemDataUtil.getStringValue(item, "wineType"));
                else if (type != WineType.valueOf(ItemDataUtil.getStringValue(item, "wineType"))) {
                    // All items in the barrel must be of the same type
                    return new ArrayList<>();
                }
                items.add(item);
            } else if (ItemDataUtil.isItem(item, ItemType.SPIRIT.getItem().getID()) && type == null) {
                if (type1 == null)
                    type1 = SpiritType.valueOf(ItemDataUtil.getStringValue(item, "spiritType"));
                else if (type1 != SpiritType.valueOf(ItemDataUtil.getStringValue(item, "spiritType"))) {
                    // All items in the barrel must be of the same type
                    return new ArrayList<>();
                }
                items.add(item);
            }

        }
        return items;
    }

    private List<ItemStack> getSpiritFlavoringMaterials(Inventory inventory) {
        List<ItemStack> fermentableItems = new ArrayList<>();
        for (ItemStack item : inventory.getContents()) {
            if (item == null) continue;
            if (item.getType() == Material.FROGSPAWN || item.getType() == Material.AIR || item.getType() == Material.POTION) continue; // Ignore yeast, air and potions

            for (SpiritType spiritType : SpiritType.values())
                if (item.getType() == spiritType.getFruit()) {
                    fermentableItems.add(item);
                    break;
                }
        }
        return fermentableItems;
    }

    private boolean age(Inventory inventory, PersistentDataContainer data) {
        List<ItemStack> containers = getContainers(inventory);
        if (containers.isEmpty()) return false;


        int yeast = Arrays.stream(inventory.getContents())
                .filter(item -> item != null && ItemDataUtil.isItem(item, ItemType.YEAST.getItem().getID()))
                .mapToInt(ItemStack::getAmount)
                .sum();
        if (yeast > 0) return false; // Cannot age while yeast is present because the yeast may cause fermentation

        BarrelType barrelType = BarrelType.valueOf(data.get(FermentListener.BARREL_TYPE_KEY, PersistentDataType.STRING));
        List<WineType> previousProducts = ReinforcedBarrel.decodePreviousProducts(data.get(FermentListener.BARREL_PRODUCTS_KEY, PersistentDataType.STRING));

        for (ItemStack item : containers) {
            // Every age tick, increase the age of the wine by 1 and the concentration by 1%, up to the cap
            boolean isWine = ItemDataUtil.isItem(item, ItemType.WINE.getItem().getID());
            int age = ItemDataUtil.getIntegerValue(item, "age")+1;
            double concentration = Math.min(isWine ? Wine.MAX_AGE_CONCENTRATION : Spirit.MAX_AGE_CONCENTRATION, ItemDataUtil.getDoubleValue(item, "concentration")+1);

            WineType secondaryFlavor = null;
            try {
                secondaryFlavor = WineType.valueOf(ItemDataUtil.getStringValue(item, "secondaryFlavor"));
            } catch (IllegalArgumentException ignored) {}

            if (age >= 2 && secondaryFlavor == null && !previousProducts.isEmpty())
                secondaryFlavor = previousProducts.get((int) (Math.random() * previousProducts.size()));

            BarrelType barrelFlavor = null;
            try {
                barrelFlavor = BarrelType.valueOf(ItemDataUtil.getStringValue(item, "tertiaryFlavor"));
            } catch (IllegalArgumentException ignored) {}

            if (age == 5)
                barrelFlavor = barrelType;

            ItemStack drink;
            if (isWine) {
                WineType type = WineType.valueOf(ItemDataUtil.getStringValue(item, "wineType"));

                drink = new Wine(concentration, type, age, secondaryFlavor, barrelFlavor).getItem();
            } else {
                SpiritType type = SpiritType.valueOf(ItemDataUtil.getStringValue(item, "spiritType"));
                // If the distillate is unflavored and has sufficient alcohol concentration, flavor it with a random fruit in the barrel
                if (type == SpiritType.DISTILLATE && concentration > 35) {
                    List<ItemStack> flavoringMaterials = getSpiritFlavoringMaterials(inventory);
                    if (!flavoringMaterials.isEmpty()) {
                        ItemStack flavoringMaterial = flavoringMaterials.get((int) (Math.random() * flavoringMaterials.size()));

                        type = SpiritType.getSpiritType(flavoringMaterial.getType());
                        if (type == SpiritType.BEER && concentration > 60)
                            type = SpiritType.VODKA;

                        flavoringMaterial.setAmount(flavoringMaterial.getAmount() - 1); // Remove the flavoring material from the inventory

                    }
                }

                drink = new Spirit(concentration, type, age, secondaryFlavor, barrelFlavor, WineType.valueOf(ItemDataUtil.getStringValue(item, "quaternaryFlavor")), ItemDataUtil.getIntegerValue(item, "hasMethanol") == 1).getItem();
            }

            // Modify the ItemStack object in the inventory directly
            item.setType(drink.getType());
            item.setItemMeta(drink.getItemMeta());
        }
        return true;
    }
}
