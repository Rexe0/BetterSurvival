package me.rexe0.bettersurvival.weather;

public enum Season {
    SPRING("Spring"),
    SUMMER("Summer"),
    AUTUMN("Autumn"),
    WINTER("Winter");

    private String name;

    Season(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Season getSeason() {
        int day = (SeasonListener.getDays()/30) % 4;
        return switch (day) {
            default -> SPRING;
            case 1 -> SUMMER;
            case 2 -> AUTUMN;
            case 3 -> WINTER;
        };
    }

    public static int getDayOfSeason() {
        int daysPassed = SeasonListener.getDays();
        return 1+(daysPassed % 30);
    }
}
