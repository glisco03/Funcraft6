package com.glisco.funcraft6.enchantments;

import com.glisco.funcraft6.utils.GlobalVars;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.util.Vector;

import static com.glisco.funcraft6.Main.p;

public class EnchantmentEventHandler implements Listener {

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onAnvilResult(InventoryClickEvent e) {
        if (e.getInventory() == null) {
            return;
        }
        if (e.getInventory().getType() != InventoryType.ANVIL) {
            return;
        }
        if (e.getSlot() > 3) {
            return;
        }
        AnvilInventory inv = (AnvilInventory) e.getInventory();
        if (e.getSlot() == 2 && e.getCurrentItem() != null) {
            if (e.getCurrentItem().getItemMeta().getLore() != null) {
                if (EnchantmentHelper.getEnchatmentFromLore(e.getCurrentItem().getItemMeta().getLore().get(0)) != null) {
                    ItemStack current = e.getCurrentItem().clone();
                    e.setCursor(current);
                    e.setCurrentItem(new ItemStack(Material.AIR));
                    inv.setItem(0, new ItemStack(Material.AIR));
                    inv.setItem(1, new ItemStack(Material.AIR));
                }
            }
        }
    }

    @EventHandler
    public void onAnvil(PrepareAnvilEvent e) {
        if (e.getInventory().getItem(1) != null && e.getInventory().getItem(0) != null) {
            e.setResult(EnchantmentHelper.combineItems(e.getInventory().getItem(0), e.getInventory().getItem(1), e.getResult()));
        }
    }

    @EventHandler
    public void onAgileSword(PlayerItemHeldEvent e) {
        ItemStack heldItem = e.getPlayer().getInventory().getItem(e.getNewSlot());
        e.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
        if (heldItem == null || !heldItem.hasItemMeta()) {
            return;
        }
        if (heldItem.getItemMeta().getLore() != null) {
            if (heldItem.getItemMeta().getLore().contains("§7Agility I")) {
                e.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4.9);
            } else if (heldItem.getItemMeta().getLore().contains("§7Agility II")) {
                e.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(6.4);
            } else if (heldItem.getItemMeta().getLore().contains("§7Agility III")) {
                e.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(12.4);
            }
        }
    }

    @EventHandler
    public void onAgileLeggings(PlayerToggleSprintEvent e) {
        if (e.getPlayer().getInventory().getLeggings() != null) {
            if (e.getPlayer().getInventory().getLeggings().getItemMeta().hasLore()) {
                if (e.isSprinting() && e.getPlayer().getInventory().getLeggings().getItemMeta().getLore().contains("§7Agility I")) {
                    e.getPlayer().setWalkSpeed(0.35f);
                } else if (e.isSprinting() && e.getPlayer().getInventory().getLeggings().getItemMeta().getLore().contains("§7Agility II")) {
                    e.getPlayer().setWalkSpeed(0.5f);
                } else if (e.isSprinting() && e.getPlayer().getInventory().getLeggings().getItemMeta().getLore().contains("§7Agility III")) {
                    e.getPlayer().setWalkSpeed(0.65f);
                } else {
                    e.getPlayer().setWalkSpeed(0.2f);
                }
            }
        }
    }

    @EventHandler
    public void onLifesteal(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            Damageable entity = (Damageable) e.getEntity();
            if (!p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                if (EnchantmentHelper.getEnchatmentFromLore("Lifesteal I").getValidItemTypes().contains(p.getInventory().getItemInMainHand().getType()) && !p.getInventory().getItemInMainHand().getType().equals(Material.ENCHANTED_BOOK)) {
                    if (p.getInventory().getItemInMainHand().getItemMeta().hasLore()) {
                        if (p.getInventory().getItemInMainHand().getItemMeta().getLore().contains("§7Lifesteal I")) {
                            if (Math.random() > 0.5) {
                                double healthChange = Math.random() + 1;
                                if (p.getHealth() + healthChange > 20)
                                    p.setHealth(20);
                                else
                                    p.setHealth(p.getHealth() + healthChange);
                                if (entity.getHealth() - healthChange < 0)
                                    entity.setHealth(0);
                                else
                                    entity.setHealth(entity.getHealth() - healthChange);
                            }
                        } else if (p.getInventory().getItemInMainHand().getItemMeta().getLore().contains("§7Lifesteal II")) {
                            if (Math.random() > 0.5) {
                                double healthChange = (Math.random() + 1) * 2;
                                if (p.getHealth() + healthChange > 20)
                                    p.setHealth(20);
                                else
                                    p.setHealth(p.getHealth() + healthChange);
                                if (entity.getHealth() - healthChange < 0)
                                    entity.setHealth(0);
                                else
                                    entity.setHealth(entity.getHealth() - healthChange);

                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onAutoSmelt(BlockBreakEvent e) {
        if (!EnchantmentHelper.getEnchatmentFromLore("Pretty Hot I").getValidItemTypes().contains(e.getPlayer().getInventory().getItemInMainHand().getType()) || e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.ENCHANTED_BOOK)) {
            return;
        }

        if (e.getPlayer().getInventory().getItemInMainHand() == null) {
            return;
        }
        if (!e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasLore()) {
            return;
        }
        CustomEnchantment smelt = EnchantmentHelper.getEnchatmentFromLore(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLore().get(0));
        if (smelt == null) {
            return;
        }
        final int level = EnchantmentHelper.getLevelFromLore(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLore().get(0));
        if (GlobalVars.smeltOres.containsKey(e.getBlock().getType())) {
            e.setDropItems(false);
            if (level == 1) {
                e.getPlayer().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(GlobalVars.smeltOres.get(e.getBlock().getType())));
                e.getPlayer().getWorld().spawnParticle(Particle.FLAME, e.getBlock().getLocation().add(0.5, 0.5, 0.5), 15, 0.25, 0.25, 0.25, 0.025);
            } else {
                int amount = 1;
                if (Math.random() > 0.5)
                    amount = 2;
                e.getPlayer().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(GlobalVars.smeltOres.get(e.getBlock().getType()), amount));
                e.getPlayer().getWorld().spawnParticle(Particle.FLAME, e.getBlock().getLocation().add(0.5, 0.5, 0.5), 15, 0.25, 0.25, 0.25, 0.025);
            }
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent e) {
        if (!EnchantmentHelper.getEnchatmentFromLore("Pretty Hot I").getValidItemTypes().contains(e.getItem().getType())) {
            return;
        }
        if (e.getExpLevelCost() < 25) {
            return;
        }
        if (Math.random() > 0.95) {
            if (Math.random() > 0.75) {
                EnchantmentHelper.addCustomEnchant(e.getItem(), "Pretty Hot II", 2);
            } else {
                EnchantmentHelper.addCustomEnchant(e.getItem(), "Pretty Hot I", 1);
            }
        }
    }

    @EventHandler
    public void onDoubleJump(PlayerToggleFlightEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE || e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        if (e.getPlayer().getInventory().getBoots() == null) {
            return;
        }
        if (!e.getPlayer().getInventory().getBoots().getItemMeta().hasLore()) {
            return;
        }
        CustomEnchantment c = EnchantmentHelper.getEnchatmentFromLore(e.getPlayer().getInventory().getBoots().getItemMeta().getLore().get(0));
        if (c == null) {
            return;
        }
        if (c != EnchantmentHelper.getEnchatmentFromLore("Air Hopper I")) {
            return;
        }
        e.getPlayer().setAllowFlight(false);
        e.setCancelled(true);
        final int level = EnchantmentHelper.getLevelFromLore(e.getPlayer().getInventory().getBoots().getItemMeta().getLore().get(0));
        Vector v = e.getPlayer().getLocation().getDirection();
        if (level == 1) {
            e.getPlayer().setVelocity(v.multiply(1.25));
        } else if (level == 2) {
            e.getPlayer().setVelocity(v.multiply(1.75));
        } else if (level == 3) {
            e.getPlayer().setVelocity(v.multiply(2.5));
        }
    }

    /*@EventHandler
    public void onDisenchant(InventoryClickEvent e){
        if(!e.getClickedInventory().getType().equals(InventoryType.GRINDSTONE)){
            return;
        }
        if(e.getSlot() != 1){
            return;
        }
        GrindstoneInventory inventory = (GrindstoneInventory) e.getClickedInventory();
        if(inventory.getItem(1) == null){
            return;
        }
        if(!inventory.getItem(1).getType().equals(Material.BOOK)){
            return;
        }
        if(inventory.getItem(0).getEnchantments().isEmpty()){
            return;
        }
        ItemStack input = inventory.getItem(0);
        Enchantment firstEnchant = input.getEnchantments().keySet().iterator().next();

        ItemStack output = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) output.getItemMeta();
        bookMeta.addStoredEnchant(firstEnchant, input.getEnchantmentLevel(firstEnchant), false);
        output.setItemMeta(bookMeta);
        inventory.setItem(2, output);
    }*/

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGH)
    public void grindItemPlacer(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) {
            return;
        }
        if (e.getClickedInventory().getType() != InventoryType.GRINDSTONE) {
            return;
        }
        if (!(e.getClick() == ClickType.LEFT)) {
            return;
        }
        if (e.getSlot() > 1) {
            return;
        }
        ItemStack cursor = e.getCursor().clone();
        ItemStack current = e.getCurrentItem();
        if (cursor == null) {
            return;
        }
        if (cursor.getType().equals(Material.AIR)) {
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(p, () -> {
            e.setCursor(current);
            e.getClickedInventory().setItem(e.getSlot(), cursor);
            ((Player) e.getView().getPlayer()).updateInventory();
            GrindstoneInventory inventory = (GrindstoneInventory) e.getClickedInventory();
            if(inventory.getItem(1) == null || inventory.getItem(0) == null){
                return;
            }
            if(!inventory.getItem(1).getType().equals(Material.BOOK)){
                return;
            }
            if(inventory.getItem(0).getEnchantments().isEmpty()){
                return;
            }
            ItemStack input = inventory.getItem(0);
            Enchantment firstEnchant = input.getEnchantments().keySet().iterator().next();

            ItemStack output = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) output.getItemMeta();
            bookMeta.addStoredEnchant(firstEnchant, input.getEnchantmentLevel(firstEnchant), false);
            output.setItemMeta(bookMeta);
            inventory.setItem(2, output);
        }, 1L);

    }

}
