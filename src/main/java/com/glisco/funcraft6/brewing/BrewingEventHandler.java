package com.glisco.funcraft6.brewing;

import com.glisco.funcraft6.utils.GlobalVars;
import org.bukkit.Material;
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

    @EventHandler(priority = EventPriority.HIGH)
    public void potionItemPlacer(InventoryClickEvent e) {
        if (e.getInventory().getType() != InventoryType.BREWING) return; //Check that the click was actually executed in the brewing stand
        if (e.getSlot() > 3) return;

        if (!(e.getClick() == ClickType.LEFT)) return;

        if (e.getCursor().getType().equals(Material.AIR)) return; //Check that we are actually trying to place an item
        if (e.getCurrentItem() == null) return;

        ItemStack cursor = e.getCursor().clone();
        ItemStack current = e.getCurrentItem().clone();

        Player p = (Player) e.getWhoClicked();
        BrewerInventory inventory = (BrewerInventory) e.getInventory();

        e.setCancelled(true); //Cancel the event so we can implement our own logic

        p.getOpenInventory().setCursor(current); //Perform the actual swapping of the items
        inventory.setItem(e.getSlot(), cursor);

        if (inventory.getIngredient() == null) return; //Check that we still have an ingredient

        BrewingHelper.validateRecipe(inventory); //Start the brewing process, in case the recipe is valid
    }

    @EventHandler
    public void onBrewerItemRemoval(InventoryClickEvent e) { //Used to cancel the brewing process if any of the integral items are removed
        if (e.getInventory().getType() != InventoryType.BREWING) return;
        if (e.getSlot() > 3) return;

        if (e.getCurrentItem() == null) return;
        if (!BrewingHelper.getValidIngredients().contains(e.getCurrentItem().getType()) && !BrewingHelper.getValidInputPotions().contains(e.getCurrentItem()))
            return; //Check if any important items were removed

        //TODO make this map a field of the brewing helper and add relevant methods
        if (GlobalVars.runningStands.containsKey(e.getInventory().getLocation().getBlock())) { //Cancel the brewing process, if it is running
            GlobalVars.runningStands.get(e.getInventory().getLocation().getBlock()).cancel();
        }
    }

    @EventHandler
    public void onBrewerBreak(BlockBreakEvent e) {
        if (GlobalVars.runningStands.containsKey(e.getBlock())) {
            GlobalVars.runningStands.get(e.getBlock()).cancel();
        }
    }


}
