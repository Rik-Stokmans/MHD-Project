package mhd.mhd.Dungeons.Room;

import org.bukkit.Bukkit;

import java.util.*;
import java.util.List;

import static mhd.mhd.Utils.ChatUtils.format;

public class RoomGenerator {

    public int width;
    public int height;

    public String seed;
    public int seedingIterations = 1;

    public int randomFillPercent;
    public int minimumEmptyTiles = 0;

    public int[][] map;

    public int usedSeed;

    public int spawnRoomTileX, spawnRoomTileY;

    private List<Room> survivingRooms;

    public RoomGenerator(int _width, int _height, String _seed, int _randomFillPercent, int _minimumEmptyTiles) {
        width = _width;
        height = _height;
        seed = _seed;
        randomFillPercent = _randomFillPercent;
        if (_minimumEmptyTiles < width * height * 0.15) minimumEmptyTiles = _minimumEmptyTiles;
        map = new int[width][height];

        //generates a valid map
        GenerateMap();

        //sets the seed that has been used for the generation, this way it can be used for generating the tileset
        usedSeed = seed.hashCode() + seedingIterations;
    }

    boolean IsValidMap() {
        if (GetEmptyTileAmount() >= minimumEmptyTiles && GetEmptyTileAmount() <= minimumEmptyTiles * 1.6) {
            return true;
        }
        else {
            seedingIterations++;
            return false;
        }
    }

    private List<Coord> generateSpawnRoom() {
        List<Coord> region = new ArrayList<>();
        boolean foundSpawnLoc = false;
        for (int x = 2; x < width - 3; x ++) {
            for (int y = 2; y < height - 3; y++) {
                if (map[x][y] == 1 && !foundSpawnLoc) {
                    if ((map[x][y+2] + map[x-1][y+1] + map[x][y+1] + map[x+1][y+1] + map[x-2][y] + map[x-1][y] + map[x+1][y] + map[x+2][y] + map[x-1][y-1] + map[x][y-1] + map[x+1][y-1] + map[x][y-2]) == 12) {
                        map[x][y+1] = 0;
                        map[x-1][y] = 0;
                        map[x][y] = 0;
                        map[x+1][y] = 0;
                        map[x][y-1] = 0;
                        spawnRoomTileX = x;
                        spawnRoomTileY = y;

                        region.add(new Coord(x,y+1));
                        region.add(new Coord(x-1,y));
                        region.add(new Coord(x,y));
                        region.add(new Coord(x+1,y));
                        region.add(new Coord(x,y-1));

                        foundSpawnLoc = true;
                    }
                }
            }
        }
        int x = spawnRoomTileX;
        int y = spawnRoomTileY;
        Bukkit.broadcastMessage(String.valueOf((map[x][y+2] + map[x-1][y+1] + map[x][y+1] + map[x+1][y+1] + map[x-2][y] + map[x-1][y] + map[x+1][y] + map[x+2][y] + map[x-1][y-1] + map[x][y-1] + map[x+1][y-1] + map[x][y-2])));
        return region;
    }

    void ProcessMap() {
        List<List<Coord>> roomRegions = GetRegions(0);
        int minimumRoomSize = 4;
        survivingRooms = new ArrayList<>();

        //removes all the rooms that are too small
        for (List<Coord> region : roomRegions) {
            if (region.size() < minimumRoomSize) {
                for (Coord coord : region) {
                    map[coord.tileX][coord.tileY] = 1;
                }
            }
            else {
                survivingRooms.add(new Room(region, map));
            }
        }

        if (IsValidMap()) {
            survivingRooms.add(new Room(generateSpawnRoom(), map));
            survivingRooms.sort(Room::compareTo);
            survivingRooms.get(0).isMainRoom = true;
            survivingRooms.get(0).isAccessibleFromMainRoom = true;
            ConnectClosestRooms (survivingRooms, false);
            Bukkit.broadcastMessage(String.valueOf(seedingIterations) + format(" &7[&aSucces&7]"));
        }
        else {
            GenerateMap();
        }
    }

    void ConnectClosestRooms(List<Room> allRooms, boolean forceAccessibilityFromMainRoom) {

        List<Room> roomListA = new ArrayList<>();
        List<Room> roomListB = new ArrayList<>();

        if (forceAccessibilityFromMainRoom) {
            for (Room room : allRooms) {
                if (room.isAccessibleFromMainRoom) {
                    roomListB.add(room);
                } else {
                    roomListA.add(room);
                }
            }
        } else {
            roomListA = allRooms;
            roomListB = allRooms;
        }

        int bestDistance = 0;
        Coord bestTileA = new Coord();
        Coord bestTileB = new Coord();
        Room bestRoomA = new Room();
        Room bestRoomB = new Room();
        boolean possibleConnectionFound = false;

        for (Room roomA : roomListA) {
            if (!forceAccessibilityFromMainRoom) {
                possibleConnectionFound = false;
                if (roomA.connectedRooms.size() > 0) {
                    continue;
                }
            }

            for (Room roomB : roomListB) {
                if (roomA == roomB || roomA.IsConnected(roomB)) {
                    continue;
                }
                if (roomA.IsConnected(roomB)) {
                    possibleConnectionFound = false;
                }

                for (int tileIndexA = 0; tileIndexA < roomA.edgeTiles.size(); tileIndexA ++) {
                    for (int tileIndexB = 0; tileIndexB < roomB.edgeTiles.size(); tileIndexB ++) {
                        Coord tileA = roomA.edgeTiles.get(tileIndexA);
                        Coord tileB = roomB.edgeTiles.get(tileIndexB);
                        int distanceBetweenRooms = (int)(Math.pow (tileA.tileX-tileB.tileX,2) + Math.pow (tileA.tileY-tileB.tileY,2));

                        if (distanceBetweenRooms < bestDistance || !possibleConnectionFound) {
                            bestDistance = distanceBetweenRooms;
                            possibleConnectionFound = true;
                            bestTileA = tileA;
                            bestTileB = tileB;
                            bestRoomA = roomA;
                            bestRoomB = roomB;
                        }
                    }
                }
            }

            if (possibleConnectionFound && !forceAccessibilityFromMainRoom) {
                CreatePassage(bestRoomA, bestRoomB, bestTileA, bestTileB);
            }
        }

        if (possibleConnectionFound && forceAccessibilityFromMainRoom) {
            CreatePassage(bestRoomA, bestRoomB, bestTileA, bestTileB);
            ConnectClosestRooms(allRooms, true);
        }

        if (!forceAccessibilityFromMainRoom) {
            ConnectClosestRooms(allRooms, true);
        }
    }

    void CreatePassage(Room roomA, Room roomB, Coord tileA, Coord tileB) {
        Room.ConnectRooms (roomA, roomB);

        List<Coord> line = GetLine(tileA, tileB);
        for (Coord coord : line) {
            map[coord.tileX][coord.tileY] = 0;
        }
    }

    List<Coord> GetLine(Coord from, Coord to) {
        List<Coord> line = new ArrayList<>();

        int x = from.tileX;
        int y = from.tileY;

        int dx = to.tileX - from.tileX;
        int dy = to.tileY - from.tileY;

        boolean inverted = false;
        int step = (dx > 0) ? 1 : -1;
        int gradientStep = (dy > 0) ? 1 : -1;

        int longest = Math.abs(dx);
        int shortest = Math.abs(dy);

        if (longest < shortest) {
            inverted = true;
            longest = Math.abs(dy);
            shortest = Math.abs(dx);

            step = (dy > 0) ? 1 : -1;
            gradientStep = (dx > 0) ? 1 : -1;
        }

        int gradientAccumulation = longest / 2;
        for (int i = 0; i < longest; i++) {
            line.add(new Coord(x,y));

            if (inverted) {
                y += step;
            } else {
                x += step;
            }

            gradientAccumulation += shortest;
            if (gradientAccumulation >= longest) {
                if (inverted) {
                    x += gradientStep;
                } else {
                    y += gradientStep;
                }
                gradientAccumulation -= longest;
            }
        }
        return line;
    }

    List<List<Coord>> GetRegions(int tileType) {
        List<List<Coord>> regions = new ArrayList<>();
        int[][] mapFlags = new int[width][height];

        for (int x = 0; x < width; x ++) {
            for (int y = 0; y < height; y ++) {
                if (mapFlags[x][y] == 0 && map[x][y] == tileType) {
                    List<Coord> newRegion = GetRegionTiles(x,y);
                    regions.add(newRegion);

                    for (Coord tile : newRegion) {
                        mapFlags[tile.tileX][tile.tileY] = 1;
                    }
                }
            }
        }
        return regions;
    }

    List<Coord> GetRegionTiles(int startX, int startY) {
        List<Coord> tiles = new ArrayList<>();
        int[][] mapFlags = new int[width][height];
        int tileType = map [startX][startY];

        Queue<Coord> queue = new ArrayDeque<>();
        queue.add(new Coord (startX, startY));
        mapFlags [startX][startY] = 1;

        while (queue.size() > 0) {
            Coord tile = queue.remove();
            tiles.add(tile);

            for (int x = tile.tileX - 1; x <= tile.tileX + 1; x++) {
                for (int y = tile.tileY - 1; y <= tile.tileY + 1; y++) {
                    if (IsInMapRange(x,y)) {
                        if (mapFlags[x][y] == 0 && map[x][y] == tileType) {
                            mapFlags[x][y] = 1;
                            queue.add(new Coord(x,y));
                        }
                    }
                }
            }
        }
        return tiles;
    }

    boolean IsInMapRange(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    void GenerateMap() {
        map = new int[width][height];
        RandomFillMap();

        for (int i = 0; i < 2; i++) {
            SmoothMap();
        }

        ProcessMap();
    }

    void RandomFillMap() {
        Random pseudoRandom = new Random(seed.hashCode() + seedingIterations);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x == 0 || x == width -1 || y == 0 || y == height -1) {
                    map[x][y] = 1;
                } else {
                    map[x][y] = (pseudoRandom.nextInt(100) > randomFillPercent) ? 1 : 0;
                }
            }
        }
    }

    void SmoothMap() {
        int[][] smoothedMap = new int[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int neighbourWallTiles = GetSurroundingWallCount(x, y);
                if (neighbourWallTiles > 4) {
                    smoothedMap[x][y] = 1;
                }
                else if (neighbourWallTiles < 4) {
                    smoothedMap[x][y] = 0;
                }
            }
        }
        map = smoothedMap;
    }

    int GetSurroundingWallCount(int gridX, int gridY) {
        int WallCount = 0;

        for (int neighbourX = gridX - 1; neighbourX <= gridX + 1; neighbourX++) {
            for (int neighbourY = gridY - 1; neighbourY <= gridY + 1; neighbourY++) {
                if (neighbourX >= 0 && neighbourX < width && neighbourY >= 0 && neighbourY < height) {
                    if (neighbourX != gridX || neighbourY != gridY) {
                        WallCount += map[neighbourX][neighbourY];
                    }
                } else {
                    WallCount++;
                }
            }
        }
        return WallCount;
    }

    int GetEmptyTileAmount() {
        int emptyTileCount = 0;
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                if (map[x][y] != 1) {
                    if (IsInMapRange(x + 1,y) && IsInMapRange(x,y + 1) && IsInMapRange(x + 1,y + 1)) {
                        if (map[x + 1][y] == 0 && map[x][y + 1] == 0 && map[x + 1][y + 1] == 0) {
                            emptyTileCount++;
                        }
                    }
                }
            }
        }
        return emptyTileCount;
    }

    private class Coord {
        public int tileX;
        public int tileY;

        public Coord() {
        }

        public Coord(int x, int y) {
            tileX = x;
            tileY = y;
        }
    }

    public class Room implements Comparable<Room> {
        public List<Coord> tiles;
        public List<Coord> edgeTiles;
        public List<Room> connectedRooms;
        public int roomSize;
        public boolean isAccessibleFromMainRoom;
        public boolean isMainRoom;

        public Room() {
        }

        public Room(List<Coord> roomTiles, int[][] map) {
            tiles = roomTiles;
            roomSize = tiles.size();
            connectedRooms = new ArrayList<>();

            edgeTiles = new ArrayList<>();
            for (Coord tile : tiles) {
                for (int x = tile.tileX-1; x <= tile.tileX+1; x++) {
                    for (int y = tile.tileY-1; y <= tile.tileY+1; y++) {
                        if (x == tile.tileX || y == tile.tileY) {
                            if (map[x][y] == 1) {
                                edgeTiles.add(tile);
                            }
                        }
                    }
                }
            }
        }

        public void setAccessibleFromMainRoom() {
             if (!isAccessibleFromMainRoom) {
                 isAccessibleFromMainRoom = true;
                 for (Room connectedRoom : connectedRooms) {
                     connectedRoom.setAccessibleFromMainRoom();
                 }
             }
        }

        public static void ConnectRooms(Room roomA, Room roomB) {
            if (roomA.isAccessibleFromMainRoom) {
                roomB.setAccessibleFromMainRoom();
            } else if (roomB.isAccessibleFromMainRoom) {
                roomA.setAccessibleFromMainRoom();
            }
            roomA.connectedRooms.add (roomB);
            roomB.connectedRooms.add (roomA);
        }

        public boolean IsConnected(Room otherRoom) {
            return connectedRooms.contains(otherRoom);
        }

        @Override
        public int compareTo(Room otherRoom) {
            return this.roomSize - otherRoom.roomSize;
        }
    }

}
