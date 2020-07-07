package com.glisco.funcraft6.brewing;

import com.glisco.funcraft6.utils.GlobalVars;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class BrewingEventHandler implements Listener {

    final JavaPlugin p;

    public BrewingEventHandler(JavaPlugin plugin) {
        p = plugin;
    }

    //ItemPlacer by NacOJerk
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGH)
    public void potionItemPlacer(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) {
            return;
        }
        if (e.getClickedInventory().getType() != InventoryType.BREWING) {
            return;
        }
        if (!(e.getClick() == ClickType.LEFT)) {
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
            if (((BrewerInventory) e.getInventory()).getIngredient() != null) {
                if (BrewingHelper.getValidIngredients().contains(((BrewerInventory) e.getInventory()).getIngredient().getType())) {
                    if (((BrewingStand) e.getClickedInventory().getLocation().getBlock().getState()).getFuelLevel() > 0) {
                        BrewingHelper.validateRecipe((BrewerInventory) e.getInventory());
                    }
                }
            }
        }, 1L);
    }

    @EventHandler
    public void onBrewerClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) {
            return;
        }
        if (e.getClickedInventory().getType() != InventoryType.BREWING) {
            return;
        }
        if (!BrewingHelper.getValidIngredients().contains(e.getCurrentItem().getType()) && !(BrewingHelper.getValidInputPotions()).contains(e.getCurrentItem())) {
            return;
        }
        if (e.getSlot() > 3) {
            return;
        }
        if (GlobalVars.runningStands.get(e.getClickedInventory().getLocation().getBlock()) != null) {
            GlobalVars.runningStands.get(e.getClickedInventory().getLocation().getBlock()).cancel();
        }
    }

    @EventHandler
    public void onBrewerBreak(BlockBreakEvent e) {
        if (GlobalVars.runningStands.containsKey(e.getBlock())) {
            GlobalVars.runningStands.get(e.getBlock()).cancel();
        }
    }


}
