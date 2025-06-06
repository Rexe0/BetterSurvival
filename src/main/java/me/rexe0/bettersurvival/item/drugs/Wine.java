package me.rexe0.bettersurvival.item.drugs;

import me.rexe0.bettersurvival.farming.alcohol.BarrelType;
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
    public static final int MAX_FERMENT_CONCENTRATION = 15;
    public static final int MAX_AGE_CONCENTRATION = 20;

    private final double concentration;
    private final WineType type;
    private final int age;
    private final WineType secondaryFlavor;
    private final BarrelType tertiaryFlavor;

    public Wine(double concentration, WineType type, int age) {
        this(concentration, type, age, null, null);
    }

    public Wine(double concentration, WineType type, int age, WineType secondaryFlavor, BarrelType tertiaryFlavor) {
        super(Material.POTION, type.getNameColor()+type.getName(), "WINE");
        this.concentration = concentration;
        this.type = type;
        this.age = age;
        this.secondaryFlavor = secondaryFlavor;
        this.tertiaryFlavor = tertiaryFlavor;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.setMaxStackSize(1);
        meta.setColor(type.getColor());
        item.setItemMeta(meta);
        item.setItemMeta(ItemDataUtil.setDoubleValue(item, "concentration", concentration));
        item.setItemMeta(ItemDataUtil.setStringValue(item, "wineType", type.name()));
        item.setItemMeta(ItemDataUtil.setIntegerValue(item, "age", age));
        if (secondaryFlavor != null)
            item.setItemMeta(ItemDataUtil.setStringValue(item, "secondaryFlavor", secondaryFlavor.name()));
        if (tertiaryFlavor != null)
            item.setItemMeta(ItemDataUtil.setStringValue(item, "tertiaryFlavor", tertiaryFlavor.name()));
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

        String formattedConcentration = ChatColor.GOLD+""+(Math.round(concentration*100)/100d);
        if (concentration < MAX_AGE_CONCENTRATION) formattedConcentration = (ItemDataUtil.getFormattedColorString((Math.round(concentration*100)/100d)+"", Math.min(concentration, MAX_FERMENT_CONCENTRATION), MAX_FERMENT_CONCENTRATION));

        lore.add(ChatColor.GRAY+"Concentration: "+formattedConcentration+"%");
        if (age > 0) {
            int days = age * 30;
            lore.add(ChatColor.GRAY + "Age: " + (ItemDataUtil.getFormattedColorString((days >= 120 ? Math.round((days/120d)*100)/100d : days) + "", Math.min(age, 5), 5) + (days >= 120 ? (days < 240 ? " Year" : " Years") : " Days")));
        }
        if (secondaryFlavor != null || tertiaryFlavor != null) {
            lore.add(" ");
            lore.add(ChatColor.GRAY+"Additional Flavors:");
            if (secondaryFlavor != null) {
                String secondaryFlavorName = secondaryFlavor == type ? ChatColor.BOLD+"Bold" : secondaryFlavor.getFlavorName();
                lore.add(ChatColor.GRAY + "- " + secondaryFlavor.getNameColor()+secondaryFlavorName);
            }
            if (tertiaryFlavor != null)
                lore.add(ChatColor.GRAY+"- "+tertiaryFlavor.getName());
        }
        return lore;
    }
}
