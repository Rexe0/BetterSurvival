package me.rexe0.bettersurvival.fishing;

import me.rexe0.bettersurvival.item.ItemType;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class NetherMob {
    private final EntityType type;
    private final int minAmount;
    private final int maxAmount;
    private int weight;
    private final boolean reelsUp;

    public NetherMob(EntityType type, int minAmount, int maxAmount, int weight, boolean reelsUp) {
        this.type = type;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.weight = weight;
        this.reelsUp = reelsUp;
    }
    public NetherMob(EntityType type, int minAmount, int maxAmount, int weight) {
        this(type, minAmount, maxAmount, weight, false);
    }

    public EntityType getType() {
        return type;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public int getWeight() {
        return weight;
    }

    public boolean isReelsUp() {
        return reelsUp;
    }

    private static List<NetherMob> getPossibleMobs(ItemType fishingRod) {
        List<NetherMob> mobs = new ArrayList<>();
        mobs.add(new NetherMob(EntityType.PIGLIN, 1, 2, 8));
        mobs.add(new NetherMob(EntityType.BLAZE, 1, 1, 6, true));
        mobs.add(new NetherMob(EntityType.WITHER_SKELETON, 1, 1, 6));
        mobs.add(new NetherMob(EntityType.MAGMA_CUBE, 1, 1, 6));

        int level = 0;

        if (fishingRod != null) level = switch (fishingRod) {
            case TUNGSTEN_FISHING_ROD -> 1;
            case NETHERITE_FISHING_ROD -> 2;
            default -> 0;
        };

        if (level >= 1) {
            mobs.add(new NetherMob(EntityType.GHAST, 1, 1, 4, true));
            mobs.add(new NetherMob(EntityType.PIGLIN_BRUTE, 1, 1, 4));
        }
        if (level >= 2) {
            mobs.add(new NetherMob(EntityType.BLAZE, 3, 3, 2, true));
            mobs.add(new NetherMob(EntityType.HOGLIN, 2, 2, 2));
            mobs.add(new NetherMob(EntityType.WITHER_SKELETON, 3, 3, 2));

            // Make the Wither much rarer
            for (NetherMob mob : mobs)
                mob.weight *= 2;
            mobs.add(new NetherMob(EntityType.WITHER, 1, 1, 1, true));
        }
        return mobs;
    }
    public static NetherMob getNetherMob(ItemType fishingRod) {
        List<NetherMob> mobs = getPossibleMobs(fishingRod);

        int totalWeight = 0;
        for (NetherMob mob : mobs)
            totalWeight += mob.getWeight();

        int idx = 0;
        for (double r = Math.random() * totalWeight; idx < mobs.size() - 1; ++idx) {
            r -= mobs.get(idx).getWeight();
            if (r <= 0.0) break;
        }

        return mobs.get(idx);
    }

}
