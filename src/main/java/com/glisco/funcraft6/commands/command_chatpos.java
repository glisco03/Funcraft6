package com.glisco.funcraft6.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class command_chatpos implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        Player p;

        if (command.getName().equalsIgnoreCase("chatpos")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("Players only!");
                return true;
            }
            p = (Player) commandSender;
            Location l = p.getLocation();

            String message = "<" + p.getName() + "> §9Position: §e" + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ();
            ClickEvent getCompassClickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/get_player_compass:" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ() + ":" + l.getWorld().getName() + ":" + p.getName());
            HoverEvent getCompassHoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click this message \nto obtain a compass"));

            TextComponent messageComponent = new TextComponent(message);
            messageComponent.setClickEvent(getCompassClickEvent);
            messageComponent.setHoverEvent(getCompassHoverEvent);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.spigot().sendMessage(messageComponent);
            }
            return true;
        }
        return false;
    }
}
