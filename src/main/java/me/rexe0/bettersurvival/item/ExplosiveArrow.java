package me.rexe0.bettersurvival.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ExplosiveArrow extends Item {
    public ExplosiveArrow() {
        super(Material.ARROW, ChatColor.GREEN+"Explosive Arrow", "EXPLOSIVE_ARROW");
    }

    @Override
    public double onArrowDamage(LivingEntity entity, Player player, Arrow arrow, double damage) {
        arrow.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, arrow.getLocation(), 10, 1, 1, 1, 0);
        arrow.getWorld().playSound(arrow.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
        for (Entity en : arrow.getNearbyEntities(3, 3, 3)) {
            if (!(en instanceof LivingEntity living)) continue;
            if (en.equals(entity)) continue;
            living.damage(5, arrow);
        }
        return damage;
    }

}