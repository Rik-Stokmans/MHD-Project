package mhd.mhd.Dungeons.Room;

import org.bukkit.Bukkit;

public class RoomDecorator {

    public int[][] map;
    public int width,height;
    public int tileMapWidth,tileMapHeight;
    public int[][] tilemap;

    public int[][] regionMap;
    int[][] tileMapCopy;

    public RoomDecorator(int[][] _map, int _width, int _height) {
        map = _map;
        width = _width;
        height = _height;
        tileMapWidth = width - 1;
        tileMapHeight = height - 1;
        regionMap = new int[tileMapHeight][tileMapWidth];

        generateTileMap();

        separateRegions();

        broadcastTileMap();
    }

    int regionNumber = 1;
    void separateRegions() {
        for (int i = 0; i < tileMapWidth; i++) {
            for (int j = 0; j < tileMapHeight; j++) {
                regionMap[i][j] = 0;
            }
        }

        for (int x = 0; x < tileMapWidth; x++) {
            for (int y = 0; y < tileMapHeight; y++) {
                if (tileMapCopy[x][y] == 0) {
                    regionMap[x][y] = regionNumber;
                    tileMapCopy[x][y] = 1;
                    checkSurroundingTiles(x,y);
                    regionNumber++;
                }
            }
        }
    }

    private void checkSurroundingTiles(int x, int y) {
        for (int i = x-1; i <= x+1; i++) {
            for (int j = y-1; j <= y+1; j++) {
                if (isInMapRange(i,j) && (x != i || y != j)) {
                    if (tileMapCopy[i][j] == 0) {
                        regionMap[i][j] = regionNumber;
                        tileMapCopy[i][j] = 1;
                        checkSurroundingTiles(i,j);
                    }
                }
            }
        }
    }

    private void broadcastTileMap() {
        for (int y = 0; y < height - 1; y++) {
            String line = "";
            for(int x = 0; x < width-1; x++) {
                if (regionMap[x][y] == 0) {
                    line += "#";
                } else if (regionMap[x][y] < 10) {
                    line += String.valueOf(regionMap[x][y]);
                } else {
                    line += "-";
                }
            }
            Bukkit.broadcastMessage(line);
        }
    }

    private void generateTileMap() {
        tilemap = new int[width-1][height-1];
        for(int x = 0; x < width-1; x++) {
            for(int y = 0; y < height-1; y++) {
                if ((map[x][y] + map[x+1][y] + map[x][y+1] + map[x+1][y+1]) == 0) {
                    tilemap[x][y] = 0;
                } else {
                    tilemap[x][y] = 1;
                }
            }
        }
        tileMapCopy = tilemap.clone();
    }

    boolean isInMapRange(int x, int y) {
        return x >= 0 && x < tileMapWidth && y >= 0 && y < tileMapHeight;
    }
}
