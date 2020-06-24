package com.glisco.funcraft6;

import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;

public class StandRemover extends BukkitRunnable {

    ArmorStand stand;

    public StandRemover(ArmorStand s){
        stand = s;
    }

    @Override
    public void run(){
        stand.remove();
    }
}
