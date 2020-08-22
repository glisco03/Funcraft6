package com.glisco.funcraft6.utils;

import com.glisco.funcraft6.brewing.BrewClock;
import org.bukkit.Bukkit;
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
    public static HashMap<Material, Integer> smeltXP;
    public static HashMap<Player, Location> specators;
    public static HashMap<Player, Integer> inBed;
    public static HashMap<Player, Location> spawnPointPreservence;
    public static HashMap<Player, DragonsEyeExecutor> runningTeleports;

    public static List<Player> freezedPlayers;
    public static List<Player> sleeping;
    public static List<PacketReader> activeReaders;

    public static Location spawn;

    public GlobalVars() {
        runningStands = new HashMap<>();
        cooldownPlayers = new HashMap<>();
        smeltOres = new HashMap<>();
        smeltXP = new HashMap<>();
        specators = new HashMap<>();
        inBed = new HashMap<>();
        spawnPointPreservence = new HashMap<>();
        runningTeleports = new HashMap<>();

        smeltOres.put(Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP);
        smeltOres.put(Material.IRON_ORE, Material.IRON_INGOT);
        smeltOres.put(Material.GOLD_ORE, Material.GOLD_INGOT);

        smeltXP.put(Material.ANCIENT_DEBRIS, 2);
        smeltXP.put(Material.IRON_ORE, 1);
        smeltXP.put(Material.GOLD_ORE, 1);

        freezedPlayers = new ArrayList<>();
        sleeping = new ArrayList<>();
        activeReaders = new ArrayList<>();

        spawn = new Location(Bukkit.getWorld("spawn"), 3.5, 81, -2.5, 50, 2);
    }
}
