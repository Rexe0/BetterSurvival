package me.rexe0.bettersurvival.mobs;

import me.rexe0.bettersurvival.BetterSurvival;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ServerLevelData;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.entity.CreatureSpawnEvent;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Optional;

public class WanderingTraderSpawner {
    private final RandomSource random = RandomSource.create();
    private final ServerLevelData serverLevelData;

    public WanderingTraderSpawner(ServerLevelData iworlddataserver) {
        this.serverLevelData = iworlddataserver;

    }

    public boolean spawn(ServerLevel worldserver) {
        ServerPlayer entityplayer = worldserver.getRandomPlayer();
        if (entityplayer == null) {
            return true;
        } else {
            BlockPos blockposition = entityplayer.blockPosition();
            PoiManager villageplace = worldserver.getPoiManager();
            Optional<BlockPos> optional = villageplace.find((holder) -> {
                return holder.is(PoiTypes.MEETING);
            }, (blockposition1x) -> {
                return true;
            }, blockposition, 48, PoiManager.Occupancy.ANY);
            BlockPos blockposition1 = (BlockPos)optional.orElse(blockposition);
            BlockPos blockposition2 = this.findSpawnPositionNear(worldserver, blockposition1, 48);
            if (blockposition2 != null && this.hasEnoughSpace(worldserver, blockposition2)) {
                if (worldserver.getBiome(blockposition2).is(BiomeTags.WITHOUT_WANDERING_TRADER_SPAWNS)) {
                    return false;
                }

                WanderingTrader entityvillagertrader = (WanderingTrader)EntityType.WANDERING_TRADER.spawn(worldserver, blockposition2, EntitySpawnReason.EVENT, CreatureSpawnEvent.SpawnReason.NATURAL);
                if (entityvillagertrader != null) {
                    for(int i = 0; i < 2; ++i) {
                        this.tryToSpawnLlamaFor(worldserver, entityvillagertrader, 4);
                    }

                    this.serverLevelData.setWanderingTraderId(entityvillagertrader.getUUID());
                    entityvillagertrader.setWanderTarget(blockposition1);
                    entityvillagertrader.setHomeTo(blockposition1, 16);
                    return true;
                }
            }

            return false;
        }
    }

    private void tryToSpawnLlamaFor(ServerLevel worldserver, WanderingTrader entityvillagertrader, int i) {
        BlockPos blockposition = this.findSpawnPositionNear(worldserver, entityvillagertrader.blockPosition(), i);
        if (blockposition != null) {
            TraderLlama entityllamatrader = (TraderLlama)EntityType.TRADER_LLAMA.spawn(worldserver, blockposition, EntitySpawnReason.EVENT, CreatureSpawnEvent.SpawnReason.NATURAL);
            if (entityllamatrader != null) {
                entityllamatrader.setLeashedTo(entityvillagertrader, true);
            }
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
}
