package me.rexe0.bettersurvival.weather;

import org.bukkit.ChatColor;

import java.awt.*;

public enum Time {
    ANY(ChatColor.GREEN+"Any"),
    DAWN(net.md_5.bungee.api.ChatColor.of(new Color(255, 197, 38))+"Dawn"),
    DAY(net.md_5.bungee.api.ChatColor.of(new Color(255, 242, 0))+"Day"),
    DUSK(net.md_5.bungee.api.ChatColor.of(new Color(235, 102, 0))+"Dusk"),
    NIGHT(net.md_5.bungee.api.ChatColor.of(new Color(0, 46, 196))+"Night");

    private String name;

    Time(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isValid(long ticks) {
        return switch (this) {
            case ANY -> true;
            case DAWN -> ticks >= 23000 || ticks < 2000;
            case DAY -> ticks >= 2000 && ticks < 12000;
            case DUSK -> ticks >= 11000 && ticks < 14000;
            case NIGHT -> ticks >= 14000 && ticks < 23000;
        };
    }
}
