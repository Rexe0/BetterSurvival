package me.rexe0.bettersurvival.item.drugs;

import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.List;

public class Beer extends Item {
    private static final int MAX_CONCENTRATION = 12;

    private final int concentration;
    public Beer(int concentration) {
        super(Material.POTION, ChatColor.YELLOW+"Beer", "BEER");
        this.concentration = Math.min(MAX_CONCENTRATION, concentration);
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.setMaxStackSize(64);
        meta.setColor(Color.fromRGB(247, 216, 104));
        item.setItemMeta(meta);
        item.setItemMeta(ItemDataUtil.setIntegerValue(item, "concentration", concentration));
        return item;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = super.getLore();
        lore.add(ChatColor.GRAY+"A rather light alcoholic");
        lore.add(ChatColor.GRAY+"beverage produced from the");
        lore.add(ChatColor.GRAY+"fermentation of starch.");
        lore.add(" ");
        lore.add(ChatColor.GRAY+"Concentration: "+(ItemDataUtil.getFormattedColorString(concentration+"", concentration, MAX_CONCENTRATION))+"%");
        return lore;
    }
}
