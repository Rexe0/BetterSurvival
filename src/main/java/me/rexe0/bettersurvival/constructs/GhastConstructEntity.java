package me.rexe0.bettersurvival.constructs;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.level.Level;

public class GhastConstructEntity extends HappyGhast {
    public GhastConstructEntity(Level level) {
        super(EntityType.HAPPY_GHAST, level);
    }
}
