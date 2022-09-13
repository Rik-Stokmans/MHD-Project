package mhd.mhd.Dungeons.Room;

import mhd.mhd.MHD;
import org.bukkit.*;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class RoomPlacer {

    public int[][] map;
    public Location spawnLocation;
    SquareGrid tileGrid;

    public RoomPlacer(int[][] _map, Location _location, String seed) {
        map = _map;
        spawnLocation = _location;

        tileGrid = new SquareGrid(map);

        buildMap();
    }

    void buildMap() {
        for (int x = 0; x < tileGrid.squares.length; x++) {
            for (int y = 0; y < tileGrid.squares[x].length; y++) {

                int finalX = x;
                int finalY = y;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        buildSquare(tileGrid.squares[finalX][finalY], finalX, finalY);
                    }
                }.runTaskLater(MHD.plugin, (long) ((x+y*tileGrid.squares.length) * 0.05));
            }
        }
    }


    void buildSquare(Square square, int x, int y) {
        Location bottomRight = spawnLocation.clone().add(x * 5, 0, y * 5);

        if (square.configuration >= 0 && square.configuration <= 14) {
            File structure = new File(Bukkit.getWorld("flat60").getWorldFolder() + "/generated/minecraft/structures/" + square.configuration + ".nbt");
            //tries to load the structure
            try {
                MHD.structureManager.loadStructure(structure).place(bottomRight, false, StructureRotation.CLOCKWISE_90, Mirror.NONE, 0, 1, new Random());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {

        }
    }

    public class SquareGrid {
        public Square[][] squares;

        public SquareGrid(int[][] map) {
            int nodeCountX = map.length;
            int nodeCountY = map[0].length;

            boolean[][] controlNodes = new boolean[nodeCountX][nodeCountY];

            for (int x = 0; x < nodeCountX; x++) {
                for (int y = 0; y < nodeCountY; y++) {
                    controlNodes[x][y] = (map[x][y] == 1);
                }
            }

            squares = new Square[nodeCountX - 1][nodeCountY - 1];
            for (int x = 0; x < nodeCountX - 1; x++) {
                for (int y = 0; y < nodeCountY - 1; y++) {
                    squares[x][y] = new Square(controlNodes[x + 1][y], controlNodes[x + 1][y + 1], controlNodes[x][y + 1], controlNodes[x][y]);
                }
            }
        }
    }

    public class Square {
        public boolean topLeft, topRight, bottomRight, bottomLeft;
        public int configuration;

        public Square(boolean _topLeft, boolean _topRight, boolean _bottomRight, boolean _bottomLeft) {
            topLeft = _topLeft;
            topRight = _topRight;
            bottomRight = _bottomRight;
            bottomLeft = _bottomLeft;

            if (topLeft)
                configuration += 8;
            if (topRight)
                configuration += 4;
            if (bottomRight)
                configuration += 2;
            if (bottomLeft)
                configuration += 1;
        }
    }
}
