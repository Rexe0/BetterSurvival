package me.rexe0.bettersurvival.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

public class AmethystArrow extends Item {
    public AmethystArrow() {
        super(Material.ARROW, ChatColor.GREEN+"Amethyst Arrow", "AMETHYST_ARROW");
    }

    @Override
    public void onArrowDamage(LivingEntity entity, Player player, Projectile projectile) {

    }

//    private static class AmethystShard {
//        private
//    }
}