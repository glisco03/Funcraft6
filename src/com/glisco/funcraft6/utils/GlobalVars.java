package com.glisco.funcraft6.utils;

import com.glisco.funcraft6.brewing.BrewClock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GlobalVars {

    public static HashMap<Block, BrewClock> runningStands;
    public static HashMap<Player, Integer> cooldownPlayers;
    public static HashMap<Material, Material> smeltOres;
    public static HashMap<Player, Location> specators;
    public static HashMap<Player, Integer> inBed;
    public static HashMap<Player, Location> spawnPointPreservence;

    public static List<Player> freezedPlayers;
    public static List<Player> sleeping;

    public GlobalVars() {
        runningStands = new HashMap<>();
        cooldownPlayers = new HashMap<>();
        smeltOres = new HashMap<>();
        specators = new HashMap<>();
        inBed = new HashMap<>();
        spawnPointPreservence = new HashMap<>();

        smeltOres.put(Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP);
        smeltOres.put(Material.IRON_ORE, Material.IRON_INGOT);
        smeltOres.put(Material.GOLD_ORE, Material.GOLD_INGOT);

        freezedPlayers = new ArrayList<>();
        sleeping = new ArrayList<>();
    }
}
