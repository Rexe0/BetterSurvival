package me.rexe0.bettersurvival.mobs;

import me.rexe0.bettersurvival.util.EntityDataUtil;
import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreedEvent;

public class HorseBreeding implements Listener {
    // Chance for a gene to mutate and change. 0.1 is 10%. 1.0 is 100%
    private final double MUTATION_CHANCE = 0.25;

    private final double HEALTH_MIN = 20;
    private final double HEALTH_MAX = 40;

    private final double SPEED_MIN = 0.15;
    private final double SPEED_MAX = 0.5;

    private final double JUMP_MIN = 0.4;
    private final double JUMP_MAX = 1.2;

    private void setHealth(Horse horse, double firstAllele, double secondAllele) {
        firstAllele = mutateGene(firstAllele, HEALTH_MIN, HEALTH_MAX);
        secondAllele = mutateGene(secondAllele, HEALTH_MIN, HEALTH_MAX);

        EntityDataUtil.setDoubleValue(horse, "horseHealth0", firstAllele);
        EntityDataUtil.setDoubleValue(horse, "horseHealth1", secondAllele);
        horse.getAttribute(Attribute.MAX_HEALTH).setBaseValue((firstAllele+secondAllele)/2);
        horse.setHealth((firstAllele+secondAllele)/2);
    }
    private void setSpeed(Horse horse, double firstAllele, double secondAllele) {
        firstAllele = mutateGene(firstAllele, SPEED_MIN, SPEED_MAX);
        secondAllele = mutateGene(secondAllele, SPEED_MIN, SPEED_MAX);


        EntityDataUtil.setDoubleValue(horse, "horseSpeed0", firstAllele);
        EntityDataUtil.setDoubleValue(horse, "horseSpeed1", secondAllele);
        horse.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue((firstAllele+secondAllele)/2);
    }
    private void setJumpHeight(Horse horse, double firstAllele, double secondAllele) {
        firstAllele = mutateGene(firstAllele, JUMP_MIN, JUMP_MAX);
        secondAllele = mutateGene(secondAllele, JUMP_MIN, JUMP_MAX);


        EntityDataUtil.setDoubleValue(horse, "horseJump0", firstAllele);
        EntityDataUtil.setDoubleValue(horse, "horseJump1", secondAllele);
        horse.getAttribute(Attribute.JUMP_STRENGTH).setBaseValue((firstAllele+secondAllele)/2);
    }

    private double mutateGene(double allele, double min, double max) {
        if (RandomUtil.getRandom().nextDouble() >= MUTATION_CHANCE) return allele;
        double change = (RandomUtil.getRandom().nextBoolean() ? -0.1 : 0.1)*(max-min);
        allele += change;

        if (allele > max) allele = max;
        else if (allele < min) allele = min;

        return allele;
    }

    private double pickRandomAllele(Horse horse, String key) {
        return EntityDataUtil.getDoubleValue(horse, key+RandomUtil.getRandom().nextInt(0, 2));
    }


    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.BREEDING) return;
        if (!(e.getEntity() instanceof Horse horse)) return;
        // Spawn normal horses with random alleles. A regular spawning horse can never have a max stat on either of its alleles to encourage horse breeding.
        setHealth(horse, RandomUtil.getRandom().nextDouble(HEALTH_MIN, HEALTH_MAX*0.75),
                RandomUtil.getRandom().nextDouble(HEALTH_MIN, HEALTH_MAX*0.75));
        setSpeed(horse, RandomUtil.getRandom().nextDouble(SPEED_MIN, SPEED_MAX*0.75),
                RandomUtil.getRandom().nextDouble(SPEED_MIN, SPEED_MAX*0.75));
        setJumpHeight(horse, RandomUtil.getRandom().nextDouble(JUMP_MIN, JUMP_MAX*0.75),
                RandomUtil.getRandom().nextDouble(JUMP_MIN, JUMP_MAX*0.75));
    }
    @EventHandler
    public void onBreed(EntityBreedEvent e) {
        if (!(e.getFather() instanceof Horse father && e.getMother() instanceof Horse mother)) return;
        Horse horse = (Horse) e.getEntity();

        // The foul always inherits one allele from the father and one from the mother. However, which one from each is random.
        // Additionally, when when inheriting the parent's genes, there is a chance for either of the alleles to mutate
        setHealth(horse, pickRandomAllele(father, "horseHealth"), pickRandomAllele(mother, "horseHealth"));
        setSpeed(horse, pickRandomAllele(father, "horseSpeed"), pickRandomAllele(mother, "horseSpeed"));
        setJumpHeight(horse, pickRandomAllele(father, "horseJump"), pickRandomAllele(mother, "horseJump"));
    }
}
