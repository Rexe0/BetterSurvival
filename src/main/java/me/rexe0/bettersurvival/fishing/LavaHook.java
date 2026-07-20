package me.rexe0.bettersurvival.fishing;

import io.papermc.paper.event.entity.FishHookStateChangeEvent;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import net.kyori.adventure.util.TriState;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.entity.FishHook;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class LavaHook extends FishingHook {
    private final RandomSource syncronizedRandom;
    private @Nullable Entity hookedIn;
    private int life;
    private boolean biting;
    private int nibble;
    private float fishAngle;
    private int outOfWaterTime;

    public LavaHook(Player player, Level level, int luck, int lureSpeed) {
        super(player, level, luck, lureSpeed);
        this.syncronizedRandom = RandomSource.create();
    }


    @Override
    public boolean fireImmune() {
        return true;
    }

    public void tick() {
        setRemainingFireTicks(0);
        visualFire = TriState.FALSE;
        clearFire();
        extinguishFire();
        displayFireAnimation();

        this.syncronizedRandom.setSeed(this.getUUID().getLeastSignificantBits() ^ this.level().getGameTime());
        this.getInterpolation().interpolate();

        Player owner = this.getPlayerOwner();
        if (owner == null) {
            this.discard(EntityRemoveEvent.Cause.DESPAWN);
        } else if (this.level().isClientSide() || !this.shouldStopFishing(owner)) {
            if (this.onGround()) {
                ++this.life;
                if (this.life >= 1200) {
                    this.discard(EntityRemoveEvent.Cause.DESPAWN);
                    return;
                }
            } else {
                this.life = 0;
            }

            float liquidHeight = 0.0F;
            BlockPos blockPos = this.blockPosition();
            FluidState fluidState = this.level().getFluidState(blockPos);
            if (fluidState.is(FluidTags.LAVA)) {
                liquidHeight = fluidState.getHeight(this.level(), blockPos);
            }

            boolean isInWater = liquidHeight > 0.0F;
            if (this.currentState == FishingHook.FishHookState.FLYING) {
                if (this.hookedIn != null) {
                    this.setDeltaMovement(Vec3.ZERO);
                    (new FishHookStateChangeEvent((FishHook)this.getBukkitEntity(), FishHook.HookState.HOOKED_ENTITY)).callEvent();
                    this.currentState = FishingHook.FishHookState.HOOKED_IN_ENTITY;
                    return;
                }

                if (isInWater) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.3, 0.2, 0.3));
                    (new FishHookStateChangeEvent((FishHook)this.getBukkitEntity(), FishHook.HookState.BOBBING)).callEvent();
                    this.currentState = FishingHook.FishHookState.BOBBING;
                    return;
                }

                this.checkCollision();
            } else {
                if (this.currentState == FishingHook.FishHookState.HOOKED_IN_ENTITY) {
                    if (this.hookedIn != null) {
                        if (!this.hookedIn.isRemoved() && this.hookedIn.canInteractWithLevel() && this.hookedIn.level().dimension() == this.level().dimension()) {
                            this.setPos(this.hookedIn.getX(), this.hookedIn.getY(0.8), this.hookedIn.getZ());
                        } else {
                            this.setHookedEntity((Entity)null);
                            (new FishHookStateChangeEvent((FishHook)this.getBukkitEntity(), FishHook.HookState.UNHOOKED)).callEvent();
                            this.currentState = FishingHook.FishHookState.FLYING;
                        }
                    }

                    return;
                }

                if (this.currentState == FishingHook.FishHookState.BOBBING) {
                    Vec3 movement = this.getDeltaMovement();
                    double force = this.getY() + movement.y - (double)(blockPos.getY()) - (double)liquidHeight;
                    if (Math.abs(force) < 0.01) {
                        force += Math.signum(force) * 0.1;
                    }

                    this.setDeltaMovement(movement.x * 0.9, movement.y - force * (double)this.random.nextFloat() * 0.2, movement.z * 0.9);

                    if (isInWater) {
                        this.outOfWaterTime = Math.max(0, this.outOfWaterTime - 1);
                        if (this.biting) {
                            this.setDeltaMovement(this.getDeltaMovement().add((double)0.0F, -0.1 * (double)this.syncronizedRandom.nextFloat() * (double)this.syncronizedRandom.nextFloat(), (double)0.0F));
                        }

                        if (!this.level().isClientSide()) {
                            this.catchingFish(blockPos);
                        }
                    } else {
                        this.outOfWaterTime = Math.min(10, this.outOfWaterTime + 1);
                    }
                }
            }

            if (!fluidState.is(FluidTags.LAVA) && !this.onGround() && this.hookedIn == null) {
                this.setDeltaMovement(this.getDeltaMovement().add((double)0.0F, -0.03, (double)0.0F));
            }

            this.move(MoverType.SELF, this.getDeltaMovement());
            this.applyEffectsFromBlocks();
            this.updateRotation();
            if (this.currentState == FishingHook.FishHookState.FLYING && (this.onGround() || this.horizontalCollision)) {
                this.setDeltaMovement(Vec3.ZERO);
            }

            double inertia = 0.92;
            this.setDeltaMovement(this.getDeltaMovement().scale(inertia));
            this.reapplyPosition();
        }

    }

    private void catchingFish(BlockPos blockPos) {
        ServerLevel serverLevel = (ServerLevel)this.level();
        int fishingSpeed = 1;

        if (this.nibble > 0) {
            --this.nibble;
            if (this.nibble <= 0) {
                this.timeUntilLured = 0;
                this.timeUntilHooked = 0;
                this.getEntityData().set(getDataBiting(), false);
                PlayerFishEvent playerFishEvent = new PlayerFishEvent((org.bukkit.entity.Player)this.getPlayerOwner().getBukkitEntity(), (org.bukkit.entity.Entity)null, (FishHook)this.getBukkitEntity(), PlayerFishEvent.State.FAILED_ATTEMPT);
                playerFishEvent.callEvent();
            }
        } else if (this.timeUntilHooked > 0) {
            this.timeUntilHooked -= fishingSpeed;
            if (this.timeUntilHooked > 0) {
                this.fishAngle += (float)this.random.triangle((double)0.0F, 9.188);
                float angle = this.fishAngle * ((float)Math.PI / 180F);
                float angleSin = Mth.sin((double)angle);
                float angleCos = Mth.cos((double)angle);
                double fishX = this.getX() + (double)(angleSin * (float)this.timeUntilHooked * 0.1F);
                double fishY = (double)((float)Mth.floor(this.getY()) + 1.0F);
                double fishZ = this.getZ() + (double)(angleCos * (float)this.timeUntilHooked * 0.1F);
                BlockState splashBlockState = serverLevel.getBlockState(BlockPos.containing(fishX, fishY - (double)1.0F, fishZ));
                if (splashBlockState.is(Blocks.LAVA)) {
                    if (this.random.nextFloat() < 0.15F) {
                        serverLevel.sendParticles(ParticleTypes.LAVA, fishX, fishY - (double)0.1F, fishZ, 1, (double)angleSin, 0.1, (double)angleCos, (double)0.0F);
                    }

                    float particleXMovement = angleSin * 0.04F;
                    float particleZMovement = angleCos * 0.04F;
                    serverLevel.sendParticles(ParticleTypes.FLAME, fishX, fishY, fishZ, 0, (double)particleZMovement, 0.01, (double)(-particleXMovement), (double)1.0F);
                    serverLevel.sendParticles(ParticleTypes.FLAME, fishX, fishY, fishZ, 0, (double)(-particleZMovement), 0.01, (double)particleXMovement, (double)1.0F);
                }
            } else {
                PlayerFishEvent playerFishEvent = new PlayerFishEvent((org.bukkit.entity.Player)this.getPlayerOwner().getBukkitEntity(), (org.bukkit.entity.Entity)null, (FishHook)this.getBukkitEntity(), PlayerFishEvent.State.BITE);
                if (!playerFishEvent.callEvent()) {
                    return;
                }

                this.playSound(SoundEvents.FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                this.playSound(SoundEvents.LAVA_EXTINGUISH, 0.25F, 0.8f + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                double y = this.getY() + (double)0.5F;
                serverLevel.sendParticles(ParticleTypes.LAVA, this.getX(), y, this.getZ(), (int)(1.0F + this.getBbWidth() * 20.0F), (double)this.getBbWidth(), (double)0.0F, (double)this.getBbWidth(), (double)0.2F);
                serverLevel.sendParticles(ParticleTypes.FLAME, this.getX(), y, this.getZ(), (int)(1.0F + this.getBbWidth() * 20.0F), (double)this.getBbWidth(), (double)0.0F, (double)this.getBbWidth(), (double)0.2F);
                this.nibble = Mth.nextInt(this.random, 20, 40);
                this.getEntityData().set(getDataBiting(), true);
            }
        } else if (this.timeUntilLured > 0) {
            this.timeUntilLured -= fishingSpeed;
            float teaseChance = 0.15F;
            if (this.timeUntilLured < 20) {
                teaseChance += (float)(20 - this.timeUntilLured) * 0.05F;
            } else if (this.timeUntilLured < 40) {
                teaseChance += (float)(40 - this.timeUntilLured) * 0.02F;
            } else if (this.timeUntilLured < 60) {
                teaseChance += (float)(60 - this.timeUntilLured) * 0.01F;
            }

            if (this.random.nextFloat() < teaseChance) {
                float angle = Mth.nextFloat(this.random, 0.0F, 360.0F) * ((float)Math.PI / 180F);
                float dist = Mth.nextFloat(this.random, 25.0F, 60.0F);
                double fishX = this.getX() + (double)(Mth.sin((double)angle) * dist) * 0.1;
                double fishY = (double)((float)Mth.floor(this.getY()) + 1.0F);
                double fishZ = this.getZ() + (double)(Mth.cos((double)angle) * dist) * 0.1;
                BlockState splashBlockState = serverLevel.getBlockState(BlockPos.containing(fishX, fishY - (double)1.0F, fishZ));
                if (splashBlockState.is(Blocks.LAVA)) {
                    serverLevel.sendParticles(ParticleTypes.LAVA, fishX, fishY, fishZ, 2 + this.random.nextInt(2), (double)0.1F, (double)0.0F, (double)0.1F, (double)0.0F);
                }
            }

            if (this.timeUntilLured <= 0) {
                this.fishAngle = Mth.nextFloat(this.random, this.minLureAngle, this.maxLureAngle);
                this.timeUntilHooked = Mth.nextInt(this.random, this.minLureTime, this.maxLureTime);
                if (this.getPlayerOwner() != null) {
                    PlayerFishEvent playerFishEvent = new PlayerFishEvent((org.bukkit.entity.Player)this.getPlayerOwner().getBukkitEntity(), (org.bukkit.entity.Entity)null, (FishHook)this.getBukkitEntity(), PlayerFishEvent.State.LURED);
                    if (!playerFishEvent.callEvent()) {
                        this.timeUntilHooked = 0;
                        return;
                    }
                }
            }
        } else {
            this.resetTimeUntilLured();
        }

    }

    public int retrieve(ItemStack rod, InteractionHand hand) {
        Player owner = this.getPlayerOwner();
        if (!this.level().isClientSide() && owner != null && !this.shouldStopFishing(owner)) {
            int dmg = 0;
            if (this.hookedIn != null) {
                PlayerFishEvent playerFishEvent = new PlayerFishEvent((org.bukkit.entity.Player)owner.getBukkitEntity(), this.hookedIn.getBukkitEntity(), (FishHook)this.getBukkitEntity(), CraftEquipmentSlot.getHand(hand), PlayerFishEvent.State.CAUGHT_ENTITY);
                if (!playerFishEvent.callEvent()) {
                    return 0;
                }

                if (this.hookedIn != null) {
                    this.pullEntity(this.hookedIn);
                    CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer)owner, rod, this, Collections.emptyList());
                    this.level().broadcastEntityEvent(this, (byte)31);
                    dmg = this.hookedIn instanceof ItemEntity ? 3 : 5;
                }
            } else if (this.nibble > 0) {
                LootParams params = (new LootParams.Builder((ServerLevel)this.level())).withParameter(LootContextParams.ORIGIN, this.position()).withParameter(LootContextParams.TOOL, rod).withParameter(LootContextParams.THIS_ENTITY, this).withLuck(owner.getLuck()).create(LootContextParamSets.FISHING);
                LootTable lootTable = this.level().getServer().reloadableRegistries().getLootTable(BuiltInLootTables.FISHING);
                List<ItemStack> items = lootTable.getRandomItems(params);
                CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer)owner, rod, this, items);

                for(ItemStack itemStack : items) {
                    ItemEntity entity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), itemStack);
                    PlayerFishEvent playerFishEvent = new PlayerFishEvent((org.bukkit.entity.Player)owner.getBukkitEntity(), entity.getBukkitEntity(), (FishHook)this.getBukkitEntity(), CraftEquipmentSlot.getHand(hand), PlayerFishEvent.State.CAUGHT_FISH);
                    playerFishEvent.setExpToDrop(this.random.nextInt(6) + 1);
                    if (!playerFishEvent.callEvent()) {
                        return 0;
                    }

                    double xa = owner.getX() - this.getX();
                    double ya = owner.getY() - this.getY();
                    double za = owner.getZ() - this.getZ();
                    double speed = 0.1;
                    entity.setDeltaMovement(xa * 0.1, ya * 0.1 + Math.sqrt(Math.sqrt(xa * xa + ya * ya + za * za)) * 0.08, za * 0.1);
                    this.level().addFreshEntity(entity);
                    if (playerFishEvent.getExpToDrop() > 0) {
                        owner.level().addFreshEntity(new ExperienceOrb(owner.level(), new Vec3(owner.getX(), owner.getY() + (double)0.5F, owner.getZ() + (double)0.5F), Vec3.ZERO, playerFishEvent.getExpToDrop(), org.bukkit.entity.ExperienceOrb.SpawnReason.FISHING, this.getPlayerOwner(), this));
                    }

                    if (itemStack.is(ItemTags.FISHES)) {
                        owner.awardStat(Stats.FISH_CAUGHT, 1);
                    }
                }

                dmg = 1;
            }

            if (this.onGround()) {
                PlayerFishEvent playerFishEvent = new PlayerFishEvent((org.bukkit.entity.Player)owner.getBukkitEntity(), (org.bukkit.entity.Entity)null, (FishHook)this.getBukkitEntity(), CraftEquipmentSlot.getHand(hand), PlayerFishEvent.State.IN_GROUND);
                if (!playerFishEvent.callEvent()) {
                    return 0;
                }

                dmg = 2;
            }

            if (dmg == 0) {
                PlayerFishEvent playerFishEvent = new PlayerFishEvent((org.bukkit.entity.Player)owner.getBukkitEntity(), (org.bukkit.entity.Entity)null, (FishHook)this.getBukkitEntity(), CraftEquipmentSlot.getHand(hand), PlayerFishEvent.State.REEL_IN);
                if (!playerFishEvent.callEvent()) {
                    return 0;
                }
            }

            this.discard(EntityRemoveEvent.Cause.DESPAWN);
            return dmg;
        } else {
            return 0;
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

        boolean canLavaFish = false;
        if (flag) {
            org.bukkit.inventory.ItemStack fishingRod = itemstack.asBukkitCopy();
            ItemType rodType = ItemDataUtil.getItemType(fishingRod);
            if (rodType != null && rodType.canFishInLava()) canLavaFish = true;
        }
        if (flag1) {
            org.bukkit.inventory.ItemStack fishingRod = itemstack1.asBukkitCopy();
            ItemType rodType = ItemDataUtil.getItemType(fishingRod);
            if (rodType != null && rodType.canFishInLava()) canLavaFish = true;
        }
        if (canLavaFish && !entityhuman.isRemoved() && entityhuman.isAlive() && this.distanceToSqr(entityhuman) <= 1024.0) {
            return false;
        } else {
            this.discard(EntityRemoveEvent.Cause.DESPAWN);
            return true;
        }
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