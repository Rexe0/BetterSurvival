package me.rexe0.bettersurvival.advs.fishing;

import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.fren_gor.ultimateAdvancementAPI.util.AdvancementKey;
import com.fren_gor.ultimateAdvancementAPI.visibilities.VanillaVisibility;
import me.rexe0.bettersurvival.advs.AdvancementTabNamespaces;
import org.bukkit.Material;

public class Double_treasure extends BaseAdvancement implements VanillaVisibility {

  public static AdvancementKey KEY = new AdvancementKey(AdvancementTabNamespaces.fishing_NAMESPACE, "double_treasure");


  public Double_treasure(Advancement parent, float x, float y) {
    super(KEY.getKey(), new AdvancementDisplay(Material.ENDER_CHEST, "TREASURE!!!!", AdvancementFrameType.GOAL, true, true, x, y , "Pull up two pieces of treasure from one catch"), parent, 1);
  }
}