package me.rexe0.bettersurvival.mobs;

import me.rexe0.bettersurvival.BetterSurvival;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PhantomChange implements Listener {
    private final Particle.DustTransition color = new Particle.DustTransition(Color.fromRGB(109, 0, 212), Color.fromRGB(27, 0, 181), 0.8f);

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (!(e.getEntity() instanceof Phantom phantom)) return;
        boolean nearbyValidPlayer = false;
        for (Player p : phantom.getWorld().getPlayers()) {
            if (p.getLocation().distanceSquared(phantom.getLocation()) > 10000) continue;
            if (!p.getScoreboardTags().contains("hasTravelledToEnd")) continue;
            nearbyValidPlayer = true;
            break;
        }

        if (!nearbyValidPlayer) return;

        phantom.setSize(2);
        phantom.addScoreboardTag("isEmpoweredPhantom");

        new BukkitRunnable() {
            @Override
            public void run() {
                if (phantom.isDead()) {
                    cancel();
                    return;
                }
                phantom.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, phantom.getEyeLocation(), 10, 0.3, 0.1, 0.3, 0, color);
            }
        }.runTaskTimer(BetterSurvival.getInstance(), 0, 1);
    }
    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Phantom)) return;
        if (!e.getEntity().getScoreboardTags().contains("isEmpoweredPhantom")) e.getDrops().clear();
    }

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        if (!player.getWorld().equals(BetterSurvival.getInstance().getDefaultEnd()) || !e.getFrom().equals(BetterSurvival.getInstance().getDefaultWorld())) return;
        if (player.getScoreboardTags().contains("hasTravelledToEnd")) return;
        player.addScoreboardTag("hasTravelledToEnd");
    }
}
