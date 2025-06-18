package me.rexe0.bettersurvival.item;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.awt.*;
import java.util.Random;

public class NoiseMap extends Item implements Listener {
    public NoiseMap() {
        super(Material.FILLED_MAP, ChatColor.GREEN+"Noise Map", "NOISE_MAP");
    }

    public static class CustomMapRenderer extends MapRenderer {
        public CustomMapRenderer() {
            super(true);
        }
        @Override
        public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
            mapView.setCenterX(player.getLocation().getBlockX());
            mapView.setCenterZ(player.getLocation().getBlockZ());
            Thread thread = new Thread(() -> {
                SimplexOctaveGenerator generator = new SimplexOctaveGenerator(new Random(mapView.getWorld().getSeed()), 2);
                generator.setScale(0.02);

                MapCursorCollection cursors = new MapCursorCollection();
                float yaw = player.getLocation().getYaw();
                if (yaw < 0) yaw += 360;
                cursors.addCursor(0, 0, getDirection(yaw));
                mapCanvas.setCursors(cursors);


                int cx = mapView.getCenterX();
                int cz = mapView.getCenterZ();

                for (int x = -64; x < 64; x++) {
                    for (int z = -64; z < 64; z++) {
                        double noise = generator.noise(cx+x, cz+z, 1, 64, true);
                        int colorValue = (int) ((noise + 1) * 127.5); // Normalize noise to 0-255
                        mapCanvas.setPixelColor(x+64, z+64, new Color(colorValue, colorValue, colorValue));
                    }
                }
            });
            thread.start();
        }

        private byte getDirection(float yaw) {
            if (yaw > 78.75 && yaw <= 101.25) return 4;
            if (yaw > 101.25 && yaw <= 123.75) return 5;
            if (yaw > 123.75 && yaw <= 146.25) return 6;
            if (yaw > 146.25 && yaw <= 168.75) return 7;
            if (yaw > 168.75 && yaw <= 191.25) return 8;
            if (yaw > 191.25 && yaw <= 213.75) return 9;
            if (yaw > 213.75 && yaw <= 236.25) return 10;
            if (yaw > 236.25 && yaw <= 258.75) return 11;
            if (yaw > 258.75 && yaw <= 281.25) return 12;
            if (yaw > 281.25 && yaw <= 303.75) return 13;
            if (yaw > 303.75 && yaw <= 326.25) return 14;
            if (yaw > 326.25 && yaw <= 348.75) return 15;
            if (yaw <= 11.25 || yaw > 348.75) return 0;
            if (yaw > 11.25  && yaw <= 33.75) return 1;
            if (yaw > 33.75  && yaw <= 56.25) return 2;
            return 3;
        }
    }
    public ItemStack getMap(World world) {
        MapView mapView = Bukkit.createMap(world);
        mapView.setScale(MapView.Scale.FARTHEST);


        mapView.setScale(MapView.Scale.NORMAL);
        mapView.getRenderers().clear();
        mapView.addRenderer(new CustomMapRenderer());

        ItemStack mapItem = new ItemStack(Material.FILLED_MAP, 1);
        MapMeta meta = (MapMeta) mapItem.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN+"Map");
        meta.setMapView(mapView);
        mapItem.setItemMeta(meta);
        return mapItem;
    }
    @EventHandler
    public void onMapGenerate(AsyncPlayerChatEvent e) {
        if (e.getPlayer().getName().equals("rexe0") && e.getMessage().equalsIgnoreCase("noisemap"))
            e.getPlayer().getInventory().addItem(getMap(e.getPlayer().getWorld()));
    }
}
