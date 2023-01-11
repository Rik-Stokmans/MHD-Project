package mhd.mhd.Dungeons.Room;

import org.bukkit.Bukkit;

public class RoomDecorator {

    public int[][] map;
    public int width,height;
    public int tileMapWidth,tileMapHeight;
    public int[][] tilemap;
    int[][] tileMapCopy;

    int mediumRoomThreshold = 6;
    int largeRoomThreshold = 15;

    public int[][] regionMap;
    int regionCount;

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

        processAllRooms();

        //generate an enlarged version for each room

        //decide what structures will be placed in each room

        //fill the room with the structures

        //place the structures

    }

    private void processAllRooms() {
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

            int[][] roomLayout =  removeUnusedRowsAndCols(roomLayoutWithUnused);

            int[][] enlargedRoomLayout = enlargeRoom(roomLayout, 5);

            //small room
            if (roomSize < mediumRoomThreshold) {

            }
            //medium room
            else  if (roomSize < largeRoomThreshold) {

            }
            //large room
            else {

            }


        }
    }

    private int[][] enlargeRoom(int[][] roomLayout, int factor) {
        int k = 1;
        for(int i = 0; i < roomLayout.length; i++)
            for(int j = 0; j < roomLayout[0].length; j++)
                roomLayout[i][j] = k++;

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

    int[][] removeUnusedRowsAndCols(int[][] arr) {
        int rows = arr.length;
        int cols = arr[0].length;

        boolean[] rowHasOne = new boolean[rows];
        boolean[] colHasOne = new boolean[cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (arr[i][j] == 1) {
                    rowHasOne[i] = true;
                    colHasOne[j] = true;
                }
            }
        }

        int newRows = 0;
        for (int i = 0; i < rows; i++) {
            if (rowHasOne[i]) {
                newRows++;
            }
        }

        int newCols = 0;
        for (int j = 0; j < cols; j++) {
            if (colHasOne[j]) {
                newCols++;
            }
        }

        int[][] newArr = new int[newRows][newCols];
        int newRowIndex = 0;
        for (int i = 0; i < rows; i++) {
            if (!rowHasOne[i]) {
                continue;
            }
            int newColIndex = 0;
            for (int j = 0; j < cols; j++) {
                if (!colHasOne[j]) {
                    continue;
                }
                newArr[newRowIndex][newColIndex] = arr[i][j];
                newColIndex++;
            }
            newRowIndex++;
        }

        return newArr;
    }
}
