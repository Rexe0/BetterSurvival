package me.rexe0.bettersurvival.item.drugs;

import me.rexe0.bettersurvival.farming.alcohol.AlcoholListener;
import me.rexe0.bettersurvival.farming.alcohol.BarrelType;
import me.rexe0.bettersurvival.farming.alcohol.SpiritType;
import me.rexe0.bettersurvival.farming.alcohol.WineType;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Spirit extends Item {
    public static final int MAX_DISTILL_CONCENTRATION = 75;
    public static final int MAX_AGE_CONCENTRATION = 80;

    private final double concentration;
    private final SpiritType type;
    private final int age;
    private final WineType secondaryFlavor;
    private final WineType tertiaryFlavor;
    private final BarrelType quaternaryFlavor;
    private final boolean hasMethanol;

    public Spirit(double concentration, SpiritType type, int age, WineType secondaryFlavor, WineType tertiaryFlavor, BarrelType quaternaryFlavor, boolean hasMethanol) {
        super(Material.POTION, type.getNameColor()+type.getName(), "SPIRIT");
        this.concentration = concentration;
        this.type = type;
        this.age = age;
        this.secondaryFlavor = secondaryFlavor;
        this.tertiaryFlavor = tertiaryFlavor;
        this.quaternaryFlavor = quaternaryFlavor;
        this.hasMethanol = hasMethanol;
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
        item.setItemMeta(ItemDataUtil.setStringValue(item, "spiritType", type.name()));
        item.setItemMeta(ItemDataUtil.setIntegerValue(item, "age", age));
        if (secondaryFlavor != null)
            item.setItemMeta(ItemDataUtil.setStringValue(item, "secondaryFlavor", secondaryFlavor.name()));
        if (tertiaryFlavor != null)
            item.setItemMeta(ItemDataUtil.setStringValue(item, "tertiaryFlavor", tertiaryFlavor.name()));
        if (quaternaryFlavor != null)
            item.setItemMeta(ItemDataUtil.setStringValue(item, "quaternaryFlavor", quaternaryFlavor.name()));
        item.setItemMeta(ItemDataUtil.setIntegerValue(item, "hasMethanol", hasMethanol ? 1 : 0));
        return item;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = super.getLore();
        if (type == SpiritType.DISTILLATE) {
            lore.add(ChatColor.GRAY+"A rather pure, flavorless");
            lore.add(ChatColor.GRAY+"alcohol produced from the");
            lore.add(ChatColor.GRAY+"distillation of fermented");
            lore.add(ChatColor.GRAY+"starch or sugar.");
        } else if (type == SpiritType.SUGAR_WASH) {
            lore.add(ChatColor.GRAY+"A liquor produced from the");
            lore.add(ChatColor.GRAY+"fermentation and distillation");
            lore.add(ChatColor.GRAY+"of cane sugar.");
        } else if (type == SpiritType.BEER) {
            lore.add(ChatColor.GRAY+"A liquor produced from the");
            lore.add(ChatColor.GRAY+"fermentation and distillation");
            lore.add(ChatColor.GRAY+"of wheat and grains.");
        } else if (type == SpiritType.VODKA) {
            lore.add(ChatColor.GRAY+"A very pure spirit produced");
            lore.add(ChatColor.GRAY+"from the repeated distillation");
            lore.add(ChatColor.GRAY+"of fermented grains.");
        } else {
            lore.add(ChatColor.GRAY + "An liquor produced from");
            lore.add(ChatColor.GRAY + "the distillation of fruit");
            lore.add(ChatColor.GRAY + "wines.");
        }
        lore.add(" ");

        String formattedConcentration = ChatColor.GOLD+""+(Math.round(concentration*100)/100d);
        if (concentration < MAX_AGE_CONCENTRATION) formattedConcentration = (ItemDataUtil.getFormattedColorString((Math.round(concentration*100)/100d)+"", Math.min(concentration, MAX_DISTILL_CONCENTRATION), MAX_DISTILL_CONCENTRATION));

        lore.add(ChatColor.GRAY+"Concentration: "+formattedConcentration+"%");
        if (age > 0) {
            int days = age * 30;
            lore.add(ChatColor.GRAY + "Age: " + (ItemDataUtil.getFormattedColorString((days >= 120 ? Math.round((days/120d)*100)/100d : days) + "", Math.min(age, 5), 5) + (days >= 120 ? (days < 240 ? " Year" : " Years") : " Days")));
        }
        if (secondaryFlavor != null || tertiaryFlavor != null || quaternaryFlavor != null) {
            lore.add(" ");
            lore.add(ChatColor.GRAY+"Additional Flavors:");
            if ((secondaryFlavor != null && tertiaryFlavor != null) && (secondaryFlavor == tertiaryFlavor)) {
                String flavorName = secondaryFlavor.getNameColor()+""+ChatColor.BOLD+"Bold "+secondaryFlavor.getFlavorName();
                lore.add(ChatColor.GRAY + "- " + flavorName);
            } else {
                if (secondaryFlavor != null)
                    lore.add(ChatColor.GRAY + "- " + secondaryFlavor.getNameColor() + secondaryFlavor.getFlavorName());
                if (tertiaryFlavor != null)
                    lore.add(ChatColor.GRAY + "- " + tertiaryFlavor.getNameColor() + tertiaryFlavor.getFlavorName());
            }
            if (quaternaryFlavor != null)
                lore.add(ChatColor.GRAY+"- "+quaternaryFlavor.getName());
        }
        if (hasMethanol) {
            lore.add(" ");
            lore.add(ChatColor.RED+"Warning: High Methanol %");
        }
        return lore;
    }

    public void onDrink(PlayerItemConsumeEvent e) {
        if (!ItemDataUtil.isItem(e.getItem(), getID())) return;
        Player player = e.getPlayer();
        ItemStack item = e.getItem();
        double concentration = ItemDataUtil.getDoubleValue(item, "concentration");
        boolean hasMethanol = ItemDataUtil.getIntegerValue(item, "hasMethanol") == 1;

        AlcoholListener.increaseAlcoholContent(player, concentration);

        if (hasMethanol) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) (200+concentration*12), 0, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, (int) (200+concentration*12), 3, true, false));
        } else
            player.setFoodLevel(Math.min(20, player.getFoodLevel()+6));

    }
}
