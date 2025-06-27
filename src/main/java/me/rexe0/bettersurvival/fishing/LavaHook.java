package me.rexe0.bettersurvival.fishing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.World;
import org.bukkit.entity.FishHook;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerFishEvent;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class LavaHook extends FishingHook {
//    private static final EntityDataAccessor<Boolean> DATA_BITING;
//    public static final EntityDataAccessor<Integer> DATA_HOOKED_ENTITY;
//
//    static {
//        DATA_HOOKED_ENTITY = SynchedEntityData.defineId(LavaHook.class, EntityDataSerializers.INT);
//        DATA_BITING = SynchedEntityData.defineId(LavaHook.class, EntityDataSerializers.BOOLEAN);
//    }

    private final RandomSource syncronizedRandom;
    private int life;
    private boolean biting;
    private int nibble;
    private float fishAngle;
    private int outOfWaterTime;

    public LavaHook(Player entityhuman, Level world) {
        super(entityhuman, world, 0, 0);
        this.syncronizedRandom = RandomSource.create();
    }

//    @Override
//    protected void defineSynchedData(SynchedEntityData.Builder datawatcher_a) {
//        datawatcher_a.define(DATA_HOOKED_ENTITY, 0);
//        datawatcher_a.define(DATA_BITING, false);
//    }
//    @Override
//    public void onSyncedDataUpdated(EntityDataAccessor<?> datawatcherobject) {
//        if (DATA_HOOKED_ENTITY.equals(datawatcherobject)) {
//            int i = (Integer)this.getEntityData().get(DATA_HOOKED_ENTITY);
//            this.hookedIn = i > 0 ? this.level().getEntity(i - 1) : null;
//        }
//
//        if (DATA_BITING.equals(datawatcherobject)) {
//            this.biting = (Boolean)this.getEntityData().get(DATA_BITING);
//            if (this.biting) {
//                this.setDeltaMovement(this.getDeltaMovement().x, (double)(-0.4F * Mth.nextFloat(this.syncronizedRandom, 0.6F, 1.0F)), this.getDeltaMovement().z);
//            }
//        }
//
//        if (DATA_POSE.equals(datawatcherobject)) {
//            this.refreshDimensions();
//        }
//
//    }

    @Override
    public void tick() {

        this.syncronizedRandom.setSeed(this.getUUID().getLeastSignificantBits() ^ this.level().getGameTime());
        Player entityhuman = this.getPlayerOwner();
        if (entityhuman == null) {
            this.discard(EntityRemoveEvent.Cause.DESPAWN);
        } else if (this.level().isClientSide || !this.shouldStopFishing(entityhuman)) {
            if (this.onGround()) {
                ++this.life;
                if (this.life >= 1200) {
                    this.discard(EntityRemoveEvent.Cause.DESPAWN);
                    return;
                }
            } else {
                this.life = 0;
            }

            float f = 0.0F;
            BlockPos blockposition = this.blockPosition();
            FluidState fluid = this.level().getFluidState(blockposition);
            if (fluid.is(FluidTags.LAVA)) {
                f = fluid.getHeight(this.level(), blockposition);
            }

            boolean flag = f > 0.0F;
            if (this.currentState == FishingHook.FishHookState.FLYING) {
                if (this.hookedIn != null) {
                    this.setDeltaMovement(Vec3.ZERO);
                    this.currentState = FishingHook.FishHookState.HOOKED_IN_ENTITY;
                    return;
                }

                if (flag) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.3, 0.2, 0.3));
                    this.currentState = FishingHook.FishHookState.BOBBING;
                    return;
                }

                this.checkCollision();
            } else {
                if (this.currentState == FishingHook.FishHookState.HOOKED_IN_ENTITY) {
                    if (this.hookedIn != null) {
                        if (!this.hookedIn.isRemoved() && this.hookedIn.level().dimension() == this.level().dimension()) {
                            this.setPos(this.hookedIn.getX(), this.hookedIn.getY(0.8), this.hookedIn.getZ());
                        } else {
                            this.setHookedEntity((Entity)null);
                            this.currentState = FishingHook.FishHookState.FLYING;
                        }
                    }

                    return;
                }

                if (this.currentState == FishingHook.FishHookState.BOBBING) {
                    Vec3 vec3d = this.getDeltaMovement();
                    double d0 = this.getY() + vec3d.y - (double)blockposition.getY() - (double)f;
                    if (Math.abs(d0) < 0.01) {
                        d0 += Math.signum(d0) * 0.1;
                    }

                    this.setDeltaMovement(vec3d.x * 0.9, vec3d.y - d0 * (double)this.random.nextFloat() * 0.2, vec3d.z * 0.9);

                    if (flag) {
                        this.outOfWaterTime = Math.max(0, this.outOfWaterTime - 1);
                        if (this.biting) {
                            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.1 * (double)this.syncronizedRandom.nextFloat() * (double)this.syncronizedRandom.nextFloat(), 0.0));
                        }

                        if (!this.level().isClientSide) {
                            this.catchingFish(blockposition);
                        }
                    } else {
                        this.outOfWaterTime = Math.min(10, this.outOfWaterTime + 1);
                    }
                }
            }

            if (!fluid.is(FluidTags.LAVA)) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.03, 0.0));
            }

            this.move(MoverType.SELF, this.getDeltaMovement());
            this.applyEffectsFromBlocks();
            this.updateRotation();
            if (this.currentState == FishingHook.FishHookState.FLYING && (this.onGround() || this.horizontalCollision)) {
                this.setDeltaMovement(Vec3.ZERO);
            }

            this.setDeltaMovement(this.getDeltaMovement().scale(0.92));
            this.reapplyPosition();
        }
    }

    private void catchingFish(BlockPos blockposition) {
        ServerLevel worldserver = (ServerLevel) this.level();
        int i = 1;

        if (this.level().getWorld().getEnvironment() != World.Environment.NETHER && this.random.nextFloat() < 0.33F)
            --i;


        PlayerFishEvent playerFishEvent;
        if (this.nibble > 0) {
            --this.nibble;
            if (this.nibble <= 0) {
                this.timeUntilLured = 0;
                this.timeUntilHooked = 0;
                this.getEntityData().set(getDataBiting(), false);
                playerFishEvent = new PlayerFishEvent((org.bukkit.entity.Player) this.getPlayerOwner().getBukkitEntity(), (org.bukkit.entity.Entity) null, (FishHook) this.getBukkitEntity(), PlayerFishEvent.State.FAILED_ATTEMPT);
                this.level().getCraftServer().getPluginManager().callEvent(playerFishEvent);
            }
        } else {
            float f6;
            float f7;
            double d4;
            double d5;
            double d6;
            BlockState iblockdata1;
            float f5;
            if (this.timeUntilHooked > 0) {
                this.timeUntilHooked -= i;
                if (this.timeUntilHooked > 0) {
                    this.fishAngle += (float) this.random.triangle(0.0, 9.188);
                    f5 = this.fishAngle * 0.017453292F;
                    f6 = Mth.sin(f5);
                    f7 = Mth.cos(f5);
                    d4 = this.getX() + (double) (f6 * (float) this.timeUntilHooked * 0.1F);
                    d5 = (double) ((float) Mth.floor(this.getY()) + 1.0F);
                    d6 = this.getZ() + (double) (f7 * (float) this.timeUntilHooked * 0.1F);
                    iblockdata1 = worldserver.getBlockState(BlockPos.containing(d4, d5 - 1.0, d6));
                    if (iblockdata1.is(Blocks.LAVA)) {
                        if (this.random.nextFloat() < 0.15F) {
                            worldserver.sendParticles(ParticleTypes.LAVA, d4, d5 - 0.10000000149011612, d6, 1, (double) f6, 0.1, (double) f7, 0.0);
                        }

                        float f3 = f6 * 0.04F;
                        float f4 = f7 * 0.04F;
                        worldserver.sendParticles(ParticleTypes.FLAME, d4, d5, d6, 0, (double) f4, 0.01, (double) (-f3), 1.0);
                        worldserver.sendParticles(ParticleTypes.FLAME, d4, d5, d6, 0, (double) (-f4), 0.01, (double) f3, 1.0);
                    }
                } else {
                    playerFishEvent = new PlayerFishEvent((org.bukkit.entity.Player) this.getPlayerOwner().getBukkitEntity(), (org.bukkit.entity.Entity) null, (FishHook) this.getBukkitEntity(), PlayerFishEvent.State.BITE);
                    this.level().getCraftServer().getPluginManager().callEvent(playerFishEvent);
                    if (playerFishEvent.isCancelled()) {
                        return;
                    }

                    this.playSound(SoundEvents.FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                    double d3 = this.getY() + 0.5;
                    worldserver.sendParticles(ParticleTypes.LAVA, this.getX(), d3, this.getZ(), (int) (1.0F + this.getBbWidth() * 20.0F), (double) this.getBbWidth(), 0.0, (double) this.getBbWidth(), 0.20000000298023224);
                    worldserver.sendParticles(ParticleTypes.FLAME, this.getX(), d3, this.getZ(), (int) (1.0F + this.getBbWidth() * 20.0F), (double) this.getBbWidth(), 0.0, (double) this.getBbWidth(), 0.20000000298023224);
                    this.nibble = Mth.nextInt(this.random, 20, 40);
                    this.getEntityData().set(getDataBiting(), true);
                }
            } else if (this.timeUntilLured > 0) {
                this.timeUntilLured -= i;
                f5 = 0.15F;
                if (this.timeUntilLured < 20) {
                    f5 += (float) (20 - this.timeUntilLured) * 0.05F;
                } else if (this.timeUntilLured < 40) {
                    f5 += (float) (40 - this.timeUntilLured) * 0.02F;
                } else if (this.timeUntilLured < 60) {
                    f5 += (float) (60 - this.timeUntilLured) * 0.01F;
                }

                if (this.random.nextFloat() < f5) {
                    f6 = Mth.nextFloat(this.random, 0.0F, 360.0F) * 0.017453292F;
                    f7 = Mth.nextFloat(this.random, 25.0F, 60.0F);
                    d4 = this.getX() + (double) (Mth.sin(f6) * f7) * 0.1;
                    d5 = (double) ((float) Mth.floor(this.getY()) + 1.0F);
                    d6 = this.getZ() + (double) (Mth.cos(f6) * f7) * 0.1;
                    iblockdata1 = worldserver.getBlockState(BlockPos.containing(d4, d5 - 1.0, d6));
                    if (iblockdata1.is(Blocks.LAVA)) {
                        worldserver.sendParticles(ParticleTypes.LAVA, d4, d5, d6, 1, 0.10000000149011612, 0.0, 0.10000000149011612, 0.0);
                    }
                }

                if (this.timeUntilLured <= 0) {
                    this.fishAngle = Mth.nextFloat(this.random, this.minLureAngle, this.maxLureAngle);
                    this.timeUntilHooked = Mth.nextInt(this.random, this.minLureTime, this.maxLureTime);
                }
            } else {
                this.timeUntilLured = Mth.nextInt(this.random, this.minWaitTime, this.maxWaitTime);
            }
        }
    }

    private void checkCollision() {
        HitResult movingobjectposition = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        this.preHitTargetOrDeflectSelf(movingobjectposition);
    }

    private boolean shouldStopFishing(Player entityhuman) {
        ItemStack itemstack = entityhuman.getMainHandItem();
        ItemStack itemstack1 = entityhuman.getOffhandItem();
        boolean flag = itemstack.is(Items.FISHING_ROD);
        boolean flag1 = itemstack1.is(Items.FISHING_ROD);
        if (!entityhuman.isRemoved() && entityhuman.isAlive() && (flag || flag1) && this.distanceToSqr(entityhuman) <= 1024.0) {
            return false;
        } else {
            this.discard(EntityRemoveEvent.Cause.DESPAWN);
            return true;
        }
    }

    @Override
    public void setHookedEntity(@Nullable Entity entity) {
        this.hookedIn = entity;
        this.getEntityData().set(DATA_HOOKED_ENTITY, entity == null ? 0 : entity.getId() + 1);
    }

    public EntityDataAccessor<Boolean> getDataBiting() {
        EntityDataAccessor<Boolean> data = null;
        try {
            Field field = FishingHook.class.getDeclaredField("DATA_BITING");
            field.setAccessible(true);
            data = (EntityDataAccessor<Boolean>) field.get(null);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return data;
    }
}
