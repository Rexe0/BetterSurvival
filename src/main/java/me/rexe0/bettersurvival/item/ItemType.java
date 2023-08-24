package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.fishing.BiomeGroup;
import me.rexe0.bettersurvival.item.fishing.*;

public enum ItemType {
    STOPWATCH(new Stopwatch()),
    METAL_DETECTOR(new MetalDetector()),
    DRILL_BLOCK(new DrillBlock()),
    STRIDER_HELMET(new StriderHelmet()),
    SADDLE_N_HORSESHOE(new SaddleNHorseshoe()),
    WEATHER_RADIO(new WeatherRadio()),
    FISH_CODEX(new FishCodex()),
    PLATINUM_ORE(new PlatinumOre()),
    PLATINUM_INGOT(new PlatinumIngot()),
    RESONANT_INGOT(new ResonantIngot()),
    FISH_STEW(new FishStew(new BiomeGroup[3], new double[3])),
    BARBED_HOOK(new BarbedHook()),
    VIBRANT_BOBBER(new VibrantBobber()),
    GOLD_BOBBER(new GoldBobber()),
    BAIT(new Bait()),
    MAGNET(new Magnet()),
    FISH(new Fish(Fish.FishType.BASS)),
    TREASURE_CHEST(new TreasureChest()),
    TREASURE_SAND(new TreasureSand()),
    COPPER_FISHING_ROD(new CopperFishingRod()),
    PLATINUM_FISHING_ROD(new PlatinumFishingRod()),
    RESONANT_FISHING_ROD(new ResonantFishingRod());


    private Item item;
    ItemType(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public boolean isBait() {
        return this == BAIT || this == MAGNET;
    }
    public boolean isTackle() {
        return this == BARBED_HOOK || this == VIBRANT_BOBBER || this == GOLD_BOBBER;
    }

    public boolean canUseBait() {
        return this == COPPER_FISHING_ROD || this == PLATINUM_FISHING_ROD || this == RESONANT_FISHING_ROD;
    }
    public boolean canUseTackle() {
        return this == RESONANT_FISHING_ROD;
    }
}
