package me.rexe0.bettersurvival.mobs;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PiglinChange implements Listener {
    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (!(e.getEntity() instanceof Zombie zombie)) return;
        if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) return;
        boolean nearbyValidPlayer = false;
        boolean nearbyEndPlayer = false;
        for (Player p : zombie.getWorld().getPlayers()) {
            if (p.getLocation().distanceSquared(zombie.getLocation()) > 10000) continue;
            if (p.getScoreboardTags().contains("hasTravelledToEnd"))
                nearbyEndPlayer = true;
            if (!p.getScoreboardTags().contains("hasMinedAncientDebris")) continue;
            nearbyValidPlayer = true;
            break;
        }

        // If there is a nearby player who has mined ancient debris before then start spawning piglins in the overworld
        if (!nearbyValidPlayer) return;

        if (RandomUtil.getRandom().nextInt(3) != 0) return;
        e.setCancelled(true);

        if (RandomUtil.getRandom().nextInt(3) == 0) {
            PiglinBrute brute = (PiglinBrute) zombie.getWorld().spawnEntity(zombie.getLocation(), EntityType.PIGLIN_BRUTE);
            brute.setImmuneToZombification(true);
            brute.getEquipment().setItemInMainHand(new ItemStack(Material.GOLDEN_AXE));
            brute.setAdult();
            return;
        }
        Piglin piglin = (Piglin) zombie.getWorld().spawnEntity(zombie.getLocation(), EntityType.PIGLIN);
        piglin.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(28);
        piglin.setHealth(28);
        piglin.setAdult();
        piglin.setImmuneToZombification(true);

        if (RandomUtil.getRandom().nextBoolean()) piglin.getEquipment().setItemInMainHand(new ItemStack(Material.CROSSBOW));
        else piglin.getEquipment().setItemInMainHand(new ItemStack(Material.GOLDEN_SWORD));

        // If a nearby player has been to the end, the piglins will have a piece of netherite armor that can't drop
        if (nearbyEndPlayer) {
            switch (RandomUtil.getRandom().nextInt(4)) {
                case 0 -> piglin.getEquipment().setHelmet(new ItemStack(Material.NETHERITE_HELMET));
                case 1 -> piglin.getEquipment().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
                case 2 -> piglin.getEquipment().setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
                case 3 -> piglin.getEquipment().setBoots(new ItemStack(Material.NETHERITE_BOOTS));
            }

            // Stop them from ever dropping, even with looting enchantment
            piglin.getEquipment().setHelmetDropChance(-327.67f);
            piglin.getEquipment().setChestplateDropChance(-327.67f);
            piglin.getEquipment().setLeggingsDropChance(-327.67f);
            piglin.getEquipment().setBootsDropChance(-327.67f);
        }


        // Constantly aggressive
        new BukkitRunnable() {
            @Override
            public void run() {
                if (piglin == null || piglin.isDead()) {
                    cancel();
                    return;
                }
                if (piglin.getTarget() != null) return;
                for (Player p : piglin.getWorld().getPlayers()) {
                    if (p.getLocation().distanceSquared(piglin.getLocation()) > 400) continue;
                    piglin.setTarget(p);
                    return;
                }
            }
        }.runTaskTimer(BetterSurvival.getInstance(), 0, 40);

    }
    @EventHandler
    public void onMine(BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.ANCIENT_DEBRIS) return;
        if (e.getPlayer().getScoreboardTags().contains("hasMinedAncientDebris")) return;
        e.getPlayer().addScoreboardTag("hasMinedAncientDebris");
    }
}
