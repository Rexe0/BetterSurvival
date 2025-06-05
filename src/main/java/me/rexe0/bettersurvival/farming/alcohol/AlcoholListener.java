package me.rexe0.bettersurvival.farming.alcohol;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.item.drugs.ReinforcedBarrel;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class AlcoholListener implements Listener {
    public static final NamespacedKey BARREL_TYPE_KEY = new NamespacedKey(BetterSurvival.getInstance(), "BARREL_TYPE");

    @EventHandler
    public void onHarvest(BlockBreakEvent e) {
        Block block = e.getBlock();

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
        if (data.has(BARREL_TYPE_KEY, PersistentDataType.STRING)) {
            e.setDropItems(false);
            BarrelType type = BarrelType.valueOf(data.get(BARREL_TYPE_KEY, PersistentDataType.STRING));

            data.remove(BARREL_TYPE_KEY);
            ItemStack item = new ReinforcedBarrel(type).getItem();
            block.getWorld().dropItemNaturally(block.getLocation(), item);
        }
    }

    private boolean isFermentable(ItemStack item) {
        if (item == null) return false;
        if (item.getType() == Material.WHEAT || item.getType() == Material.POTATO) return true;
        for (WineType type : WineType.values()) {
            if (item.getType() == type.getFruit()) return true;
        }
        return false;
    }

    private void checkForFerment(Block block) {
        if (block.getType() != Material.BARREL) return;
        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
        if (!data.has(BARREL_TYPE_KEY, PersistentDataType.STRING)) return;
        BarrelType type = BarrelType.valueOf(data.get(BARREL_TYPE_KEY, PersistentDataType.STRING));

        ferment(((Barrel)block.getState()).getInventory(), type);
    }


    private void ferment(Inventory inventory, BarrelType type) {
        int water = (int) Arrays.stream(inventory.getContents())
                .filter(item -> item != null && item.getType() == Material.WATER_BUCKET)
                .count();
        int yeast = (int) Arrays.stream(inventory.getContents())
                .filter(item -> item != null && ItemDataUtil.isItem(item, ItemType.YEAST.getItem().getID()))
                .count();


    }
}
