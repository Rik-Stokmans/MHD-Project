package mhd.mhd.Dungeons.Room;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GenerateNewRoom implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean useRandomSeed = false;
        if (args.length == 0) {
            useRandomSeed = true;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;

            String seed;
            if (useRandomSeed) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd HH:mm:ss:nnn");
                LocalDateTime now = LocalDateTime.now();
                seed = dtf.format(now);
            } else {
                seed = args[0];
            }

            clearMap();

            RoomGenerator rg = new RoomGenerator(100, 100, seed, 35, 1000);
            RoomPlacer rp = new RoomPlacer(rg.map, new Location(Bukkit.getWorld("flat60"), 1, 80, 1), seed);

            return true;
        } else {
            return false;
        }
    }

    void clearMap() {
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        String command = "fill 0 80 0 150 80 150 air";
        Bukkit.dispatchCommand(console, command);
    }
}
