package de.thm.chat.hamster;

public class Suche {

    private final Map map;

    private static boolean found = false; //Wahrheitswert 'found', welcher aussagt, ob Target gefunden wurde, wird mit FALSCH initialisiert

    private static int rows, cols, targetCol = Integer.MAX_VALUE / 2, targetRow = Integer.MAX_VALUE / 2; //Variablen f?r Koordinaten des Target anlegen

    /*+++++++++++++++++++++++++++++++++++++++++++++++++++++
    Zweidimensionales Integer Array, welches das
    Territorium mit folgenden Werten repraesentiert:
    -------------------------------------------------------
    -1 = Mauer
    0 = frei
    1 = Start
    Werte > 1 sind Indizes
    +++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    //int[][] map = new int[cols][rows];
    private final int[][] tiles;

    private final Pfad route = new Pfad();



    public Suche(Map map) {
        this.map = map;
        rows = map.getRows();
        cols = map.getCols();
        tiles = map.getTiles();
    }

    /* methods */
    public void suchePfad(Hamster h) {
        selectTarget(h);
        int ind = 1; //Erstelle Indexvariable und setze sie auf 1 //Diese wird verwendet, um die Reihenfolge der Felder zu speichern
        //Solange das Target nicht erreicht wurde iteriere durch das Array
        do {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (tiles[i][j] == ind) { //wenn das Feld mit dem aktuellen Index gefunden wurde
                        //Markiere die Nachbarfelder mit dem Index + 1
                        //Diese werden in der naechsten Iteration auf die gleiche Weise behandelt
                        mark(i - 1, j, ind + 1);
                        mark(i + 1, j, ind + 1);
                        mark(i, j - 1, ind + 1);
                        mark(i, j + 1, ind + 1);
                    }
                }
            }
            ind++; //Erhoehe den Index um 1
        } while (!found);

        route.erstellePfad(ind, targetRow, targetCol, tiles);
        route.hamsterLauf(h);
    }

    void mark(int row, int col, int ind) {
        //Methode, die gueltige Nachbarn markiert
        if (col == targetCol && row == targetRow) { //Wenn dieses Feld die Koordinaten des Target hat
            found = true; //Setze 'gefunden' auf WAHR
        }
        if (row >= 0 && col >= 0 && row < rows && col < cols && tiles[row][col] == 0) { //Wenn Feld innerhalb des Territoriums liegt und frei ist
            tiles[row][col] = ind; //Markiere es mit einem Index
        }
    }

    void selectTarget(Hamster h) {
        tiles[h.getRow()][h.getCol()] = 1; //Markiere die Position des Hamsters mit 1
        //Iteriere ?ber jededes Feld im Territorium
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (map.isSeed(i, j) && gridDistance(h, i, j) < gridDistance(h, targetRow, targetCol)) { //Wenn auf dem Feld Koerner liegen, speichere seine Koordinaten als die des Targets
                    targetRow = i;
                    targetCol = j;
                }
            }
        }
    }

    private int gridDistance(Hamster h, int row, int col) {
        int deltaRow = Math.abs(h.getRow() - row);
        int deltaCol = Math.abs(h.getCol() - col);
        return deltaRow + deltaCol;
    }

}
