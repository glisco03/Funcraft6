package com.glisco.funcraft6;

import org.bukkit.block.Block;

import java.util.HashMap;

public class GlobalVars {

    public static HashMap<Block, BrewClock> runningStands;

    public GlobalVars() {
        runningStands = new HashMap<Block, BrewClock>();
    }
}
