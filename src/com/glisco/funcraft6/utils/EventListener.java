package com.glisco.funcraft6.utils;

import com.glisco.funcraft6.Main;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventListener implements Listener {

    final JavaPlugin p;

    public EventListener(JavaPlugin plugin) {
        p = plugin;
    }

    @EventHandler
    public void onRecall(PlayerItemConsumeEvent e) {
        if (e.getItem().equals(FuncraftItems.RECALL_POTION)) {
            if (e.getPlayer().getBedSpawnLocation() != null) {
                e.getPlayer().teleport(e.getPlayer().getBedSpawnLocation());
            } else {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCoffee(PlayerItemConsumeEvent e) {
        if (e.getItem().equals(FuncraftItems.COFFEE)) {
            e.getPlayer().setStatistic(Statistic.TIME_SINCE_REST, 0);
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
            e.getPlayer().removeScoreboardTag("OVERDOSE");
            Bukkit.getScheduler().scheduleSyncDelayedTask(p, () -> {
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 2400, 0));
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, 2400, 3));
            }, 60);
        }
    }

    @EventHandler
    public void onSignPlace(SignChangeEvent e) {
        Sign s = (Sign) e.getBlock().getState();
        Player p = e.getPlayer();
        PersistentDataContainer signContainer = s.getPersistentDataContainer();
        signContainer.set(Main.key("owner"), PersistentDataType.STRING, p.getUniqueId().toString());
        s.update();
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!(e.getClickedBlock().getState() instanceof Sign)) {
            return;
        }
        if (!e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
            return;
        }

        Block b = e.getClickedBlock();
        Player p = e.getPlayer();
        Sign s = (Sign) b.getState();
        PersistentDataContainer signData = s.getPersistentDataContainer();

        boolean confirmed = false;
        if (signData.has(Main.key("public"), PersistentDataType.STRING)) {
            if (signData.get(Main.key("public"), PersistentDataType.STRING).equalsIgnoreCase("true")) {
                confirmed = true;
            }
        }
        if (!signData.get(Main.key("owner"), PersistentDataType.STRING).equals(p.getUniqueId().toString()) && !confirmed) {
            p.sendMessage(Main.prefix + "§cThis is not your sign, idiot!");
            return;
        }

        String text = s.getLine(0);
        text = text + "\n";
        text = text + s.getLine(1);
        text = text + "\n";
        text = text + s.getLine(2);
        text = text + "\n";
        text = text + s.getLine(3);

        int[] signLocation = new int[]{s.getX(), s.getY(), s.getZ()};
        List<String> lore = ItemHelper.createSingleLineLore("§r§7[" + signLocation[0] + " " + signLocation[1] + " " + signLocation[2] + "]");

        ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        bookMeta.addPage(text);
        bookMeta.getPersistentDataContainer().set(Main.key("sign"), PersistentDataType.INTEGER_ARRAY, signLocation);
        bookMeta.setDisplayName("§r§aSign Editing Book");
        bookMeta.setLore(lore);
        book.setItemMeta(bookMeta);

        for (ItemStack itemStack : p.getInventory().getContents()) {
            if (itemStack == null) {
                continue;
            }
            if (!itemStack.getItemMeta().hasLore()) {
                continue;
            }
            if (itemStack.getItemMeta().getLore().get(0).equalsIgnoreCase(lore.get(0).substring(2))) {
                itemStack.setAmount(0);
            }
        }

        p.getInventory().setItemInMainHand(book);
    }

    @EventHandler
    public void onSignEdit(PlayerEditBookEvent e) {
        if (!e.getNewBookMeta().hasDisplayName()) {
            return;
        }
        if (!e.getNewBookMeta().getDisplayName().equals("§aSign Editing Book")) {
            return;
        }
        e.getPlayer().getInventory().getItemInMainHand().setAmount(0);
        PersistentDataContainer bookData = e.getNewBookMeta().getPersistentDataContainer();

        if (bookData.has(Main.key("sign"), PersistentDataType.INTEGER_ARRAY)) {
            BookMeta bookMeta = e.getNewBookMeta();

            int[] signCoordinates = bookData.get(Main.key("sign"), PersistentDataType.INTEGER_ARRAY);
            Location signLocation = new Location(e.getPlayer().getWorld(), signCoordinates[0], signCoordinates[1], signCoordinates[2]);

            if(e.getPlayer().getWorld().getBlockAt(signLocation).getState() instanceof Sign){
                Sign sign = (Sign) e.getPlayer().getWorld().getBlockAt(signLocation).getState();

                List<String> bookLines = new ArrayList<>(Arrays.asList(bookMeta.getPage(1).split("\n")));
                while (bookLines.size() < 4) {
                    bookLines.add("");
                }

                sign.setLine(0, bookLines.get(0));
                sign.setLine(1, bookLines.get(1));
                sign.setLine(2, bookLines.get(2));
                sign.setLine(3, bookLines.get(3));
                sign.update();
            } else {
                e.getPlayer().sendMessage(Main.prefix + "§cThis sign does not exist anymore");
            }
        }
    }

    @EventHandler
    public void onWarpPotionCreation(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player damager = (Player) e.getDamager();
            Player damaged = (Player) e.getEntity();
            if (damager.getInventory().getItemInMainHand().equals(FuncraftItems.UNBOUND_WARP_POTION)) {
                PotionMeta potionMeta = (PotionMeta) damager.getInventory().getItemInMainHand().getItemMeta();
                potionMeta.setColor(Color.RED);
                potionMeta.setDisplayName("§4Bound Warp Potion");
                List<String> lore = new ArrayList<>();
                lore.add("§r§7" + damaged.getDisplayName());
                potionMeta.setLore(lore);
                damager.getInventory().getItemInMainHand().setItemMeta(potionMeta);
            }
        }
    }

    @EventHandler
    public void onWarpPotion(PlayerItemConsumeEvent e) {
        if (e.getItem().equals(FuncraftItems.UNBOUND_WARP_POTION)) {
            e.getPlayer().sendMessage("§7§oWhat did you think that would do?");
            return;
        }
        if (!e.getItem().getItemMeta().hasDisplayName()) {
            return;
        }
        if (e.getItem().getItemMeta().getDisplayName().equals("§4Bound Warp Potion")) {
            OfflinePlayer target = null;
            for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                if (p.getName().equalsIgnoreCase(e.getItem().getItemMeta().getLore().get(0).substring(2))) {
                    target = p;
                    break;
                }
            }
            if (target == null) {
                e.setCancelled(true);
                return;
            }
            if (target.isOnline()) {
                if (((Player) target).hasPotionEffect(PotionEffectType.LUCK)) {
                    e.getPlayer().teleport((Player) target);
                } else {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage("§7§oYour target does not like you");
                }
            } else {
                e.setCancelled(true);
                e.getPlayer().sendMessage("§7§oYour target is not online");
            }
        }
    }

    @EventHandler
    public void onEndLock(PlayerPortalEvent e) {
        if (e.getTo().getWorld().getEnvironment() == World.Environment.THE_END && Main.config.getBoolean("endlock")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Main.prefix + "§4Sorry, but the End is still disabled");
        }
    }

    @EventHandler
    public void onEChest(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            return;
        }
        if (!e.getItem().getType().equals(Material.ENDER_CHEST) && !e.getItem().getType().equals(Material.CRAFTING_TABLE)) {
            return;
        }
        if (!e.getPlayer().isSneaking()) {
            return;
        }
        if (e.getItem().getType().equals(Material.CRAFTING_TABLE)) {
            e.getPlayer().openWorkbench(e.getPlayer().getLocation(), true);
        } else {
            e.getPlayer().openInventory(e.getPlayer().getEnderChest());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Location l = e.getEntity().getLocation();
        e.getEntity().sendMessage("");
        e.getEntity().sendMessage(Main.prefix + "§4U ded boi. Have some coordinates: §7" + Math.round(l.getX()) + " " + Math.round(l.getY()) + " " + Math.round(l.getZ()) + " (" + l.getWorld().getName() + ")");
        e.getEntity().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§4" + e.getEntity().getDisplayName() + " died at: §7" + Math.round(l.getX()) + " " + Math.round(l.getY()) + " " + Math.round(l.getZ()) + " (" + l.getWorld().getName() + ")");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getPlayer().getDisplayName().contains("AFK") && e.getFrom().getY() < e.getTo().getY()) {
            e.getPlayer().sendMessage(Main.prefix + "§2You are no longer marked as AFK!");
            e.getPlayer().setDisplayName(e.getPlayer().getName());
            e.getPlayer().setPlayerListName(e.getPlayer().getName());
        } else if (e.getPlayer().getDisplayName().contains("Fishing") && e.getFrom().getY() < e.getTo().getY()) {
            e.getPlayer().sendMessage(Main.prefix + "§2You are no longer marked as fishing!");
            e.getPlayer().setDisplayName(e.getPlayer().getName());
            e.getPlayer().setPlayerListName(e.getPlayer().getName());
        }
    }

    @EventHandler
    public void onFreezedPlayerMove(PlayerMoveEvent e) {
        if (GlobalVars.freezedPlayers.contains(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFreezedPlayerInteract(PlayerInteractEvent e) {
        if (GlobalVars.freezedPlayers.contains(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFreezedPlayerBreak(BlockBreakEvent e) {
        if (GlobalVars.freezedPlayers.contains(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFreezedPlayerBuild(BlockPlaceEvent e) {
        if (GlobalVars.freezedPlayers.contains(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpectatorViolation(PlayerMoveEvent e) {
        if (GlobalVars.specators.containsKey(e.getPlayer())) {
            if (e.getTo().distance(GlobalVars.specators.get(e.getPlayer())) > 80) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSpectatorLeave(PlayerQuitEvent e) {
        if (GlobalVars.specators.containsKey(e.getPlayer())) {
            e.getPlayer().setGameMode(GameMode.SURVIVAL);
            GlobalVars.specators.remove(e.getPlayer());
        }
    }

    @EventHandler
    public static void onJoin(PlayerJoinEvent e) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "recipe give " + e.getPlayer().getName() + " *");
    }

    @EventHandler
    public static void onSleep(PlayerBedEnterEvent e) {
        if (!e.getBed().getWorld().getName().equalsIgnoreCase("world")) {
            return;
        }
        if (e.getBedEnterResult().equals(PlayerBedEnterEvent.BedEnterResult.OK)) {
            GlobalVars.inBed.put(e.getPlayer(), 0);
        }
    }

    @EventHandler
    public static void onUnSleep(PlayerBedLeaveEvent e) {
        if (!e.getBed().getWorld().getName().equalsIgnoreCase("world")) {
            return;
        }
        GlobalVars.inBed.remove(e.getPlayer());
        if (GlobalVars.sleeping.contains(e.getPlayer())) {
            GlobalVars.sleeping.remove(e.getPlayer());
            if (e.getBed().getWorld().getTime() > 10) {
                Bukkit.broadcastMessage(Main.prefix + "§6" + e.getPlayer().getName() + " left their bed");
            }
        }
    }

    @EventHandler
    public void onMiningNether(PlayerPortalEvent e) {
        if (e.getFrom().getWorld().getName().equalsIgnoreCase("mining")) {
            e.setCancelled(true);
        }
    }
}

