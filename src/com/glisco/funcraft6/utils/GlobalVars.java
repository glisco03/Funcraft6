package com.glisco.funcraft6.utils;

import com.glisco.funcraft6.brewing.BrewClock;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class GlobalVars {

    public static HashMap<Block, BrewClock> runningStands;
    public static HashMap<Player, Integer> cooldownPlayers;

    public GlobalVars() {
        runningStands = new HashMap<Block, BrewClock>();
        cooldownPlayers = new HashMap<>();
    }
}
