package me.rexe0.bettersurvival.weather;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;

public enum Holiday {
    // Holidays are special days that are spread across the year that feature unique events

    // Surfacing glow squids will drop special coloured ink sacs that can be used in an anvil to colour the name of an item
    WINTER_SOLSTICE(ChatColor.of(new Color(148, 198, 255)), "Winter Solstice", 104, SeasonListener.Weather.SNOW, "The night will be darker and longer than usual but make sure to check out the spectacle in the ocean."),
    SUMMER_SOLSTICE(ChatColor.of(new Color(247, 157, 0)), "Summer Solstice", 44, SeasonListener.Weather.CLEAR, "The day will be longer than usual.");

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
