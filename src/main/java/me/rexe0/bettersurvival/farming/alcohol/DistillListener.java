package me.rexe0.bettersurvival.farming.alcohol;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.item.drugs.Spirit;
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

public class DistillListener implements Listener {
    // Stores the last time the barrel had an action
    public static final NamespacedKey LAST_DISTILL_KEY = new NamespacedKey(BetterSurvival.getInstance(), "LAST_DISTILL");
    private static final int AGE_TIME = 1000 * 10; // It takes 15 minutes for one distill tick

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getClickedBlock() == null) return;
        Block block = e.getClickedBlock();

        if (block.getType() != Material.BARREL || block.getRelative(BlockFace.DOWN).getType() != Material.CAMPFIRE) return;

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
        if (data.has(LAST_DISTILL_KEY, PersistentDataType.LONG)) {
            long lastAction = data.get(LAST_DISTILL_KEY, PersistentDataType.LONG);
            long currentTime = System.currentTimeMillis();

            // Only ferment if the time has passed the threshold
            if (currentTime - lastAction < AGE_TIME) return;
            Inventory inventory = ((Barrel) block.getState()).getInventory();

            if (distill(inventory))
                e.getPlayer().playSound(block.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1f, 1.5f);

            // Update the last action time
            data.set(LAST_DISTILL_KEY, PersistentDataType.LONG, (long) (currentTime-(AGE_TIME*((double) (currentTime - lastAction) / AGE_TIME) % 1)));
        }
    }

    private List<ItemStack> getContainers(Inventory inventory) {
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack item : inventory.getContents()) {
            if (item == null) continue;
            if (item.getType() != Material.POTION) continue;
            if (!ItemDataUtil.isItem(item, ItemType.WINE.getItem().getID()) && !ItemDataUtil.isItem(item, ItemType.SPIRIT.getItem().getID())) continue;

            items.add(item);
        }
        return items;
    }
    private List<ItemStack> getBottles(Inventory inventory) {
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack item : inventory.getContents()) {
            if (item == null) continue;
            if (item.getType() != Material.GLASS_BOTTLE) continue;
            items.add(item);
        }
        return items;
    }

    private boolean distill(Inventory inventory) {
        // Kill off any yeast due to heat
        Arrays.stream(inventory.getContents())
                .filter(item -> item != null && item.getType() == Material.FROGSPAWN && ItemDataUtil.isItem(item, ItemType.YEAST.getItem().getID()))
                .forEach(item -> item.setAmount(0));

        List<ItemStack> bottles = getBottles(inventory);
        if (bottles.isEmpty()) return false;

        List<ItemStack> containers = getContainers(inventory);
        if (containers.size() <= 1) return false;

        int k = 0;
        for (ItemStack bottle : bottles) {
            int j = 0; // Counter for the number of distilled wines
            double concentration = 1;
            WineType tertiaryFlavor = null;

            if (k >= containers.size()) return true; // No more containers to distill

            for (int i = k; i < containers.size(); i++) {
                if (j == 4) break; // Can only distill 4 wines at a time per bottle
                ItemStack item = containers.get(i);

                concentration *= (1-(ItemDataUtil.getDoubleValue(item, "concentration")/100));

                if (ItemDataUtil.isItem(item, ItemType.WINE.getItem().getID()))
                    tertiaryFlavor = WineType.valueOf(ItemDataUtil.getStringValue(item, "wineType"));
                else if (tertiaryFlavor == null)
                    tertiaryFlavor = WineType.valueOf(ItemDataUtil.getStringValue(item, "tertiaryFlavor"));

                item.setAmount(0); // Remove the item directly from the inventory
                j++;
                k++;
            }
            // Modify the bottle directly
            ItemStack item = (new Spirit(Math.min(Spirit.MAX_DISTILL_CONCENTRATION, (1-concentration)*100), SpiritType.DISTILLATE, 0, null, tertiaryFlavor, null, Math.random() < 0.1)).getItem();
            bottle.setType(item.getType());
            bottle.setItemMeta(item.getItemMeta());


        }
        return true;
    }
}
