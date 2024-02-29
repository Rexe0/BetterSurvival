package me.rexe0.bettersurvival.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

public class ExplosiveArrow extends Item {
    public ExplosiveArrow() {
        super(Material.ARROW, ChatColor.GREEN+"Explosive Arrow", "EXPLOSIVE_ARROW");
    }

    @Override
    public void onArrowDamage(LivingEntity entity, Player player, Projectile projectile) {
        projectile.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, projectile.getLocation(), 10, 1, 1, 1, 0);
        projectile.getWorld().playSound(projectile.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
        for (Entity en : projectile.getNearbyEntities(3, 3, 3)) {
            if (!(en instanceof LivingEntity living)) continue;
            if (en.equals(entity)) continue;
            living.damage(5, projectile);
        }
    }

}