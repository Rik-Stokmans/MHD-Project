package mhd.mhd.Loot.LootTables;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

import static mhd.mhd.Utils.ChatUtils.format;

public class DesertLootTable {

    public static HashMap<lootItems, ItemStack> desertDungeonLoot = new HashMap<>();

    public static void init() {
        desertDungeonLoot.put(lootItems.COIN, generateItem("&6Coin", Material.SUNFLOWER));
    }

    public enum lootItems {
        COIN
    }

    static ItemStack generateItem(String name, Material type) {
        ItemStack item =  new ItemStack(type);
        ItemMeta iMeta = item.getItemMeta();
        iMeta.setDisplayName(format(name));
        item.setItemMeta(iMeta);

        return item;
    }
}
