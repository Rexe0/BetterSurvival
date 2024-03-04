package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.util.ItemDataUtil;
import me.rexe0.bettersurvival.weather.Holiday;
import me.rexe0.bettersurvival.weather.Season;
import me.rexe0.bettersurvival.weather.SeasonListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.awt.*;

public class WeatherRadio extends Item {
    public WeatherRadio() {
        super(Material.CLOCK, ChatColor.GREEN+"Weather Radio", "WEATHER_RADIO");
    }

    @Override
    public void onRightClick(Player player) {
        Season season = Season.getSeason();

        int day = Season.getDayOfSeason();
        int year = (int) (1 + Math.floor(SeasonListener.getDays()/120f));
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
            case TEMPEST -> net.md_5.bungee.api.ChatColor.of(new Color(26, 46, 255))+"A tempest will hit tomorrow. Expect thick fog and torrential rain with constant lightning storms.";
            case SNOW -> net.md_5.bungee.api.ChatColor.of(new Color(219, 245, 255))+"Its going to snow all day tomorrow.";
            case BLIZZARD -> net.md_5.bungee.api.ChatColor.of(new Color(92, 228, 255))+"A blizzard is approaching tomorrow. Stay inside or stay warm.";
            case WINDY -> net.md_5.bungee.api.ChatColor.of(season == Season.AUTUMN ? new Color(255, 94, 0)
                        : new Color(255, 189, 246))+"Light winds are expected tomorrow.";
        };

        for (Holiday holiday : Holiday.values()) {
            if (holiday.isDay(SeasonListener.getDays()+1)) {
                forecast += holiday.getColor()+" Typical weather for the "+holiday.getName()+". "+holiday.getDescription();
                break;
            }
        }

        player.sendMessage(ChatColor.AQUA+"[Weather Radio] "+ChatColor.WHITE+"It is currently the "+day+suffix+" of "+season.getName()+", Year "+year+". The weather forecast for tomorrow is: ");
        player.sendMessage(forecast);
    }

    public void onAcquireTrade(Villager villager, VillagerAcquireTradeEvent e) {
        if (villager.getProfession() != org.bukkit.entity.Villager.Profession.CARTOGRAPHER) return;

        // Replace banner trade once for cartographers
        if (!e.getRecipe().getResult().getType().toString().contains("BANNER")) return;
        for (MerchantRecipe recipe : villager.getRecipes())
            if (ItemDataUtil.isItem(recipe.getResult(), ItemType.WEATHER_RADIO.getItem().getID())) return;

        MerchantRecipe trade = new MerchantRecipe(ItemType.WEATHER_RADIO.getItem().getItem(), 0, 4, true, 15, 0);
        trade.addIngredient(new ItemStack(Material.EMERALD, 16));
        trade.addIngredient(new ItemStack(Material.GOLD_INGOT, 16));
        e.setRecipe(trade);
    }
}
