package com.glisco.funcraft6.brewing;

import com.glisco.funcraft6.utils.GlobalVars;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BrewClock extends BukkitRunnable {

    private int brewTime = 400;
    private Block stand;
    private BrewingRecipe recipe;

    public BrewClock(Block b, BrewingRecipe recp, JavaPlugin p) {
        stand = b;
        recipe = recp;
        BlockState now = stand.getState();
        ((BrewingStand) now).setFuelLevel(((BrewingStand) now).getFuelLevel() - 1);
        now.update();
        runTaskTimer(p, 0, 1);
    }

    @Override
    public void run() {
        BlockState now = stand.getState();
        if (brewTime > 0) {
            ((BrewingStand) now).setBrewingTime(brewTime);
            now.update();
            brewTime--;
        } else {
            ((BrewingStand) now).setBrewingTime(0);
            now.update();
            for (int i = 0; i < 3; i++) {
                if (((BrewingStand) now).getInventory().getItem(i) != null) {
                    if (((BrewingStand) now).getInventory().getItem(i).equals(recipe.getInputPotions())) {
                        ((BrewingStand) now).getInventory().setItem(i, recipe.getOutputPotions());
                    }
                }
            }
            ((BrewingStand) now).getInventory().getIngredient().setAmount(((BrewingStand) now).getInventory().getIngredient().getAmount() - 1);
            stand.getWorld().playSound(stand.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
            this.cancel();
        }
    }

    @Override
    public void cancel() {
        BlockState now = stand.getState();
        ((BrewingStand) now).setBrewingTime(0);
        GlobalVars.runningStands.remove(stand);
        super.cancel();
    }
}
