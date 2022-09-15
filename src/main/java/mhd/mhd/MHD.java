package mhd.mhd;

import mhd.mhd.Dungeons.Room.GenerateNewRoom;
import mhd.mhd.Loot.LootCrate;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.structure.StructureManager;

import java.util.ArrayList;

public final class MHD extends JavaPlugin {

    public static StructureManager structureManager;
    public static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        this.getCommand("GenerateNewRoom").setExecutor(new GenerateNewRoom());
        structureManager = getServer().getStructureManager();

        ArrayList<Listener> events = new ArrayList<>();
        //list of events
        events.add(new LootCrate());

        for (Listener l : events){
            getServer().getPluginManager().registerEvents(l, this);
        }

        LootCrate.initLootTables();
    }

    @Override
    public void onDisable() {

    }
}
