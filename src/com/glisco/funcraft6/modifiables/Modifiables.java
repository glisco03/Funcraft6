package com.glisco.funcraft6.modifiables;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Modifiables {

    private static HashMap<Material, Enchantment> modifierItems;
    private static HashMap<Enchantment, Integer> modifierAmounts;

    public Modifiables(){
        modifierItems = new HashMap<>();
        modifierAmounts = new HashMap<>();
    }

    public static void registerModifier(Enchantment targetEnchantment, Material modifierMaterial, int modifierAmount){
        modifierItems.put(modifierMaterial, targetEnchantment);
        modifierAmounts.put(targetEnchantment, modifierAmount);
    }

    public static ItemStack addModifier(Modifiable targetItem, ItemStack modifier){
        Enchantment enchantment = modifierItems.get(modifier.getType());
        if(enchantment == null){
            return null;
        }
        if(modifier.getAmount() < modifierAmounts.get(enchantment)){
            return null;
        }
        ItemStack item = targetItem.getAttachedItem();
        if(item.getEnchantments().isEmpty()){
            return null;
        }
        if(!item.getEnchantments().containsKey(enchantment)){
            return null;
        }
        if(item.getEnchantments().get(enchantment) < enchantment.getMaxLevel()){
            return null;
        }
        if(targetItem.getModifiersLeft() > 0){
            targetItem.removeModifier();
            int level = item.getEnchantments().get(enchantment);
            item.removeEnchantment(enchantment);
            item.addUnsafeEnchantment(enchantment, level+1);
            return item;
        }
        return null;
    }
}
