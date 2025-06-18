package me.rexe0.bettersurvival.worldgen.structures;

import me.rexe0.bettersurvival.BetterSurvival;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class StructureOrderManager {
    private static StructureOrderManager instance;

    public static StructureOrderManager getInstance() {
        if (instance == null)
            instance = new StructureOrderManager();
        return instance;
    }


    private final List<StructureOrder> currentOrders;
    private final List<StructureOrder> toAdd;
    private final Random random;

    public StructureOrderManager() {
        this.currentOrders = new LinkedList<>();
        this.toAdd = new LinkedList<>();
        this.random = new Random();
    }

    public void queueOrder(StructureOrder order) {
        for (StructureOrder existingOrder : new LinkedList<>(currentOrders))
            if (order.overlaps(existingOrder)) return;
        for (StructureOrder existingOrder : toAdd)
            if (order.overlaps(existingOrder)) return;
        toAdd.add(order);
    }

    public BukkitRunnable start() {
        BukkitRunnable runnable = new StructureManagerRunnable();
        runnable.runTaskTimerAsynchronously(BetterSurvival.getInstance(), 0, 5);
        return runnable;
    }

    public class StructureManagerRunnable extends BukkitRunnable {
        private boolean isRunning; // Flag to prevent multiple runs at the same time and avoid comodification exception
        @Override
        public void run() {
            if (isRunning) return;
            isRunning = true;

            currentOrders.addAll(toAdd);
            toAdd.clear();

            for (Iterator<StructureOrder> iterator = currentOrders.iterator(); iterator.hasNext();) {
                if (isCancelled()) return; // Exit early if server stops
                StructureOrder order = iterator.next();

                int x = order.getLocation().getChunk().getX();
                int z = order.getLocation().getChunk().getZ();

                boolean allGenerated = true;
                // Check if adjacent chunks are generated
                for (int i = 0; i < 9; i++) {
                    int offsetX = (i % 3) - 1; // -1, 0, 1
                    int offsetZ = (i / 3) - 1; // -1, 0, 1
                    if (offsetX == 0 && offsetZ == 0) continue; // Skip the center chunk

                    if (!order.getLocation().getWorld().getChunkAt(x+offsetX, z+offsetZ).isGenerated()) {
                        allGenerated = false;
                        break;
                    }
                }
                if (!allGenerated) continue;

                // Place the structure
                Bukkit.getScheduler().runTask(BetterSurvival.getInstance(), () -> order.place(random));
                iterator.remove();
            }

            isRunning = false;
        }
    }
}
