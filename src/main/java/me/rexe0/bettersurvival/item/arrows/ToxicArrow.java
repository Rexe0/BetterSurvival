package me.rexe0.bettersurvival.item.arrows;

import me.rexe0.bettersurvival.item.Item;
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
        lore.add(ChatColor.RED+"Slowness I (0:05)");
        lore.add(ChatColor.RED+"Blindness (0:05)");
        lore.add(ChatColor.RED+"Nausea (0:05)");
        return lore;
    }

    @Override
    public double onArrowDamage(LivingEntity entity, Player player, Arrow arrow, double damage) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 0));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 0));
        return damage;
    }
}