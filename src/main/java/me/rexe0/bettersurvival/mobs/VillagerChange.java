package me.rexe0.bettersurvival.mobs;

import me.rexe0.bettersurvival.BetterSurvival;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.npc.Villager;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;

public class VillagerChange implements Listener {
    public static void limitMinorPositiveMax() {
        try {
            // Get Max field
            Field field = GossipType.class.getField("k");
            field.setAccessible(true);
            field.setInt(GossipType.MINOR_POSITIVE, 100);
            field.setAccessible(false);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onCure(CreatureSpawnEvent e) {
        if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CURED) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                Villager villager = ((CraftVillager)e.getEntity()).getHandle();
                villager.getGossips().remove(GossipType.MAJOR_POSITIVE);
            }
        }.runTaskLater(BetterSurvival.getInstance(), 1);
    }
}
