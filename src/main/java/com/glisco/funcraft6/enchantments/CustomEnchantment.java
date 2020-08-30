package com.glisco.funcraft6.enchantments;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CustomEnchantment {

    private final int maxLevel;
    private final String identifier;
    private final List<Material> validItemTypes;
    private final HashMap<Integer, String> romanNumerals;

    public CustomEnchantment(String name, int levelMax, Material[] validMaterials) {
        maxLevel = levelMax;
        identifier = name;
        validItemTypes = Arrays.asList(validMaterials);
        romanNumerals = new HashMap<>();
        romanNumerals.put(1, "I");
        romanNumerals.put(2, "II");
        romanNumerals.put(3, "III");
        romanNumerals.put(4, "IV");
        romanNumerals.put(5, "V");
        romanNumerals.put(6, "VI");
    }

    public List<String> getLore(List<String> oldLore, int level) {
        List<String> lore;
        lore = new ArrayList<>();
        String modifiers = null;

        if (oldLore != null) {
            lore.addAll(oldLore);
            lore.removeIf(s -> s.contains(identifier));
        }

        for (String s : lore) {
            if (s.contains("modifiers left")) {
                modifiers = s;
            }
        }

        if (modifiers != null) {
            lore.remove(modifiers);
        }

        lore.add("§7" + identifier + " " + romanNumerals.get(level));
        if (modifiers != null) {
            lore.add(modifiers);
        }
        return lore;
    }

    public List<String> getLeveledLore(List<String> oldLore, int level1, int level2) {
        int level;
        if (level1 == level2) {
            level = Math.min(level1 + 1, maxLevel);
        } else if (level2 > 1) {
            level = level2;
        } else {
            level = level1;
        }

        List<String> lore;
        lore = new ArrayList<>();
        String modifiers = null;

        if (oldLore != null) {
            lore.addAll(oldLore);
            lore.removeIf(s -> s.contains(identifier));
        }

        for (String s : lore) {
            if (s.contains("modifiers left")) {
                modifiers = s;
            }
        }

        if (modifiers != null) {
            lore.remove(modifiers);
        }

        lore.add("§7" + identifier + " " + romanNumerals.get(level));
        if (modifiers != null) {
            lore.add(modifiers);
        }
        return lore;
    }

    public List<Material> getValidItemTypes() {
        return validItemTypes;
    }

    public boolean isValidLore(String lore) {
        return lore.contains(identifier);
    }

    public String getIdentifier() {
        return identifier;
    }
}
