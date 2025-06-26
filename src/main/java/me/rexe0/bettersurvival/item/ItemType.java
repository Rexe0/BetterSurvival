package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.farming.alcohol.BarrelType;
import me.rexe0.bettersurvival.farming.alcohol.SpiritType;
import me.rexe0.bettersurvival.farming.alcohol.WineType;
import me.rexe0.bettersurvival.fishing.BiomeGroup;
import me.rexe0.bettersurvival.item.arrows.AmethystArrow;
import me.rexe0.bettersurvival.item.arrows.ExplosiveArrow;
import me.rexe0.bettersurvival.item.arrows.SonicArrow;
import me.rexe0.bettersurvival.item.arrows.ToxicArrow;
import me.rexe0.bettersurvival.item.drugs.*;
import me.rexe0.bettersurvival.item.farming.Ambrosia;
import me.rexe0.bettersurvival.item.farming.FarmerBoots;
import me.rexe0.bettersurvival.item.farming.Fertilizer;
import me.rexe0.bettersurvival.item.fishing.*;
import me.rexe0.bettersurvival.item.golf.*;
import org.bukkit.ChatColor;

public enum ItemType {
    STOPWATCH(new Stopwatch()),
    METAL_DETECTOR(new MetalDetector()),
    DRILL_BLOCK(new DrillBlock()),
    STRIDER_HELMET(new StriderHelmet()),
    SADDLE_N_HORSESHOE(new SaddleNHorseshoe()),
    DRAGON_SCALE(new DragonScale()),
    FARMER_BOOTS(new FarmerBoots()),
    AMBROSIA(new Ambrosia()),
    WEATHER_RADIO(new WeatherRadio()),
    WEATHER_BEACON(new WeatherBeacon()),
    FERTILIZER(new Fertilizer(1)),
    COLORED_INK_SAC(new ColoredInkSac(ChatColor.WHITE)),
    AMETHYST_ARROW(new AmethystArrow()),
    EXPLOSIVE_ARROW(new ExplosiveArrow()),
    TOXIC_ARROW(new ToxicArrow()),
    SONIC_ARROW(new SonicArrow()),
    UPGRADE_BOOK(new UpgradeBook(UpgradeBook.Upgrade.BREWING)),
    WITHER_RING(new WitherRing()),

    CANNABIS(new Cannabis(0)),
    SMOKE_PIPE(new SmokePipe()),
    COCA_LEAVES(new CocaLeaves(0)),
    COCAINE(new Cocaine(0)),
    BLOCK_OF_COCAINE(new BlockOfCocaine(0)),
    YEAST(new Yeast()),
    REINFORCED_BARREL(new ReinforcedBarrel(BarrelType.OAK)),
    WINE(new Wine(0, WineType.SWEET_BERRY, 0)),
    SPIRIT(new Spirit(0, SpiritType.SWEET_BERRY, 0, null, null, null, false)),
    BOOK_OF_BREWERY(new BookOfBrewery()),

    GOLF_BALL(new GolfBall()),
    GOLF_TEE(new GolfTee()),
    GOLF_CUP(new GolfCup()),
    GOLF_HORN(new GolfHorn()),
    DRIVER(new Driver()),
    IRON(new Iron()),
    WEDGE(new Wedge()),
    PUTTER(new Putter()),

    FISH_CODEX(new FishCodex()),
    PLATINUM_ORE(new PlatinumOre()),
    PLATINUM_INGOT(new PlatinumIngot()),
    RESONANT_INGOT(new ResonantIngot()),
    FISH_STEW(new FishStew(new BiomeGroup[3], new double[3])),
    BARBED_HOOK(new BarbedHook()),
    JUMBO_HOOK(new JumboHook()),
    VIBRANT_BOBBER(new VibrantBobber()),
    GOLD_BOBBER(new GoldBobber()),
    DULL_LURE(new DullLure()),
    SHINY_LURE(new ShinyLure()),
    LEAD_SINKER(new LeadSinker()),
    STEEL_SINKER(new SteelSinker()),
    BAIT(new Bait()),
    MAGNET(new Magnet()),
    PREMIUM_BAIT(new PremiumBait()),
    FISH(new Fish(Fish.FishType.BASS)),
    TREASURE_CHEST(new TreasureChest(null)),
    TREASURE_SAND(new TreasureSand(null)),
    COPPER_FISHING_ROD(new CopperFishingRod()),
    PLATINUM_FISHING_ROD(new PlatinumFishingRod()),
    RESONANT_FISHING_ROD(new ResonantFishingRod()),
    GLEAMING_PEARL(new GleamingPearl());


    private Item item;
    ItemType(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public boolean isBait() {
        return this == BAIT || this == MAGNET || this == PREMIUM_BAIT
                || this == DULL_LURE || this == SHINY_LURE;
    }

    public double getBaitMultiplier() {
        if (!isBait()) return 1;
        return switch (this) {
            case BAIT,MAGNET -> 0.5;
            case PREMIUM_BAIT -> 0.25;
            case DULL_LURE -> 0.8;
            case SHINY_LURE -> 0.6;
            default -> 1;
        };
    }

    public boolean isTackle() {
        return this == BARBED_HOOK || this == JUMBO_HOOK || this == VIBRANT_BOBBER
                || this == GOLD_BOBBER || this == DULL_LURE || this == SHINY_LURE
                || this == LEAD_SINKER || this == STEEL_SINKER;
    }

    public boolean canUseBait() {
        return this == COPPER_FISHING_ROD || this == PLATINUM_FISHING_ROD || this == RESONANT_FISHING_ROD;
    }
    public boolean canUseTackle() {
        return this == RESONANT_FISHING_ROD || this == PLATINUM_FISHING_ROD;
    }
    public boolean isArrow() {
        return this == AMETHYST_ARROW || this == EXPLOSIVE_ARROW || this == TOXIC_ARROW || this == SONIC_ARROW;
    }
}
