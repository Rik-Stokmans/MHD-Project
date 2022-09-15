package mhd.mhd.Loot;

import de.tr7zw.nbtapi.NBTItem;
import mhd.mhd.Loot.LootTables.DesertLootTable;
import mhd.mhd.MHD;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
                Block clickedBlock = e.getClickedBlock();
                Location crateLocation = clickedBlock.getLocation().add(0.5, 0.5, 0.5);
                clickedBlock.setType(Material.AIR);

                player.sendMessage(format("&7Opened A Crate"));
                player.playSound(crateLocation, Sound.ENTITY_WITHER_BREAK_BLOCK, 50.0f, 1.0f);

                ArrayList<ItemStack> lootItems = DesertLootTable.getCrateLoot();

                dropItems(lootItems, crateLocation, player);

                e.setCancelled(true);
            }
        }
    }

    void dropItems(ArrayList<ItemStack> items, Location loc, Player player) {
        Random random = new Random();

        if (items.size() > 0) {
            int count = 0;
            for (ItemStack item : items) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Vector velocity = new Vector(random.nextDouble() * 0.15 - 0.075,0.35,random.nextDouble() * 0.15 - 0.075);

                        Item dropItem = player.getWorld().dropItem(loc, item);
                        dropItem.setVelocity(velocity);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                dropItem.remove();
                            }
                        }.runTaskLater(MHD.plugin, 600);
                    }
                }.runTaskLater(MHD.plugin, count);
                count++;
            }
        }
    }

    @EventHandler
    private void onItemMerge(ItemMergeEvent e) {
        NBTItem nbti = new NBTItem(e.getEntity().getItemStack());

        if (!nbti.getBoolean("CanMerge")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onItemPickup(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player) {
            NBTItem nbti = new NBTItem(e.getItem().getItemStack());
            if (nbti.hasKey("CanMerge")) nbti.setBoolean("CanMerge", true);
            nbti.applyNBT(e.getItem().getItemStack());
        } else {
            e.setCancelled(true);
            e.getItem().remove();
        }
    }
}
