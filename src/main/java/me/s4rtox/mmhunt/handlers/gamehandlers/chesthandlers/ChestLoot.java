package me.s4rtox.mmhunt.handlers.gamehandlers.chesthandlers;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ChestLoot {
    @Getter
    private final List<LootItem> items;
    private final List<Double> cumulativeWeights;

    public ChestLoot(List<LootItem> items) {
        this.items = items;
        this.cumulativeWeights = new ArrayList<>();
        initCumulativeWeights();
    }

    private void initCumulativeWeights() {
        double totalWeight = 0;
        for (LootItem item : items) {
            totalWeight += item.getChance();
            cumulativeWeights.add(totalWeight);
        }
    }

    public LootItem getRandomItem(ThreadLocalRandom random) {
        double randomWeight = random.nextDouble() * cumulativeWeights.get(cumulativeWeights.size() - 1);
        for (int i = 0; i < cumulativeWeights.size(); i++) {
            if (randomWeight < cumulativeWeights.get(i)) {
                return items.get(i);
            }
        }
        return null; //Only available when list is null(should never be)
    }
}
