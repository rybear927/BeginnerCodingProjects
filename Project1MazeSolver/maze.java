/**
 * Maze Class
 * 
 * This class represents a maze with functionalities to initialize, navigate, and modify it. 
 * It includes methods for path finding, random cell blocking, and checking for valid moves.
 * Part of the findPath method implementation involved assistance from ChatGPT for recursion logic.
 * 
 * Author: Ryan Hellmann
 * Date: 11/19/23
 * Note: Part of a project for learning and demonstrating recursion and array manipulation in Java.
 */

package Project1MazeSolver;

//imports
import java.util.Arrays;
import java.util.Random;

public class Maze {
    private final int EMPTY = 0;
    private final int FILLED = 1;
    private final int BLOCKED = 2;
    private final int VISITED = 3;
    private final int MAZE_SIZE = 8;
    private int[][] mazeArray;
    private final int[] START_LOCATION = {0, 0};
    private int currentRow;
    private int currentCol;
    private final int[] END_LOCATION = {MAZE_SIZE - 1, MAZE_SIZE - 1};
    private final int[][] MOVES = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

    public Maze() {
        mazeArray = new int[MAZE_SIZE][MAZE_SIZE];
        initializeMaze();
    }

    public Maze(int cellsToBlock) {
         mazeArray = new int[MAZE_SIZE][MAZE_SIZE];
         initializeMaze();
         randomlyBlockCells(cellsToBlock);
    }

    //fill array with FILLED and set start location
    public void initializeMaze() {
        for (int i = 0; i < mazeArray.length; i++) {
            Arrays.fill(mazeArray[i], EMPTY);
        }
        currentRow = START_LOCATION[0];
        currentCol = START_LOCATION[1];
    }

    //find path
    public boolean findPath(int row, int col) {
        if (row == END_LOCATION[0] && col == END_LOCATION[1]) {
            return true;
        }

        //go through list of possible moves
        for (int i = 0; i < MOVES.length; i++) {
            //if move is valid, move there and recursively call this function
            if (moveIsValid(MOVES[i], row, col)) {
                int prevRow = currentRow;
                int prevCol = currentCol;
                moveToLocation(MOVES[i], row, col);
                if (findPath(currentRow, currentCol)) {
                    return true;
                } else { //if not, mark the cell as visited so we know not to visit it again
                    mazeArray[currentRow][currentCol] = VISITED;
                    currentRow = prevRow;
                    currentCol = prevCol;
                }
            }
        }

        //there is no path
        return false;
    }

    //move to this location, mark it, and update currentRow and currentCol
    public void moveToLocation(int[] nextLocation, int currentRow, int currentCol) {
        int nextRow = currentRow + nextLocation[0];
        int nextCol = currentCol + nextLocation[1];
        mazeArray[nextRow][nextCol] = FILLED;
        this.currentRow = nextRow;
        this.currentCol = nextCol;
    }


    //declare if a move is valid
    public boolean moveIsValid(int[] nextLocation, int currentRow, int currentCol) {
        int nextRow = currentRow + nextLocation[0];
        int nextCol = currentCol + nextLocation[1];
        if (nextRow >= 0 && nextRow < MAZE_SIZE && nextCol >= 0 && nextCol < MAZE_SIZE) {
            if (mazeArray[nextRow][nextCol] == EMPTY && !completesASquare(nextRow, nextCol)) {
                return true;
            }
        }
        return false;
    }

    //check if this cell completes the 4th part of a square of cells, as a way to add a bit of pathing efficiency
    public boolean completesASquare(int row, int col) {
        //is it the top left part of a square?
        if (row < MAZE_SIZE - 1 && col < MAZE_SIZE - 1) { //is it far enough away from the edge?
            if (mazeArray[row][col + 1] == FILLED &&
                    mazeArray[row + 1][col] == FILLED &&
                    mazeArray[row + 1][col + 1] == FILLED) {
                return true;
            }
        }

        //is it the top right part of a square?
        if (row < MAZE_SIZE - 1 && col > 0) { //is it far enough away from the edge?
            if (mazeArray[row][col - 1] == FILLED &&
                    mazeArray[row + 1][col] == FILLED &&
                    mazeArray[row + 1][col - 1] == FILLED) {
                return true;
            }
        }

        //is it the bottom left part of a square?
        if (row > 0 && col < MAZE_SIZE - 1) { //is it far enough away from the edge?
            if (mazeArray[row - 1][col + 1] == FILLED &&
                    mazeArray[row - 1][col] == FILLED &&
                    mazeArray[row][col + 1] == FILLED) {
                return true;
            }
        }

        //is it the bottom right part of a square?
        if (row > 0 && col > 0) { //is it far enough away from the edge?
            if (mazeArray[row - 1][col - 1] == FILLED &&
                    mazeArray[row - 1][col] == FILLED &&
                    mazeArray[row][col - 1] == FILLED) {
                return true;
            }
        }

        return false;
    }

    //print to console for testing purposes
    public void printMaze() {
        //column markers
        System.out.print("    ");
        for (int i = 0; i < mazeArray.length; i++) {
            System.out.print(i + " ");
        }
        System.out.println("\n");

        for (int i = 0; i < mazeArray.length; i++) {
            System.out.print(i + "   "); //row markers
            for (int j = 0; j < mazeArray[i].length; j++) {
                System.out.print(mazeArray[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("-------------------");
    }

    //if user doesn't block cells manually, you can use this method to randomly block a specified number of cells
    public void randomlyBlockCells(int numberOfCellsToBlock) {
        Random random = new Random();
        int rowOfBlockage;
        int colOfBlockage;
        for (int i = 0; i < numberOfCellsToBlock; i++) {
            while(true) {
                rowOfBlockage = random.nextInt(MAZE_SIZE);
                colOfBlockage = random.nextInt(MAZE_SIZE);
                if (!(rowOfBlockage == 0 && colOfBlockage == 0)
                        && !(rowOfBlockage == MAZE_SIZE - 1 && colOfBlockage == MAZE_SIZE - 1)
                        && mazeArray[rowOfBlockage][colOfBlockage] == EMPTY) {
                    mazeArray[rowOfBlockage][colOfBlockage] = BLOCKED;
                    break;
                }
            }
        }
    }

    public int getMAZE_SIZE() {
        return MAZE_SIZE;
    }

    public int getBLOCKED() {
        return BLOCKED;
    }

    public int getFILLED() {
        return FILLED;
    }

    public int getEMPTY() {
        return EMPTY;
    }

    public int[][] getMazeArray() {
        return mazeArray;
    }
}
