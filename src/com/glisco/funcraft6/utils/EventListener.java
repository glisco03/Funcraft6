package com.glisco.funcraft6.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EventListener implements Listener {

    JavaPlugin p;

    public EventListener(JavaPlugin plugin) {
        p = plugin;
    }

    @EventHandler
    public void onAgileSword(PlayerItemHeldEvent e) {
        ItemStack heldItem = e.getPlayer().getInventory().getItem(e.getNewSlot());
        e.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
        if(heldItem == null){
            return;
        }
        if (heldItem.getItemMeta().getLore() != null) {
            if (heldItem.getItemMeta().getLore().contains("§7Agility I")) {
                e.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(8);
            } else if (heldItem.getItemMeta().getLore().contains("§7Agility II")) {
                e.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(12);
            } else if (heldItem.getItemMeta().getLore().contains("§7Agility III")) {
                e.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(16);
            }
        }
    }

    @EventHandler
    public void onRecall(PlayerItemConsumeEvent e) {
        if (e.getItem().equals(FuncraftItems.RECALL_POTION)) {
            e.getPlayer().teleport(e.getPlayer().getBedSpawnLocation());
        }
    }

    @EventHandler
    public void onSnort(PlayerInteractEvent e) {
        if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.GUNPOWDER) && e.getPlayer().isSneaking() && e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            e.getPlayer().getInventory().getItemInMainHand().setAmount(e.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
            e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), "minecraft:fc6.snort", 1, (float) Math.random() + 0.5f);
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100, 1));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 150, 1));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 19));
            if (Math.random() > 0.85) {
                e.getPlayer().addScoreboardTag("SNORTER");
                TNTPrimed tnt = (TNTPrimed) e.getPlayer().getWorld().spawnEntity(e.getPlayer().getLocation(), EntityType.PRIMED_TNT);
                tnt.setFuseTicks(0);
                tnt.addScoreboardTag("NO_BLOCK_DAMAGE");
            }
        }
    }

    @EventHandler
    public void onSnortExplosion(EntityExplodeEvent e) {
        if (e.getEntity().getScoreboardTags().contains("NO_BLOCK_DAMAGE")) {
            e.blockList().clear();
        }
    }

    @EventHandler
    public void onSnortDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            if (((Player) e.getEntity()).getHealth() - e.getFinalDamage() > 0 && e.getEntity().getScoreboardTags().contains("SNORTER") && e.getDamager().getScoreboardTags().contains("NO_BLOCK_DAMAGE")) {
                e.getEntity().removeScoreboardTag("SNORTER");
            }
        }
    }

    @EventHandler
    public void onSnortDeath(PlayerDeathEvent e) {
        if (e.getEntity().getScoreboardTags().contains("SNORTER")) {
            e.setDeathMessage(e.getEntity().getName() + " had an overdose");
            e.getEntity().removeScoreboardTag("SNORTER");
            e.getEntity().addScoreboardTag("OVERDOSE");
        } else if (e.getEntity().getScoreboardTags().contains("MAGIC")) {
            e.setDeathMessage(e.getEntity().getName() + " came face to face with dark energy");
            e.getEntity().removeScoreboardTag("MAGIC");
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (e.getPlayer().getScoreboardTags().contains("OVERDOSE")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(p, new Runnable() {
                @Override
                public void run() {
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 2400, 0));
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, 2400, 3));
                    e.getPlayer().removeScoreboardTag("OVERDOSE");
                }
            }, 60);
        }
    }
}

