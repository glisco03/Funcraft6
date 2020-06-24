package com.glisco.funcraft6;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin {

    public static JavaPlugin p;

    @Override
    public void onEnable(){
        p = this;
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
        new FuncraftItems();
        new GlobalVars();
        getCommand("diminfo").setExecutor(new command_diminfo());
        getCommand("dimtp").setExecutor(new command_dimtp());

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
