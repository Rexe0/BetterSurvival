package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.fishing.TreasureDrop;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.List;

public class TreasureChest extends Item {
    public TreasureChest() {
        super(Material.CHEST, ChatColor.BLUE+"Treasure Chest", "TREASURE_CHEST");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"There may be goodies inside...");
        return lore;
    }

    public void onBlockPlace(BlockPlaceEvent e) {
        if (!ItemDataUtil.isItem(e.getItemInHand(), getID())) return;
        Block block = e.getBlock();
        Chest chest = (Chest) block.getState();
        chest.setCustomName(null);
        chest.update();
        for (int i = 0; i < 3; i++) {
            int index;
            do index = RandomUtil.getRandom().nextInt(27);
            while (chest.getBlockInventory().getItem(index) != null);
            chest.getBlockInventory().setItem(index, TreasureDrop.getTreasureItem());
        }
    }
}
