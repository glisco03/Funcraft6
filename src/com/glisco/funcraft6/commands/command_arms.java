package com.glisco.funcraft6.commands;

import com.glisco.funcraft6.Main;
import com.glisco.funcraft6.utils.InsultManager;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;

public class command_arms implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player p;
        if (command.getName().equalsIgnoreCase("arms")) {
            if (commandSender instanceof Player) {
                p = (Player) commandSender;
                Location origin = p.getEyeLocation().add(p.getEyeLocation().getDirection());
                RayTraceResult result = p.getWorld().rayTraceEntities(origin, origin.getDirection(), 5);
                if (result == null) {
                    p.sendMessage(Main.prefix + "§cYou're not looking at an armor stand!");
                    return true;
                }
                Entity e = result.getHitEntity();
                if (e instanceof ArmorStand) {
                    ArmorStand armorStand = (ArmorStand) e;
                    if(armorStand.hasArms()){
                        armorStand.setArms(false);
                        p.sendMessage(Main.prefix + "§aYou cut the boi's arms off!");
                    } else {
                        armorStand.setArms(true);
                        p.sendMessage(Main.prefix + "§aYou gave the boi his arms back!");
                    }
                } else {
                    p.sendMessage(Main.prefix + "§cYou're not looking at an armor stand!");
                }
                return true;
            } else {
                commandSender.sendMessage("§cPlayers only!");
            }
            return true;
        } else {
            return false;
        }
    }
}
