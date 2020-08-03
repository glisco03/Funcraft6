package com.glisco.funcraft6.modifiable;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Modifiable {

    private final ItemStack attachedItem;
    private final ItemMeta meta;
    private int modifiersLeft;

    public Modifiable(ItemStack item) {
        attachedItem = item.clone();
        meta = attachedItem.getItemMeta();
        modifiersLeft = 0;

        if (meta.hasLore()) {
            List<String> lore = meta.getLore();
            for (String s : lore) {
                if (s.contains("modifiers left")) {
                    modifiersLeft = Integer.parseInt(s.split(" ")[0].substring(2));
                }
            }
        }
    }

    public ItemStack getAttachedItem() {
        return attachedItem;
    }

    public int getModifiersLeft() {
        return modifiersLeft;
    }

    public void addModifiers(int modifiers) {
        modifiersLeft = modifiersLeft + modifiers;
        computeLore();
    }

    public void removeModifier(){
        modifiersLeft--;
        computeLore();
    }

    private void computeLore() {
        List<String> lore = meta.getLore();
        if(lore == null){
            lore = new ArrayList<>();
        }
        if (modifiersLeft > 0) {
            for (String s : lore) {
                if (s.contains("modifiers left")) {
                    lore.set(lore.indexOf(s), "§b" + modifiersLeft + " §7modifiers left");
                    meta.setLore(lore);
                    attachedItem.setItemMeta(meta);
                    return;
                }
            }
            lore.add("§b" + modifiersLeft + " §7modifiers left");
            meta.setLore(lore);
            attachedItem.setItemMeta(meta);
        } else {
            String toRemove = null;
            for (String s : lore) {
                if (s.contains("modifiers left")) {
                    toRemove = s;
                }
            }
            if(toRemove != null){
                lore.remove(toRemove);
                meta.setLore(lore);
                attachedItem.setItemMeta(meta);
            }
        }
    }
}
