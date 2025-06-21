package me.rexe0.bettersurvival.golf;

import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;

public class GolfBallSpawner {
    public static void spawnGolfBall(Player player, BlockDisplay tee) {
        for (GolfBallEntity golfBall : GolfBallEntity.getGolfBalls().toArray(new GolfBallEntity[0]))
            if (golfBall.getOwner().equals(player)) golfBall.remove();

        GolfBallEntity golfBall = new GolfBallEntity(player, tee);
        golfBall.spawn();
    }


}
