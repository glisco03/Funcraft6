package com.glisco.funcraft6.utils;

import com.glisco.funcraft6.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class InsultManager {

    private static HashMap<Player, Integer> insultScores;
    private static HashMap<Player, Location> insultlocations;
    private static ArrayList<String> insults;

    public InsultManager() {
        insultScores = new HashMap<>();
        insultlocations = new HashMap<>();
        insults = new ArrayList<>();

        insults.add("ya dunce");
        insults.add("imbecile");
        insults.add("idiot");
        insults.add("moron");
        insults.add("twat");
        insults.add("pathetic creature");
    }

    public static void serveInsult(Player p, Location loc) {
        if (insultlocations.containsKey(p)) {
            if (!insultlocations.get(p).equals(loc)) {
                insultScores.put(p, 1);
                insultlocations.put(p, loc);
                p.sendMessage(Main.prefix + "§cThis is not your sign, " + insults.get((int) Math.round(Math.random() * 5)) + "!");
                return;

            }
        }
        if (insultScores.containsKey(p)) {
            if (insultScores.get(p) < 5) {
                p.sendMessage(Main.prefix + "§cThis is not your sign, " + insults.get((int) Math.round(Math.random() * 5)) + "!");
                insultScores.replace(p, insultScores.get(p) + 1);
            } else {
                p.setHealth(0);
            }
        } else {
            insultScores.put(p, 1);
            insultlocations.put(p, loc);
            p.sendMessage(Main.prefix + "§cThis is not your sign, " + insults.get((int) Math.round(Math.random() * 6)) + "!");
        }
    }

    public static boolean isInsultDeath(Player p) {
        if (!insultScores.containsKey(p)) {
            return false;
        }
        if (insultScores.get(p) == 5) {
            insultScores.remove(p);
            insultlocations.remove(p);
            return true;
        }
        return false;
    }
}
