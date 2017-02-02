/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minesweeper;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Nacho Verdón
 */
public class Grid {
    private final int width;
    private final int height;
    private final int length;
    private int mineAmount = 0;
    private int flags = 0;
    private int[] minesLocation;
    private final String[] grid;
    private final List<Integer> cellsToReveal = new ArrayList<>();
    private final List<Integer> flagsLocation = new ArrayList<>();
    private final ArrayList movements = new ArrayList<>();
    private int movementsAmount = 0;
    private boolean lost = false;
    private boolean win = false;
    public String[] hiddenGrid;

    public static void main(String[] args) {}

    // Given its difficulty, widht and height, creates a grid of a calculated
    // size and generates its content.
    public Grid(int mines, int w, int h) {
        this.mineAmount = mines;
        this.flags = mines;
        this.width = w;
        this.height = h;
        this.length = this.width * this.height;
        this.grid = new String[this.length];
        this.hiddenGrid = new String[this.length];

        generate();
    }

    // Check if the cell is out of range.
    private boolean isInside(int cell) {
        return cell > -1 && cell < this.length;
    }

    // Checks if the given cell is a mine.
    private boolean isMine(int cell) {
        return this.grid[cell].equals(MineSweeper.MINE);
    }

    // Checks if the given cell is empty.
    private boolean isNothing(int cell) {
//        if (cell < 0 || cell > this.grid.length) return false;

        return this.grid[cell].equals(MineSweeper.NOTHING);
    }

    // Checks if the given cell is a number.
    private boolean isNumber(int cell) {
        int numberAsInt = Integer.parseInt(this.grid[cell]);

        return numberAsInt > 0;
    }

    private boolean isFlag(int cell) {
        return this.flagsLocation.contains(cell);
    }

    // Fill the holes without mines with NOTHING
    private void fillHoles() {
        for (int i = 0; i < this.length; i++) {
            this.grid[i] = MineSweeper.NOTHING;
            this.hiddenGrid[i] = MineSweeper.HIDDEN;
        }
    }

    // Set number of mines and
    // Randomly place mines until there's no 'mines'
    private void placeMines() {
        int minesLeft = this.mineAmount;
        Random rnd = new Random();
        this.minesLocation = new int[this.mineAmount];

        for (int i = 0; i < minesLeft; i++) {
            int randomCell = rnd.nextInt(this.length);

            if (!isMine(randomCell)) {
                this.grid[randomCell] = MineSweeper.MINE;
                this.minesLocation[i] = randomCell;
            } else {
                i--;
            }
        }

        Arrays.sort(this.minesLocation);
    }

    // Given a cell, return its surroundings
    private int[] surroundingCells(int cell) {
        int w = this.width,
        upLeft = cell - w - 1,
        up = cell - w,
        upRight = cell - w + 1,
        left = cell - 1,
        right = cell + 1,
        downLeft = cell + w - 1,
        down = cell + w,
        downRight = cell + w + 1;

        // If its on the left wall, don't wrap mines on the right wall
        if (cell % w == 0) {
            upLeft = -1;
            left = -1;
            downLeft = -1;
        } else if ((cell + 1) % w == 0) {
            upRight = -1;
            right = -1;
            downRight = -1;
        }

        int[] cells = {
                upLeft, up, upRight,
                left, right,
                downLeft, down, downRight
        };

        return cells;
    }

    // Given a cell number, increment its number by 1
    private void incrementCellNumber(int cell) {
        int cellNumberAsInt = Integer.parseInt(this.grid[cell]);
        String cellNumberAsString;

        cellNumberAsInt++;
        cellNumberAsString = Integer.toString(cellNumberAsInt);
        this.grid[cell] = cellNumberAsString;
    }


    // Given a list of cells, check if they are inside the grid
    // and if they are not mines, call the incrementCellNumber function on
    // them.
    private void incrementIfInside(int[] cells) {
        for (int cell: cells) {
            if (isInside(cell) && !isMine(cell)) {
                incrementCellNumber(cell);
            }
        }
    }

    // Places the numbers that indicate that mines are near
    // Several mines near will mean that the numbers will be higher.
    private void placeNumbers() {
        for (int i = 0; i < this.minesLocation.length; i++) {
            int cell = this.minesLocation[i];
            int[] cells = surroundingCells(cell);

            if (isMine(cell)) {
                incrementIfInside(cells);
            }
        }
    }

    // Generates the grid, placing the mines and the corresponding numbers.
    private void generate() {
        fillHoles();
        placeMines();
        placeNumbers();
    }

    // MAKE PRIVATE BEFORE DELIVER.
    // Prints the real grid on screen.
    public void rawPrint() {
        String line;

        for (int i = 0; i < this.height; i++) {
            line = "";

            for (int j = 0; j < this.width; j++) {
                line += " " + this.grid[(i * this.width) + j];
            }

            System.out.println(line);
        }

        System.out.println("\n");
    }

    // Prints the grid with hidden elements on screen.
    public void print() {
        System.out.println(printGrid());
    }

    // Returns the grid with hidden elements on screen.
    private String printGrid() {
        String gridToPrint = "";

        this.cellsToReveal.forEach((c) -> {
            showCell(c);
        });

        this.flagsLocation.forEach((c) -> {
            if (!this.hiddenGrid[c].equals(MineSweeper.DEFUSED)) {
                this.hiddenGrid[c] = MineSweeper.FLAG;
            }
        });

        System.out.println("     X-->");
        for (int y = 0; y < this.width + 1; y++) {
            if (y == 0) {
                System.out.print("     ");
            } else {
                System.out.print(y + " ");
            }
        }
        System.out.println("");

        for (int y = 0; y < this.height; y++) {

            if (y == 0) {
                gridToPrint += "\nY " + (y + 1) + "  ";
            } else if (y == 1) {
                gridToPrint += "| " + (y + 1) + "  ";
            } else if (y == 2) {
                gridToPrint += "v " + (y + 1) + "  ";
            } else {
                gridToPrint += "  " + (y + 1) + "  ";
            }

            for (int x = 0; x < this.width; x++) {
                gridToPrint += this.hiddenGrid[(y * this.width) + x] + " ";
            }

            gridToPrint += "\n";
        }

        return gridToPrint;
    }

    // Turns the cell in hiddenGrid the same as in grid.
    private void showCell(int cell) {
        this.hiddenGrid[cell] = this.grid[cell];
    }

    // Given a cell, reveals its content and its surroundings if necessary.
    private void reveal(int givenCell) {
        ArrayList<Integer> cellsChecked = new ArrayList<>();
        ArrayList<Integer> cellsToCheck = new ArrayList<>();
        cellsToCheck.add(givenCell);

        // If it's a mine, display all the mines and show a message then return.
        if (!isNothing(givenCell)) {
            if (isMine(givenCell)) {
                this.cellsToReveal.add(givenCell);
                this.lost = true;
                showMinesPositions();

                return;
            }
        }

        // Check the cell and its surroundings if it's empty.
        // Keep checking the surroundings as long as they are empty.
        // If they are numbers, queue them to be displayed
        // If they are mines, ignore them
        // Keeps track of cells already checked to avoid repetition.
        for (int i = 0; i < cellsToCheck.size(); i++) {
            int cell = cellsToCheck.get(i);

            // If cell is has not been checked yet
            if (cellsChecked.indexOf(cell) == -1 && isInside(cell)) {

                if (isNothing(cell)) {
                    int[] cells = surroundingCells(cell);

                    for (int c: cells) {
                        cellsToCheck.add(c);
                    }

                    this.cellsToReveal.add(cell);
                } else if (!isMine(cell)) {
                    this.cellsToReveal.add(cell);
                }

                cellsChecked.add(cell);

            }
        }

        print();
    }

    // Prints the grid with the location of the mines revealed.
    private void showMinesPositions() {
        for (int mine: this.minesLocation) {
            if (this.flagsLocation.contains(mine)) {
                this.hiddenGrid[mine] = MineSweeper.DEFUSED;
            } else {
                showCell(mine);
            }
        }

        print();
    }

    private void checkIfWon() {
        if (cellsLeft() == this.mineAmount && !this.lost) {
            this.win = true;
        }
    }

    private int cellsLeft() {
        return this.length - this.cellsToReveal.size();
    }

    // Given the coordenates, call the reveal method on the cell.
    // If it's the first move and the cell is a mine, regenerates the grid
    // to avoid losing on the first move.
    public String[] click(int x, int y) {
        int cell = (x - 1) + ((y - 1) * this.width);

        if (this.movements.contains(cell)) {
            System.out.println("Movimiento ya realizado");

            return this.hiddenGrid;
        }

        if (isFlag(cell)) {
            System.out.println("Esa casilla está ocupada por una banderita.");

            return this.hiddenGrid;
        }

        // To avoid clicking on a mine on the first move, keep
        // regenerating the grid if the cell is not empty.
        if (this.movements.isEmpty()) {

            while (!isNothing(cell)) {
                generate();
            }

        }

        reveal(cell);
        this.movements.add(cell);
        this.movementsAmount++;
        checkIfWon();

        return this.hiddenGrid;
    }

    // Returns false if the game is lost.
    public boolean hasLost() {
        return this.lost;
    }

    // Returns true if the game is won.
    public boolean hasWon() {
        return this.win;
    }

    // Returns the number of movements.
    public int getMovements() {
        return this.movementsAmount;
    }

    public void useFlag(int x, int y) {
        int cell = (x - 1) + ((y - 1) * this.width);

        if (!isInside(cell)) {
            return;
        }

        if (this.cellsToReveal.contains(cell)) {
            System.out.println("Casilla ya revelada. No se puede colocar banderita");

            return;
        }

        if (!isFlag(cell)) {
            this.flagsLocation.add(cell);
            this.flags--;
        } else if (isFlag(cell)) {
            int idx = this.flagsLocation.indexOf(cell);

            if (idx != -1) {
                this.flagsLocation.remove(idx);
                this.flags++;
            }
        }
    }

    public int flagsLeft() {
        return this.flags;
    }

}
