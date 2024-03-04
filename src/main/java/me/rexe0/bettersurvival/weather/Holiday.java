package me.rexe0.bettersurvival.weather;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;

public enum Holiday {
    // Holidays are special days that are spread across the year that feature unique events

    START_OF_SPRING(ChatColor.of(new Color(0, 255, 0)), "the start of Spring", 0, SeasonListener.Weather.CLEAR, "Crops will grow faster, animals will be happy to breed and the weather is almost always beautiful."),
    START_OF_SUMMER(ChatColor.of(new Color(255,78,80)), "the start of Summer", 30, SeasonListener.Weather.STORM, "Rain and Storms will be far more common with Storms being able to develop into strong Tempests."),
    START_OF_AUTUMN(ChatColor.of(new Color(255, 111, 0)), "the start of Autumn", 60, SeasonListener.Weather.CLEAR, "The cold weather will start to take effect on the crops and animals, with their growth being slowed."),
    START_OF_WINTER(ChatColor.of(new Color(0, 140, 255)), "the start of Winter", 90, SeasonListener.Weather.SNOW, "Snow will start to fall during Winter with a chance of powerful Blizzards that can cover the land in snow. " +
            "Crops and Animals will also grow the slowest at this time."),
    // Surfacing glow squids will drop special coloured ink sacs that can be used in an anvil to colour the name of an item
    WINTER_SOLSTICE(ChatColor.of(new Color(148, 198, 255)), "Winter Solstice", 104, SeasonListener.Weather.SNOW, "The night will be darker and longer than usual but make sure to check out the spectacle in the ocean."),
    SUMMER_SOLSTICE(ChatColor.of(new Color(247, 157, 0)), "Summer Solstice", 44, SeasonListener.Weather.CLEAR, "The day will be longer than usual."),
    BUMPER_CROP(ChatColor.of(new Color(182, 245, 22)), "Bumper Crop", 19, SeasonListener.Weather.CLEAR, "Crops will grow faster and produce an unusually high yield throughout the day.");


    private ChatColor color;
    private String name;
    private int day;
    private SeasonListener.Weather weather;

    private String description;

    Holiday(ChatColor color, String name, int day, SeasonListener.Weather weather, String description) {
        this.color = color;
        this.name = name;
        this.day = day;
        this.weather = weather;
        this.description = description;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public SeasonListener.Weather getWeather() {
        return weather;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDay(int day) {
        return day % 120 == this.day;
    }
}
