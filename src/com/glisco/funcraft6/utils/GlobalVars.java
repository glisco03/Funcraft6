package com.glisco.funcraft6.utils;

import com.glisco.funcraft6.brewing.BrewClock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GlobalVars {

    public static HashMap<Block, BrewClock> runningStands;
    public static HashMap<Player, Integer> cooldownPlayers;
    public static HashMap<Player, Sign> signEditors;
    public static HashMap<Material, Material> smeltOres;
    public static HashMap<Player, Location> specators;

    public static List<Player> freezedPlayers;
    public static List<Player> signLocker;
    public static List<Player> signPublishers;

    public GlobalVars() {
        runningStands = new HashMap<>();
        cooldownPlayers = new HashMap<>();
        signEditors = new HashMap<>();
        smeltOres = new HashMap<>();
        smeltOres.put(Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP);
        smeltOres.put(Material.IRON_ORE, Material.IRON_INGOT);
        smeltOres.put(Material.GOLD_ORE, Material.GOLD_INGOT);
        freezedPlayers = new ArrayList<>();
        signLocker = new ArrayList<>();
        signPublishers = new ArrayList<>();
        specators = new HashMap<>();
    }
}
