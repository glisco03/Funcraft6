package com.glisco.funcraft6;

import com.glisco.funcraft6.brewing.BrewingEventHandler;
import com.glisco.funcraft6.brewing.BrewingHelper;
import com.glisco.funcraft6.brewing.BrewingRecipe;
import com.glisco.funcraft6.commands.command_diminfo;
import com.glisco.funcraft6.commands.command_dimtp;
import com.glisco.funcraft6.ritual.RitualEventHandler;
import com.glisco.funcraft6.ritual.StructureHelper;
import com.glisco.funcraft6.utils.*;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class Main extends JavaPlugin {

    public static JavaPlugin p;
    public static GlowEnchant glowEnchant;

    @Override
    public void onEnable() {
        p = this;
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
        this.getServer().getPluginManager().registerEvents(new RitualEventHandler(this), this);
        this.getServer().getPluginManager().registerEvents(new BrewingEventHandler(this), this);

        glowEnchant = new GlowEnchant(key("GLOW_ENCHANT"));
        try {
            try {
                Field f = Enchantment.class.getDeclaredField("acceptingNew");
                f.setAccessible(true);
                f.set(null, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Enchantment.registerEnchantment(glowEnchant);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        new FuncraftItems();
        new GlobalVars();
        new BrewingHelper(this);
        new StructureHelper(this);
        getCommand("diminfo").setExecutor(new command_diminfo());
        getCommand("dimtp").setExecutor(new command_dimtp());

        Bukkit.getScheduler().runTaskTimer(this, new Timer1L(), 1, 1);

        BrewingHelper.registerRecipe(new BrewingRecipe(Material.ENDER_PEARL, FuncraftItems.REGEN_POTION, FuncraftItems.RECALL_POTION));
        BrewingHelper.registerRecipe(new BrewingRecipe(Material.CLOCK, FuncraftItems.AWKWARD_POTION, FuncraftItems.HASTE_POTION));
        BrewingHelper.registerRecipe(new BrewingRecipe(Material.GLOWSTONE_DUST, FuncraftItems.HASTE_POTION, FuncraftItems.HASTE_II_POTION));

        WorldCreator wc = new WorldCreator("mining");
        wc.environment(World.Environment.NORMAL);
        Bukkit.createWorld(wc);
    }

    @Override
    public void onDisable() {
        Bukkit.unloadWorld("mining", true);
    }

    public static NamespacedKey key(String name) {
        return new NamespacedKey(p, name);
    }
}
