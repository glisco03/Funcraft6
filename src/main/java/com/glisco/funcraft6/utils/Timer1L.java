package com.glisco.funcraft6.utils;

import com.glisco.funcraft6.Main;
import com.glisco.funcraft6.enchantments.EnchantmentHelper;
import com.glisco.funcraft6.items.ItemHelper;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R2.EntityInsentient;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;

public class Timer1L implements Runnable {

    private Boolean flightEngaged;
    int setPieces;

    @Override
    public void run() {

        for (Player p : Bukkit.getOnlinePlayers()) {
            setPieces = 0;
            processDragonArmor(p);
            processFlight(p);
            processCooldown(p);
            processSleepers(p);
            processFreezedPlayers(p);
            processItemActionbar(p);
            processVillagers(p);
        }
    }

    private void processFlight(Player p) {
        flightEngaged = false;
        if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) return;

        if (p.getInventory().getChestplate() != null) {
            if (p.getInventory().getChestplate().getItemMeta().hasDisplayName()) {
                if (p.getInventory().getChestplate().getItemMeta().getDisplayName().equalsIgnoreCase("§dDraconic Wings")) {
                    p.setAllowFlight(true);
                    p.setFlySpeed(0.1f);
                    flightEngaged = true;
                }
            }
        }
        if (p.getInventory().getBoots() != null) {
            if (p.getInventory().getBoots().getItemMeta().hasLore()) {
                if (EnchantmentHelper.getEnchatmentFromLore(p.getInventory().getBoots().getItemMeta().getLore().get(0)) != null && p.isOnGround()) {
                    p.setAllowFlight(true);
                    p.setFlySpeed(0.1f);
                    flightEngaged = true;
                }
                if (!p.isOnGround()) {
                    flightEngaged = true;
                }
            }
        }
        if (setPieces == 4) {
            p.setAllowFlight(true);
            flightEngaged = true;
        }
        if (!flightEngaged) {
            p.setAllowFlight(false);
        }
    }

    private void processCooldown(Player p) {
        if (!GlobalVars.cooldownPlayers.containsKey(p)) return;
        if (GlobalVars.cooldownPlayers.get(p) > 0) {
            GlobalVars.cooldownPlayers.replace(p, GlobalVars.cooldownPlayers.get(p) - 1);
        } else {
            GlobalVars.cooldownPlayers.remove(p);
        }
    }

    private void processSleepers(Player p) {
        if (!GlobalVars.inBed.containsKey(p)) return;
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

    private void processFreezedPlayers(Player p) {
        if (GlobalVars.freezedPlayers.contains(p)) {
            p.closeInventory();
        }
    }

    private void processItemActionbar(Player p) {
        Location origin = p.getEyeLocation().add(p.getEyeLocation().getDirection());
        RayTraceResult result = p.getWorld().rayTraceEntities(origin, origin.getDirection(), 5);
        if (result == null) {
            if (p.getScoreboardTags().contains("SEND_EMPTY_ACTIONBAR")) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
                p.removeScoreboardTag("SEND_EMPTY_ACTIONBAR");
            }
            return;
        }
        if (result.getHitEntity().getType().equals(EntityType.DROPPED_ITEM)) {
            if (((Item) result.getHitEntity()).getItemStack().getItemMeta() == null) return;
            ;
            String name = ((Item) result.getHitEntity()).getItemStack().getItemMeta().getDisplayName();
            if (name.equals("")) {
                name = result.getHitEntity().getName();
            }
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(name));
            p.addScoreboardTag("SEND_EMPTY_ACTIONBAR");
        } else {
            if (p.getScoreboardTags().contains("SEND_EMPTY_ACTIONBAR")) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
                p.removeScoreboardTag("SEND_EMPTY_ACTIONBAR");
            }
        }
    }

    private void processVillagers(Player p) {
        if (!p.getInventory().getItemInMainHand().getType().equals(Material.EMERALD_BLOCK)) return;

        for (Entity e : p.getNearbyEntities(10, 10, 10)) {
            if (e.getType().equals(EntityType.VILLAGER)) {
                followPlayer(p, (LivingEntity) e, 0.4);
            }
        }
    }

    private void processDragonArmor(Player p) {
        PlayerInventory inventory = p.getInventory();

        if (ItemHelper.compareCustomItemID(inventory.getBoots(), "dragon_boots")) {
            setPieces++;
        }
        if (ItemHelper.compareCustomItemID(inventory.getLeggings(), "dragon_leggings")) {
            setPieces++;
        }
        if (ItemHelper.compareCustomItemID(inventory.getChestplate(), "dragon_chestplate")) {
            p.setFireTicks(0);
            p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 2, 0, true, false));
            Integer speed = inventory.getChestplate().getItemMeta().getPersistentDataContainer().get(Main.key("flight_speed"), PersistentDataType.INTEGER);
            switch (speed) {
                case 1:
                    p.setFlySpeed(0.1f);
                    break;
                case 2:
                    p.setFlySpeed(0.2f);
                    break;
                case 3:
                    p.setFlySpeed(0.4f);
                    break;
            }
            setPieces++;
        }
        if (ItemHelper.compareCustomItemID(inventory.getHelmet(), "dragon_helmet")) {
            int nightVisionEnabled = inventory.getHelmet().getItemMeta().getPersistentDataContainer().get(Main.key("night_vision"), PersistentDataType.INTEGER);
            if (nightVisionEnabled == 1) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 210, 0, true, false));
            }
            setPieces++;
        }
    }

    public void followPlayer(Player player, LivingEntity entity, double d) {
        final LivingEntity e = entity;
        final Player p = player;
        final float f = (float) d;
        ((EntityInsentient) ((CraftEntity) e).getHandle()).getNavigation().a(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), f);
    }
}
