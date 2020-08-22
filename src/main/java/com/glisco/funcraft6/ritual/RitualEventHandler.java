package com.glisco.funcraft6.ritual;

import com.glisco.funcraft6.utils.GlobalVars;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class RitualEventHandler implements Listener {

    final JavaPlugin p;

    public RitualEventHandler(JavaPlugin plugin) {
        p = plugin;
    }

    @EventHandler
    public void onDiamondClick(PlayerInteractEvent e) throws IllegalAccessException {
        if (GlobalVars.cooldownPlayers.containsKey(e.getPlayer())) {
            return;
        }
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getClickedBlock().getType().equals(Material.DIAMOND_BLOCK) && e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.HEART_OF_THE_SEA)) {
                Player p = e.getPlayer();
                GlobalVars.cooldownPlayers.put(e.getPlayer(), 75);
                if (StructureHelper.checkStructure(e.getClickedBlock())) {
                    if (e.getClickedBlock().getWorld().getTime() > 14000 && e.getClickedBlock().getWorld().getTime() < 22000 && e.getPlayer().getWorld().getHighestBlockAt(e.getClickedBlock().getLocation()).equals(e.getClickedBlock())) {
                        p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                        for (Player player : getNearbyPlayers(p.getLocation())) {
                            player.sendMessage("§7§oA sudden surge of power flows into the ritual. You feel like one with time and space as the world around you begins to destabilize.");
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 600, 2));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 600, 0));
                        }
                        new ActivationRitualExecutor(e.getClickedBlock().getLocation()).runTaskTimer(this.p, 0, 1);
                    } else {
                        e.getClickedBlock().getWorld().spawnParticle(Particle.SMOKE_LARGE, e.getClickedBlock().getLocation().add(0.5, 0.5, 0.5), 45, 0.5, 0.5, 0.5, 0);
                        p.sendMessage("§7§oYou feel a whisp of energy emitting from the ritual, yet it does not activate. Maybe the children of light have to be present?");
                    }
                } else {
                    e.getClickedBlock().getWorld().spawnParticle(Particle.SMOKE_LARGE, e.getClickedBlock().getLocation().add(0.5, 0.5, 0.5), 45, 0.5, 0.5, 0.5, 0);
                }
            } else if (e.getClickedBlock().getType().equals(Material.BEDROCK)) {
                if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DIAMOND)) {
                    GlobalVars.cooldownPlayers.put(e.getPlayer(), 75);
                    Player p = e.getPlayer();
                    if (StructureHelper.checkStructure(e.getClickedBlock())) {
                        p.sendMessage("Destroying link...");
                        p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                        e.getClickedBlock().setType(Material.DIAMOND_BLOCK);
                    }
                } else if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.EMERALD)) {
                    GlobalVars.cooldownPlayers.put(e.getPlayer(), 75);
                    Player p = e.getPlayer();
                    if (StructureHelper.checkStructure(e.getClickedBlock())) {
                        if (e.getClickedBlock().getWorld().getTime() > 14000 && e.getClickedBlock().getWorld().getTime() < 22000 && e.getPlayer().getWorld().getHighestBlockAt(e.getClickedBlock().getLocation()).equals(e.getClickedBlock())) {
                            p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                            for (Player player : getNearbyPlayers(p.getLocation())) {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 600, 2));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 600, 0));
                            }
                            new TeleportRitualExecutor(e.getClickedBlock().getLocation()).runTaskTimer(this.p, 0, 1);
                        } else {
                            e.getClickedBlock().getWorld().spawnParticle(Particle.SMOKE_LARGE, e.getClickedBlock().getLocation().add(0.5, 0.5, 0.5), 45, 0.5, 0.5, 0.5, 0);
                            p.sendMessage("§7§oYou feel a whisp of energy emitting from the ritual, yet it does not activate. Maybe the children of light have to be present?");
                        }
                    } else {
                        e.getClickedBlock().getWorld().spawnParticle(Particle.SMOKE_LARGE, e.getClickedBlock().getLocation().add(0.5, 0.5, 0.5), 45, 0.5, 0.5, 0.5, 0);
                    }
                }

            }
        }
    }

    @EventHandler
    public void onArmorStandClick(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked().getType().equals(EntityType.ARMOR_STAND)) {
            ArmorStand a = (ArmorStand) e.getRightClicked();
            if (a.isSmall()) {
                a.getLocation().add(0, 0.5, 0).getBlock().setType(e.getPlayer().getInventory().getItemInMainHand().getType());
                e.getPlayer().getInventory().getItemInMainHand().setAmount(e.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
            }
        }
    }

    public ArrayList<Player> getNearbyPlayers(Location loc) {
        ArrayList<Player> nearby = new ArrayList<>();
        double range = 7;
        for (Entity e : loc.getWorld().getNearbyEntities(loc, range, range, range)) {
            if (e instanceof Player) {
                nearby.add((Player) e);
            }
        }
        return nearby;
    }
}
