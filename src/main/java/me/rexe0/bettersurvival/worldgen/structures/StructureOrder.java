package me.rexe0.bettersurvival.worldgen.structures;

import org.bukkit.Location;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.structure.Structure;
import org.bukkit.util.BlockTransformer;
import org.bukkit.util.BlockVector;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.EntityTransformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class StructureOrder {
    private final Structure structure;
    private final Location location;
    private final boolean includeEntities;
    private final StructureRotation rotation;
    private final Mirror mirror;
    private final int palette;
    private final float integrity;
    private final Collection<BlockTransformer> blockTransformers;
    private final Collection<EntityTransformer> entityTransformers;

    private boolean isGrounded;

    public StructureOrder(Structure structure, Location location, boolean includeEntities, StructureRotation rotation, Mirror mirror, int palette, float integrity) {
        this.structure = structure;
        this.location = location;
        location.add((mirror == Mirror.FRONT_BACK ? 1 : -1)* structure.getSize().getX()/2,
                0,
                (mirror == Mirror.LEFT_RIGHT ? 1 : -1)* structure.getSize().getZ()/2);
        this.includeEntities = includeEntities;
        this.rotation = rotation;
        this.mirror = mirror;
        this.palette = palette;
        this.integrity = integrity;
        this.blockTransformers = new ArrayList<>();
        this.entityTransformers = new ArrayList<>();
    }

    public Structure getStructure() {
        return structure;
    }

    public Location getLocation() {
        return location;
    }

    public void setGrounded(boolean grounded) {
        isGrounded = grounded;
    }

    public void addBlockTransformer(BlockTransformer blockTransformer) {
        this.blockTransformers.add(blockTransformer);
    }
    public void addEntityTransformer(EntityTransformer entityTransformer) {
        this.entityTransformers.add(entityTransformer);
    }

    public boolean overlaps(StructureOrder other) {
        if (other == null) return false;
        if (!location.getWorld().equals(other.getLocation().getWorld())) return false;
        Location otherLoc = other.getLocation();
        Structure otherStructure = other.getStructure();

        BoundingBox thisBB = new BoundingBox(
                location.getX(), location.getY(), location.getZ(),
                location.getX() + structure.getSize().getX(),
                location.getY() + structure.getSize().getY(),
                location.getZ() + structure.getSize().getZ()
        );
        BoundingBox otherBB = new BoundingBox(
                otherLoc.getX(), otherLoc.getY(), otherLoc.getZ(),
                otherLoc.getX() + otherStructure.getSize().getX(),
                otherLoc.getY() + otherStructure.getSize().getY(),
                otherLoc.getZ() + otherStructure.getSize().getZ()
        );
        return thisBB.overlaps(otherBB);
    }

    public void place(Random random) {
        if (isGrounded) {
            BlockVector size = structure.getSize();
            for (int x = 0; x < size.getX(); x++)
                for (int z = 0; z < size.getZ(); z++) {
                    int dx = location.getBlockX() + (mirror == Mirror.FRONT_BACK ? -x : x);
                    int dz = location.getBlockZ() + (mirror == Mirror.LEFT_RIGHT ? -z : z);
                    if (!location.getWorld().getBlockAt(dx, location.getBlockY()-1, dz).getType().isSolid()) {
                        return;
                    }
                }
        }
        structure.place(location, includeEntities, rotation, mirror, palette, integrity, random, blockTransformers, entityTransformers);
    }
}
