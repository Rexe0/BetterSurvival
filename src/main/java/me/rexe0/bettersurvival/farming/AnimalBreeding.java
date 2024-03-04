package me.rexe0.bettersurvival.farming;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.weather.Season;
import org.bukkit.Particle;
import org.bukkit.entity.Breedable;
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
            mother.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, mother.getLocation().add(0, 1, 0)
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
}
