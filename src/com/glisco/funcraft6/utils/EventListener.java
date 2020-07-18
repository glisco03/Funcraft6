package com.glisco.funcraft6.utils;

import com.glisco.funcraft6.Main;
import org.bukkit.*;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
    public void onItemPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getEntity();
        if (p.isSneaking()) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onLeafUpdate(BlockPhysicsEvent e) {
        if (e.getBlock().getBlockData() instanceof Leaves) {
            Leaves leafBlock = (Leaves) e.getBlock().getBlockData();
            if (leafBlock.getDistance() > 5 && !leafBlock.isPersistent()) {
                e.getBlock().breakNaturally();
            }
        }
    }

    @EventHandler
    public void onDoorOpen(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (!(e.getClickedBlock().getBlockData() instanceof Door)) {
            return;
        }
        Block oppositeBlock = getOppositeDoor(e.getClickedBlock());
        if (!(oppositeBlock.getBlockData() instanceof Door)) {
            return;
        }
        Door door = (Door) e.getClickedBlock().getBlockData();
        Door opposite = (Door) oppositeBlock.getBlockData();
        if (opposite.getHinge().equals(door.getHinge())) {
            return;
        }
        if (door.isOpen()) {
            opposite.setOpen(false);
        } else {
            opposite.setOpen(true);
        }
        oppositeBlock.setBlockData(opposite);
    }

    @EventHandler
    public void onCreeper(EntityExplodeEvent e) {
        if (e.getEntityType().equals(EntityType.CREEPER)) {
            e.blockList().clear();
        }
    }

    @EventHandler
    public void onCoffee(PlayerItemConsumeEvent e) {
        if (e.getItem().equals(FuncraftItems.COFFEE)) {
            e.getPlayer().setStatistic(Statistic.TIME_SINCE_REST, 0);
        }
    }

    @EventHandler
    public void onXPTomeInteract(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            return;
        }
        if (e.getItem() == null) {
            return;
        }
        ItemMeta itemMeta = e.getItem().getItemMeta();
        if (!itemMeta.hasDisplayName()) {
            return;
        }
        if (!itemMeta.getDisplayName().equalsIgnoreCase("§eXP Tome")) {
            return;
        }
        String lore = itemMeta.getLore().get(0);
        int storedXP = Integer.parseInt(lore.split("/")[0].substring(2));
        Player p = e.getPlayer();
        if (p.isSneaking()) {
            if (storedXP < 1395) {
                int freeStorage = 1395 - storedXP;
                int playerXP = ExperienceManager.getTotalExperience(p);
                if (playerXP >= freeStorage) {
                    ExperienceManager.setTotalExperience(p, playerXP - freeStorage);
                    itemMeta.setLore(ItemHelper.createSingleLineLore("§r§7" + (storedXP + freeStorage) + "/1395XP Stored"));
                    e.getItem().setItemMeta(itemMeta);
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, (float) (Math.random() * 0.2 + 0.9));
                } else {
                    ExperienceManager.setTotalExperience(p, 0);
                    itemMeta.setLore(ItemHelper.createSingleLineLore("§r§7" + (storedXP + playerXP) + "/1395XP Stored"));
                    e.getItem().setItemMeta(itemMeta);
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, (float) (Math.random() * 0.2 + 0.9));
                }
            }
        } else {
            ExperienceManager.setTotalExperience(p, ExperienceManager.getTotalExperience(p) + storedXP);
            itemMeta.setLore(ItemHelper.createSingleLineLore("§r§f0/1395XP Stored"));
            if (storedXP != 0) {
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            }
            e.getItem().setItemMeta(itemMeta);
        }
    }

    @EventHandler
    public void onNewPlayer(PlayerSpawnLocationEvent e) {
        if (!e.getPlayer().getPersistentDataContainer().has(Main.key("hasPlayedBefore"), PersistentDataType.STRING)) {
            e.setSpawnLocation(GlobalVars.spawn);
            Bukkit.getScheduler().scheduleSyncDelayedTask(p, () -> {
                e.getPlayer().getPersistentDataContainer().set(Main.key("hasPlayedBefore"), PersistentDataType.STRING, "true");
                e.getPlayer().sendTitle("§bFuncraft §96", "§aWillkommen, " + e.getPlayer().getName(), 20, 100, 20);
            }, 60);
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
    public void onDragonDeath(EntityDeathEvent e) {
        if (e.getEntity().getType().equals(EntityType.ENDER_DRAGON)) {
            e.getEntity().getWorld().getBlockAt(new Location(e.getEntity().getWorld(), 0, 65, 0)).setType(Material.DRAGON_EGG);
        }
    }

    @EventHandler
    public void onChestClick(PlayerInteractEvent e) throws IOException {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (!e.getClickedBlock().getType().equals(Material.BARREL)) {
            return;
        }
        Barrel barrel = (Barrel) e.getClickedBlock().getState();
        PersistentDataContainer barrelData = barrel.getPersistentDataContainer();
        Player p = e.getPlayer();
        Location l = p.getLocation();
        World w = l.getWorld();
        if (barrel.getCustomName() == null) {
            return;
        }
        if (!barrel.getCustomName().equalsIgnoreCase("§3death_chest")) {
            return;
        }
        UUID chestID = UUID.fromString(barrelData.get(Main.key("graveOwner"), PersistentDataType.STRING));
        UUID holo1 = UUID.fromString(barrelData.get(Main.key("holo1"), PersistentDataType.STRING));
        UUID holo2 = UUID.fromString(barrelData.get(Main.key("holo2"), PersistentDataType.STRING));
        if (!p.getUniqueId().equals(chestID)) {
            p.sendMessage(Main.prefix + "§cThis is not your stuff!");
            e.setCancelled(true);
            return;
        }
        e.setCancelled(true);
        if (p.isSneaking()) {
            for (ItemStack i : p.getInventory().getContents()) {
                if (i == null) {
                    continue;
                }
                p.getInventory().remove(i);
                Item drop = w.dropItemNaturally(l, i);
            }
            InventorySerializer.restoreFromDataContainer(e.getPlayer().getInventory(), barrelData);
            e.getClickedBlock().setType(Material.AIR);
            Bukkit.getEntity(holo1).remove();
            Bukkit.getEntity(holo2).remove();
            w.spawnParticle(Particle.FIREWORKS_SPARK, e.getClickedBlock().getLocation().add(0.5, 0.5, 0.5), 100, 0.5, 0.5, 0.5, 0.05);
            w.playSound(l, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1, 1);
            p.sendMessage(Main.prefix + "§aYour inventory has been restored!");
        } else {
            p.sendMessage(Main.prefix + "§bSneak and Right Click to restore your inventory");
            p.sendMessage(Main.prefix + "§4This will drop your current items!");
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (e.getEntity().getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY)) {
            return;
        }
        e.getDrops().clear();
        Player p = e.getEntity();

        p.getLocation().getBlock().setType(Material.BARREL);
        Barrel barrel = (Barrel) p.getLocation().getBlock().getState();
        Directional directional = (Directional) barrel.getBlockData();
        directional.setFacing(BlockFace.UP);
        barrel.setBlockData(directional);
        barrel.setCustomName("§3death_chest");

        ArmorStand holo1 = spawnHolo(barrel.getLocation().add(0.5, -0.65, 0.5), "§b" + e.getEntity().getName() + "™");
        ArmorStand holo2 = spawnHolo(barrel.getLocation().add(0.5, -0.9, 0.5), "§bSupply Crate");

        PersistentDataContainer barrelData = barrel.getPersistentDataContainer();
        barrelData.set(Main.key("graveOwner"), PersistentDataType.STRING, p.getUniqueId().toString());
        barrelData.set(Main.key("holo1"), PersistentDataType.STRING, holo1.getUniqueId().toString());
        barrelData.set(Main.key("holo2"), PersistentDataType.STRING, holo2.getUniqueId().toString());
        InventorySerializer.serializeIntoDataContainer(p.getInventory(), barrelData);

        barrel.update();

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
    public void onSpawnerBreak(BlockBreakEvent e) {
        if (e.getBlock().getType().equals(Material.SPAWNER)) {
            e.setExpToDrop(315);
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

        if (InsultManager.isInsultDeath(e.getEntity())) {
            e.setDeathMessage(e.getEntity().getName() + " reached the limit");
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
            InsultManager.serveInsult(p, e.getClickedBlock().getLocation());
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

            if (e.getPlayer().getWorld().getBlockAt(signLocation).getState() instanceof Sign) {
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
        } else {
            if (GlobalVars.spawnPointPreservence.containsKey(e.getPlayer())) {
                Location spawn = GlobalVars.spawnPointPreservence.get(e.getPlayer());
                if (spawn != null) {
                    e.getPlayer().setBedSpawnLocation(spawn, true);
                } else {
                    e.getPlayer().sendMessage(Main.prefix + "§aRespawn point set!");
                }
                GlobalVars.spawnPointPreservence.remove(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onSpawnpointChange(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (!(e.getClickedBlock().getBlockData() instanceof Bed)) {
            return;
        }
        if (!e.getPlayer().isSneaking()) {
            GlobalVars.spawnPointPreservence.put(e.getPlayer(), e.getPlayer().getBedSpawnLocation());
        } else {
            e.getPlayer().sendMessage(Main.prefix + "§aRespawn point set!");
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
        if (GlobalVars.spawnPointPreservence.containsKey(e.getPlayer())) {
            Location spawn = GlobalVars.spawnPointPreservence.get(e.getPlayer());
            if (spawn != null) {
                e.getPlayer().setBedSpawnLocation(spawn, true);
            } else {
                e.getPlayer().sendMessage(Main.prefix + "§aRespawn point set!");
            }
            GlobalVars.spawnPointPreservence.remove(e.getPlayer());
        }
    }

    @EventHandler
    public void onMiningNether(PlayerPortalEvent e) {
        if (e.getFrom().getWorld().getName().equalsIgnoreCase("mining")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDoorRedstone(BlockRedstoneEvent e) {
        if (!(e.getBlock().getBlockData() instanceof Door)) {
            return;
        }
        Block oppositeBlock = getOppositeDoor(e.getBlock());
        if (!(oppositeBlock.getBlockData() instanceof Door)) {
            return;
        }
        Door door = (Door) e.getBlock().getBlockData();
        Door opposite = (Door) oppositeBlock.getBlockData();
        if (opposite.getHinge().equals(door.getHinge())) {
            return;
        }
        if (door.isOpen()) {
            if (opposite.isOpen()) {
                opposite.setOpen(false);
            }
        } else {
            if (!opposite.isOpen()) {
                opposite.setOpen(true);
            }
        }
        oppositeBlock.setBlockData(opposite);
    }

    private Block getOppositeDoor(Block doorBlock) {
        Door door = (Door) doorBlock.getBlockData();
        Location targetDoor = doorBlock.getLocation();
        if (door.getFacing().equals(BlockFace.SOUTH)) {
            if (door.getHinge().equals(Door.Hinge.LEFT)) {
                targetDoor.add(-1, 0, 0);
            } else {
                targetDoor.add(1, 0, 0);
            }
        } else if (door.getFacing().equals(BlockFace.NORTH)) {
            if (door.getHinge().equals(Door.Hinge.LEFT)) {
                targetDoor.add(1, 0, 0);
            } else {
                targetDoor.add(-1, 0, 0);
            }
        } else if (door.getFacing().equals(BlockFace.WEST)) {
            if (door.getHinge().equals(Door.Hinge.LEFT)) {
                targetDoor.add(0, 0, -1);
            } else {
                targetDoor.add(0, 0, 1);
            }
        } else {
            if (door.getHinge().equals(Door.Hinge.LEFT)) {
                targetDoor.add(0, 0, 1);
            } else {
                targetDoor.add(0, 0, -1);
            }
        }
        return doorBlock.getWorld().getBlockAt(targetDoor);
    }

    private ArmorStand spawnHolo(Location l, String text) {
        ArmorStand stand = (ArmorStand) l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setCustomNameVisible(true);
        stand.setCustomName(text);
        return stand;
    }
}

