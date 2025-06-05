package me.rexe0.bettersurvival.item.drugs;

import me.rexe0.bettersurvival.farming.alcohol.WineType;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.List;

public class Wine extends Item {
    private static final int MAX_CONCENTRATION = 15;

    private final int concentration;
    private final WineType type;

    public Wine(int concentration, WineType type) {
        super(Material.POTION, ChatColor.DARK_PURPLE+type.getName(), "WINE");
        this.concentration = Math.min(MAX_CONCENTRATION, concentration);
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
        item.setItemMeta(ItemDataUtil.setIntegerValue(item, "concentration", concentration));
        item.setItemMeta(ItemDataUtil.setStringValue(item, "wineType", type.name()));
        return item;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = super.getLore();
        lore.add(ChatColor.GRAY+"An alcoholic beverage");
        lore.add(ChatColor.GRAY+"produced from the fermentation");
        lore.add(ChatColor.GRAY+"of fruit.");
        lore.add(" ");
        lore.add(ChatColor.GRAY+"Concentration: "+(ItemDataUtil.getFormattedColorString(concentration+"", concentration, MAX_CONCENTRATION))+"%");
        return lore;
    }
}
