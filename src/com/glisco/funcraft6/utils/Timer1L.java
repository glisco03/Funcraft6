package com.glisco.funcraft6.utils;

import com.glisco.funcraft6.Main;
import com.glisco.funcraft6.enchantments.EnchantmentHelper;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R1.EntityInsentient;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.util.RayTraceResult;

public class Timer1L implements Runnable {

    private Boolean flightEngaged;

    @Override
    public void run() {
        for (Player p : GlobalVars.cooldownPlayers.keySet()) {
            if (GlobalVars.cooldownPlayers.get(p) > 0) {
                GlobalVars.cooldownPlayers.replace(p, GlobalVars.cooldownPlayers.get(p) - 1);
            } else {
                GlobalVars.cooldownPlayers.remove(p);
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            flightEngaged = false;
            if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) {
                continue;
            }
            if (p.getInventory().getChestplate() != null) {
                if (p.getInventory().getChestplate().getItemMeta().hasDisplayName()) {
                    if (p.getInventory().getChestplate().getItemMeta().getDisplayName().equalsIgnoreCase("§dDraconic Wings")) {
                        p.setAllowFlight(true);
                        flightEngaged = true;
                    }
                }
            }
            if (p.getInventory().getBoots() != null) {
                if (p.getInventory().getBoots().getItemMeta().hasLore()) {
                    if (EnchantmentHelper.getEnchatmentFromLore(p.getInventory().getBoots().getItemMeta().getLore().get(0)) != null && p.isOnGround()) {
                        p.setAllowFlight(true);
                        flightEngaged = true;
                    }
                    if (!p.isOnGround()) {
                        flightEngaged = true;
                    }
                }
            }
            if (!flightEngaged) {
                p.setAllowFlight(false);
            }
        }

        for (Player p : GlobalVars.inBed.keySet()) {
            if (GlobalVars.inBed.get(p) < 101) {
                GlobalVars.inBed.replace(p, GlobalVars.inBed.get(p) + 1);
            } else if (GlobalVars.inBed.get(p) == 101) {
                GlobalVars.sleeping.add(p);
                double percentage = (double) GlobalVars.sleeping.size() / (double) p.getWorld().getPlayers().size();
                percentage = percentage * 100d;
                Bukkit.broadcastMessage(Main.prefix + "§6" + p.getName() + " is now sleeping (§a" + Math.round(percentage) + "%§6)");
                GlobalVars.inBed.replace(p, GlobalVars.inBed.get(p) + 1);
                if (percentage >= 50) {
                    p.getWorld().setTime(0);
                    p.getWorld().setThundering(false);
                    p.getWorld().setStorm(false);
                    Bukkit.broadcastMessage(Main.prefix + "§6Wakey, wakey, rise and shine... §lGOOD MORNING EVERYONE!");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), "fc6.nightskip", 1, 1);
                    }
                }
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (GlobalVars.freezedPlayers.contains(p)) {
                p.closeInventory();
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            Location origin = p.getEyeLocation().add(p.getEyeLocation().getDirection());
            RayTraceResult result = p.getWorld().rayTraceEntities(origin, origin.getDirection(), 5);
            if (result == null) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
                continue;
            }
            if (result.getHitEntity().getType().equals(EntityType.DROPPED_ITEM)) {
                String name = ((Item) result.getHitEntity()).getItemStack().getItemMeta().getDisplayName();
                if (name.equals("")) {
                    name = result.getHitEntity().getName();
                }
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(name));
            } else {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.getInventory().getItemInMainHand().getType().equals(Material.EMERALD_BLOCK)) continue;

            for (Entity e : p.getNearbyEntities(10, 10, 10)) {
                if (e.getType().equals(EntityType.VILLAGER)) {
                    followPlayer(p, (LivingEntity) e, 0.4);
                }
            }
        }
    }

    public void followPlayer(Player player, LivingEntity entity, double d) {
        final LivingEntity e = entity;
        final Player p = player;
        final float f = (float) d;
        ((EntityInsentient) ((CraftEntity) e).getHandle()).getNavigation().a(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), f);
    }
}
