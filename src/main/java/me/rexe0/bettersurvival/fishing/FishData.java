package me.rexe0.bettersurvival.fishing;

import me.rexe0.bettersurvival.item.fishing.Fish;

import java.util.Map;

public class FishData {
    private Map<Fish.FishType, Integer> map;

    public FishData(Map<Fish.FishType, Integer> map) {
        this.map = map;
    }

    public void addFish(Fish.FishType type) {
        map.putIfAbsent(type, 0);
        map.put(type, map.get(type)+1);
    }

    public int getAmountFished(Fish.FishType type) {
        map.putIfAbsent(type, 0);
        return map.get(type);
    }
    public Map<Fish.FishType, Integer> getFishes() {
        return map;
    }
}
