package me.rexe0.bettersurvival.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class ToxicArrow extends Item {
    public ToxicArrow() {
        super(Material.ARROW, ChatColor.GREEN+"Toxic Arrow", "TOXIC_ARROW");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RED+"Slowness I (0:08)");
        lore.add(ChatColor.RED+"Blindness (0:08)");
        lore.add(ChatColor.RED+"Nausea (0:08)");
        return lore;
    }

    @Override
    public void onArrowDamage(LivingEntity entity, Player player, Arrow arrow, double damage) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 160, 0));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 160, 0));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 160, 0));
    }
}