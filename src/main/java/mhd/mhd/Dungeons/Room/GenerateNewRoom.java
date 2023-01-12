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

import static mhd.mhd.Utils.ChatUtils.format;

public class GenerateNewRoom implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 4) return false;

        if (sender instanceof Player) {

            int height = Integer.parseInt(args[0]);
            if (height < 15) {
                Bukkit.broadcastMessage(format("&cHeight must be 15 or more, defaulted to 15"));
                height = 15;
            }
            int width = Integer.parseInt(args[1]);
            if (width < 15) {
                Bukkit.broadcastMessage(format("&cWidth must be 15 or more, defaulted to 15"));
                width = 15;
            }
            String seed = args[2];
            //Bukkit.broadcastMessage(String.valueOf(seed.length()));
            int randomInfillPercent = Integer.parseInt(args[3]);
            if (randomInfillPercent < 30 || randomInfillPercent > 40) {
                randomInfillPercent = 35;
                Bukkit.broadcastMessage(format("&cInfill Percentage should be between 30 and 40, defaulted to 35"));
            }

            if (seed.equals("RANDOM")) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd HH:mm:ss:nnn");
                LocalDateTime now = LocalDateTime.now();
                seed = dtf.format(now);

            }

            RoomGenerator rg = new RoomGenerator(width, height, seed, randomInfillPercent, (width*height/8));
            RoomPlacer rp = new RoomPlacer(rg.map, new Location(Bukkit.getWorld("world"), 1, 80, 1), seed);
            RoomDecorator rd = new RoomDecorator(rg.map, width, height);
            return true;
        } else {
            return false;
        }
    }
}