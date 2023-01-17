package de.thm.chat.hamster;

public class Pfad {

    /**
    *Lege 'path' als eindimensionales Integer Array an.
    *Dieses dient der Erfassung der Bewegungsrichtungen zum
    *Erreichen des Targets.
    *Soll mit Werten von 0 bis 3 gefüllt werden:
    *-------------------------------------------------------
    *0 = NORD
    *1 = OST
    *2 = SÜD
    *3 = WEST
    **/
    int[] path;

    /*methods*/

    /**
     *Iteriert rückwärts durch alle Indizes, sucht dabei
     *ausgehend vom Target immer genau einen indizierten Nachbarn und
     *speichert die Bewegungsrichtung in 'path'.
     **/
    void erstellePfad(int ind, int row, int col, int[][] map) {

        int rows = map.length;
        int cols = map[0].length;
        path = new int[ind - 1];
        for (int i = ind; i > 1; i--) {
            //Prüfe jeweils vorher, ob der Nachbar gültig ist
            if (row + 1 < rows && map[row + 1][col] == i - 1) {
                row++;
                path[ind - i] = 0;
            } else if (row - 1 >= 0 && map[row - 1][col] == i - 1) {
                row--;
                path[ind - i] = 2;
            } else if (col + 1 < cols && map[row][col + 1] == i - 1) {
                col++;
                path[ind - i] = 3;
            } else if (col - 1 >= 0 && map[row][col - 1] == i - 1) {
                col--;
                path[ind - i] = 1;
            }
        }
    }
    /**
     *Iteriert rückwärts durch 'path'.
     *Lässt den Hamster sich so oft drehen, bis er in die
     *Richtung schaut, die an der aktuellen Position in
     *'path' angegeben ist und ihn dann einen Schritt laufen. Zum schluss nimmt er das Korn.
     * **/
    void hamsterLauf(Hamster h) {
        for (int i = path.length - 1; i >= 0; i--) {
            while (h.getFacing() != path[i]) {
                h.turnLeft();
            }
            h.move();
        }
        // h.pickUp();  // wird für Wettrennen ausgelassen
    }
}
