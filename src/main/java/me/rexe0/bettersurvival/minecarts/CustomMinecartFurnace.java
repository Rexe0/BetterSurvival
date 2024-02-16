package me.rexe0.bettersurvival.minecarts;

import net.minecraft.world.entity.vehicle.MinecartFurnace;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;

public class CustomMinecartFurnace extends MinecartFurnace {
    public CustomMinecartFurnace(Location location) {
        super(((CraftWorld)location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ());
    }

    @Override
    protected double getMaxSpeed() {
        return this.isInWater() ? this.maxSpeed / 2.0 : this.maxSpeed;
    }

}
