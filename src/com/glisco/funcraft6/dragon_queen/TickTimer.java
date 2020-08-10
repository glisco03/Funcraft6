package com.glisco.funcraft6.dragon_queen;

import com.glisco.funcraft6.Main;
import org.bukkit.*;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

import java.util.Random;

public class TickTimer implements Runnable {

    private static boolean eggSummoned = false;

    @Override
    public void run() {
        World w = Bukkit.getWorld("world_the_end");
        if (w.getEnderDragonBattle().getRespawnPhase().equals(DragonBattle.RespawnPhase.SUMMONING_DRAGON) && !eggSummoned && DragonBattleListener.confirmed == 4) {
            w.getBlockAt(0, 66, 0).setType(Material.AIR);
            w.spawnParticle(Particle.LAVA, new Location(w, 0.5, 65.5, 0.5), 30, 0.1, 0.1, 0.1, 0.1);

            FallingBlock egg = w.spawnFallingBlock(new Location(w, 0.5, 65.5, 0.5), Material.DRAGON_EGG.createBlockData());
            egg.setDropItem(false);
            egg.setGravity(false);
            egg.setVelocity(new Vector(0, 1.4, 0));

            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.p, () -> {
                egg.remove();
                w.spawnParticle(Particle.DRAGON_BREATH, new Location(w, 0.5, 128, 0.5), 3500, 2, 2, 2, 0.5, null, true);
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.p, new QueenAbility(), new Random().nextInt(100) + 100);
            }, 100);
            eggSummoned = true;
        } else if (w.getEnderDragonBattle().getRespawnPhase().equals(DragonBattle.RespawnPhase.START) && eggSummoned) {
            eggSummoned = false;
        }
    }
}
