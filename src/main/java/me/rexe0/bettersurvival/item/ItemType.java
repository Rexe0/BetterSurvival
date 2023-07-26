package me.rexe0.bettersurvival.item;

public enum ItemType {
    STOPWATCH(new Stopwatch()),
    METAL_DETECTOR(new MetalDetector()),
    DRILL_BLOCK(new DrillBlock()),
    STRIDER_HELMET(new StriderHelmet());

    private Item item;
    ItemType(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
