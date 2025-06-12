package me.rexe0.bettersurvival.farming;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.farming.Fertilizer;
import me.rexe0.bettersurvival.util.RandomUtil;
import me.rexe0.bettersurvival.weather.Season;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class AnimalBreeding implements Listener {
    @EventHandler
    public void onBreed(EntityBreedEvent e) {
        int amount = 0;
        Breedable mother = (Breedable) e.getMother();
        Breedable father = (Breedable) e.getFather();

        for (Breedable entity : mother.getWorld().getEntitiesByClass(mother.getClass()))
            if (entity.getLocation().distanceSquared(mother.getLocation()) < 4) amount++;

        int time = switch (Season.getSeason()) {
            default -> 24000;
            case AUTUMN -> 30000;
            case WINTER -> 36000;
        };
        int age = amount > 5 ? 0 : time;

        if (amount > 5) {
            mother.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, mother.getLocation().add(0, 1, 0)
                    , 10, 0.3, 0.15, 0.3, 0);
            e.setCancelled(true);
        }


        new BukkitRunnable() {
            @Override
            public void run() {
                mother.setAge(age);
                father.setAge(age);
                father.setBreed(false);
                mother.setBreed(false);
            }
        }.runTaskLater(BetterSurvival.getInstance(), 1);
    }

    public static void run() {
        World world = BetterSurvival.getInstance().getDefaultWorld();
        for (LivingEntity entity : world.getLivingEntities()) {
            if (entity instanceof Cow || entity instanceof Pig || entity instanceof Sheep) {
                if (RandomUtil.getRandom().nextInt(12000) != 0) continue;
                boolean nearbyPlayer = entity.getWorld().getPlayers().stream().anyMatch(player -> player.getLocation().distanceSquared(entity.getLocation()) < 10000);
                if (!nearbyPlayer) continue;
                entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 0);
                entity.getWorld().dropItemNaturally(entity.getBoundingBox().getCenter().toLocation(world), new Fertilizer(RandomUtil.getRandom().nextInt(5) == 0 ? 2 : 1).getItem());
            }
        }
    }
}
