package me.rexe0.bettersurvival.constructs;

import java.util.List;

public enum ModificationType {
    HARNESS("Harness", Harness.class),
    ENGINE("Engine", Engine.class),
    LOAD("Load", Load.class),
    MISCELLANEOUS("Miscellaneous", Miscellaneous.class);

    private final String name;
    private final Class<? extends Modification> modificationClass;

    ModificationType(String name, Class<? extends Modification> modificationClass) {
        this.name = name;
        this.modificationClass = modificationClass;
    }
    public String getName() {
        return name;
    }

    public Class<? extends Modification> getModificationClass() {
        return modificationClass;
    }

    public Modification getModification(int id) {
        return getAllModifications().get(id);
    }
    public List<? extends Modification> getAllModifications() {
        return switch (this) {
            case HARNESS -> Harness.getAllHarnesses();
            case ENGINE -> Engine.getAllEngines();
            case LOAD -> Load.getAllLoads();
            case MISCELLANEOUS -> Miscellaneous.getAllMiscellaneous();
        };
    }

    public static ModificationType getFromName(String name) {
        for (ModificationType type : ModificationType.values())
            if (type.getName().equalsIgnoreCase(name))
                return type;

        return null;
    }
}
