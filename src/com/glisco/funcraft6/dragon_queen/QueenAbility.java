package com.glisco.funcraft6.dragon_queen;

import com.glisco.funcraft6.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QueenAbility implements Runnable {

    @Override
    public void run() {
        DragonBattle battle = Bukkit.getWorld("world_the_end").getEnderDragonBattle();

        if (battle.getEnderDragon() == null) return;
        if (battle.getEnderDragon().isDead()) return;

        List<Player> possibleTargets = new ArrayList<>();
        for (Entity e : battle.getEnderDragon().getNearbyEntities(80, 80, 80)) {
            if (!(e instanceof Player)) continue;
            possibleTargets.add((Player) e);
        }
        if (possibleTargets.isEmpty()) return;

        Location target = possibleTargets.get(new Random().nextInt(possibleTargets.size())).getLocation().subtract(0, 7, 0);

        Fireball fireball = battle.getEnderDragon().launchProjectile(Fireball.class);
        fireball.setDirection(target.toVector().subtract(battle.getEnderDragon().getLocation().toVector()));
        fireball.setYield(3.5f);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.p, this, new Random().nextInt(100) + 100);
    }
}
