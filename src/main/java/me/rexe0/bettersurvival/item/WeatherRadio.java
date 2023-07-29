package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.weather.Season;
import me.rexe0.bettersurvival.weather.SeasonListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.awt.*;

public class WeatherRadio extends Item {
    public WeatherRadio() {
        super(Material.CLOCK, ChatColor.GREEN+"Weather Radio", "WEATHER_RADIO");
    }

    @Override
    public void onRightClick(Player player) {
        Season season = Season.getSeason();

        int day = Season.getDayOfSeason();
        String suffix = switch (day % 10) {
            default -> "th";
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
        };
        if (day/10 == 1) suffix = "th";

        String forecast = switch (SeasonListener.getWeatherForecast()) {
            case CLEAR -> net.md_5.bungee.api.ChatColor.of(new Color(255, 221, 54))+"It will be clear and sunny all day tomorrow.";
            case RAIN -> net.md_5.bungee.api.ChatColor.of(new Color(166, 212, 255))+"It's going to rain all day tomorrow.";
            case STORM -> net.md_5.bungee.api.ChatColor.of(new Color(99, 112, 255))+"A storm is brewing tomorrow. Expect heavy rain, thunder and lightning.";
            case SNOW -> net.md_5.bungee.api.ChatColor.of(new Color(219, 245, 255))+"Its going to snow all day tomorrow.";
            case BLIZZARD -> net.md_5.bungee.api.ChatColor.of(new Color(92, 228, 255))+"A blizzard is approaching tomorrow. Stay inside or stay warm.";
        };

        player.sendMessage(ChatColor.AQUA+"[Weather Radio] "+ChatColor.WHITE+"It is currently the "+day+suffix+" of "+season.getName()+". The weather forecast for tomorrow is: ");
        player.sendMessage(forecast);
    }
}
