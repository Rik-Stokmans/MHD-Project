package mhd.mhd;

import mhd.mhd.Dungeons.Room.GenerateNewRoom;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.structure.StructureManager;

public final class MHD extends JavaPlugin {

    public static StructureManager structureManager;
    public static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        this.getCommand("GenerateNewRoom").setExecutor(new GenerateNewRoom());
        structureManager = getServer().getStructureManager();
    }

    @Override
    public void onDisable() {

    }
}
