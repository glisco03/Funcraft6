package com.glisco.funcraft6.brewing;

import com.glisco.funcraft6.utils.GlobalVars;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BrewClock extends BukkitRunnable {

    private int brewTime = 400;
    private final Block standHolder;
    private final BrewingRecipe recipe;
    private final BrewingStand stand;

    public BrewClock(Block standHolder, BrewingRecipe recipe, JavaPlugin p) {
        this.standHolder = standHolder;
        this.recipe = recipe;

        stand = (BrewingStand) standHolder.getState();

        stand.setFuelLevel(stand.getFuelLevel() - 1); //Reduce the stand's fuel level by 1 when starting the process to emulate vanilla behaviour
        stand.update();

        runTaskTimer(p, 0, 1);
    }

    @Override
    public void run() {

        if (brewTime > 0) {
            stand.setBrewingTime(brewTime); //Track the time it takes to brew (400 ticks) and run the brewing animation
            stand.update();

            brewTime--;
        } else {
            stand.setBrewingTime(0); //Not really necessary as the server does this every tick, but probably good practice
            stand.update();

            for (int i = 0; i < 3; i++) { //Loop through all items in the output slots and replace them if they match our input item
                if (stand.getInventory().getItem(i) != null) {
                    if (stand.getInventory().getItem(i).equals(recipe.getInputPotions())) {
                        stand.getInventory().setItem(i, recipe.getOutputPotions());
                    }
                }
            }

            stand.getInventory().getIngredient().setAmount(stand.getInventory().getIngredient().getAmount() - 1); //Remove one item from the input stack and play the brewing sound
            standHolder.getWorld().playSound(standHolder.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
            this.cancel();
        }
    }

    @Override
    public void cancel() {
        stand.setBrewingTime(0); //Stop any currently running brewing animations
        GlobalVars.runningStands.remove(standHolder);
        super.cancel();
    }
}
