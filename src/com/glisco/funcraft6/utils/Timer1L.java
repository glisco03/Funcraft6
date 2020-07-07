package com.glisco.funcraft6.utils;

import com.glisco.funcraft6.Main;
import com.glisco.funcraft6.enchantments.EnchantmentHelper;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

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
                    if(!p.isOnGround()){
                        flightEngaged = true;
                    }
                }
            }
            if (!flightEngaged) {
                p.setAllowFlight(false);
            }
        }

        for(Player p: GlobalVars.inBed.keySet()){
            if(GlobalVars.inBed.get(p) < 101){
                GlobalVars.inBed.replace(p, GlobalVars.inBed.get(p) + 1);
            } else if(GlobalVars.inBed.get(p) == 101){
                GlobalVars.sleeping.add(p);
                double percentage = (double) GlobalVars.sleeping.size() / (double)p.getWorld().getPlayers().size();;
                percentage = percentage * 100d;
                Bukkit.broadcastMessage(Main.prefix + "§6" + p.getName() + " is now sleeping (§a" + Math.round(percentage) + "%§6)");
                GlobalVars.inBed.replace(p, GlobalVars.inBed.get(p) + 1);
                if(percentage >= 50){
                    p.getWorld().setTime(0);
                }
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (GlobalVars.freezedPlayers.contains(p)) {
                p.closeInventory();
            }
        }
    }
}
