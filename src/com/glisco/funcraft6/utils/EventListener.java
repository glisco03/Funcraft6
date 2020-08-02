package com.glisco.funcraft6.utils;

import com.glisco.funcraft6.Main;
import com.glisco.funcraft6.items.FuncraftItems;
import com.glisco.funcraft6.items.InventorySerializer;
import com.glisco.funcraft6.items.ItemFactory;
import com.glisco.funcraft6.items.ItemHelper;
import com.glisco.funcraft6.modifiable.Modifiable;
import com.glisco.funcraft6.modifiable.Modifiables;
import net.minecraft.server.v1_16_R1.PacketPlayOutAnimation;
import org.bukkit.*;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
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
        if (!ItemHelper.checkForItemStack(e.getItem(), FuncraftItems.RECALL_POTION)) return;

        if (e.getPlayer().getBedSpawnLocation() != null)
            e.getPlayer().teleport(e.getPlayer().getBedSpawnLocation());
        else
            e.setCancelled(true);

    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        Player p = (Player) e.getEntity();
        String sneakingEnabled = p.getPersistentDataContainer().get(Main.key("itemsneaking"), PersistentDataType.STRING);
        List<Material> pickupList = new ArrayList<>();
        if (p.hasMetadata("pickuplist")) {
            pickupList = (List<Material>) p.getMetadata("pickuplist").get(0).value();
        }
        if (sneakingEnabled == null) {
            if (p.isSneaking()) return;
        } else if (sneakingEnabled.equalsIgnoreCase("false")) {
            return;
        } else if (sneakingEnabled.equalsIgnoreCase("true")) {
            if (p.isSneaking() || e.getItem().getPersistentDataContainer().get(Main.key("instant_pickup"), PersistentDataType.STRING) != null || pickupList.contains(e.getItem().getItemStack().getType()))
                return;
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onLeafUpdate(BlockPhysicsEvent e) {
        if (!(e.getBlock().getBlockData() instanceof Leaves)) return;

        Leaves leafBlock = (Leaves) e.getBlock().getBlockData();
        if (leafBlock.getDistance() > 5 && !leafBlock.isPersistent()) {
            e.getBlock().breakNaturally();
        }
    }

    @EventHandler
    public void onDoorOpen(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        if (!(e.getClickedBlock().getBlockData() instanceof Door)) return;

        Block oppositeBlock = getOppositeDoor(e.getClickedBlock());
        if (!(oppositeBlock.getBlockData() instanceof Door)) return;

        Door door = (Door) e.getClickedBlock().getBlockData();
        Door opposite = (Door) oppositeBlock.getBlockData();
        if (opposite.getHinge().equals(door.getHinge())) return;

        if (door.isOpen()) {
            opposite.setOpen(false);
        } else {
            opposite.setOpen(true);
        }

        oppositeBlock.setBlockData(opposite);
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

    @EventHandler
    public void onCreeper(EntityExplodeEvent e) {
        if (e.getEntityType().equals(EntityType.CREEPER)) {
            e.blockList().clear();
        }
    }

    @EventHandler
    public void onCoffee(PlayerItemConsumeEvent e) {
        if (!ItemHelper.checkForItemStack(e.getItem(), FuncraftItems.COFFEE)) return;

        e.getPlayer().setStatistic(Statistic.TIME_SINCE_REST, 0);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onXPTomeInteract(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !e.getAction().equals(Action.RIGHT_CLICK_AIR)) return;

        if (!ItemHelper.compareCustomItemID(e.getItem(), "xptome")) return;
        e.setCancelled(true);

        ItemMeta itemMeta = e.getItem().getItemMeta();

        String lore = itemMeta.getLore().get(0);
        int storedXP = Integer.parseInt(lore.split("/")[0].substring(2));
        Player p = e.getPlayer();

        if (p.isSneaking()) {
            if (storedXP < 1395) {
                int freeStorage = 1395 - storedXP;
                int playerXP = ExperienceManager.getTotalExperience(p);

                if (playerXP >= freeStorage) {
                    ExperienceManager.setTotalExperience(p, playerXP - freeStorage);
                    itemMeta.setLore(ItemFactory.createSingleLineLore("§r§7" + (storedXP + freeStorage) + "/1395XP Stored"));
                    if (storedXP + freeStorage > 0) {
                        itemMeta.addEnchant(Main.glowEnchant, 1, false);
                    }
                } else {
                    ExperienceManager.setTotalExperience(p, 0);
                    itemMeta.setLore(ItemFactory.createSingleLineLore("§r§7" + (storedXP + playerXP) + "/1395XP Stored"));
                    if (storedXP + playerXP > 0) {
                        itemMeta.addEnchant(Main.glowEnchant, 1, false);
                    }
                }

                e.getItem().setItemMeta(itemMeta);
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, (float) (Math.random() * 0.2 + 0.9));
            }
        } else {
            ExperienceManager.setTotalExperience(p, ExperienceManager.getTotalExperience(p) + storedXP);
            itemMeta.setLore(ItemFactory.createSingleLineLore("§r§70/1395XP Stored"));
            itemMeta.removeEnchant(Main.glowEnchant);
            if (storedXP != 0) {
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            }
            e.getItem().setItemMeta(itemMeta);
        }
    }

    @EventHandler
    public void onNewPlayer(PlayerSpawnLocationEvent e) {
        if (e.getPlayer().getPersistentDataContainer().has(Main.key("hasPlayedBefore"), PersistentDataType.STRING)) return;

        e.setSpawnLocation(GlobalVars.spawn);

        Bukkit.getScheduler().scheduleSyncDelayedTask(p, () -> {
            e.getPlayer().getPersistentDataContainer().set(Main.key("hasPlayedBefore"), PersistentDataType.STRING, "true");
            e.getPlayer().sendTitle("§bFuncraft §96", "§aWillkommen, " + e.getPlayer().getName(), 20, 100, 20);
        }, 60);

    }

    @EventHandler
    public void onSnort(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_AIR)) return;

        Player p = e.getPlayer();
        if (!p.isSneaking()) return;

        if (!ItemHelper.checkForMaterial(e.getItem(), Material.GUNPOWDER)) return;

        e.getItem().setAmount(e.getItem().getAmount() - 1);
        p.getWorld().playSound(p.getLocation(), "minecraft:fc6.snort", 1, (float) Math.random() + 0.5f);
        p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 150, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 19));

        if (Math.random() > 0.85) {
            e.getPlayer().addScoreboardTag("SNORTER");
            TNTPrimed tnt = (TNTPrimed) e.getPlayer().getWorld().spawnEntity(e.getPlayer().getLocation(), EntityType.PRIMED_TNT);
            tnt.setFuseTicks(0);
            tnt.addScoreboardTag("NO_BLOCK_DAMAGE");
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
            e.getEntity().getWorld().getBlockAt(new Location(e.getEntity().getWorld(), 0, 135, 0)).setType(Material.DRAGON_EGG);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (e.getEntity().getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY)) return;

        e.getDrops().clear();
        Player p = e.getEntity();

        Location barrelLocation = p.getLocation();
        if (barrelLocation.getY() < 0) barrelLocation.setY(0);

        int[] deathLocation = new int[]{barrelLocation.getBlockX(), barrelLocation.getBlockY() + 1, barrelLocation.getBlockZ()};

        p.getPersistentDataContainer().set(Main.key("last_death_location"), PersistentDataType.INTEGER_ARRAY, deathLocation);
        p.getPersistentDataContainer().set(Main.key("last_death_world"), PersistentDataType.STRING, p.getWorld().getName());

        barrelLocation.getBlock().setType(Material.BARREL);
        Barrel barrel = (Barrel) barrelLocation.getBlock().getState();
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
    public void onInventoryRestore(PlayerInteractEvent e) throws IOException {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        if (!e.getClickedBlock().getType().equals(Material.BARREL)) return;

        Barrel barrel = (Barrel) e.getClickedBlock().getState();
        PersistentDataContainer barrelData = barrel.getPersistentDataContainer();
        Player p = e.getPlayer();
        Location l = p.getLocation();
        World w = l.getWorld();

        if (barrel.getCustomName() == null) return;

        if (!barrel.getCustomName().equalsIgnoreCase("§3death_chest")) return;

        e.setCancelled(true);

        UUID chestID = UUID.fromString(barrelData.get(Main.key("graveOwner"), PersistentDataType.STRING));
        UUID holo1 = UUID.fromString(barrelData.get(Main.key("holo1"), PersistentDataType.STRING));
        UUID holo2 = UUID.fromString(barrelData.get(Main.key("holo2"), PersistentDataType.STRING));

        if (!p.getUniqueId().equals(chestID)) {
            p.sendMessage(Main.prefix + "§cThis is not your stuff!");
            return;
        }

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
    public void onBarrelDestruction(BlockBreakEvent e) throws IOException {
        if (!(e.getBlock().getState() instanceof Barrel)) return;

        Barrel barrel = (Barrel) e.getBlock().getState();
        PersistentDataContainer barrelData = barrel.getPersistentDataContainer();
        Player p = e.getPlayer();
        Location l = p.getLocation();
        World w = l.getWorld();

        if (barrel.getCustomName() == null) return;
        if (!barrel.getCustomName().equals("§3death_chest")) return;

        e.setDropItems(false);

        UUID chestID = UUID.fromString(barrelData.get(Main.key("graveOwner"), PersistentDataType.STRING));
        UUID holo1 = UUID.fromString(barrelData.get(Main.key("holo1"), PersistentDataType.STRING));
        UUID holo2 = UUID.fromString(barrelData.get(Main.key("holo2"), PersistentDataType.STRING));

        if (!p.getUniqueId().equals(chestID)) {
            p.sendMessage(Main.prefix + "§cThis is not your stuff!");
            e.setCancelled(true);
            return;
        }

        for (ItemStack i : p.getInventory().getContents()) {
            if (i == null) {
                continue;
            }
            p.getInventory().remove(i);
            Item drop = w.dropItemNaturally(l, i);
        }
        InventorySerializer.restoreFromDataContainer(e.getPlayer().getInventory(), barrelData);

        Bukkit.getEntity(holo1).remove();
        Bukkit.getEntity(holo2).remove();

        w.spawnParticle(Particle.FIREWORKS_SPARK, e.getBlock().getLocation().add(0.5, 0.5, 0.5), 100, 0.5, 0.5, 0.5, 0.05);
        w.playSound(l, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1, 1);

        p.sendMessage(Main.prefix + "§aYour inventory has been restored!");
    }

    @EventHandler
    public void onSnortDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        //TODO test if check for non lethal damage can be avoided
        if (((Player) e.getEntity()).getHealth() - e.getFinalDamage() > 0 && e.getEntity().getScoreboardTags().contains("SNORTER") && e.getDamager().getScoreboardTags().contains("NO_BLOCK_DAMAGE")) {
            e.getEntity().removeScoreboardTag("SNORTER");
        }

    }

    @EventHandler
    public void onSpawnerBreak(BlockBreakEvent e) {
        if (e.getBlock().getType().equals(Material.SPAWNER)) e.setExpToDrop(315);
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
        s.getPersistentDataContainer().set(Main.key("owner"), PersistentDataType.STRING, p.getUniqueId().toString());
        ;
        s.update();
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (!(e.getClickedBlock().getState() instanceof Sign)) return;

        if (!e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR)) return;

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
        List<String> lore = ItemFactory.createSingleLineLore("§r§7[" + signLocation[0] + " " + signLocation[1] + " " + signLocation[2] + "]");

        ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        bookMeta.addPage(text);
        bookMeta.getPersistentDataContainer().set(Main.key("sign"), PersistentDataType.INTEGER_ARRAY, signLocation);
        bookMeta.setDisplayName("§r§aSign Editing Book");
        bookMeta.setLore(lore);
        book.setItemMeta(bookMeta);

        for (ItemStack itemStack : p.getInventory().getContents()) {
            if (!ItemHelper.doItemSanityChecks(itemStack, false, true)) {
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
        if (!e.getNewBookMeta().hasDisplayName()) return;

        if (!e.getNewBookMeta().getDisplayName().equals("§aSign Editing Book")) return;

        e.getPlayer().getInventory().getItemInMainHand().setAmount(0);
        PersistentDataContainer bookData = e.getNewBookMeta().getPersistentDataContainer();

        if (!bookData.has(Main.key("sign"), PersistentDataType.INTEGER_ARRAY)) return;

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
                potionMeta.getPersistentDataContainer().set(Main.key("target_uuid"), PersistentDataType.STRING, damaged.getUniqueId().toString());
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

        if (!ItemHelper.compareCustomItemID(e.getItem(), "unbound_warp_potion")) return;

        OfflinePlayer target = null;
        UUID targetUUID = UUID.fromString(e.getItem().getItemMeta().getPersistentDataContainer().get(Main.key("target_uuid"), PersistentDataType.STRING));
        Bukkit.getOfflinePlayer(targetUUID);

        //TODO Make this UUID based
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

    @EventHandler
    public void onEndLock(PlayerPortalEvent e) {
        if (e.getTo().getWorld().getEnvironment() == World.Environment.THE_END && Main.config.getBoolean("endlock")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Main.prefix + "§4Sorry, but the End is still disabled");
        }
    }

    @EventHandler
    public void onEChest(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_AIR)) return;

        if (!e.getItem().getType().equals(Material.ENDER_CHEST) && !e.getItem().getType().equals(Material.CRAFTING_TABLE)) return;

        if (!e.getPlayer().isSneaking()) return;

        if (e.getItem().getType().equals(Material.CRAFTING_TABLE)) {
            e.getPlayer().openWorkbench(e.getPlayer().getLocation(), true);
        } else {
            e.getPlayer().openInventory(e.getPlayer().getEnderChest());
        }
        e.setCancelled(true);
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
    public void onBackFromAFK(PlayerMoveEvent e) {
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
        if (GlobalVars.freezedPlayers.contains(e.getPlayer())) e.setCancelled(true);

    }

    @EventHandler
    public void onFreezedPlayerInteract(PlayerInteractEvent e) {
        if (GlobalVars.freezedPlayers.contains(e.getPlayer())) e.setCancelled(true);

    }

    @EventHandler
    public void onFreezedPlayerBreak(BlockBreakEvent e) {
        if (GlobalVars.freezedPlayers.contains(e.getPlayer())) e.setCancelled(true);

    }

    @EventHandler
    public void onFreezedPlayerBuild(BlockPlaceEvent e) {
        if (GlobalVars.freezedPlayers.contains(e.getPlayer())) e.setCancelled(true);

    }

    @EventHandler
    public void onSpectatorViolation(PlayerMoveEvent e) {
        if (!GlobalVars.specators.containsKey(e.getPlayer())) return;

        if (!e.getTo().getWorld().getName().equalsIgnoreCase(GlobalVars.specators.get(e.getPlayer()).getWorld().getName())) {
            e.getPlayer().setGameMode(GameMode.SURVIVAL);
            e.getPlayer().teleport(GlobalVars.specators.get(e.getPlayer()));
            GlobalVars.specators.remove(e.getPlayer());
        }

        if (e.getTo().distance(GlobalVars.specators.get(e.getPlayer())) > 120) {
            e.setCancelled(true);
            return;
        }

        if (!e.getTo().getBlock().isPassable()) {
            e.setCancelled(true);
            return;
        }

        if (!e.getPlayer().getEyeLocation().getBlock().isPassable()) {
            if (!e.getPlayer().hasPotionEffect(PotionEffectType.BLINDNESS)) {
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 19999980, 0, true));
                e.getPlayer().addScoreboardTag("SPECTATE_BLIND");
            }
        } else {
            if (e.getPlayer().getScoreboardTags().contains("SPECTATE_BLIND")) {
                e.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
                e.getPlayer().removeScoreboardTag("SPECTATE_BLIND");
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
        if (!e.getBed().getWorld().getName().equalsIgnoreCase("world")) return;

        if (e.getBedEnterResult().equals(PlayerBedEnterEvent.BedEnterResult.OK)) {
            GlobalVars.inBed.put(e.getPlayer(), 0);
        }

        PersistentDataContainer playerData = e.getPlayer().getPersistentDataContainer();
        if (playerData.has(Main.key("spawnpoint"), PersistentDataType.INTEGER_ARRAY)) {
            int[] spawnpoint = playerData.get(Main.key("spawnpoint"), PersistentDataType.INTEGER_ARRAY);
            Location spawn = new Location(e.getBed().getWorld(), spawnpoint[0], spawnpoint[1], spawnpoint[2]);
            e.getPlayer().setBedSpawnLocation(spawn);
        }
    }

    @EventHandler
    public void onSpawnpointChange(PlayerInteractEvent e) {
        //TODO Make this respect Respawn Anchors
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        if (!(e.getClickedBlock().getBlockData() instanceof Bed)) return;

        if (e.getHand().equals(EquipmentSlot.OFF_HAND)) return;

        if (e.getPlayer().isSneaking()) {
            if (e.getPlayer().getWorld().getName().equalsIgnoreCase("world")) {
                e.getPlayer().sendMessage(Main.prefix + "§aRespawn point changed!");
                PersistentDataContainer playerData = e.getPlayer().getPersistentDataContainer();
                int[] bedCoordinates = new int[]{e.getClickedBlock().getX(), e.getClickedBlock().getY(), e.getClickedBlock().getZ()};
                playerData.set(Main.key("spawnpoint"), PersistentDataType.INTEGER_ARRAY, bedCoordinates);
            }
        } else if (e.getPlayer().getBedSpawnLocation() == null) {
            e.getPlayer().sendMessage(Main.prefix + "§aRespawn point set!");
            PersistentDataContainer playerData = e.getPlayer().getPersistentDataContainer();
            int[] bedCoordinates = new int[]{e.getClickedBlock().getX(), e.getClickedBlock().getY(), e.getClickedBlock().getZ()};
            playerData.set(Main.key("spawnpoint"), PersistentDataType.INTEGER_ARRAY, bedCoordinates);
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
    public void onMiningPortal(PlayerPortalEvent e) {
        if (e.getFrom().getWorld().getName().equalsIgnoreCase("mining")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onArmorstandSpawn(EntitySpawnEvent e) {
        if (e.getEntityType().equals(EntityType.ARMOR_STAND)) {
            ArmorStand a = (ArmorStand) e.getEntity();
            a.setArms(true);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Location l = e.getTo();
        World w = l.getWorld();
        w.spawnParticle(Particle.PORTAL, e.getFrom(), 1000, 0.25, 1, 0.25, 0.5);
        w.spawnParticle(Particle.REVERSE_PORTAL, e.getTo(), 1000, 0.25, 1, 0.25, 0.025);
        w.playSound(l, Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 1);
    }

    @EventHandler
    public void onExcavatorPrepare(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;

        if (!ItemHelper.compareCustomItemID(e.getItem(), "excavator_pickaxe")) ;

        MetadataValue blockFace = new FixedMetadataValue(this.p, e.getBlockFace());
        e.getPlayer().setMetadata("lastBlockFace", blockFace);
    }

    @EventHandler
    public void onExcavatorPickaxe(BlockBreakEvent e) {
        if (!ItemHelper.compareCustomItemID(e.getPlayer().getInventory().getItemInMainHand(), "excavator_pickaxe")) return;

        MetadataValue blockFace = e.getPlayer().getMetadata("lastBlockFace").get(0);
        if (blockFace == null) return;

        if (!e.isDropItems()) return;

        for (Block b : getSurroundingBlocks(e.getBlock(), (BlockFace) blockFace.value())) {
            b.breakNaturally();
        }
    }

    @EventHandler
    public void onSmith(InventoryClickEvent e) {
        if (!e.getInventory().getType().equals(InventoryType.SMITHING)) return;

        Bukkit.getScheduler().scheduleSyncDelayedTask(this.p, () -> {
            SmithingInventory inv = (SmithingInventory) e.getInventory();

            ItemStack tool = inv.getItem(0);
            ItemStack modifier = inv.getItem(1);

            if (tool == null) return;
            if (tool.getType().getMaxDurability() < 1) return;

            if (modifier == null) return;

            if (modifier.equals(FuncraftItems.HARDENING_CRYSTAL)) {
                ItemStack result = tool.clone();

                ItemMeta resultMeta = result.getItemMeta();
                resultMeta.setUnbreakable(true);
                ((Damageable) resultMeta).setDamage(0);
                result.setItemMeta(resultMeta);

                inv.setItem(2, result);
            } else if (modifier.getType().equals(Material.NETHER_STAR)) {
                Modifiable output = new Modifiable(tool);
                output.addModifiers(2);

                inv.setItem(2, output.getAttachedItem());
            } else {
                ItemStack result = Modifiables.addModifier(new Modifiable(tool), modifier);
                if (result != null) {
                    inv.setItem(2, result);
                }
            }
        }, 1);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onSmithingResult(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;

        if (e.getClickedInventory().getType() != InventoryType.SMITHING) return;

        if (e.getSlot() != 2) return;

        SmithingInventory inv = (SmithingInventory) e.getClickedInventory();
        if (e.getCurrentItem() != null) {
            ItemStack current = e.getCurrentItem().clone();
            e.setCursor(current);
            e.setCurrentItem(new ItemStack(Material.AIR));
            inv.setItem(0, new ItemStack(Material.AIR));
            inv.getItem(1).setAmount(inv.getItem(1).getAmount() - 1);
        }
    }

    @EventHandler
    public void onDragonEyeCreation(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        if (e.getHand().equals(EquipmentSlot.OFF_HAND)) return;

        if (!e.getClickedBlock().getType().equals(Material.DRAGON_EGG)) return;

        if (!(e.getPlayer().getWorld().getBlockAt(e.getClickedBlock().getLocation().add(0, -1, 0)).getBlockData() instanceof RespawnAnchor)) return;

        if (!e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.NETHER_STAR)) return;

        e.getClickedBlock().setType(Material.AIR);

        e.getPlayer().getInventory().getItemInMainHand().setAmount(e.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
        e.getPlayer().getInventory().addItem(ItemFactory.createTeleportEye(e.getClickedBlock().getLocation(), e.getPlayer()));

        e.setCancelled(true);
    }

    @EventHandler
    public void onDragonEyeUse(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_AIR) && !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        if (e.getHand().equals(EquipmentSlot.OFF_HAND)) return;

        if (!ItemHelper.compareCustomItemID(e.getItem(), "dragoneye")) return;

        e.setCancelled(true);

        if (e.getPlayer().isSneaking()) {
            if (ExperienceManager.getTotalExperience(e.getPlayer()) >= 1395) {
                ExperienceManager.setTotalExperience(e.getPlayer(), ExperienceManager.getTotalExperience(e.getPlayer()) - 1395);
            } else {
                e.getPlayer().sendMessage(Main.prefix + "§cYou don't have enough XP to do this!");
                return;
            }
        } else {
            if (ExperienceManager.getTotalExperience(e.getPlayer()) >= 55) {
                ExperienceManager.setTotalExperience(e.getPlayer(), ExperienceManager.getTotalExperience(e.getPlayer()) - 55);
            } else {
                e.getPlayer().sendMessage(Main.prefix + "§cYou don't have enough XP to do this!");
                return;
            }
        }

        if (GlobalVars.runningTeleports.containsKey(e.getPlayer())) {
            GlobalVars.runningTeleports.get(e.getPlayer()).cancel();
            GlobalVars.runningTeleports.remove(e.getPlayer());
        }

        e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.PLAYERS, 1, 1);

        DragonsEyeExecutor executor = new DragonsEyeExecutor(e.getPlayer(), e.getPlayer().isSneaking(), e.getItem());
        GlobalVars.runningTeleports.put(e.getPlayer(), executor);
    }

    @EventHandler
    public void onTeleportMove(PlayerMoveEvent e) {
        if (!GlobalVars.runningTeleports.containsKey(e.getPlayer())) return;

        DragonsEyeExecutor executor = GlobalVars.runningTeleports.get(e.getPlayer());
        if (executor.isSneaking) {
            if (e.getTo().distance(executor.center) > 3) {
                e.setCancelled(true);
            }
        } else {
            if (e.getTo().distance(executor.center) > 0.2) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSpawnRespawn(PlayerRespawnEvent e) {
        if (!e.getPlayer().getWorld().getName().equals("spawn")) return;

        e.setRespawnLocation(GlobalVars.spawn);
    }

    @EventHandler
    public void onSpawnBarrel(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        if (!(e.getClickedBlock().getState() instanceof Barrel)) return;
        if (!e.getPlayer().getWorld().getName().equals("spawn")) return;

        int[] loc = e.getPlayer().getPersistentDataContainer().get(Main.key("coordinates"), PersistentDataType.INTEGER_ARRAY);
        String worldName = e.getPlayer().getPersistentDataContainer().get(Main.key("world"), PersistentDataType.STRING);

        Location lastLocation = new Location(Bukkit.getWorld(worldName), loc[0], loc[1], loc[2]);
        if (!worldName.equals("world")) {
            e.getPlayer().sendMessage(Main.prefix + "§cYou are too far from your bed to do this!");
            e.setCancelled(true);
            return;
        }
        if (lastLocation.distance(e.getPlayer().getBedSpawnLocation()) > 100) {
            e.getPlayer().sendMessage(Main.prefix + "§cYou are too far from your bed to do this!");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemEntityClick(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !e.getAction().equals(Action.RIGHT_CLICK_AIR)) return;
        if (e.getHand().equals(EquipmentSlot.OFF_HAND)) return;

        Player p = e.getPlayer();
        Location origin = p.getEyeLocation().add(p.getEyeLocation().getDirection());
        RayTraceResult result = p.getWorld().rayTraceEntities(origin, origin.getDirection(), 5);
        if (result == null) {
            return;
        }
        if (result.getHitEntity().getType().equals(EntityType.DROPPED_ITEM)) {
            result.getHitEntity().teleport(p);
            ((Item) result.getHitEntity()).setPickupDelay(0);
            result.getHitEntity().getPersistentDataContainer().set(Main.key("instant_pickup"), PersistentDataType.STRING, "true");
            PacketPlayOutAnimation packet = new PacketPlayOutAnimation(((CraftPlayer) p).getHandle(), 0);
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onRecallTotemClick(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_AIR) && !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!ItemHelper.compareCustomItemID(e.getItem(), "recall_totem")) return;

        e.setCancelled(true);

        int[] loc = e.getPlayer().getPersistentDataContainer().get(Main.key("last_death_location"), PersistentDataType.INTEGER_ARRAY);
        String worldName = e.getPlayer().getPersistentDataContainer().get(Main.key("last_death_world"), PersistentDataType.STRING);
        if (loc != null) {
            e.getPlayer().getPersistentDataContainer().remove(Main.key("last_death_location"));
            e.getPlayer().getPersistentDataContainer().remove(Main.key("last_death_world"));
            Location target = new Location(Bukkit.getWorld(worldName), loc[0], loc[1], loc[2]);
            e.getPlayer().damage(200);
            Bukkit.getScheduler().runTaskLater(this.p, () -> {
                e.getPlayer().teleport(target);
                e.getPlayer().getInventory().setItemInMainHand(null);
            }, 40);
        } else {
            e.getPlayer().sendMessage(Main.prefix + "§cYou've already use a totem for this life!");
        }

    }

    @EventHandler
    public void onPlayerCompass(PlayerCommandPreprocessEvent e) {
        if (!e.getMessage().startsWith("/get_player_compass")) return;

        String[] message = e.getMessage().split(":");

        String playerName = message[5];
        String worldName = message[4];
        int[] coords = new int[]{Integer.parseInt(message[1]), Integer.parseInt(message[2]), Integer.parseInt(message[3])};

        Location compassTarget = new Location(Bukkit.getWorld(worldName), coords[0], coords[1], coords[2]);

        for (ItemStack i : e.getPlayer().getInventory()) {
            if (ItemHelper.compareCustomItemID(i, "player_compass")) {
                e.getPlayer().getInventory().remove(i);
            }
        }

        e.getPlayer().getInventory().addItem(ItemFactory.createPlayerCompass(compassTarget, playerName));


        e.setCancelled(true);
    }

    @EventHandler
    public void onParrotDismount(CreatureSpawnEvent e) {
        if (!e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SHOULDER_ENTITY)) return;
        if (!e.getEntity().getType().equals(EntityType.PARROT)) return;

        Parrot parrot = (Parrot) e.getEntity();

        if (Bukkit.getPlayer(parrot.getOwner().getUniqueId()).isSneaking()) return;

        e.setCancelled(true);
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

    private Location getBedFromSpawn(Location spawn) {
        if (spawn.getBlock().getBlockData() instanceof Bed) {
            return spawn;
        }
        if (spawn.clone().add(1, 0, 0).getBlock().getBlockData() instanceof Bed) {
            return spawn.clone().add(1, 0, 0);
        }
        if (spawn.clone().add(-1, 0, 0).getBlock().getBlockData() instanceof Bed) {
            return spawn.clone().add(-1, 0, 0);
        }
        if (spawn.clone().add(0, 0, 1).getBlock().getBlockData() instanceof Bed) {
            return spawn.clone().add(0, 0, 1);
        }
        if (spawn.clone().add(0, 0, -1).getBlock().getBlockData() instanceof Bed) {
            return spawn.clone().add(0, 0, -1);
        }
        if (spawn.clone().add(1, 0, -1).getBlock().getBlockData() instanceof Bed) {
            return spawn.clone().add(1, 0, -1);
        }
        if (spawn.clone().add(1, 0, 1).getBlock().getBlockData() instanceof Bed) {
            return spawn.clone().add(1, 0, 1);
        }
        if (spawn.clone().add(-1, 0, 1).getBlock().getBlockData() instanceof Bed) {
            return spawn.clone().add(-1, 0, 1);
        }
        if (spawn.clone().add(-1, 0, -1).getBlock().getBlockData() instanceof Bed) {
            return spawn.clone().add(-1, 0, -1);
        }
        return null;
    }

    private List<Block> getSurroundingBlocks(Block b, BlockFace f) {
        List<Block> blocks = new ArrayList<>();
        World w = b.getWorld();
        if (f.equals(BlockFace.UP) || f.equals(BlockFace.DOWN)) {
            blocks.add(w.getBlockAt(b.getLocation().add(1, 0, 0)));
            blocks.add(w.getBlockAt(b.getLocation().add(1, 0, 1)));
            blocks.add(w.getBlockAt(b.getLocation().add(0, 0, 1)));
            blocks.add(w.getBlockAt(b.getLocation().add(-1, 0, 0)));
            blocks.add(w.getBlockAt(b.getLocation().add(-1, 0, -1)));
            blocks.add(w.getBlockAt(b.getLocation().add(0, 0, -1)));
            blocks.add(w.getBlockAt(b.getLocation().add(1, 0, -1)));
            blocks.add(w.getBlockAt(b.getLocation().add(-1, 0, 1)));
            return blocks;
        } else if (f.equals(BlockFace.NORTH) || f.equals(BlockFace.SOUTH)) {
            blocks.add(w.getBlockAt(b.getLocation().add(0, 1, 0)));
            blocks.add(w.getBlockAt(b.getLocation().add(0, -1, 0)));
            blocks.add(w.getBlockAt(b.getLocation().add(1, 0, 0)));
            blocks.add(w.getBlockAt(b.getLocation().add(-1, 0, 0)));
            blocks.add(w.getBlockAt(b.getLocation().add(1, 1, 0)));
            blocks.add(w.getBlockAt(b.getLocation().add(1, -1, 0)));
            blocks.add(w.getBlockAt(b.getLocation().add(-1, 1, 0)));
            blocks.add(w.getBlockAt(b.getLocation().add(-1, -1, 0)));
            return blocks;
        } else if (f.equals(BlockFace.WEST) || f.equals(BlockFace.EAST)) {
            blocks.add(w.getBlockAt(b.getLocation().add(0, 1, 0)));
            blocks.add(w.getBlockAt(b.getLocation().add(0, -1, 0)));
            blocks.add(w.getBlockAt(b.getLocation().add(0, 0, 1)));
            blocks.add(w.getBlockAt(b.getLocation().add(0, 0, -1)));
            blocks.add(w.getBlockAt(b.getLocation().add(0, 1, 1)));
            blocks.add(w.getBlockAt(b.getLocation().add(0, -1, 1)));
            blocks.add(w.getBlockAt(b.getLocation().add(0, 1, -1)));
            blocks.add(w.getBlockAt(b.getLocation().add(0, -1, -1)));
            return blocks;
        }
        return null;
    }
}

