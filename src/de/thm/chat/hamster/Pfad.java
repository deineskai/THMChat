package de.thm.chat.hamster;

public class Pfad {

    /*+++++++++++++++++++++++++++++++++++++++++++++++++++++
    Lege 'path' als eindimensionales Integer Array an.
    Dieses dient der Erfassung der Bewegungsrichtungen zum
    Erreichen des Targets.
    Soll mit Werten von 0 bis 3 gefüllt werden:
    -------------------------------------------------------
    0 = NORD
    1 = OST
    2 = SUED
    3 = WEST
    +++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    int[] path;

    void erstellePfad(int ind, int row, int col, int[][] map) {
    	/*+++++++++++++++++++++++++++++++++++++++++++++++++++++
    	Iteriere rückwärts durch alle Indizes, suche dabei
    	ausgehend vom Target immer genau einen Nachbarn und
    	speichere die Bewegungsrichtung in 'path'.
    	+++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        int rows = map.length;
        int cols = map[0].length;
        path = new int[ind - 1];
        for (int i = ind; i > 1; i--) {
            //Pr?fe jeweils vorher, ob der Nachbar gültig ist
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

    void hamsterLauf(Hamster h) {
    	/*+++++++++++++++++++++++++++++++++++++++++++++++++++++
    	Iteriere r?ckw?rts durch 'path'.
    	+++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        for (int i = path.length - 1; i >= 0; i--) {

    		/*+++++++++++++++++++++++++++++++++++++++++++++++++++++
    		Lass den Hamster sich so oft drehen, bis er in die
    		Richtung schaut, die an der aktuellen Position im
    		'path' angegeben ist.
    		+++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            while (h.getFacing() != path[i]) {
                h.turnLeft();
            }
            h.move(); //Lass den Hamster einen Schritt laufen.
        }
        // h.pickUp();
    }
}
