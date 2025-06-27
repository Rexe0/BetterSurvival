package me.rexe0.bettersurvival.minecarts;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R5.CraftWorld;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

public class CustomMinecart extends Minecart {
    public CustomMinecart(Location location) {
        super(EntityType.MINECART, ((CraftWorld)location.getWorld()).getHandle());
        setPos(location.getX(), location.getY(), location.getZ());
    }
    @Override
    protected double getMaxSpeed(ServerLevel var0) {
        return getBehavior().getMaxSpeed(var0);
    }

    @Override
    protected Vec3 applyNaturalSlowdown(Vec3 vec3d) {
        double d0 = getBehavior().getSlowdownFactor();
        Vec3 vec3d1 = vec3d.multiply(d0, 0.0, d0);
        if (this.isInWater()) {
            vec3d1 = vec3d1.scale(0.949999988079071);
        }

        return vec3d1;
    }
    @Override
    public void push(Entity entity) {
        if (!this.level().isClientSide && !entity.noPhysics && !this.noPhysics && !this.hasPassenger(entity)) {
            VehicleEntityCollisionEvent collisionEvent = new VehicleEntityCollisionEvent((Vehicle)this.getBukkitEntity(), entity.getBukkitEntity());
            this.level().getCraftServer().getPluginManager().callEvent(collisionEvent);
            if (collisionEvent.isCancelled()) {
                return;
            }

            double d0 = entity.getX() - this.getX();
            double d1 = entity.getZ() - this.getZ();
            double d2 = d0 * d0 + d1 * d1;
            if (d2 >= 9.999999747378752E-5) {
                d2 = Math.sqrt(d2);
                d0 /= d2;
                d1 /= d2;
                double d3 = 1.0 / d2;
                if (d3 > 1.0) {
                    d3 = 1.0;
                }

                d0 *= d3;
                d1 *= d3;
                d0 *= 0.10000000149011612;
                d1 *= 0.10000000149011612;
                d0 *= 0.5;
                d1 *= 0.5;
                if (!(entity instanceof AbstractMinecart)) {
                    this.push(-d0, 0.0, -d1);
                    entity.push(d0 / 4.0, 0.0, d1 / 4.0);
                }
            }
        }
    }
}
