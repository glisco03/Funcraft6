package com.glisco.funcraft6.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class PlayerList {

    private static String header;

    public static void init() {
        header = "";
        header = header.concat("§7------\n");
        header = header.concat("§a   Funcraft §b6   \n");
        header = header.concat("§7----");
    }

    private static String assembleFooter(Player p) {
        String footer = "";
        BigDecimal tps = BigDecimal.valueOf(Bukkit.getTPS()[0]).setScale(2, BigDecimal.ROUND_HALF_UP);

        footer = footer.concat("\n");
        footer = footer.concat("  §9TPS §7" + tps + "  §9Ping §7" + p.spigot().getPing() + "  \n");

        return footer;
    }

    public static void sendTabList(Player p) {
        p.setPlayerListHeaderFooter(header, assembleFooter(p));
    }

    public static void updateGlobalTablist() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setPlayerListHeaderFooter(header, assembleFooter(p));
        }
    }
}
