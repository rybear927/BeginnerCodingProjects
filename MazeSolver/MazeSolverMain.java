package MazeSolver;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;




public class MazeSolverMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Maze maze = new Maze();
        final int CELL_SIZE = 30;

        //set up grid
        GridPane mazeGrid = new GridPane();
        mazeGrid.setVgap(1);
        mazeGrid.setHgap(1);
        mazeGrid.setPadding(new Insets(2));

        //set up squares and Xs in a StackPane array
        StackPane[][] stackPaneArray = new StackPane[maze.getMAZE_SIZE()][maze.getMAZE_SIZE()];
        for (int i = 0; i < maze.getMAZE_SIZE(); i++) {
            for (int j = 0; j < maze.getMAZE_SIZE(); j++) {
                StackPane stackPane = new StackPane();

                //add rectangle
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE, Color.WHITE);
                cell.setStroke(Color.BLACK);

                //add X for blocked cells
                Line xPart1 = new Line(0, 0, CELL_SIZE, CELL_SIZE);
                Line xPart2 = new Line(CELL_SIZE, 0, 0, CELL_SIZE);
                xPart1.setVisible(false);
                xPart2.setVisible(false);

                stackPane.getChildren().addAll(cell, xPart1, xPart2);

                //add the ability to click on a cell and add or remove an X
                final int row = i;
                final int col = j;
                stackPane.setOnMouseClicked(e -> {
                    if (stackPane.getChildren().get(1).isVisible()) {
                        maze.getMazeArray()[row][col] = 0;
                        stackPane.getChildren().get(1).setVisible(false);
                        stackPane.getChildren().get(2).setVisible(false);
                    } else {
                        maze.getMazeArray()[row][col] = 2;
                        stackPane.getChildren().get(1).setVisible(true);
                        stackPane.getChildren().get(2).setVisible(true);
                    }
                });

                //add stackpanes to gridpane
                stackPaneArray[i][j] = stackPane;
                mazeGrid.add(stackPaneArray[i][j], j, i);
            }
        }

        //set up the buttons
        Button btFindPath = new Button("Find Path");
        Button btClearPath = new Button("Clear Path");
        HBox buttonPane = new HBox(btFindPath, btClearPath);
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.setSpacing(3);
        buttonPane.setPadding(new Insets(4));

        //Find Path button functionality
        btFindPath.setOnAction(e -> {
            //find path
            maze.getMazeArray()[0][0] = maze.getFILLED();
            maze.findPath(0, 0);

            //color in squares on gui
            for (int i = 0; i < maze.getMAZE_SIZE(); i++) {
                for (int j = 0; j < maze.getMAZE_SIZE(); j++) {
                    if (maze.getMazeArray()[i][j] == maze.getFILLED()) {
                        Rectangle rectangle = (Rectangle) stackPaneArray[i][j].getChildren().get(0);
                        rectangle.setFill(Color.RED);
                    }
                }
            }
        });

        //Clear Path button functionality
        btClearPath.setOnAction(e -> {
            for (int i = 0; i < maze.getMAZE_SIZE(); i++) {
                for (int j = 0; j < maze.getMAZE_SIZE(); j++) {
                    if (maze.getMazeArray()[i][j] != maze.getBLOCKED()) {
                        Rectangle rectangle = (Rectangle) stackPaneArray[i][j].getChildren().get(0);
                        rectangle.setFill(Color.WHITE);
                        maze.getMazeArray()[i][j] = maze.getEMPTY();
                    }
                }
            }
        });

        VBox masterPane = new VBox(mazeGrid, buttonPane);
        masterPane.setSpacing(3);

        Scene scene = new Scene(masterPane);
        primaryStage.setTitle("Exercise 18_26");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
