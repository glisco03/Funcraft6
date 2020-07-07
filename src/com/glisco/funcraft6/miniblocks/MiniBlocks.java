package com.glisco.funcraft6.miniblocks;

import com.glisco.funcraft6.utils.SkullCreator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class MiniBlocks {


    public static ItemStack createMiniBlock(String name, String id) {
        ItemStack item = SkullCreator.itemFromBase64(id);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setDisplayName("§r§e" + name);
        item.setItemMeta(meta);
        return item;
    }
}
