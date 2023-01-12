package de.thm.chat.hamster;

import de.thm.chat.cmd.Command;

public class Hamster {

    private static int facing, row, col;
    private final Command c;

    public Hamster(Command c) {
        facing = 1;
        row = 0;
        col = 0;
        this.c = c;
    }

    public void move() {
        c.sendText("vor", "hamster22ws");
        switch (facing) {
            case 0 -> row--;
            case 1 -> col++;
            case 2 -> row++;
            case 3 -> col--;
        }
    }

    public void turnLeft() {
        c.sendText("linksUm", "hamster22ws");
        facing = (facing + 3) % 4;
    }

    public void pickUp() {
        c.sendText("nimm", "hamster22ws");
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getFacing() {
        return facing;
    }

}
