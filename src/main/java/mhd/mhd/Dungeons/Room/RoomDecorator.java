package mhd.mhd.Dungeons.Room;

import mhd.mhd.MHD;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static mhd.mhd.Dungeons.Room.DungeonInfo.*;

public class RoomDecorator {

    public String dungeonTheme;
    public int[][] map;
    public int width,height;
    public int tileMapWidth,tileMapHeight;
    public int[][] tilemap;
    int[][] tileMapCopy;
    String seed;
    Random rand;

    public Location spawnLocation;

    int mediumRoomThreshold = 6;
    int largeRoomThreshold = 15;

    public int[][] regionMap;
    int regionCount;

    public RoomDecorator(int[][] _map, int _width, int _height, String _seed, Location _location, String _dungeonTheme) throws CloneNotSupportedException {
        map = _map;
        width = _width;
        height = _height;
        seed = _seed;
        spawnLocation = _location;
        dungeonTheme = _dungeonTheme;
        tileMapWidth = width - 1;
        tileMapHeight = height - 1;
        regionMap = new int[tileMapHeight][tileMapWidth];
        rand = new Random(seed.hashCode());

        generateTileMap();

        separateRegions();

        broadcastTileMap();

        processAllRooms();

        //generate an enlarged version for each room

        //decide what structures will be placed in each room

        //fill the room with the structures

        //place the structures

    }

    private void processAllRooms() throws CloneNotSupportedException {
        int smallRoomCount = 0;
        int mediumRoomCount = 0;
        int largeRoomCount = 0;

        for (int regionNumber = 1; regionNumber <= regionCount; regionNumber++) {
            int[][] roomLayoutWithUnused = new int[tileMapWidth][tileMapHeight];
            int roomSize = 0;


            for (int i = 0; i < tileMapWidth; i++) {
                for (int j = 0; j < tileMapHeight; j++) {
                    if (regionMap[i][j] == regionNumber) {
                        roomLayoutWithUnused[i][j] = 0;
                        roomSize++;
                    } else {
                        roomLayoutWithUnused[i][j] = 1;
                    }
                }
            }

            MapShrinkInformation shrinkInfo = generateShrinkInfo(roomLayoutWithUnused);
            int[][] roomLayout = shrinkInfo.getNewMap();

            boolean roomHasTwoByTwoArea = shrinkInfo.roomHasTwoByTwoArea;

            Bukkit.broadcastMessage(shrinkInfo.getRemovedInFront() + ", " + shrinkInfo.getRemovedAbove());

            Location roomTopLeftLocation = spawnLocation.clone().add(shrinkInfo.getRemovedInFront()*5, 0, shrinkInfo.getRemovedAbove()*5);

            int[][] enlargedRoomLayout = enlargeRoom(roomLayout, 5);

            //large room
            if (roomHasTwoByTwoArea && roomSize > largeRoomThreshold) {
                largeRoomCount++;
                int totalPlacementLocations = 1;

                String roomTheme = largeRoomThemes.get(rand.nextInt(largeRoomThemes.size()));

                //select a decoration to place and the size
                Decoration decoration = selectDecoration(dungeonTheme, roomTheme, true);
                Bukkit.broadcastMessage(decoration.name);

                //decide where to place the decoration and remove these empty spots from the map
                int[][] placements = generatePlacements(enlargedRoomLayout, decoration.size);

                /*for (int x = 0; x < roomLayout.length; x++) {
                    String line = "";
                    for (int z = 0; z < roomLayout[0].length; z++) {
                        line = line + roomLayout[x][z];
                    }
                    Bukkit.broadcastMessage(line);
                }
                Bukkit.broadcastMessage(" ");*/

                for (int x = enlargedRoomLayout.length - 1; x >= 0; x--) {
                    String line = "";
                    for (int z = 0; z < enlargedRoomLayout[0].length; z++) {
                        line = line + enlargedRoomLayout[x][z];
                    }
                    Bukkit.broadcastMessage(line);
                }
                Bukkit.broadcastMessage(" ");

                for (int x = placements.length - 1; x >= 0; x--) {
                    String line = "";
                    for (int z = 0; z < placements[0].length; z++) {
                        line = line + placements[x][z];
                    }
                    Bukkit.broadcastMessage(line);
                }

                for (int x = 0; x < placements.length; x++) {
                    for (int z = 0; z < placements[0].length; z++) {
                        if (placements[x][z] == 0) {
                            decoration.locationInRoom = new Coord(x, 10, z);
                            break;
                        }
                    }
                }

                //keep doing this until there are enough decorations
                placeDecoration(decoration, enlargedRoomLayout, roomTopLeftLocation);
                placements = generatePlacements(enlargedRoomLayout, decoration.size);

                for (int x = enlargedRoomLayout.length - 1; x >= 0; x--) {
                    String line = "";
                    for (int z = 0; z < enlargedRoomLayout[0].length; z++) {
                        line = line + enlargedRoomLayout[x][z];
                    }
                    Bukkit.broadcastMessage(line);
                }
                Bukkit.broadcastMessage(" ");

                for (int x = placements.length - 1; x >= 0; x--) {
                    String line = "";
                    for (int z = 0; z < placements[0].length; z++) {
                        line = line + placements[x][z];
                    }
                    Bukkit.broadcastMessage(line);
                }

            } else if (roomHasTwoByTwoArea && roomSize > mediumRoomThreshold) {
                mediumRoomCount++;
            } else {
                smallRoomCount++;
            }
        }
        Bukkit.broadcastMessage("small: " + smallRoomCount + ", medium: " + mediumRoomCount + ", large: " + largeRoomCount);
    }

    private int[][] generatePlacements(int[][] enlargedRoomLayout, Size size) {

        int[][] placements = new int[enlargedRoomLayout.length][enlargedRoomLayout[0].length];
        for (int x = 0; x < placements.length; x++) {
            for (int y = 0; y < placements[0].length; y++) {
                placements[x][y] = 1;
            }
        }

        for (int x = 0; x <= placements.length - size.x; x++) {
            for (int y = 0; y <= placements[0].length - size.y; y++) {
                boolean isValidPlacement = true;
                for (int i = 0; i < size.x; i++) {
                    for (int j = 0; j < size.y; j++) {
                        if (enlargedRoomLayout[x + i][y + j] != 0) isValidPlacement = false;
                    }
                }
                if (isValidPlacement) {
                    placements[x][y] = 0;
                }
            }
        }
        return placements;
    }

    public Decoration selectDecoration(String dungeonTheme, String roomTheme, boolean isMainStructure) throws CloneNotSupportedException {
        Decoration decoration = new Decoration("error", new Coord(0,0,0), new Size(3,3));
        if (roomTheme == "shop") {
            if (isMainStructure) decoration = (Decoration) shopStructures.get(0).clone();
            else decoration = (Decoration) shopStructures.get(rand.nextInt(shopStructures.size() - 1) + 1).clone();
        } else if (roomTheme == "camp") {
            if (isMainStructure) decoration = (Decoration) campStructures.get(0).clone();
            else decoration = (Decoration) campStructures.get(rand.nextInt(campStructures.size() - 1) + 1).clone();
        } else if (roomTheme == "boss") {
            if (isMainStructure) decoration = (Decoration) bossStructures.get(0).clone();
            else decoration = (Decoration) bossStructures.get(rand.nextInt(bossStructures.size() - 1) + 1).clone();
        }
        String name = decoration.name;
        decoration.setName(dungeonTheme + "_" + name);

        return decoration;
    }

    public void placeDecoration(Decoration decoration, int[][] enlargedRoom, Location placement) {
        Location decorationPlacementLocation = placement.add(decoration.locationInRoom.x, decoration.locationInRoom.y, decoration.locationInRoom.z);

        for (int x = decoration.locationInRoom.x; x < decoration.locationInRoom.x + decoration.size.x; x++) {
            for (int z = decoration.locationInRoom.z; z < decoration.locationInRoom.z + decoration.size.y; z++) {
                enlargedRoom[x][z] = 1;
            }
        }

        try {
            MHD.structureManager.loadStructure(decoration.structure).place(decorationPlacementLocation, false, StructureRotation.NONE, Mirror.NONE, 0, 1, new Random());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Decoration implements Cloneable {
        String name;
        Coord locationInRoom;
        Size size;

        File structure;
        public Decoration(String _name, Coord _locationInRoom, Size _size) {
            name = _name;
            locationInRoom = _locationInRoom;
            size = _size;

            structure = new File(Bukkit.getWorld("world").getWorldFolder() + "/generated/minecraft/structures/" + name + ".nbt");

        }

        public void setName(String _name) {
            name = _name;
            structure = new File(Bukkit.getWorld("world").getWorldFolder() + "/generated/minecraft/structures/" + name + ".nbt");
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    public static class Coord {
        int x;
        int y;
        int z;
        public Coord(int _x, int _y, int _z) {
            x = _x;
            y = _y;
            z = _z;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        public void setX(int _x) {
            x = _x;
        }

        public void setY(int _y) {
            y = _y;
        }

        public void setZ(int _z) {
            z = _z;
        }
    }

    public static class Size {
        int x;
        int y;
        public Size(int _x, int _y) {
            x = _x;
            y = _y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setX(int _x) {
            x = _x;
        }

        public void setY(int _y) {
            y = _y;
        }
    }

    private int[][] enlargeRoom(int[][] roomLayout, int factor) {
        int[][] newArray = new int[roomLayout.length*factor][roomLayout[0].length*factor];

        for(int i = 0; i < newArray.length; i++)
            for(int j = 0; j < newArray[0].length; j++)
                newArray[i][j] = roomLayout[i/factor][j/factor];

        return newArray;
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
        regionCount = regionNumber - 1;
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

    private class MapShrinkInformation {
        int[][] newMap;
        int removedInFront;
        int removedAbove;
        int width;
        int height;
        boolean roomHasTwoByTwoArea;
        public MapShrinkInformation(int[][] _newMap, int _removedInFront, int _removedAbove, boolean _roomHasTwoByTwoArea) {
            newMap = _newMap;
            removedInFront = _removedInFront;
            removedAbove = _removedAbove;
            width = newMap.length;
            height = newMap[0].length;
            roomHasTwoByTwoArea = _roomHasTwoByTwoArea;
        }

        public int[][] getNewMap() {
            return newMap;
        }

        public void setNewMap(int[][] newMap) {
            this.newMap = newMap;
        }

        public int getRemovedInFront() {
            return removedInFront;
        }

        public void setRemovedInFront(int removedInFront) {
            this.removedInFront = removedInFront;
        }

        public int getRemovedAbove() {
            return removedAbove;
        }

        public void setRemovedAbove(int removedAbove) {
            this.removedAbove = removedAbove;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    MapShrinkInformation generateShrinkInfo(int[][] arr) {
        boolean roomHasTwoByTwoArea = false;
        int rows = arr.length;
        int cols = arr[0].length;

        boolean[] rowHasZero = new boolean[rows];
        boolean[] colHasZero = new boolean[cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (arr[i][j] == 0) {
                    rowHasZero[i] = true;
                    colHasZero[j] = true;

                    if (!roomHasTwoByTwoArea && i+1 < rows && j+1 < cols) {
                        if (arr[i+1][j] + arr[i][j+1] + arr[i+1][j+1] == 0) {
                            roomHasTwoByTwoArea = true;
                        }
                    }
                }
            }
        }

        int removedInFront = 0;
        for (boolean Bool : rowHasZero) {
            if (!Bool) {
                removedInFront++;
            } else {
                break;
            }
        }

        int removedAbove = 0;
        for (boolean Bool : colHasZero) {
            if (!Bool) {
                removedAbove++;
            } else {
                break;
            }
        }

        int newRows = 0;
        for (int i = 0; i < rows; i++) {
            if (rowHasZero[i]) {
                newRows++;
            }
        }

        int newCols = 0;
        for (int j = 0; j < cols; j++) {
            if (colHasZero[j]) {
                newCols++;
            }
        }

        int[][] newArr = new int[newRows][newCols];
        int newRowIndex = 0;

        for (int i = 0; i < rows; i++) {
            if (!rowHasZero[i]) {
                continue;
            }
            int newColIndex = 0;
            for (int j = 0; j < cols; j++) {
                if (!colHasZero[j]) {
                    continue;
                }
                newArr[newRowIndex][newColIndex] = arr[i][j];
                newColIndex++;
            }
            newRowIndex++;
        }

        return new MapShrinkInformation(newArr, removedInFront, removedAbove, roomHasTwoByTwoArea);
    }
}
