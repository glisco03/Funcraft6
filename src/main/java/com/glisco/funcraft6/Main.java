package com.glisco.funcraft6;

import com.glisco.funcraft6.brewing.BrewingEventHandler;
import com.glisco.funcraft6.brewing.BrewingHelper;
import com.glisco.funcraft6.brewing.BrewingRecipe;
import com.glisco.funcraft6.commands.*;
import com.glisco.funcraft6.dragon_queen.DragonAccelerator;
import com.glisco.funcraft6.dragon_queen.DragonBattleListener;
import com.glisco.funcraft6.dragon_queen.TickTimer;
import com.glisco.funcraft6.enchantments.EnchantmentEventHandler;
import com.glisco.funcraft6.enchantments.EnchantmentHelper;
import com.glisco.funcraft6.enchantments.GlowEnchant;
import com.glisco.funcraft6.items.FuncraftItems;
import com.glisco.funcraft6.modifiables.Modifiables;
import com.glisco.funcraft6.ritual.RitualEventHandler;
import com.glisco.funcraft6.ritual.StructureHelper;
import com.glisco.funcraft6.utils.*;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class Main extends JavaPlugin {

    public static JavaPlugin p;
    public static GlowEnchant glowEnchant;
    public static FileConfiguration config;

    public static final String prefix = "§9[§bFuncraft 6§9]§r ";

    @SuppressWarnings("InstantiationOfUtilityClass")
    @Override
    public void onEnable() {
        p = this;
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
        this.getServer().getPluginManager().registerEvents(new RitualEventHandler(this), this);
        this.getServer().getPluginManager().registerEvents(new BrewingEventHandler(this), this);
        this.getServer().getPluginManager().registerEvents(new EnchantmentEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new DragonBattleListener(), this);

        glowEnchant = new GlowEnchant(key("GLOW_ENCHANT"));
        try {
            try {
                Field f = Enchantment.class.getDeclaredField("acceptingNew");
                f.setAccessible(true);
                f.set(null, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Enchantment.getByKey(key("GLOW_ENCHANT")) == null) {
                try {
                    Enchantment.registerEnchantment(glowEnchant);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        new EnchantmentHelper();
        EnchantmentHelper.registerEnchantment("Agility", 3, Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD, Material.DIAMOND_LEGGINGS, Material.LEATHER_LEGGINGS, Material.IRON_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.NETHERITE_LEGGINGS, Material.ENCHANTED_BOOK);
        EnchantmentHelper.registerEnchantment("Lifesteal", 2, Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD, Material.ENCHANTED_BOOK);
        EnchantmentHelper.registerEnchantment("Pretty Hot", 2, Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE, Material.ENCHANTED_BOOK);
        EnchantmentHelper.registerEnchantment("Air Hopper", 3, Material.LEATHER_BOOTS, Material.GOLDEN_BOOTS, Material.IRON_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS, Material.CHAINMAIL_BOOTS, Material.ENCHANTED_BOOK);
        EnchantmentHelper.registerEnchantment("Beheading", 1, Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD, Material.ENCHANTED_BOOK);

        WorldCreator wc = new WorldCreator("mining");
        wc.environment(World.Environment.NORMAL);
        wc.createWorld();
        if (Bukkit.getWorld("mining") == null) {
            Bukkit.createWorld(wc);
        }

        WorldCreator spawnCreator = new WorldCreator("spawn");
        spawnCreator.environment(World.Environment.NORMAL);
        spawnCreator.type(WorldType.FLAT);
        spawnCreator.createWorld();
        if (Bukkit.getWorld("spawn") == null) {
            Bukkit.createWorld(spawnCreator);
        }

        new FuncraftItems();
        new GlobalVars();
        new BrewingHelper(this);
        new StructureHelper(this);
        new InsultManager();
        new Modifiables();

        Modifiables.registerModifier(Enchantment.DIG_SPEED, Material.REDSTONE_BLOCK, 64);
        Modifiables.registerModifier(Enchantment.DAMAGE_ALL, Material.QUARTZ_BLOCK, 64);
        Modifiables.registerModifier(Enchantment.KNOCKBACK, Material.PISTON, 24);
        Modifiables.registerModifier(Enchantment.LOOT_BONUS_BLOCKS, Material.LAPIS_BLOCK, 64);
        Modifiables.registerModifier(Enchantment.PROTECTION_ENVIRONMENTAL, Material.OBSIDIAN, 48);
        Modifiables.registerModifier(Enchantment.PROTECTION_FALL, Material.FEATHER, 64);
        Modifiables.registerModifier(Enchantment.OXYGEN, Material.HEART_OF_THE_SEA, 2);
        Modifiables.registerModifier(Enchantment.LOOT_BONUS_MOBS, Material.GOLDEN_APPLE, 64);
        Modifiables.registerModifier(Enchantment.ARROW_DAMAGE, Material.TNT, 64);

        getCommand("diminfo").setExecutor(new command_diminfo());
        getCommand("dimtp").setExecutor(new command_dimtp());
        getCommand("endlock").setExecutor(new command_endlock());
        getCommand("miniblocks").setExecutor(new command_miniblocks());
        getCommand("fishing").setExecutor(new command_fishing());
        getCommand("afk").setExecutor(new command_afk());
        getCommand("freeze").setExecutor(new command_freeze());
        getCommand("spectate").setExecutor(new command_spectate());
        getCommand("public").setExecutor(new command_public());
        getCommand("rulez").setExecutor(new command_rulez());
        getCommand("private").setExecutor(new command_private());
        getCommand("getitem").setExecutor(new command_getitem());
        getCommand("getitem").setTabCompleter(new command_getitem());
        getCommand("spawn").setExecutor(new command_spawn());
        getCommand("arms").setExecutor(new command_arms());
        getCommand("smol").setExecutor(new command_smol());
        getCommand("isneak").setExecutor(new command_isneak());
        getCommand("isneak").setTabCompleter(new command_isneak());
        getCommand("updateitem").setExecutor(new command_updateitem());
        getCommand("chatpos").setExecutor(new command_chatpos());
        getCommand("invsee").setExecutor(new command_invsee());
        getCommand("dragondelete").setExecutor(new command_dragondelete());

        Bukkit.getScheduler().runTaskTimer(this, new Timer1L(), 1, 1);
        Bukkit.getScheduler().runTaskTimer(this, new Timer20L(), 1, 20);
        Bukkit.getScheduler().runTaskTimer(this, new TickTimer(), 1, 1);
        Bukkit.getScheduler().runTaskTimer(this, new DragonAccelerator(), 1, 100);

        BrewingHelper.registerRecipe(new BrewingRecipe(Material.ENDER_PEARL, FuncraftItems.REGEN_POTION, FuncraftItems.RECALL_POTION));
        BrewingHelper.registerRecipe(new BrewingRecipe(Material.TOTEM_OF_UNDYING, FuncraftItems.RECALL_POTION, FuncraftItems.RECALL_TOTEM));
        BrewingHelper.registerRecipe(new BrewingRecipe(Material.HEART_OF_THE_SEA, FuncraftItems.REGEN_POTION, FuncraftItems.UNBOUND_WARP_POTION));
        BrewingHelper.registerRecipe(new BrewingRecipe(Material.EMERALD, FuncraftItems.WATER_POTION, FuncraftItems.ACCEPTANCE_POTION));
        BrewingHelper.registerRecipe(new BrewingRecipe(Material.COCOA_BEANS, FuncraftItems.WATER_POTION, FuncraftItems.COFFEE));
        BrewingHelper.registerRecipe(new BrewingRecipe(Material.CLOCK, FuncraftItems.AWKWARD_POTION, FuncraftItems.HASTE_POTION));
        BrewingHelper.registerRecipe(new BrewingRecipe(Material.GLOWSTONE_DUST, FuncraftItems.HASTE_POTION, FuncraftItems.HASTE_II_POTION));

        config = this.getConfig();
        config.addDefault("endlock", false);
        config.options().copyDefaults(true);
        this.saveConfig();

        PlayerList.init();

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            World spawn = Bukkit.getWorld("spawn");
            spawn.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            spawn.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            spawn.setGameRule(GameRule.DO_INSOMNIA, false);
            spawn.setGameRule(GameRule.KEEP_INVENTORY, true);
            spawn.setTime(23500);
            spawn.setStorm(false);
            spawn.setDifficulty(Difficulty.PEACEFUL);
        }, 40);

        PacketReader.attachAll();

        super.onEnable();
    }

    @Override
    public void onDisable() {
        Bukkit.unloadWorld("mining", true);
        Bukkit.unloadWorld("spawn", true);
        PacketReader.detachAll();
        super.onDisable();
    }

    public void save() {
        saveConfig();
    }

    public static NamespacedKey key(String name) {
        return new NamespacedKey(p, name);
    }
}
