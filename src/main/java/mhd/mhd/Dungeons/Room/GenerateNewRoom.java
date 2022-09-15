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
        if (args.length != 4) return false;

        if (sender instanceof Player) {

            int height = Integer.parseInt(args[0]);
            int width = Integer.parseInt(args[1]);
            String seed = args[2];
            Bukkit.broadcastMessage(String.valueOf(seed.length()));
            int randomInfillPercent = Integer.parseInt(args[3]);

            if (seed.equals("RANDOM")) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd HH:mm:ss:nnn");
                LocalDateTime now = LocalDateTime.now();
                seed = dtf.format(now);

            }

            clearMap();

            RoomGenerator rg = new RoomGenerator(width, height, seed, randomInfillPercent, (width*height/10));
            RoomPlacer rp = new RoomPlacer(rg.map, new Location(Bukkit.getWorld("world"), 1, 80, 1), seed);

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