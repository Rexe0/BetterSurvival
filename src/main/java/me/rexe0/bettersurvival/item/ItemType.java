package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.fishing.BiomeGroup;
import me.rexe0.bettersurvival.item.fishing.*;
import org.bukkit.ChatColor;

public enum ItemType {
    STOPWATCH(new Stopwatch()),
    METAL_DETECTOR(new MetalDetector()),
    DRILL_BLOCK(new DrillBlock()),
    STRIDER_HELMET(new StriderHelmet()),
    SADDLE_N_HORSESHOE(new SaddleNHorseshoe()),
    DRAGON_SCALE(new DragonScale()),
    CORNUCOPIA(new Cornucopia()),
    WEATHER_RADIO(new WeatherRadio()),
    WEATHER_BEACON(new WeatherBeacon()),
    FERTILIZER(new Fertilizer(1)),
    COLORED_INK_SAC(new ColoredInkSac(ChatColor.WHITE)),
    AMETHYST_ARROW(new AmethystArrow()),
    EXPLOSIVE_ARROW(new ExplosiveArrow()),
    TOXIC_ARROW(new ToxicArrow()),
    SONIC_ARROW(new SonicArrow()),
    FISH_CODEX(new FishCodex()),
    PLATINUM_ORE(new PlatinumOre()),
    PLATINUM_INGOT(new PlatinumIngot()),
    RESONANT_INGOT(new ResonantIngot()),
    FISH_STEW(new FishStew(new BiomeGroup[3], new double[3])),
    BARBED_HOOK(new BarbedHook()),
    JUMBO_HOOK(new JumboHook()),
    VIBRANT_BOBBER(new VibrantBobber()),
    GOLD_BOBBER(new GoldBobber()),
    BAIT(new Bait()),
    MAGNET(new Magnet()),
    PREMIUM_BAIT(new PremiumBait()),
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
        return this == BAIT || this == MAGNET || this == PREMIUM_BAIT;
    }
    public boolean isTackle() {
        return this == BARBED_HOOK || this == JUMBO_HOOK || this == VIBRANT_BOBBER || this == GOLD_BOBBER;
    }

    public boolean canUseBait() {
        return this == COPPER_FISHING_ROD || this == PLATINUM_FISHING_ROD || this == RESONANT_FISHING_ROD;
    }
    public boolean canUseTackle() {
        return this == RESONANT_FISHING_ROD;
    }
    public boolean isArrow() {
        return this == AMETHYST_ARROW || this == EXPLOSIVE_ARROW || this == TOXIC_ARROW || this == SONIC_ARROW;
    }
}
