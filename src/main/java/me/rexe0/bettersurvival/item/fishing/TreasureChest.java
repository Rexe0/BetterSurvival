package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.fishing.TreasureDrop;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TreasureChest extends Item {
    private final ItemType fishingRod;
    public TreasureChest(ItemType fishingRod) {
        super(Material.CHEST, ChatColor.BLUE+"Treasure Chest", "TREASURE_CHEST");
        this.fishingRod = fishingRod;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"There may be goodies inside...");
        return lore;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        item.setItemMeta(ItemDataUtil.setStringValue(item, "fishingRodType", fishingRod != null ? fishingRod.getItem().getID() : ""));
        return item;
    }

    public void onBlockPlace(BlockPlaceEvent e) {
        if (!ItemDataUtil.isItem(e.getItemInHand(), getID())) return;
        ItemType type;
        try {
            type = ItemType.valueOf(ItemDataUtil.getStringValue(e.getItemInHand(), "fishingRodType"));
        } catch (IllegalArgumentException ex) {
            type = null;
        }

        Block block = e.getBlock();
        Chest chest = (Chest) block.getState();
        chest.setCustomName(null);
        chest.update();
        for (int i = 0; i < 3; i++) {
            int index;
            do index = RandomUtil.getRandom().nextInt(27);
            while (chest.getBlockInventory().getItem(index) != null);
            chest.getBlockInventory().setItem(index, TreasureDrop.getTreasureItem(e.getPlayer(), type));
        }
    }
}
