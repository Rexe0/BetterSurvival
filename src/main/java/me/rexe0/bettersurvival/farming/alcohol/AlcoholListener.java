package me.rexe0.bettersurvival.farming.alcohol;

import me.rexe0.bettersurvival.util.EntityDataUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AlcoholListener implements Listener {
    public static void increaseAlcoholContent(Player player, double concentration) {
        double alcoholContent = getAlcoholContent(player);
        alcoholContent += concentration;

        int duration = 200+((int) alcoholContent);
        // Alcohol poisoning
        if (alcoholContent >= 300) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, duration, 0, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, duration-100, 0, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, duration-100, 4, true, false));
        } else if (alcoholContent >= 160) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, duration, 0, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, duration-100, 0, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration-100, alcoholContent >= 240 ? 3 : 0, true, false));
        } else if (alcoholContent >= 80)
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, duration, 0, true, false));


        setAlcoholContent(player, alcoholContent);
    }
    // Runs every minute to decrease alcohol levels
    public static void alcoholTick(Player player) {
        double alcoholContent = getAlcoholContent(player);
        if (alcoholContent <= 0) return;

        // Decrease alcohol content over time
        alcoholContent -= 5;
        if (alcoholContent < 0) {
            alcoholContent = 0;
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 400, 0, true, false));
        }
        setAlcoholContent(player, alcoholContent);
    }
    private static double getAlcoholContent(Player player) {
        return EntityDataUtil.getDoubleValue(player, "alcoholContent");
    }
    private static void setAlcoholContent(Player player, double concentration) {
        EntityDataUtil.setDoubleValue(player, "alcoholContent", concentration);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        setAlcoholContent(e.getEntity(), 0);
    }
    @EventHandler
    public void onExhaust(EntityExhaustionEvent e) {
        Player player = (Player) e.getEntity();
        if (getAlcoholContent(player) > 0)
            e.setExhaustion(e.getExhaustion()/2);
    }
}
