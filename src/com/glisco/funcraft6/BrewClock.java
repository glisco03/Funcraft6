package com.glisco.funcraft6;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BrewClock extends BukkitRunnable {

    private int brewTime = 400;
    private int ingredientAmount;
    private Block stand;

    public BrewClock(Block b, int amount, JavaPlugin p) {
        stand = b;
        ingredientAmount = amount;
        runTaskTimer(p, 0, 1);
        BlockState now = stand.getState();
        ((BrewingStand) now).setFuelLevel(((BrewingStand) now).getFuelLevel() - 1);
        now.update();
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
                    if (((BrewingStand) now).getInventory().getItem(i).equals(FuncraftItems.REGEN_POTION)) {
                        ((BrewingStand) now).getInventory().setItem(i, FuncraftItems.RECALL_POTION);
                    }
                }
            }
            ((BrewingStand) now).getInventory().getIngredient().setAmount(((BrewingStand) now).getInventory().getIngredient().getAmount() - 1);
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
