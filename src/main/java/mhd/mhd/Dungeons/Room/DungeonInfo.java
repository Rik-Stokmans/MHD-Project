package mhd.mhd.Dungeons.Room;

import java.util.ArrayList;

public class DungeonInfo {

    public static ArrayList<String> dungeonThemes = new ArrayList<>();

    public static ArrayList<String> smallRoomThemes = new ArrayList<>();
    public static ArrayList<String> mediumRoomThemes = new ArrayList<>();
    public static ArrayList<String> largeRoomThemes = new ArrayList<>();

    public static void initDungeonInfo() {
        initDungeonThemes();
        initRoomThemes();
        initStructures();
    }

    private static void initDungeonThemes() {
        dungeonThemes.add("sandstone");
    }

    private static void initRoomThemes() {
        //small rooms
        smallRoomThemes.add("shop");

        //medium rooms
        mediumRoomThemes.add("camp");

        //large rooms
        largeRoomThemes.add("boss");
    }

    private static void initStructures() {
        //shop
        shopStructures.add(new RoomDecorator.Decoration("main_stall", new RoomDecorator.Coord(0, 0, 0), new RoomDecorator.Size(5,5)));

        //camp
        campStructures.add(new RoomDecorator.Decoration("main_tent", new RoomDecorator.Coord(0, 0, 0), new RoomDecorator.Size(7,7)));

        //boss
        bossStructures.add(new RoomDecorator.Decoration("main_altar", new RoomDecorator.Coord(0, 0, 0), new RoomDecorator.Size(10,10)));
    }

    public static ArrayList<RoomDecorator.Decoration> shopStructures = new ArrayList<>();

    public static ArrayList<RoomDecorator.Decoration> campStructures = new ArrayList<>();

    public static ArrayList<RoomDecorator.Decoration> bossStructures = new ArrayList<>();

}
