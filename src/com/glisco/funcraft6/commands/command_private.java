package com.glisco.funcraft6.commands;

import com.glisco.funcraft6.Main;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class command_private implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player p;
        if (command.getName().equalsIgnoreCase("private")) {
            if (commandSender instanceof Player) {
                p = (Player) commandSender;
                Block b = p.getTargetBlockExact(4, FluidCollisionMode.ALWAYS);
                if(b == null){
                    p.sendMessage(Main.prefix + "§cYou're not looking at a sign!");
                    return true;
                }
                BlockState targetBlock = b.getState();
                if (targetBlock instanceof Sign) {
                    Sign sign = (Sign) targetBlock;
                    PersistentDataContainer signData = sign.getPersistentDataContainer();
                    if (!signData.get(Main.key("owner"), PersistentDataType.STRING).equals(p.getUniqueId().toString())) {
                        p.sendMessage(Main.prefix + "§cThis is not your sign, idiot!");
                        return true;
                    }

                    signData.set(Main.key("public"), PersistentDataType.STRING, "false");
                    sign.update();

                    p.sendMessage(Main.prefix + "§aThis sign is now private again!");
                } else {
                    p.sendMessage(Main.prefix + "§cYou're not looking at a sign!");
                }
            } else {
                commandSender.sendMessage("§cPlayers only!");
            }
            return true;
        } else {
            return false;
        }
    }
}
