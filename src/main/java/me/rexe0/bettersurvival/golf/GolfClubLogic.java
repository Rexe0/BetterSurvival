package me.rexe0.bettersurvival.golf;

import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.item.golf.GolfClub;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GolfClubLogic implements Listener {
    private static GolfClubLogic instance;
    public static GolfClubLogic getInstance() {
        if (instance == null)
            instance = new GolfClubLogic();
        return instance;
    }

    private final Map<Player, GolfClubMenu> golfMenu = new HashMap<>();

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        golfMenu.remove(e.getPlayer());

        GolfBallEntity golfBall = getGolfBall(e.getPlayer());
        if (golfBall != null) {
            golfBall.setItemsVisible(true);
            golfBall.remove();
        }
    }
    @EventHandler
    public void onLeftClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK) return;
        Player player = e.getPlayer();
        ItemStack item = player.getEquipment().getItemInMainHand();

        ItemType type = ItemDataUtil.getItemType(item);
        if (!isValid(player, type)) return;
        e.setCancelled(true);

        if (!golfMenu.containsKey(player)) return;

        double progress = golfMenu.get(player).getProgress();

        GolfClub club = (GolfClub) type.getItem();

        GolfBallEntity golfBall = getGolfBall(player);
        golfBall.hit(club, progress);
    }
    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = e.getPlayer();

        GolfBallEntity golfBall = getGolfBall(player);
        if (golfBall == null || golfBall.getSpeedSquared() == 0) return;
        golfBall.setCamera(true);
    }


    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack item = player.getEquipment().getItemInMainHand();
            ItemType type = ItemDataUtil.getItemType(item);

            if (!isValid(player, type)) {
                golfMenu.remove(player);
                continue;
            }

            showGolfMenu(player);
        }
    }


    private GolfBallEntity getGolfBall(Player player) {
        for (GolfBallEntity golfBall : GolfBallEntity.getGolfBalls())
            if (golfBall.getOwner().equals(player))
                return golfBall;
        return null;
    }
    private boolean isValid(Player player, ItemType type) {
        GolfBallEntity golfBall = getGolfBall(player);
        return type != null && type.getItem() instanceof GolfClub && player.isSneaking()
                && golfBall != null && golfBall.canBeHit();
    }

    private void showGolfMenu(Player player) {
        golfMenu.putIfAbsent(player, new GolfClubMenu());
        GolfClubMenu menu = golfMenu.get(player);
        menu.run();
        menu.showUI(player);
    }

    public static class GolfClubMenu {
        private double progress;
        private boolean goingUp;

        public GolfClubMenu() {
            reset();
        }
        public double getProgress() {
            return progress;
        }

        public void run() {
            if (goingUp) {
                progress += 0.04;
                if (progress >= 1.0) {
                    progress = 1;
                    goingUp = false;
                }
            } else {
                progress -= 0.04;
                if (progress <= 0.0) {
                    progress = 0;
                    goingUp = true;
                }
            }


        }
        public void reset() {
            this.progress = 0;
            this.goingUp = true;
        }

        public void showUI(Player player) {
            String UI = "";
            for (int i = 0; i < 41; i++) {
                if ((int) (progress * 40) == i)
                    UI += ChatColor.BLUE+"|";
                else
                    UI += ItemDataUtil.getFormattedColorString("|", i, 40);
            }
            player.sendTitle(UI, "", 0, 2, 10);
        }
    }
}
