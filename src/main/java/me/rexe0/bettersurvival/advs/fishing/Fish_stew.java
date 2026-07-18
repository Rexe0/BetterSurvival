package me.rexe0.bettersurvival.advs.fishing;

import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.fren_gor.ultimateAdvancementAPI.util.AdvancementKey;
import com.fren_gor.ultimateAdvancementAPI.visibilities.VanillaVisibility;
import me.rexe0.bettersurvival.advs.AdvancementTabNamespaces;
import org.bukkit.Material;

public class Fish_stew extends BaseAdvancement implements VanillaVisibility {

  public static AdvancementKey KEY = new AdvancementKey(AdvancementTabNamespaces.fishing_NAMESPACE, "fish_stew");


  public Fish_stew(Advancement parent, float x, float y) {
    super(KEY.getKey(), new AdvancementDisplay(Material.MUSHROOM_STEW, "Fish Stew", AdvancementFrameType.TASK, true, true, x, y , "Make fish stew by putting three fish in a heated cauldron full of water"), parent, 1);
  }
}