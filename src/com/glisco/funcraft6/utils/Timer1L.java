package com.glisco.funcraft6.utils;

import com.glisco.funcraft6.enchantments.EnchantmentHelper;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Timer1L implements Runnable {
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
            if (GlobalVars.freezedPlayers.contains(p)) {
                p.closeInventory();
            }
            if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) {
                continue;
            }
            if (p.getInventory().getBoots() == null) {
                continue;
            }
            if (!p.getInventory().getBoots().getItemMeta().hasLore()) {
                continue;
            }
            if (EnchantmentHelper.getEnchatmentFromLore(p.getInventory().getBoots().getItemMeta().getLore().get(0)) != null && p.isOnGround()) {
                p.setAllowFlight(true);
            }
        }
    }
}
