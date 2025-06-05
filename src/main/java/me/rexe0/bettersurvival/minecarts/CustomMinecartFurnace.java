package me.rexe0.bettersurvival.minecarts;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R3.CraftWorld;

public class CustomMinecartFurnace extends MinecartFurnace {
    public CustomMinecartFurnace(Location location) {
        super(EntityType.FURNACE_MINECART, ((CraftWorld)location.getWorld()).getHandle());
        setPos(location.getX(), location.getY(), location.getZ());
    }
    @Override
    protected double getMaxSpeed(ServerLevel var0) {
        return this.isInWater() ? super.getMaxSpeed(var0) / 2.0 : super.getMaxSpeed(var0);
    }

}
