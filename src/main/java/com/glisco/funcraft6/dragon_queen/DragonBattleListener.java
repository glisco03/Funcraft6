package com.glisco.funcraft6.dragon_queen;

import com.glisco.funcraft6.Main;
import com.glisco.funcraft6.items.FuncraftItems;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DragonBattleListener implements Listener {

    private static List<Location> eggCrystals;
    private static World w;
    private static boolean stomped = false;

    public static int confirmed = 0;

    public DragonBattleListener() {
        eggCrystals = new ArrayList<>();
        w = Bukkit.getWorld("world_the_end");
        eggCrystals.add(new Location(w, 7, 62, 0));
        eggCrystals.add(new Location(w, -7, 62, 0));
        eggCrystals.add(new Location(w, 0, 62, 7));
        eggCrystals.add(new Location(w, 0, 62, -7));
    }

    @EventHandler
    public void onCrystalPlace(EntitySpawnEvent e) {
        if (e.getEntityType().equals(EntityType.ENDER_CRYSTAL)) {
            if (eggCrystals.contains(e.getLocation().getBlock().getLocation()) && e.getLocation().getWorld().getBlockAt(new Location(e.getLocation().getWorld(), 0, 65, 0)).getType().equals(Material.DRAGON_EGG)) {
                EnderCrystal crystal = (EnderCrystal) e.getEntity();
                crystal.setBeamTarget(new Location(e.getLocation().getWorld(), 0, 63, 0));
                confirmed++;
            }
        }
    }

    @EventHandler
    public void onEggBreak(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !e.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;

        if (e.getClickedBlock().getLocation().equals(new Location(w, 0, 65, 0)) && confirmed > 0) {
            confirmed = 0;
            new Location(w, 0, 65, 0).getBlock().setType(Material.AIR);
            w.createExplosion(new Location(w, 0, 65, 0), 10, true, false);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onQueenSpawn(CreatureSpawnEvent e) {
        if (e.getEntity().getType().equals(EntityType.ENDER_DRAGON)) {
            if (confirmed == 4) {
                e.getEntity().setCustomName("§5§lDragon Queen");
                e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(300);
                e.getEntity().setHealth(300);
                e.getEntity().getWorld().getEnderDragonBattle().getBossBar().setStyle(BarStyle.SEGMENTED_6);

                confirmed = 0;
            } else {
                e.getEntity().getWorld().getEnderDragonBattle().getBossBar().setStyle(BarStyle.SOLID);
                e.getEntity().getWorld().getEnderDragonBattle().getBossBar().setTitle("Ender Dragon");
            }
        }
    }

    @EventHandler
    public void onCrystalRanged(EntityDamageEvent e) {
        if (!e.getEntityType().equals(EntityType.ENDER_CRYSTAL)) return;

        if (w.getEnderDragonBattle().getEnderDragon() == null) return;

        if (e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE) && Objects.equals(w.getEnderDragonBattle().getEnderDragon().getCustomName(), "§5§lDragon Queen")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDragonStomp(EnderDragonChangePhaseEvent e) {
        if (!Objects.equals(e.getEntity().getCustomName(), "§5§lDragon Queen")) return;

        if (e.getNewPhase().equals(EnderDragon.Phase.SEARCH_FOR_BREATH_ATTACK_TARGET)) {
            if (!stomped) {
                for (Entity entity : e.getEntity().getNearbyEntities(15, 15, 15)) {
                    if (!(entity instanceof Player)) continue;
                    Player p = (Player) entity;
                    p.setVelocity(new Location(w, 0, 40, 0).toVector().subtract(p.getLocation().toVector()).multiply(-1));
                }
                stomped = true;
            }
        } else if (e.getNewPhase().equals(EnderDragon.Phase.LEAVE_PORTAL)) {
            stomped = false;
        }
    }

    @EventHandler
    public void onQueenDeath(EntityDeathEvent e) {
        if (!e.getEntityType().equals(EntityType.ENDER_DRAGON)) return;
        if (!Objects.equals(e.getEntity().getCustomName(), "§5§lDragon Queen")) return;

        ItemStack drop = FuncraftItems.QUEEN_SCALE.clone();
        drop.setAmount(new Random().nextInt(7) + 3);

        e.getDrops().add(drop);
    }

    @EventHandler
    public void onImpact(EntityExplodeEvent e) {
        if (!e.getEntity().getWorld().equals(w)) return;
        if (!e.getEntity().getType().equals(EntityType.FIREBALL)) return;

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.p, () -> {
            for (int i = 0; i < new Random().nextInt(4) + 3; i++) {
                Endermite endermite = (Endermite) w.spawnEntity(e.getLocation(), EntityType.ENDERMITE);
                endermite.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1000, 0, true));
            }
        }, 3);

    }
}
