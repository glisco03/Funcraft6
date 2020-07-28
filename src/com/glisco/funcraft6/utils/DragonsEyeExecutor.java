package com.glisco.funcraft6.utils;

import com.glisco.funcraft6.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class DragonsEyeExecutor extends BukkitRunnable {

    private int step = 0;
    private final int amount;
    private final double radius;

    private final Player p;
    public final Location center;
    private final ItemStack eye;

    public final boolean isSneaking;

    public DragonsEyeExecutor(Player p, Boolean isSneaking, ItemStack eye) {
        this.p = p;
        this.isSneaking = isSneaking;

        this.center = p.getLocation().clone();

        this.eye = eye.clone();

        if (isSneaking) {
            radius = 3;
            amount = 150;
        } else {
            radius = 0.65;
            amount = 55;
        }

        runTaskTimer(Main.p, 0, 1);
    }


    @Override
    public void run() {
        if (step < 100) {
            Location tempCenter = center.clone().add(0, step * 0.02, 0);
            ArrayList<Location> circle = getCircle(tempCenter, radius, amount);
            for (Location l : circle) {
                p.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, l, 1, 0, 0, 0, 0);
            }
            step++;
        } else {
            PersistentDataContainer eyeData = eye.getItemMeta().getPersistentDataContainer();
            int[] location = eyeData.get(Main.key("location"), PersistentDataType.INTEGER_ARRAY);
            World world = Bukkit.getWorld(eyeData.get(Main.key("world"), PersistentDataType.STRING));
            Location target = new Location(world, location[0], location[1], location[2]);

            if (isSneaking) {
                for (Entity e : p.getWorld().getNearbyEntities(center.add(0, 1, 0), 3, 2, 3)) {
                    e.teleport(target);
                }
            } else {
                p.teleport(target);
            }

            cancel();
        }
        if (step == 60) {
            p.getWorld().spawnParticle(Particle.PORTAL, center, 2500, 0.25, 1, 0.25, 1);
        }
    }

    @Override
    public void cancel() {
        GlobalVars.runningTeleports.remove(p);
        super.cancel();
    }

    public ArrayList<Location> getCircle(Location center, double radius, int amount) {
        World world = center.getWorld();
        double increment = (2 * Math.PI) / amount;
        ArrayList<Location> locations = new ArrayList<Location>();
        for (int i = 0; i < amount; i++) {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(world, x, center.getY(), z));
        }
        return locations;
    }
}
