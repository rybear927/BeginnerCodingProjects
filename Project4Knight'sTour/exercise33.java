/**
 * Knight's Tour Animation
 *
 * This JavaFX application presents an interactive Knight's Tour challenge, allowing the user
 * to move a knight piece to any starting square on a chessboard and then visualize the knight's
 * tour through animation. The program features drag-and-drop mechanics for knight placement,
 * dynamic path calculation, and a path-tracing animation that highlights each move of the knight.
 *
 * The application combines GUI elements like buttons, image views, Timeline animation and drag-and-drop event handling.
 * It implements a recursive backtracking algorithm to solve the Knight's Tour problem. DropShadow effects
 * give feedback and a color-changing line helps the user understand the knight's path.
 *
 * I would say I wrote 80% of the code, and chatgpt showed me how to do the remaining 20%, which was mainly
 * using DropShadow, dragging and dropping the knight, and 3 lines in the recursive part of the logic.
 *
 * Author: Ryan Hellmann
 * Date: 12/11/23
 * Note: This project is based on an exercise from "Liang's Introduction to Java Programming, 12th Edition",
 *       Chapter 18, Exercise 33. 
 */


package Project4Knight'sTour;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class exercise33 extends Application {
    // Class-level constants
    private static final int EMPTY = 0;
    private static final int BOARD_SIZE = 8;
    private static final double GUI_SQUARE_SIZE = 50;
    private static final int[] KNIGHT_ROW_MOVES = {-2, -2, -1, -1, 1, 1, 2, 2};
    private static final int[] KNIGHT_COL_MOVES = {-1, 1, -2, 2, -2, 2, -1, 1};

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane chessBoardPane = new GridPane(); // the gui board
        Pane linePane = new Pane(); // the gui lines
        ChessSquare[][] chessSquares = new ChessSquare[BOARD_SIZE][BOARD_SIZE]; // the gui squares
        int[][] board = new int[BOARD_SIZE][BOARD_SIZE]; // the logic board
        int counter = 1;
        int startRow = 0;
        int startCol = 0;

        /*
        *
        GUI section below
        *
         */

        // configure linePane
        linePane.setMouseTransparent(true);

        // initialize GUI chess squares and put them in chessboardpane
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ChessSquare chessSquare = new ChessSquare();
                final int currentRow = row;
                final int currentCol = col;

                if ((row + col) % 2 == 0) {
                    chessSquare.setToDarkSquare();
                }
                chessSquares[row][col] = chessSquare;
                chessBoardPane.add(chessSquare, col, row);

                // enable highlighting when dragging knight over square
                chessSquare.setOnDragOver(e -> {
                    e.acceptTransferModes(TransferMode.MOVE);
                    chessSquare.getShadowRectangle().setEffect(new DropShadow(20, 1, 1, Color.DODGERBLUE));
                    e.consume();
                });

                // disable highlighting when exiting a square while dragging
                chessSquare.setOnDragExited(e -> chessSquare.getShadowRectangle().setEffect(null));
            }
        }

        // set the GUI knight on the board
        Image image = new Image(getClass().getResourceAsStream("/ch18/chapter18/knight-chess-piece.png"));
        ImageView knightPic = new ImageView(image);
        knightPic.setPreserveRatio(true);
        knightPic.setFitHeight(45);
        knightPic.setFitWidth(45);
        chessSquares[startRow][startCol].getChildren().add(knightPic);

        // repositioning the GUI knight functionality
        // (i could do a very basic version by myself, but chatgpt helped me make this version and talk me through
        // what each part does cause I wanted to learn something new and make it look cooler)
        knightPic.setOnDragDetected(e -> {
            linePane.getChildren().clear();
            Dragboard dragboard = knightPic.startDragAndDrop(TransferMode.MOVE);
            SnapshotParameters snapshotParameters = new SnapshotParameters();
            Image dragImage = knightPic.snapshot(snapshotParameters, null);
            dragboard.setDragView(dragImage);

            // set data to be transferred
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putImage(dragImage);
            dragboard.setContent(clipboardContent);

            e.consume();
        });

        // this loop allows us to set what dropping the GUI knight does in all the squares on the board
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                final int currentRow = row;
                final int currentCol = col;

                // set what dropping does
                chessSquares[row][col].setOnDragDropped(e -> {
                    Dragboard dragboard = e.getDragboard();
                    boolean success = false;

                    if (dragboard.hasImage()) {
                        // remove the knight
                        ((Pane) knightPic.getParent()).getChildren().remove(knightPic);

                        // add knight to new square
                        chessSquares[currentRow][currentCol].getChildren().add(knightPic);

                        success = true;
                    }

                    e.setDropCompleted(success);
                    e.consume();
                });
            }
        }

        // cleanup after dragging the GUI knight
        knightPic.setOnDragDone(e -> e.consume());

        // "Solve" button and its function of solving the algorithm and drawing lines on the board
        Button btSolve = new Button("Solve");
        btSolve.setOnAction(e -> {
            initializeBoard(board);
            linePane.getChildren().clear();
            int[] knightLocation = getKnightLocation(chessSquares, knightPic);

            // check if we could find the knight on the GUI board
            if (knightLocation[0] != -1) {
                board[knightLocation[0]][knightLocation[1]] = counter;
                tourTheBoard(board, knightLocation[0], knightLocation[1], counter + 1);

                int[][] orderOfMoves = getOrderOfMoves(board);

                // animate the knight moving from place to place and draw line
                int[] counterForKnightAnimation = new int[1];
                Timeline knightAnimation = new Timeline(new KeyFrame(Duration.millis(80), f -> {

                    //draw line
                    drawIndividualLines(linePane, orderOfMoves, counterForKnightAnimation[0]);

                    // remove knight
                    int currentRow = orderOfMoves[counterForKnightAnimation[0]][0];
                    int currentCol = orderOfMoves[counterForKnightAnimation[0]][1];
                    chessSquares[currentRow][currentCol].getChildren().remove(knightPic);

                    counterForKnightAnimation[0]++;

                    // place knight
                    int nextRow = orderOfMoves[counterForKnightAnimation[0]][0];
                    int nextCol = orderOfMoves[counterForKnightAnimation[0]][1];
                    chessSquares[nextRow][nextCol].getChildren().add(knightPic);
                }));
                knightAnimation.setCycleCount(63);
                knightAnimation.play();

            } else {
                // print to console that knight couldn't be found
                System.out.println("Uh, couldn't find the knight. Sorry.");
            }
        });

        // masterPane
        StackPane lineAndBoardPane = new StackPane(chessBoardPane, linePane);
        VBox masterPane = new VBox(lineAndBoardPane, btSolve);
        masterPane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(masterPane);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Knight's Tour GUI");
        primaryStage.show();
    }

    // draws a single line
    public static void drawIndividualLines(Pane linePane, int[][] orderOfMoves, int startIndex) {

            double startX = orderOfMoves[startIndex][1] * GUI_SQUARE_SIZE + GUI_SQUARE_SIZE / 2;
            double startY = orderOfMoves[startIndex][0] * GUI_SQUARE_SIZE + GUI_SQUARE_SIZE / 2;
            double endX = orderOfMoves[startIndex + 1][1] * GUI_SQUARE_SIZE + GUI_SQUARE_SIZE / 2;
            double endY = orderOfMoves[startIndex + 1][0] * GUI_SQUARE_SIZE + GUI_SQUARE_SIZE / 2;

            Line line = new Line(startX, startY, endX, endY);
            line.setMouseTransparent(true);

            //color transition (chatgpt helped with this part as I was not aware of Color.hsb previously)
            double hue = (360 / 63) * startIndex;
            line.setStroke(Color.hsb(hue, 1.0, 1.0));
            line.setStrokeWidth(1.5);

            linePane.getChildren().add(line);
    }

    // searches the GUI board and returns where user has placed knight
    public static int[] getKnightLocation(ChessSquare[][] chessSquares, ImageView knightPic) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (chessSquares[row][col].getChildren().contains(knightPic)) {
                    return new int[] {row, col};
                }
            }
        }
        return new int[] {-1, -1}; // failed to find knight
    }

    // this GUI class is a modified StackPane that has a Rectangle object acting as the chessboard square
    private static class ChessSquare extends StackPane {
        private Rectangle colorRectangle; // this holds the color
        private Rectangle shadowRectangle; // because colorRectangle butts up against the pain, the dropshadow effect
                                            // doesn't work well, so this slightly smaller  rectangle allows the dropshaddow to work properly

        public ChessSquare() {
            colorRectangle = new Rectangle(GUI_SQUARE_SIZE, GUI_SQUARE_SIZE, Color.web("#F0D9B5")); // light square
            shadowRectangle = new Rectangle(GUI_SQUARE_SIZE - 5, GUI_SQUARE_SIZE - 5, Color.web("#F0D9B5")); // light square
            getChildren().addAll(colorRectangle, shadowRectangle);
        }

        public void setToDarkSquare() {
            colorRectangle.setFill(Color.web("#B58863"));
            shadowRectangle.setFill(Color.web("#B58863"));
        }

        public Rectangle getShadowRectangle() {
            return shadowRectangle;
        }
    }

    /*
    *
    Logic section below
    *
     */

    // sets all cells to EMPTY
    public static void initializeBoard(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            Arrays.fill(board[i], EMPTY);
        }
    }

    // recursive method for finding the knight's tour solution
    public static boolean tourTheBoard(int[][] board, int row, int col, int counter) {
        if (counter == 65) {
            return true;
        }

        //label and sort possible moves by most available future moves
        ArrayList<Move> labeledMoves = labelPossibleMoves(board, row, col);
        Collections.sort(labeledMoves);
        for (Move move : labeledMoves) {
            // mark the board and recursively call method
            board[move.getRow()][move.getColumnn()] = counter;
            if (tourTheBoard(board, move.getRow(), move.getColumnn(), counter + 1)) {
                return true;
            }
            board[move.getRow()][move.getColumnn()] = EMPTY;
        }
        return false;
    }

    // returns all possible moves you can make from row, col
    public static ArrayList<Move> labelPossibleMoves(int[][] board, int row, int col) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        // go through each move in the knight's moveset
        for (int i = 0; i < KNIGHT_ROW_MOVES.length; i++) {
            int nextRow = KNIGHT_ROW_MOVES[i] + row;
            int nextCol = KNIGHT_COL_MOVES[i] + col;
            // if a move is valid, throw it in the list along with how many moves are possible from it
            if (moveIsValid(board, nextRow, nextCol)) {
                possibleMoves.add(new Move(nextRow, nextCol, getNumberOfOnwardMoves(board, nextRow, nextCol)));
            }
        }
        return possibleMoves; // return the list
    }

    // returns the number of moves you can make from a given position
    public static int getNumberOfOnwardMoves(int[][] board, int row, int col) {
        int count = 0;
        // go through all the moves in the knight's move set
        for (int i = 0; i < KNIGHT_ROW_MOVES.length; i++) {
            int nextRow = KNIGHT_ROW_MOVES[i] + row;
            int nextCol = KNIGHT_COL_MOVES[i] + col;
            // if a move is valid, increase the counter
            if (moveIsValid(board, nextRow, nextCol)) {
                count++;
            }
        }
        return count;
    }

    // check if the move is valid
    public static boolean moveIsValid(int[][] board, int row, int col) {
        if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
            return board[row][col] == EMPTY;
        }
        return false;
    }

    // prints board to console for testing purposes
    public static void printBoard(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(String.format("%2d ", board[i][j]));
            }
            System.out.println();
        }
        System.out.println("------------------");
    }

    // returns an ordered array of all the moves
    public static int[][] getOrderOfMoves(int[][] board) {
        int[][] orderedArray = new int[64][2];

        // go through the board, one row at a time. take the number at that location. that number minus 1 is the index
        // of the ordered array. the row and col are the location
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board.length; col++) {
                orderedArray[board[row][col] - 1] = new int[] {row, col};
            }
        }
        return orderedArray;
    }
}
