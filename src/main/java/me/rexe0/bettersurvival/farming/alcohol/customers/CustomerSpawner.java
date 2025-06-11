package me.rexe0.bettersurvival.farming.alcohol.customers;

import me.rexe0.bettersurvival.BetterSurvival;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.entity.CreatureSpawnEvent;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Iterator;

public class CustomerSpawner {
    private final RandomSource random = RandomSource.create();

    public void spawn(ServerLevel worldserver) {
        for (ServerPlayer entityplayer : worldserver.getPlayers(LivingEntity::isAlive)) {
            if (random.nextInt(3) != 0) continue;
            BlockPos blockPosition = entityplayer.blockPosition();

            BlockPos spawnPosition = this.findSpawnPositionNear(worldserver, blockPosition, 48);
            if (spawnPosition == null || !this.hasEnoughSpace(worldserver, spawnPosition))
                continue;
            Villager nitwitVillager = (Villager) EntityType.VILLAGER.spawn(worldserver, spawnPosition, EntitySpawnReason.EVENT, CreatureSpawnEvent.SpawnReason.NATURAL);
            if (nitwitVillager == null) continue;
            nitwitVillager.goalSelector.addGoal(2, new WanderToPositionGoal(nitwitVillager, 2.0, 0.35, blockPosition));
            nitwitVillager.restrictTo(blockPosition, 16);
            nitwitVillager.addTag("isTravellingCustomer");
        }
    }

    @Nullable
    private BlockPos findSpawnPositionNear(LevelReader iworldreader, BlockPos blockposition, int i) {
        BlockPos blockposition1 = null;
        SpawnPlacementType spawnplacementtype = SpawnPlacements.getPlacementType(EntityType.WANDERING_TRADER);

        for(int j = 0; j < 10; ++j) {
            int k = blockposition.getX() + this.random.nextInt(i * 2) - i;
            int l = blockposition.getZ() + this.random.nextInt(i * 2) - i;
            int i1 = iworldreader.getHeight(Heightmap.Types.WORLD_SURFACE, k, l);

            Block block = BetterSurvival.getInstance().getDefaultWorld().getBlockAt(k, i1-1, l);
            if (block.getType() == Material.SNOW) {
                block.setType(Material.AIR);
                i1--;
            }

            BlockPos blockposition2 = new BlockPos(k, i1, l);
            if (spawnplacementtype.isSpawnPositionOk(iworldreader, blockposition2, EntityType.WANDERING_TRADER)) {
                blockposition1 = blockposition2;
                break;
            }
        }

        return blockposition1;
    }

    private boolean hasEnoughSpace(BlockGetter iblockaccess, BlockPos blockposition) {
        Iterator iterator = BlockPos.betweenClosed(blockposition, blockposition.offset(1, 2, 1)).iterator();

        while(iterator.hasNext()) {
            BlockPos blockposition1 = (BlockPos)iterator.next();
            if (!iblockaccess.getBlockState(blockposition1).getCollisionShape(iblockaccess, blockposition1).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private class WanderToPositionGoal extends Goal {
        private final Villager villager;
        private final double stopDistance;
        private final double speedModifier;
        private BlockPos wanderTarget;

        WanderToPositionGoal(final Villager villager, final double stopDistance, final double speedModifier, final BlockPos wanderTarget) {
            this.villager = villager;
            this.stopDistance = stopDistance;
            this.speedModifier = speedModifier;
            this.wanderTarget = wanderTarget;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public void stop() {
            wanderTarget = null;
            villager.getNavigation().stop();
        }

        public boolean canUse() {
            return wanderTarget != null && this.isTooFarAway(wanderTarget, this.stopDistance);
        }

        public void tick() {
            if (wanderTarget != null && villager.getNavigation().isDone()) {
                if (this.isTooFarAway(wanderTarget, 10.0)) {
                    Vec3 vec3d = (new Vec3((double)wanderTarget.getX() - villager.getX(), (double)wanderTarget.getY() - villager.getY(), (double)wanderTarget.getZ() - villager.getZ())).normalize();
                    Vec3 vec3d1 = vec3d.scale(10.0).add(villager.getX(), villager.getY(), villager.getZ());
                    villager.getNavigation().moveTo(vec3d1.x, vec3d1.y, vec3d1.z, this.speedModifier);
                } else {
                    villager.getNavigation().moveTo((double)wanderTarget.getX(), (double)wanderTarget.getY(), (double)wanderTarget.getZ(), this.speedModifier);
                }
            }

        }

        private boolean isTooFarAway(BlockPos blockposition, double d0) {
            return !blockposition.closerToCenterThan(villager.position(), d0);
        }
    }
}
