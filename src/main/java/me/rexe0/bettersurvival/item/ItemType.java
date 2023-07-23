package me.rexe0.bettersurvival.item;

public enum ItemType {
    STOPWATCH(new Stopwatch()),
    METAL_DETECTOR(new MetalDetector()),
    DRILL_BLOCK(new DrillBlock());

    private Item item;
    ItemType(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
