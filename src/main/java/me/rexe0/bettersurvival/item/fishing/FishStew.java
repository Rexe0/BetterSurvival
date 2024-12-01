package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.fishing.BiomeGroup;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class FishStew extends Item {
    // Arrays are linked by index
    private BiomeGroup[] biomeGroups;
    private double[] fishWeights;

    public FishStew(BiomeGroup[] biomeGroups, double[] fishWeights) {
        super(Material.MUSHROOM_STEW, ChatColor.GREEN+"Fish Stew", "FISH_STEW");
        this.biomeGroups = biomeGroups;
        this.fishWeights = fishWeights;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        for (int i = 0; i < biomeGroups.length; i++) {
            BiomeGroup group = biomeGroups[i];
            PotionEffectType effect = group.getEffect();
            double weight = fishWeights[i];

            if (effect == PotionEffectType.HUNGER)
                lore.add(ChatColor.GRAY+"+"+(Math.round((weight/group.getEffectDivisor())*100)/100)+" Hunger");
            else if (effect == PotionEffectType.SATURATION)
                lore.add(ChatColor.GRAY+"+"+(Math.round((weight/group.getEffectDivisor())*100)/100)+" Saturation");
            else {
                int ticks = effect == PotionEffectType.REGENERATION ? (int) (weight * 10) : (int) weight*100;
                int minutes = ticks/1200;
                int seconds = (ticks/20)%60;
                lore.add(ChatColor.GRAY+getEffectName(effect)+" "
                        +(ItemDataUtil.IntegerToRomanNumeral((int) (weight/group.getEffectDivisor()+1)))
                        +" ("+minutes+":"+String.format("%02d", seconds)+")");
            }
        }
        return lore;
    }

    private String getEffectName(PotionEffectType effect) {
        String key = effect.getKey().getKey().toLowerCase();
        StringBuilder builder = new StringBuilder();
        boolean capitalize = true;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (c == '_') {
                builder.append(' ');
                capitalize = true;
                continue;
            }
            builder.append(capitalize ? Character.toUpperCase(c) : c);
            capitalize = false;

        }
        return key.substring(0, 1).toUpperCase() + key.substring(1).toLowerCase();
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        for (int i = 0; i < biomeGroups.length; i++)
            item.setItemMeta(ItemDataUtil.setDoubleValue(item, "fishStew"+biomeGroups[i].name(), fishWeights[i]));
        return item;
    }

    public void onDrink(PlayerItemConsumeEvent e) {
        if (!ItemDataUtil.isItem(e.getItem(), getID())) return;
        Player player = e.getPlayer();
        ItemStack item = e.getItem();

        for (BiomeGroup group : BiomeGroup.values()) {
            if (ItemDataUtil.getDoubleValue(item, "fishStew"+group.name()) == 0) continue;
            PotionEffectType effect = group.getEffect();
            double weight = ItemDataUtil.getDoubleValue(item, "fishStew"+group.name());

            if (effect == PotionEffectType.HUNGER)
                player.setFoodLevel((int) (player.getFoodLevel()+(weight/group.getEffectDivisor())));
            else if (effect == PotionEffectType.SATURATION)
                player.setSaturation((float) (player.getSaturation()+(weight/group.getEffectDivisor())));
            else
                player.addPotionEffect(new PotionEffect(effect, effect == PotionEffectType.REGENERATION ? (int) (weight * 10) : (int) weight*100, (int) weight/group.getEffectDivisor(), true, true));
        }
    }
}
