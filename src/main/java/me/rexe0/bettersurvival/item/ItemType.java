package me.rexe0.bettersurvival.item;

public enum ItemType {
    STOPWATCH(new Stopwatch()),
    METAL_DETECTOR(new MetalDetector()),
    DRILL_BLOCK(new DrillBlock()),
    STRIDER_HELMET(new StriderHelmet()),
    SADDLE_N_HORSESHOE(new SaddleNHorseshoe()),
    WEATHER_RADIO(new WeatherRadio());


    private Item item;
    ItemType(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
