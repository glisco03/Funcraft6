package com.glisco.funcraft6.ritual;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class StructureHelper {

    private static JavaPlugin p;

    public StructureHelper(JavaPlugin pl) {
        p = pl;
    }

    public static boolean checkStructure(Block b) {
        int checksPassed = 0;
        Location l = b.getLocation().add(0, -1, 0);
        checksPassed += checkForBlock(l, Material.IRON_BLOCK);
        checksPassed += checkForBlock(l.add(0, -1, 0), Material.IRON_BLOCK);

        l = b.getLocation().add(2, -1, 2);
        checksPassed += checkForBlock(l, Material.LAPIS_ORE);
        checksPassed += checkForBlock(l.add(0, -1, 0), Material.QUARTZ_PILLAR);

        l = b.getLocation().add(2, -1, -2);
        checksPassed += checkForBlock(l, Material.GOLD_ORE);
        checksPassed += checkForBlock(l.add(0, -1, 0), Material.QUARTZ_PILLAR);

        l = b.getLocation().add(-2, -1, 2);
        checksPassed += checkForBlock(l, Material.COAL_ORE);
        checksPassed += checkForBlock(l.add(0, -1, 0), Material.QUARTZ_PILLAR);

        l = b.getLocation().add(-2, -1, -2);
        checksPassed += checkForBlock(l, Material.REDSTONE_ORE);
        checksPassed += checkForBlock(l.add(0, -1, 0), Material.QUARTZ_PILLAR);

        return checksPassed == 10;
    }

    public static int checkForBlock(Location l, Material m) {
        if (l.getBlock().getType().equals(m)) {
            return 1;
        } else {
            Particle.DustOptions dust = new Particle.DustOptions(
                    Color.fromRGB(255, 0, 0), 1);
            double expansion = 0.25;
            int count = 35;
            if (!l.getBlock().getType().equals(Material.AIR)) {
                expansion = 0.4;
                count = 50;
            }
            l.add(0.5, 0.5, 0.5);
            l.getWorld().spawnParticle(Particle.REDSTONE, l, count, expansion, expansion, expansion, 0, dust);
            l.subtract(0, 0.85, 0);
            ArmorStand a = l.getWorld().spawn(l, ArmorStand.class, as -> {
                as.setSmall(true);
                as.setVisible(false);
                as.setGravity(false);
                as.getEquipment().setHelmet(new ItemStack(m));
            });
            new StandRemover(a).runTaskLater(p, 75);
            l.subtract(0.5, -0.35, 0.5);
            return 0;
        }
    }

}
