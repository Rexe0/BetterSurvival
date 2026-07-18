package me.rexe0.bettersurvival.advs.fishing;

import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.fren_gor.ultimateAdvancementAPI.util.AdvancementKey;
import com.fren_gor.ultimateAdvancementAPI.visibilities.VanillaVisibility;
import me.rexe0.bettersurvival.advs.AdvancementTabNamespaces;
import org.bukkit.Material;

public class First_catch extends BaseAdvancement implements VanillaVisibility {

  public static AdvancementKey KEY = new AdvancementKey(AdvancementTabNamespaces.fishing_NAMESPACE, "first_catch");


  public First_catch(Advancement parent, float x, float y) {
    super(KEY.getKey(), new AdvancementDisplay(Material.COD, "First Catch", AdvancementFrameType.TASK, true, true, x, y , "Catch your first fish using a fishing rod"), parent, 1);
  }
}