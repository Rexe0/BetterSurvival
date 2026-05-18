package me.rexe0.bettersurvival.item.smithing;

public enum SmithingType {
    SWORD("Sword"),
    SPEAR("Spear"),
    AXE("Axe"),
    PICKAXE("Pickaxe"),
    SHOVEL("Shovel"),
    HOE("Hoe"),
    HELMET("Helmet"),
    CHESTPLATE("Chestplate"),
    LEGGINGS("Leggings"),
    BOOTS("Boots"),
    HORSE_ARMOR("Horse Armor"),
    NAUTILUS_ARMOR("Nautilus Armor"),
    ;

    private final String name;

    SmithingType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
