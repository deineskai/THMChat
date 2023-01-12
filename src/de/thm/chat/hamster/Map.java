package de.thm.chat.hamster;

import java.util.ArrayList;

public class Map {

    private int[][] tiles;

    public Map(String rawData) {
        gatherMapData(rawData);
    }

    private void gatherMapData(String rawdata) {
        ArrayList<Integer> chopped = chopUpMapData(rawdata);
        int cols = chopped.get(0);
        chopped.remove(0);
        int rows = chopped.get(0);
        chopped.remove(0);
        tiles = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                tiles[i][j] = chopped.get(0);
                chopped.remove(0);
            }
        }
    }

    private ArrayList<Integer> chopUpMapData(String rawData) {
        ArrayList<Integer> chopped = new ArrayList<>();
        while (!rawData.equals("")) {
            int tileValue = getTileValue(rawData.substring(0, rawData.indexOf(" ")));
            chopped.add(tileValue);
            rawData = rawData.substring(rawData.indexOf(" ") + 1);
        }
        return chopped;
    }

    private int getTileValue(String s) {
        int val = 0;
        switch (s) {
            case "x" -> val -= 1;
            case "!" -> val -= 2;
            default -> val = Integer.parseInt(s);
        }
        return val;
    }

    public int[][] getTiles() {
        return tiles;
    }

    public boolean isSeed(int row, int col) {
        return tiles[row][col] == -2;
    }

    public int getRows() {
        return tiles.length;
    }

    public int getCols() {
        return tiles[0].length;
    }

}
