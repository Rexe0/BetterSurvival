package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.fishing.TreasureDrop;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;

import java.util.ArrayList;
import java.util.List;

public class TreasureBundle extends Item {
    private final ItemType fishingRod;
    public TreasureBundle(ItemType fishingRod) {
        super(Material.BLACK_BUNDLE, ChatColor.BLUE+"Treasure Bundle", "TREASURE_BUNDLE");
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
        BundleMeta meta = (BundleMeta) item.getItemMeta();

        List<ItemStack> loot = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            loot.add(TreasureDrop.getTreasureItem(null, fishingRod, true));
        }
        meta.setItems(loot);
        item.setItemMeta(meta);
        item.setItemMeta(ItemDataUtil.setStringValue(item, "fishingRodType", fishingRod != null ? fishingRod.getItem().getID() : ""));
        return item;
    }

}
