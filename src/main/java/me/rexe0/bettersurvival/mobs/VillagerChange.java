package me.rexe0.bettersurvival.mobs;

import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.item.WeatherRadio;
import me.rexe0.bettersurvival.item.fishing.JumboHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;

public class VillagerChange implements Listener {
    @EventHandler
    public void onAcquireTrade(VillagerAcquireTradeEvent e) {
        if (!(e.getEntity() instanceof org.bukkit.entity.Villager villager)) return;
        ((WeatherRadio)ItemType.WEATHER_RADIO.getItem()).onAcquireTrade(villager, e);
        ((JumboHook)ItemType.JUMBO_HOOK.getItem()).onAcquireTrade(villager, e);

    }
}
