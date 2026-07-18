package me.rexe0.bettersurvival.advs.fishing;

import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.fren_gor.ultimateAdvancementAPI.util.AdvancementKey;
import com.fren_gor.ultimateAdvancementAPI.visibilities.VanillaVisibility;
import me.rexe0.bettersurvival.advs.AdvancementTabNamespaces;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Gleaming_pearl extends BaseAdvancement implements VanillaVisibility {

    public static AdvancementKey KEY = new AdvancementKey(AdvancementTabNamespaces.fishing_NAMESPACE, "gleaming_pearl");


    public Gleaming_pearl(Advancement parent, float x, float y) {
        super(KEY.getKey(), new AdvancementDisplay(Material.PEARLESCENT_FROGLIGHT, "Gleaming Treasure", AdvancementFrameType.CHALLENGE, true, true, x, y, "Find a Gleaming Pearl from fishing"), parent, 1);
    }

    @Override
    public void giveReward(@NotNull Player player) {
        player.giveExp(250);
    }
}