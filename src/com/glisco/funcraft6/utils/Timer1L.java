package com.glisco.funcraft6.utils;

import org.bukkit.entity.Player;

public class Timer1L implements Runnable {
    @Override
    public void run() {
        for (Player p : GlobalVars.cooldownPlayers.keySet()) {
            if (GlobalVars.cooldownPlayers.get(p) > 0) {
                GlobalVars.cooldownPlayers.replace(p, GlobalVars.cooldownPlayers.get(p) - 1);
            } else {
                GlobalVars.cooldownPlayers.remove(p);
            }
        }
    }
}
