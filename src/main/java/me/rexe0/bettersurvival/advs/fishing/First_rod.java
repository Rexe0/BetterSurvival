package me.rexe0.bettersurvival.advs.fishing;

import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.fren_gor.ultimateAdvancementAPI.util.AdvancementKey;
import com.fren_gor.ultimateAdvancementAPI.visibilities.VanillaVisibility;
import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.advs.AdvancementTabNamespaces;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;

public class First_rod extends RootAdvancement implements VanillaVisibility {

    public static AdvancementKey KEY = new AdvancementKey(AdvancementTabNamespaces.fishing_NAMESPACE, "copper_rod");


    public First_rod(float x, float y) {
        super(BetterSurvival.getInstance().fishing, KEY.getKey(), new AdvancementDisplay(Material.FISHING_ROD, "Fishy Beginnings", AdvancementFrameType.TASK, true, true, x, y, "Craft your first fishing rod"), "textures/block/spruce_planks.png", 1);
        registerEvent(CraftItemEvent.class, e -> {
            if (e.getRecipe().getResult().getType() == Material.FISHING_ROD) incrementProgression((Player) e.getWhoClicked());
        });
    }
}