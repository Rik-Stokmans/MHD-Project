package mhd.mhd.Loot;

import mhd.mhd.Loot.LootTables.DesertLootTable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

import static mhd.mhd.Utils.ChatUtils.format;

public class LootCrate implements Listener {

    static HashMap<DesertLootTable.lootItems, ItemStack> desertLoot;

    public static void initLootTables() {
        DesertLootTable.init();
        desertLoot = DesertLootTable.desertDungeonLoot;
    }

    @EventHandler
    private void onCrateClick(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if (e.getClickedBlock().getType().equals(Material.BARREL)) {
                Player player = e.getPlayer();
                Location crateLocation = e.getClickedBlock().getLocation();

                player.sendMessage(format("&7Opened A Crate"));

                player.getWorld().dropItem(crateLocation, desertLoot.get(DesertLootTable.lootItems.COIN));

                e.setCancelled(true);
            }
        }
    }
}
