package com.glisco.funcraft6;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Consumer;

import java.util.ArrayList;

public class EventListener implements Listener {

    JavaPlugin p;

    public EventListener(JavaPlugin plugin) {
        p = plugin;
    }

    @EventHandler
    public void onDiamondClick(PlayerInteractEvent e) {
        if (e.getPlayer().getScoreboardTags().contains("clicked")) {
            e.getPlayer().removeScoreboardTag("clicked");
            return;
        } else {
            e.getPlayer().addScoreboardTag("clicked");
        }
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getClickedBlock().getType().equals(Material.DIAMOND_BLOCK) && e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.HEART_OF_THE_SEA)) {
                Player p = e.getPlayer();
                if (checkStructure(e.getClickedBlock()) == true) {
                    if (e.getClickedBlock().getWorld().getTime() > 14000 && e.getClickedBlock().getWorld().getTime() < 22000) {
                        p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                        for (Player player : getNearbyPlayers(p.getLocation())) {
                            player.sendMessage("§7§oA sudden surge of power flows into the ritual. You feel like one with time and space as the world around you begins to destabilize.");
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 120000, 2));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 120000, 0));
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
                    Player p = e.getPlayer();
                    if (checkStructure(e.getClickedBlock()) == true) {
                        p.sendMessage("Destroying link...");
                        p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                        e.getClickedBlock().setType(Material.DIAMOND_BLOCK);
                    }
                } else if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.EMERALD)) {
                    Player p = e.getPlayer();
                    if (checkStructure(e.getClickedBlock()) == true) {
                        if (e.getClickedBlock().getWorld().getTime() > 14000 && e.getClickedBlock().getWorld().getTime() < 22000) {
                            p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                            for (Player player : getNearbyPlayers(p.getLocation())) {
                                player.sendMessage("§7§oA sudden surge of power flows into the ritual. You feel like one with time and space as the world around you begins to destabilize.");
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 120000, 2));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 120000, 0));
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

            } else if (e.getClickedBlock().getType().equals(Material.SPONGE)) {
                e.getPlayer().getInventory().addItem(FuncraftItems.RECALL_POTION);
                e.getPlayer().setWalkSpeed(0.2f);
            }

        }
    }

    /*@EventHandler
    public void onPotionCraft(PrepareItemCraftEvent e) {
        if (e.getInventory().getItem(2) != null && e.getInventory().getItem(5) != null && e.getInventory().getItem(8) != null) {
            if (e.getInventory().getItem(2).getType().equals(Material.ENDER_PEARL) && e.getInventory().getItem(5).equals(FuncraftItems.REGEN_POTION) && e.getInventory().getItem(8).getType().equals(Material.BLAZE_ROD)) {
                e.getInventory().setResult(FuncraftItems.RECALL_POTION);
            }
        }
    }*/

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
            e.getPlayer().playSound(e.getPlayer().getLocation(), "minecraft:fc6.snort", 1, (float) Math.random() + 0.5f);
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
        }
    }


    //ItemPlacer by NacOJerk
    @EventHandler
    public void potionItemPlacer(InventoryClickEvent e) {
        if(e.getClickedInventory() == null){
            return;
        }
        if (e.getClickedInventory().getType() != InventoryType.BREWING) {
            return;
        }
        if (!(e.getClick() == ClickType.LEFT)) {
            return;
        }
        if (!e.getCursor().getType().equals(Material.ENDER_PEARL)) {
            return;
        }
        if (!e.getCurrentItem().getType().equals(Material.AIR)) {
            return;
        }
        if (e.getSlot() != 3) {
            return;
        }
        ItemStack insertion = e.getCurrentItem().clone();
        Bukkit.getScheduler().scheduleSyncDelayedTask(p, new Runnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                e.getClickedInventory().setItem(e.getSlot(), new ItemStack(e.getCursor().getType(), e.getCursor().getAmount()));
                e.setCursor(null);
            }
        }, 1L);
        ((Player) e.getWhoClicked()).updateInventory();
        BrewerInventory inv = (BrewerInventory) e.getClickedInventory();
        boolean contained = false;
        for (ItemStack i : inv.getStorageContents()) {
            if (i != null) {
                if (i.equals(FuncraftItems.REGEN_POTION)) {
                    contained = true;
                    break;
                }
            }
        }
        if (contained && ((BrewingStand)e.getClickedInventory().getLocation().getBlock().getState()).getFuelLevel() > 0) {
            BrewClock clock = new BrewClock(e.getClickedInventory().getLocation().getBlock(), insertion.getAmount(), p);
            GlobalVars.runningStands.put(e.getClickedInventory().getLocation().getBlock(), clock);
        }
    }

    @EventHandler
    public void onBrewerClick(InventoryClickEvent e){
        if(e.getClickedInventory() == null){
            return;
        }
        if (e.getClickedInventory().getType() != InventoryType.BREWING) {
            return;
        }
        if(!e.getCurrentItem().getType().equals(Material.ENDER_PEARL) && !e.getCurrentItem().equals(FuncraftItems.REGEN_POTION)){
            return;
        }
        if(e.getSlot() > 3){
            return;
        }
        if(GlobalVars.runningStands.get(e.getClickedInventory().getLocation().getBlock()) != null){
            GlobalVars.runningStands.get(e.getClickedInventory().getLocation().getBlock()).cancel();
        }
    }

    @EventHandler
    public void onBrewerBreak(BlockBreakEvent e){
        if(GlobalVars.runningStands.keySet().contains(e.getBlock())){
            GlobalVars.runningStands.get(e.getBlock()).cancel();
        }
    }

    private boolean checkStructure(Block b) {
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

        if (checksPassed == 10) {
            return true;
        }
        return false;
    }

    private int checkForBlock(Location l, Material m) {
        if (l.getBlock().getType().equals(m)) {
            return 1;
        } else {
            Particle.DustOptions dust = new Particle.DustOptions(
                    Color.fromRGB(255, 0, 0), 1);
            Double expansion = 0.25;
            int count = 35;
            if (!l.getBlock().getType().equals(Material.AIR)) {
                expansion = 0.4;
                count = 50;
            }
            l.add(0.5, 0.5, 0.5);
            l.getWorld().spawnParticle(Particle.REDSTONE, l, count, expansion, expansion, expansion, 0, dust);
            l.subtract(0, 0.85, 0);
            ArmorStand a = (ArmorStand) l.getWorld().spawn(l, ArmorStand.class, new Consumer<ArmorStand>() {
                @Override
                public void accept(ArmorStand as) {
                    as.setSmall(true);
                    as.setVisible(false);
                    as.setGravity(false);
                    as.getEquipment().setHelmet(new ItemStack(m));
                }
            });
            new StandRemover(a).runTaskLater(this.p, 75);
            l.subtract(0.5, -0.35, 0.5);
            return 0;
        }
    }

    public ArrayList<Player> getNearbyPlayers(Location loc) {
        ArrayList<Player> nearby = new ArrayList<Player>();
        double range = 7;
        for (Entity e : loc.getWorld().getNearbyEntities(loc, range, range, range)) {
            if (e instanceof Player) {
                nearby.add((Player) e);
            }
        }
        return nearby;
    }
}

