package me.rexe0.bettersurvival.mobs;

import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CamelChanges implements Listener {
    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) return;
        LivingEntity entity = e.getEntity();
        if (entity.getType() != EntityType.RABBIT) return;
        if (entity.getLocation().getBlock().getBiome() != Biome.DESERT) return;
        if (RandomUtil.getRandom().nextInt(3) != 0) return;
        e.setCancelled(true);
        entity.getWorld().spawnEntity(entity.getLocation(), EntityType.CAMEL);
    }
}
