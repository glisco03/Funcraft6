package com.glisco.funcraft6.ritual;

import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;

public class StandRemover extends BukkitRunnable {

    final ArmorStand stand;

    public StandRemover(ArmorStand s) {
        stand = s;
    }

    @Override
    public void run() {
        stand.remove();
    }
}
