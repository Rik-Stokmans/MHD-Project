package mhd.mhd.Loot.LootTables;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import static mhd.mhd.Utils.ChatUtils.format;

public class DesertLootTable {

    public static HashMap<lootItems, ItemStack> desertDungeonLoot = new HashMap<>();

    public static void init() {
        desertDungeonLoot.put(lootItems.COIN, generateItem("&6Coin", Material.SUNFLOWER, 1.0));

        desertDungeonLoot.put(lootItems.EMPTY_BOTTLE, generateItem("&6Empty Bottle", Material.GLASS_BOTTLE, 0.6));
        desertDungeonLoot.put(lootItems.DUST, generateItem("&8Dust", Material.GUNPOWDER, 0.3));
    }

    public enum lootItems {
        COIN,
        EMPTY_BOTTLE,
        DUST
    }

    public static ArrayList<ItemStack> getCrateLoot() {
        ArrayList<ItemStack> loot = new ArrayList<>();
        int amount;
        ItemStack tempItem;
        Random random = new Random();

        tempItem = desertDungeonLoot.get(lootItems.COIN).clone();
        amount = random.nextInt(5) + 1;
        for (int i = 0; i < amount; i++) loot.add(tempItem);

        tempItem = desertDungeonLoot.get(lootItems.DUST).clone();
        amount = random.nextInt(8) + 1;
        for (int i = 0; i < amount; i++) loot.add(tempItem);

        tempItem = desertDungeonLoot.get(lootItems.EMPTY_BOTTLE).clone();
        amount = random.nextInt(3) + 1;
        for (int i = 0; i < amount; i++) loot.add(tempItem);

        Collections.shuffle(loot);
        return loot;
    }

    static ItemStack generateItem(String name, Material type, double value) {
        ItemStack item =  new ItemStack(type);
        ItemMeta iMeta = item.getItemMeta();
        iMeta.setDisplayName(format(name));
        item.setItemMeta(iMeta);

        NBTItem nbti = new NBTItem(item);
        nbti.setString("Type", "LOOT");
        nbti.setBoolean("CanMerge", false);
        nbti.setDouble("Value", value);
        nbti.applyNBT(item);

        return item;
    }
}
