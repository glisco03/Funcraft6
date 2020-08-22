package com.glisco.funcraft6.utils;

import com.glisco.funcraft6.items.FuncraftItems;
import com.glisco.funcraft6.items.ItemHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;

public class Timer20L implements Runnable {
    @Override
    public void run() {
        PlayerList.updateGlobalTablist();

        for (Player p : Bukkit.getOnlinePlayers()) {
            for (ItemStack item : p.getInventory()) {
                if (item == null) continue;
                if (!item.hasItemMeta()) continue;
                if (!item.getItemMeta().hasDisplayName()) continue;
                if (!item.getItemMeta().hasLore()) continue;
                if (!item.getItemMeta().getDisplayName().equals("Locked Compass")) continue;


                for (Entity nearby : p.getWorld().getNearbyEntities(p.getLocation(), 10, 10, 10)) {
                    if (!(nearby instanceof Player)) continue;

                    if (item.getItemMeta().getLore().get(2).split("§b")[1].equals(nearby.getName())) {
                        p.getInventory().remove(item);
                        ((Player) nearby).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 0));
                        break;
                    }
                }
            }
        }

        for (Entity e : Bukkit.getWorld("world_the_end").getEntities()) {
            if (!e.getType().equals(EntityType.DROPPED_ITEM)) continue;
            if (!ItemHelper.compareCustomItemID(((Item) e).getItemStack(), "queen_scale")) continue;
            if (((Item) e).getItemStack().getAmount() != 2) continue;

            BoundingBox box = null;
            AreaEffectCloud cloud = null;
            Item endrod = null;

            for (Entity entity : e.getNearbyEntities(3, 3, 3)) {
                if (entity.getType().equals(EntityType.AREA_EFFECT_CLOUD)) {
                    cloud = (AreaEffectCloud) entity;
                    if (!cloud.getParticle().equals(Particle.DRAGON_BREATH)) break;
                    box = cloud.getBoundingBox().clone();
                } else if (entity.getType().equals(EntityType.DROPPED_ITEM)) {
                    if (!((Item) entity).getItemStack().getType().equals(Material.END_ROD)) continue;
                    if (((Item) entity).getItemStack().getAmount() != 1) continue;
                    endrod = (Item) entity;
                }
            }
            if (box == null) continue;
            if (endrod == null) continue;

            box.shift(0, -0.5, 0);
            if (!box.overlaps(e.getBoundingBox())) continue;

            cloud.remove();
            endrod.remove();

            e.getWorld().spawnParticle(Particle.LAVA, e.getLocation(), 100, 0.1, 0.1, 0.1, 0.15);
            ((Item) e).setItemStack(FuncraftItems.DRAGON_SWORD);
        }
    }
}
