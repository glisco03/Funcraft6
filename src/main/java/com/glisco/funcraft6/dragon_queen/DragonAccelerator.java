package com.glisco.funcraft6.dragon_queen;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Objects;

public class DragonAccelerator implements Runnable {
    @Override
    public void run() {
        for (Entity e : Bukkit.getWorld("world_the_end").getEntities()) {
            if (e.getType().equals(EntityType.ENDER_DRAGON) && Objects.equals(e.getCustomName(), "§5§lDragon Queen")) {
                e.setVelocity(e.getVelocity().multiply(3));
            }
        }
    }
}
