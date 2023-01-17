package de.thm.chat.hamster;

public class Suche {

    private final Map map;

    /*Wahrheitswert 'found', welcher aussagt, ob Target gefunden wurde, wird mit FALSCH initialisiert*/
    private static boolean found = false;
    /*Variablen f?r Koordinaten des Target anlegen*/
    private static int rows, cols, targetCol = Integer.MAX_VALUE / 2, targetRow = Integer.MAX_VALUE / 2;

    /**
    *Zweidimensionales Integer Array, welches das
    *Territorium mit folgenden Werten repraesentiert:
    *-------------------------------------------------------
    *-1 = Mauer
    *0 = frei
    *1 = Start
    *Werte > 1 sind Indizes
    **/
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
        /**Erstelle Indexvariable, um die Reihenfolge der Felder zu speichern.
        *Solange das Target nicht erreicht wurde iteriere durch das Array
        **/
        int ind = 1;
        do {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    /**Wenn das Feld mit aktuellem Index gefunden wurde
                     * makiere Nachbarfelder mit Index+1
                    **/
                    if (tiles[i][j] == ind) {
                        mark(i - 1, j, ind + 1);
                        mark(i + 1, j, ind + 1);
                        mark(i, j - 1, ind + 1);
                        mark(i, j + 1, ind + 1);
                    }
                }
            }
            ind++;
        } while (!found);

        route.erstellePfad(ind, targetRow, targetCol, tiles);
        route.hamsterLauf(h);
    }

    void mark(int row, int col, int ind) {
        /*Methode, die gueltige Nachbarn markiert*/
        if (col == targetCol && row == targetRow) {
            found = true;
        }
        if (row >= 0 && col >= 0 && row < rows && col < cols && tiles[row][col] == 0) {
            tiles[row][col] = ind;
        }
    }

    void selectTarget(Hamster h) {
        tiles[h.getRow()][h.getCol()] = 1;
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
