package com.glisco.funcraft6.utils;

import com.glisco.funcraft6.Main;
import com.glisco.funcraft6.items.FuncraftItems;
import com.glisco.funcraft6.items.InventorySerializer;
import com.glisco.funcraft6.items.ItemFactory;
import com.glisco.funcraft6.items.ItemHelper;
import com.glisco.funcraft6.modifiables.Modifiable;
import com.glisco.funcraft6.modifiables.Modifiables;
import net.minecraft.server.v1_16_R2.BlockPosition;
import net.minecraft.server.v1_16_R2.PacketPlayOutAnimation;
import net.minecraft.server.v1_16_R2.PacketPlayOutOpenSignEditor;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
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
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.io.IOException;
import java.util.*;

public class EventListener implements Listener {

    final JavaPlugin p;
    final List<Material> flowers;

    public EventListener(JavaPlugin plugin) {
        p = plugin;
        flowers = new ArrayList<>();
        flowers.add(Material.DANDELION);
        flowers.add(Material.POPPY);
        flowers.add(Material.BLUE_ORCHID);
        flowers.add(Material.ALLIUM);
        flowers.add(Material.AZURE_BLUET);
        flowers.add(Material.RED_TULIP);
        flowers.add(Material.ORANGE_TULIP);
        flowers.add(Material.WHITE_TULIP);
        flowers.add(Material.PINK_TULIP);
        flowers.add(Material.OXEYE_DAISY);
        flowers.add(Material.CORNFLOWER);
        flowers.add(Material.LILY_OF_THE_VALLEY);
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

        opposite.setOpen(!door.isOpen());

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
        if (e.getEntity().getType().equals(EntityType.ENDER_DRAGON) && !Objects.equals(e.getEntity().getCustomName(), "§5§lDragon Queen")) {
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

        if (((Player) e.getEntity()).getHealth() - e.getFinalDamage() > 0 && e.getEntity().getScoreboardTags().contains("SNORTER") && e.getDamager().getScoreboardTags().contains("NO_BLOCK_DAMAGE")) {
            e.getEntity().removeScoreboardTag("SNORTER");
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

        ((CraftPlayer) e.getPlayer()).getHandle().playerConnection.sendPacket(new PacketPlayOutOpenSignEditor(new BlockPosition(s.getX(), s.getY(), s.getZ())));
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
                ItemFactory.setCustomItemID(damager.getInventory().getItemInMainHand(), "bound_warp_potion");
            }
        }
    }

    @EventHandler
    public void onWarpPotion(PlayerItemConsumeEvent e) {
        if (ItemHelper.compareCustomItemID(e.getItem(), "unbound_warp_potion")) {
            e.getPlayer().sendMessage("§7§oWhat did you think that would do?");
            return;
        }

        if (!ItemHelper.compareCustomItemID(e.getItem(), "bound_warp_potion")) return;

        OfflinePlayer target = null;
        UUID targetUUID = UUID.fromString(e.getItem().getItemMeta().getPersistentDataContainer().get(Main.key("target_uuid"), PersistentDataType.STRING));
        target = Bukkit.getOfflinePlayer(targetUUID);

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
    public void onExcavatorPrepare(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;

        if (!ItemHelper.compareCustomItemID(e.getItem(), "excavator_hammer")) ;

        MetadataValue blockFace = new FixedMetadataValue(this.p, e.getBlockFace());
        e.getPlayer().setMetadata("lastBlockFace", blockFace);
    }

    @EventHandler
    public void onExcavatorHammer(BlockBreakEvent e) {
        if (!ItemHelper.compareCustomItemID(e.getPlayer().getInventory().getItemInMainHand(), "excavator_hammer")) return;
        if (e.getBlock().hasMetadata("no_excavate")) {
            e.getBlock().removeMetadata("no_excavate", p);
            return;
        }

        MetadataValue blockFace = e.getPlayer().getMetadata("lastBlockFace").get(0);
        if (blockFace == null) return;

        if (!e.isDropItems()) return;

        for (Block b : getSurroundingBlocks(e.getBlock(), (BlockFace) blockFace.value())) {
            if (b.getType().getHardness() == -1) continue;

            b.setMetadata("no_excavate", new FixedMetadataValue(p, "true"));

            BlockBreakEvent blockBreakEvent = new BlockBreakEvent(b, e.getPlayer());
            Bukkit.getPluginManager().callEvent(blockBreakEvent);
            if (!blockBreakEvent.isCancelled()) {
                b.breakNaturally(e.getPlayer().getInventory().getItemInMainHand());
            }
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

            if (ItemHelper.compareCustomItemID(modifier, "hardening_crystal") && modifier.getAmount() == 1) {
                ItemStack result = tool.clone();

                ItemMeta resultMeta = result.getItemMeta();
                resultMeta.setUnbreakable(true);
                ((org.bukkit.inventory.meta.Damageable) resultMeta).setDamage(0);
                result.setItemMeta(resultMeta);

                inv.setItem(2, result);
            } else if (modifier.getType().equals(Material.NETHER_STAR) && modifier.getAmount() == 1) {
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

    @EventHandler
    public void onSmithingResult(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;

        if (e.getClickedInventory().getType() != InventoryType.SMITHING) return;

        if (e.getSlot() != 2) return;

        SmithingInventory inv = (SmithingInventory) e.getClickedInventory();
        if (e.getCurrentItem() != null) {
            ItemStack current = e.getCurrentItem().clone();
            e.getView().setCursor(current);
            e.setCurrentItem(new ItemStack(Material.AIR));
            inv.setItem(0, new ItemStack(Material.AIR));
            inv.setItem(1, new ItemStack(Material.AIR));
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

    @EventHandler
    public void onQueenPearlThrow(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_AIR) && !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        if (!ItemHelper.compareCustomItemID(e.getItem(), "queen_pearl")) return;

        Bukkit.getScheduler().scheduleSyncDelayedTask(p, () -> {
            e.getPlayer().getInventory().setItemInMainHand(FuncraftItems.ENDER_QUEEN_PEARL);
            e.getPlayer().addScoreboardTag("SAFE_PEARL");
        }, 1);
    }

    @EventHandler
    public void onQueenPearlTeleport(PlayerTeleportEvent e) {
        if (!e.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) return;
        if (e.getPlayer().getScoreboardTags().contains("SAFE_PEARL")) {
            e.setCancelled(true);

            e.getPlayer().removeScoreboardTag("SAFE_PEARL");
            e.getPlayer().teleport(e.getTo(), PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
            e.getPlayer().setNoDamageTicks(1);
        }
    }

    @EventHandler
    public void onBannerClick(BlockDropItemEvent e) {
        if (!(e.getBlockState() instanceof Banner)) return;
        if (e.getPlayer().getInventory().getHelmet() != null) return;

        if (!e.getPlayer().isSneaking()) return;

        e.setCancelled(true);
        e.getPlayer().getInventory().setHelmet(e.getItems().get(0).getItemStack());
    }

    @EventHandler
    public void onDragonSword(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        if (!(e.getEntity() instanceof Damageable)) return;
        if (e.getEntity() instanceof Player) return;
        Player p = (Player) e.getDamager();

        if (!ItemHelper.compareCustomItemID(p.getInventory().getItemInMainHand(), "dragon_sword")) return;

        if (!(((Damageable) e.getEntity()).getHealth() - e.getFinalDamage() <= 0)) {
            Location target = e.getEntity().getLocation().clone();
            Random r = new Random();
            target.add(r.nextInt(10) - 5, 0, r.nextInt(10) - 5);
            e.getEntity().teleport(target.getWorld().getHighestBlockAt(target).getLocation().add(0, 1, 0));
        }
    }

    @EventHandler
    public void onQueenPearlDrop(EntityDeathEvent e) {
        if (!e.getEntityType().equals(EntityType.ENDERMAN)) return;

        if (e.getEntity().getKiller() == null) return;
        if (!ItemHelper.compareCustomItemID(e.getEntity().getKiller().getInventory().getItemInMainHand(), "dragon_sword")) return;

        if (Math.random() > 0.95) {
            e.getDrops().add(FuncraftItems.ENDER_QUEEN_PEARL);
            e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 2, 1);
        }
    }

    @EventHandler
    public void onCropClick(BlockBreakEvent e) {
        if (!e.getPlayer().getInventory().getItemInMainHand().getType().toString().contains("HOE")) return;
        if (!(e.getBlock().getBlockData() instanceof Ageable)) return;

        Ageable crop = (Ageable) e.getBlock().getBlockData();
        if (crop.getAge() != crop.getMaximumAge()) return;
        Bukkit.getScheduler().scheduleSyncDelayedTask(p, () -> {
            crop.setAge(0);
            e.getBlock().setBlockData(crop);
        }, 5);

        FixedMetadataValue value = new FixedMetadataValue(Main.p, "REDUCE_DROP");
        e.getBlock().getState().setMetadata("REDUCE_DROP", value);
    }

    @EventHandler
    public void onCropDrop(BlockDropItemEvent e) {
        if (!(e.getBlockState().getBlockData() instanceof Ageable)) return;
        if (e.getBlock().getState().getMetadata("REDUCE_DROP").size() < 1) return;
        e.getBlock().getState().removeMetadata("REDUCE_DROP", p);
        e.getBlock().getState().update();

        Material seed = e.getBlockState().getType();
        if (e.getBlockState().getType().equals(Material.WHEAT)) {
            seed = Material.WHEAT_SEEDS;
        }

        for (Item i : e.getItems()) {
            if (!i.getItemStack().getType().equals(seed)) continue;
            i.getItemStack().setAmount(i.getItemStack().getAmount() - 1);
        }
    }

    @EventHandler
    public void onComposterClick(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!e.getClickedBlock().getType().equals(Material.COMPOSTER)) return;
        if (!e.getHand().equals(EquipmentSlot.HAND)) return;

        Levelled composter = (Levelled) e.getClickedBlock().getBlockData();
        if (composter.getLevel() != composter.getMaximumLevel()) return;

        e.setCancelled(true);
        composter.setLevel(0);
        e.getClickedBlock().setBlockData(composter);

        Item drop = e.getClickedBlock().getWorld().dropItemNaturally(e.getClickedBlock().getLocation().add(0.5, 0.5, 0.5), FuncraftItems.FERTILIZER);
    }

    @EventHandler
    public void onFlowerFertilization(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!flowers.contains(e.getClickedBlock().getType())) return;
        if (!ItemHelper.compareCustomItemID(e.getItem(), "fertilizer")) return;
        if (!e.getHand().equals(EquipmentSlot.HAND)) return;

        e.getItem().setAmount(e.getItem().getAmount() - 1);

        e.getClickedBlock().getWorld().dropItemNaturally(e.getClickedBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(e.getClickedBlock().getType()));
        e.getClickedBlock().getWorld().spawnParticle(Particle.VILLAGER_HAPPY, e.getClickedBlock().getLocation().add(0.5, 0.5, 0.5), 20, 0.25, 0.25, 0.25);
    }

    @EventHandler
    public void onStairSit(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!e.getClickedBlock().getType().name().contains("STAIR")) return;
        if (e.getPlayer().isSneaking()) return;
        if (!e.getHand().equals(EquipmentSlot.HAND)) return;

        e.getClickedBlock().getWorld().spawn(e.getClickedBlock().getLocation().add(0.5, -0.05, 0.5), AreaEffectCloud.class, cloud -> {
            cloud.setGravity(false);
            cloud.addPassenger(e.getPlayer());
            cloud.setRadius(0);
            cloud.setDuration(100000);
        });
    }

    @EventHandler
    public void onArrowDismount(EntityDismountEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!e.getDismounted().getType().equals(EntityType.AREA_EFFECT_CLOUD)) return;

        Bukkit.getScheduler().scheduleSyncDelayedTask(p, () -> e.getEntity().teleport(e.getEntity().getLocation().add(0, 1, 0)), 1);
        e.getDismounted().remove();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage("§7[§a+§7]§3 " + e.getPlayer().getName());
        PlayerList.sendTabList(e.getPlayer());
        PacketReader reader = new PacketReader(e.getPlayer());
        reader.attach();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        e.setQuitMessage("§7[§c-§7]§3 " + e.getPlayer().getName());
        for (PacketReader reader : GlobalVars.activeReaders) {
            if (!e.getPlayer().getUniqueId().equals(reader.p.getUniqueId())) continue;
            reader.detach();
            break;
        }
    }

    @EventHandler
    public void onDeathMessage(PlayerDeathEvent e) {
        String[] message = e.getDeathMessage().split(" ", 2);
        message[0] = "§3" + message[0];
        message[1] = "§7" + message[1];
        String finalMessage = message[0] + " " + message[1];
        e.setDeathMessage(finalMessage);
    }

    @EventHandler
    public void onBarrelExplode(BlockExplodeEvent e) {
        if (!(e.getBlock().getState() instanceof Barrel)) return;
        if (!Objects.equals(((Barrel) e.getBlock().getState()).getCustomName(), "§3death_chest")) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onDragonArmorControls(PlayerSwapHandItemsEvent e) {
        if (ItemHelper.compareCustomItemID(e.getOffHandItem(), "dragon_chestplate")) {
            e.setCancelled(true);
            ItemMeta meta = e.getOffHandItem().getItemMeta();

            Integer currentSpeed = meta.getPersistentDataContainer().get(Main.key("flight_speed"), PersistentDataType.INTEGER);
            switch (currentSpeed) {
                case 1:
                    currentSpeed = 2;
                    e.getPlayer().sendMessage(Main.prefix + "§aFlight speed set to §enormal");
                    break;
                case 2:
                    currentSpeed = 3;
                    e.getPlayer().sendMessage(Main.prefix + "§aFlight speed set to §efast");
                    break;
                case 3:
                    currentSpeed = 1;
                    e.getPlayer().sendMessage(Main.prefix + "§aFlight speed set to §eslow");
                    break;
            }

            meta.getPersistentDataContainer().set(Main.key("flight_speed"), PersistentDataType.INTEGER, currentSpeed);
            e.getPlayer().getInventory().getItemInMainHand().setItemMeta(meta);
            return;
        } else if (ItemHelper.compareCustomItemID(e.getOffHandItem(), "dragon_helmet")) {
            e.setCancelled(true);
            ItemMeta meta = e.getOffHandItem().getItemMeta();

            String currentValue = meta.getPersistentDataContainer().get(Main.key("night_vision"), PersistentDataType.STRING);
            if (currentValue == "true") {
                currentValue = "false";
                e.getPlayer().sendMessage(Main.prefix + "§aNight vision §edisabled");
            } else {
                currentValue = "true";
                e.getPlayer().sendMessage(Main.prefix + "§aNight vision §eenabled");
            }

            meta.getPersistentDataContainer().set(Main.key("night_vision"), PersistentDataType.STRING, currentValue);
            e.getPlayer().getInventory().getItemInMainHand().setItemMeta(meta);
            return;
        }
    }

    @EventHandler
    public void onInfinity(PlayerItemHeldEvent e) {
        ItemStack oldItem = e.getPlayer().getInventory().getItem(e.getPreviousSlot());
        ItemStack newItem = e.getPlayer().getInventory().getItem(e.getNewSlot());

        if (newItem == null) newItem = new ItemStack(Material.AIR);
        if (oldItem == null) oldItem = new ItemStack(Material.AIR);

        if (newItem.getType().equals(Material.BOW) && newItem.getEnchantments().containsKey(Enchantment.ARROW_INFINITE)) {
            MetadataValue offhand = new FixedMetadataValue(p, e.getPlayer().getInventory().getItemInOffHand().clone());
            e.getPlayer().setMetadata("offhand_previous", offhand);
            e.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.ARROW));
            ItemFactory.setCustomItemID(e.getPlayer().getInventory().getItemInOffHand(), "infinity_arrow");
        } else if (oldItem.getType().equals(Material.BOW) && e.getPlayer().hasMetadata("offhand_previous")) {
            e.getPlayer().getInventory().setItemInOffHand((ItemStack) e.getPlayer().getMetadata("offhand_previous").get(0).value());
            e.getPlayer().removeMetadata("offhand_previous", p);
        }
    }

    @EventHandler
    public void onInfityArrowDupe(PlayerSwapHandItemsEvent e) {
        if (e.getOffHandItem().getType().equals(Material.BOW) && e.getPlayer().hasMetadata("offhand_previous")) e.setCancelled(true);
    }

    @EventHandler
    public void onInfityArrowDupe(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (!e.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;
        if (e.getCurrentItem() == null) return;

        if (ItemHelper.compareCustomItemID(e.getCurrentItem(), "infinity_arrow")) {
            e.setCancelled(true);
            e.getWhoClicked().getInventory().setItemInOffHand((ItemStack) e.getWhoClicked().getMetadata("offhand_previous").get(0).value());
            e.getWhoClicked().removeMetadata("offhand_previous", p);
        }

        if (e.getCurrentItem().getEnchantments().containsKey(Enchantment.ARROW_INFINITE) && e.getWhoClicked().hasMetadata("offhand_previous")) {
            e.getWhoClicked().getInventory().setItemInOffHand((ItemStack) e.getWhoClicked().getMetadata("offhand_previous").get(0).value());
            e.getWhoClicked().removeMetadata("offhand_previous", p);
        }
    }

    @EventHandler
    public void onInfityArrowDupe(PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().getEnchantments().containsKey(Enchantment.ARROW_INFINITE) && e.getPlayer().hasMetadata("offhand_previous")) {
            e.getPlayer().getInventory().setItemInOffHand((ItemStack) e.getPlayer().getMetadata("offhand_previous").get(0).value());
            e.getPlayer().removeMetadata("offhand_previous", p);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        String message = e.getMessage();
        e.setMessage(message.replace('&', '§'));

        String format = e.getFormat().replace("<", "§3");
        e.setFormat(format.replace(">", "§7 ⊳"));
    }

    @EventHandler
    public void onNBTCraft(PrepareItemCraftEvent e) {
        for (ItemStack[] matrix : FuncraftItems.NBTrecipes.keySet()) {
            if (Arrays.equals(e.getInventory().getMatrix(), matrix)) {
                e.getInventory().setResult(FuncraftItems.NBTrecipes.get(matrix));
            }
        }
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

