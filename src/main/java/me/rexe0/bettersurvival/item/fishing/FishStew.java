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
