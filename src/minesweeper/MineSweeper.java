/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minesweeper;

import java.util.Scanner;
/**
 *
 * @author Nacho Verdón
 */
public class MineSweeper {
    private final static String YES = "SI";
    private final static String NO = "NO";

    private final static String OPT_MOVE = "M";
    private final static String OPT_FLAG = "B";
    private final static String OPT_REPLAY = "N";

    private final static String EASY = "EASY";
    private final static String MEDIUM = "MEDIUM";
    private final static String HARD = "HARD";
    private final static String CUSTOM = "CUSTOM";

    public final static String HIDDEN = "#";
    public final static String MINE = "*";
    public final static String DEFUSED = "+";
    public final static String FLAG = "F";
    public final static String NOTHING = "0";

    private final static int MINES_AT_EASY = 5;
    private final static int MINES_AT_MEDIUM = 10;
    private final static int MINES_AT_HARD = 15;

    private final static int MIN_WIDTH_HEIGHT = 4;
    private final static int MAX_NUMBER = 4;

    private final Scanner sc = new Scanner(System.in);
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MineSweeper game = new MineSweeper();
    }

    public MineSweeper() {
        System.out.println("Bienvenido al juego del buscaminas.");
        System.out.println(
            "\nLeyenda:\n" +
                "Casilla tapada (" + MineSweeper.HIDDEN + "), " +
                "Casilla vacía (" + MineSweeper.NOTHING + "), " +
                "Número de minas al rededor (1-8)" +
                "\n" +
                "Mina (" + MineSweeper.MINE + "), " +
                "Mina desactivada (" + MineSweeper.DEFUSED + "), " +
                "Banderita (" + MineSweeper.FLAG + ")" +
                "\n"
        );
        Grid grid = newGame();
        play(grid);
    }

    // Given a message, prints it on screen and asks for an Integer
    private int selectInt(String msg) {
        System.out.println(msg);
        int num;

        while (!sc.hasNextInt()) {
            System.out.println("Por favor, introduce un número válido.");
            sc.next();
        }

        num = sc.nextInt();

        while (num < 0 || num > MineSweeper.MAX_NUMBER) {
            num = selectInt("El número tiene que ser entre 1 y " +
                    MineSweeper.MAX_NUMBER);
        }

        return num;
    }

    private int selectDifficulty(String difficulty) {
        int mines;

        switch (difficulty.toUpperCase()) {
            case MineSweeper.EASY:
                mines = MineSweeper.MINES_AT_EASY;
                break;
            case MineSweeper.MEDIUM:
                mines = MineSweeper.MINES_AT_MEDIUM;
                break;
            case MineSweeper.HARD:
                mines = MineSweeper.MINES_AT_HARD;
                break;
            case MineSweeper.CUSTOM:
                mines = selectInt("Cantidad de minas: ");
                break;
            default:
                System.out.println("Por favor, introduce una dificultad válida: ");
                difficulty = sc.next();
                mines = selectDifficulty(difficulty);
        }

        return mines;
    }

    private int selectIntWhileSmaller(String msg, int condition) {
        int num = selectInt(msg);

        while (num < condition) {
            System.out.println("El número es inferior al mínimo (" +
                    condition + ")."
            );
            num = selectInt(msg);
        }

        return num;
    }

    private int selectWidthOrHeight(String msg) {
        int min = MineSweeper.MIN_WIDTH_HEIGHT;
        int widthOrHeight = selectIntWhileSmaller(msg, min);

        return widthOrHeight;
    }

    private Grid newGame() {
        int width = selectWidthOrHeight("Selecciona el ancho del campo: ");
        int height = selectWidthOrHeight("Selecciona el alto del campo: ");
        int mines;
        int maxMines;
        String difficulty;

        System.out.println("Dificultades: '" +
                MineSweeper.EASY + "', '" +
                MineSweeper.MEDIUM + "', '" +
                MineSweeper.HARD + "' o '" +
                MineSweeper.CUSTOM +
                "'.\nSelecciona una dificultad: "
        );

        difficulty = sc.next().toUpperCase();
        mines = selectDifficulty(difficulty);
        maxMines = (width * height) - 2;

        while (mines >= maxMines) {
            System.out.println(
                "La cantidad de minas es superior al número " +
                "de celdas del campo."
            );
            mines = selectInt("Cantidad de minas: ");
        }

        return new Grid(mines, width, height);
    }

    private boolean isYes(String txt) {
        txt = txt.toUpperCase();
        return txt.equals(MineSweeper.YES);
    }

    private boolean isNo(String txt) {
        txt = txt.toUpperCase();
        return txt.equals(MineSweeper.NO);
    }

    private void menu(Grid grid) {
        String action;

        if (grid.hasWon()) {
            String congratulations = "Enhorabuena, has ganado en " +
                grid.getMovements() + " movimiento(s). 8)";

            if (askReplay(congratulations)) {
                replay(grid);
            } else {
                return;
            }
        }

        System.out.println("¿Qué quieres hacer? :)");
        System.out.println(
                "Mover (" + MineSweeper.OPT_MOVE + "), " +
                "añadir/quitar banderas (" + MineSweeper.OPT_FLAG + ") " +
                "o empezar de nuevo (" + MineSweeper.OPT_REPLAY + ")" +
                "\n" +
                grid.flagsLeft() + " banderas. | " +
                grid.getMovements() + " movimientos realizados."
        );

        action = sc.next();

        switch (action.toUpperCase()) {
            case MineSweeper.OPT_MOVE:
                play(grid);
                break;
            case MineSweeper.OPT_FLAG:
                useFlag(grid);
                break;
            case MineSweeper.OPT_REPLAY:
                replay(grid);
                break;
            default:
                System.out.println("No entiendo esa acción.");
                menu(grid);
                break;
        }
    }

    private void replay(Grid grid) {
        grid = newGame();
        play(grid);
    }

    private void useFlag(Grid grid) {
        int x = selectInt("Dame una posición X: ");
        int y = selectInt("Dame una posición Y: ");

        grid.useFlag(x, y);
        grid.print();
        menu(grid);
    }

    private boolean askReplay(String msg) {
        System.out.println(msg);
        String playAgain = "¿Quieres jugar de nuevo? ('" +
                MineSweeper.YES + "' / '" +
                MineSweeper.NO + "')";
        String play = "";

        while (!isYes(play) && !isNo(play)) {
            System.out.println(playAgain);
            play = sc.next();
        }

        if (isYes(play)) {
            return true;
        } else {
            System.out.println("Nos vemos. ;)");
            return false;
        }
    }

    private void play(Grid grid) {
        grid.print();

        if (!grid.hasLost()) {

            int x = selectInt("Dame una posición X: ");
            int y = selectInt("Dame una posición Y: ");

            System.out.println(":o\n");

            grid.click(x, y);

            if (grid.hasLost()) {
                if (askReplay("Has perdido x(")) {
                        replay(grid);
                }
                return;
            }

            menu(grid);
        }

//        if (grid.hasLost()) {
//            if (askReplay("Has perdido x(")) {
//                    replay(grid);
//            }
//        }
    }
}
