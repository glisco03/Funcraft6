package com.glisco.funcraft6.ritual;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class TeleportRitualExecutor extends BukkitRunnable {

    int iteration;
    Location l;
    Location l1;
    Location l2;
    Location l3;
    Location l4;
    World w;

    public TeleportRitualExecutor(Location loc) {
        l = loc;
        w = l.getWorld();
        l.add(0.5, 0, 0.5);
        l1 = l.add(2, 0, 2).clone();
        l2 = l.add(-4, 0, 0).clone();
        l3 = l.add(0, 0, -4).clone();
        l4 = l.add(4, 0, 0).clone();
        l.add(-2.5, 0, 1.5);
        l.getWorld().spawnParticle(Particle.FLAME, l.add(0.5, 0.5, 0.5), 150, 0.5, 0.5, 0.5, 0.15);
        l.getWorld().playSound(l, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
        l.getWorld().strikeLightningEffect(l.add(0, 0.5, 0));
    }

    @Override
    public void run() {
        if (iteration < 25) {
            w.spawnParticle(Particle.SPELL_WITCH, l1, 5, 0.25, 0.1, 0.25, 0);
            w.spawnParticle(Particle.SPELL_WITCH, l2, 5, 0.25, 0.1, 0.25, 0);
            w.spawnParticle(Particle.SPELL_WITCH, l3, 5, 0.25, 0.1, 0.25, 0);
            w.spawnParticle(Particle.SPELL_WITCH, l4, 5, 0.25, 0.1, 0.25, 0);
            for (Player p : getNearbyPlayers(l)) {
                p.removePotionEffect(PotionEffectType.BLINDNESS);
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, iteration, 0));
            }
            iteration++;
        } else if (iteration < 35) {
            for (Player p : getNearbyPlayers(l)) {
                p.removePotionEffect(PotionEffectType.BLINDNESS);
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, iteration, 0));
            }
            iteration++;
        } else if (iteration == 35) {
            for (Player p : getNearbyPlayers(l)) {
                Location to = p.getLocation();
                to.setX(to.getX() * 20);
                to.setZ(to.getZ() * 20);
                to.setWorld(Bukkit.getWorld("mining"));
                p.removePotionEffect(PotionEffectType.BLINDNESS);
                p.removePotionEffect(PotionEffectType.CONFUSION);
                p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                p.teleport(to);
            }
            this.cancel();
        }
    }

    public ArrayList<Player> getNearbyPlayers(Location loc) {
        ArrayList<Player> nearby = new ArrayList<Player>();
        double range = 7;
        for (Entity e : loc.getWorld().getNearbyEntities(loc, range, range, range)) {
            if (e instanceof Player) {
                nearby.add((Player) e);
            }
        }
        return nearby;
    }
}
