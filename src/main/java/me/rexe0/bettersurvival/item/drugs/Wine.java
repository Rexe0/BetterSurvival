package me.rexe0.bettersurvival.item.drugs;

import me.rexe0.bettersurvival.farming.alcohol.WineType;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.List;

public class Wine extends Item {
    public static final int MAX_CONCENTRATION = 15;

    private final double concentration;
    private final WineType type;

    public Wine(double concentration, WineType type) {
        super(Material.POTION, type.getName(), "WINE");
        this.concentration = concentration;
        this.type = type;
    }


    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.setMaxStackSize(64);
        meta.setColor(type.getColor());
        item.setItemMeta(meta);
        item.setItemMeta(ItemDataUtil.setDoubleValue(item, "concentration", concentration));
        item.setItemMeta(ItemDataUtil.setStringValue(item, "wineType", type.name()));
        return item;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = super.getLore();
        if (type == WineType.SUGAR_WASH) {
            lore.add(ChatColor.GRAY+"A fermented combination of");
            lore.add(ChatColor.GRAY+"sugar and water. It doesn't");
            lore.add(ChatColor.GRAY+"taste very nice...");
        } else if (type == WineType.BEER) {
            lore.add(ChatColor.GRAY+"A rather light alcoholic");
            lore.add(ChatColor.GRAY+"beverage produced from the");
            lore.add(ChatColor.GRAY+"fermentation of starch.");
        } else {
            lore.add(ChatColor.GRAY + "An alcoholic beverage");
            lore.add(ChatColor.GRAY + "produced from the fermentation");
            lore.add(ChatColor.GRAY + "of fruit.");
        }
        lore.add(" ");
        lore.add(ChatColor.GRAY+"Concentration: "+(ItemDataUtil.getFormattedColorString(concentration+"", Math.round(concentration*100)/100d, MAX_CONCENTRATION))+"%");
        return lore;
    }
}
