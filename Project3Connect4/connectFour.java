/**
 * Connect Four Game Implementation
 *
 * This JavaFX application implements a Connect Four game with a graphical user interface.
 * It includes features like slot management, disc dropping animation, sound effects,
 * and winning condition checks.
 *
 * Challenges faced include issues with multiple transitions and audio file handling, specifically integrating background music
 * with other sound effects, which remains an area for future improvement.
 *
 * Author: Ryan Hellmann
 * Date: 10/19/23
 * Note: Developed as part of learning JavaFX and multimedia integration in Java applications.
 */

package Project3Connect4;

//imports
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;

public class connectFour extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Slot[][] slots = new Slot[6][7];
        Model board = new Model();

        //set up the board on a BorderPane
        BorderPane masterPane = new BorderPane();
        Pane boardPane = new Pane();
        masterPane.setCenter(boardPane);
        LinearGradient linearGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.LIGHTBLUE), new Stop(1, Color.DARKBLUE));
        boardPane.setBackground(new Background(new BackgroundFill(linearGradient, CornerRadii.EMPTY, Insets.EMPTY)));

        //set up the player indicator pane
        VBox playerIndicatorPane = new VBox(5);
        playerIndicatorPane.setPadding(new Insets(5));
        playerIndicatorPane.setAlignment(Pos.CENTER);
        playerIndicatorPane.setStyle("-fx-background-color: orange");
        Text playerIndicatorText = new Text("Current\nPlayer");
        playerIndicatorText.setFont(Font.font("Trebuchet MS", FontWeight.BLACK, 14));
        playerIndicatorText.setTextAlignment(TextAlignment.CENTER);
        Slot playerIndicatorSlot = new Slot(35, 35);
        playerIndicatorSlot.setColor(board.getCurrentColor());
        playerIndicatorPane.getChildren().addAll(playerIndicatorText, playerIndicatorSlot);
        masterPane.setRight(playerIndicatorPane);

        /*
        I could not get the song to consistently work. It has something to do with the drop sound below
        but it's way beyond me what's going on.
         */
        //Load song
//        File songFile = new File("resources/03 three d mambo.mp3");
//        Media media = new Media(songFile.toURI().toString());
//        MediaPlayer song = new MediaPlayer(media);
//        song.setCycleCount(MediaPlayer.INDEFINITE);

        //Set up drop sound
        File dropSoundFile = new File("resources/sf_laser_13.mp3");
        AudioClip clip = new AudioClip(dropSoundFile.toURI().toString());

        //set up slots on the pane
        for (int i = 0, y = 35; i < slots.length; i++, y += 70) {
            for (int j = 0, x = 35; j < slots[0].length; x += 70, j++) {
                slots[i][j] = new Slot(x, y);

                final int finalI = i;
                final int finalJ = j;
                slots[finalI][finalJ].setOnMouseClicked(e -> {
                    if (board.isBottommostOpenSlot(finalI, finalJ)) {

                        //falling disc
                        Slot fallingDisc = new Slot(slots[finalI][finalJ].getCenterX(), -35);
                        fallingDisc.setColor(board.getCurrentColor());
                        boardPane.getChildren().add(fallingDisc);

                        //transitions for the drop and bounce
                        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(400), fallingDisc);
                        translateTransition.setToY(slots[finalI][finalJ].getCenterY() - -35 + 10); // +10 to go under for the bounce effect
                        translateTransition.setOnFinished(f -> {
                            TranslateTransition bounceUp = new TranslateTransition(Duration.millis(50), fallingDisc);
                            bounceUp.setToY(slots[finalI][finalJ].getCenterY() - -35 - 5); //-5 for the bounce up
                            clip.play();

                            bounceUp.setOnFinished(g -> {
                                TranslateTransition settleDown = new TranslateTransition(Duration.millis(50), fallingDisc);
                                settleDown.setToY(slots[finalI][finalJ].getCenterY() - -35);

                                settleDown.setOnFinished(h -> {
                                    boardPane.getChildren().remove(fallingDisc);

                                    //background game logic
                                    slots[finalI][finalJ].setColor(board.getCurrentColor());
                                    board.placeDisc(finalI, finalJ);
                                    if (board.isThereAWinner(finalI, finalJ)) {
                                        showWinningScreen(masterPane);
                                    } else if (board.isThereADraw()) {
                                        System.out.println("There's a draw!");
                                    } else {
                                        board.switchCurrentColor();
                                        playerIndicatorSlot.setColor(board.getCurrentColor());
                                    }
                                });
                                settleDown.play();
                            });
                            bounceUp.play();
                        });
                        translateTransition.play();
                    }
                });
                boardPane.getChildren().add(slots[i][j]);
            }
        }

        Scene scene = new Scene(masterPane);
        primaryStage.setTitle("Connect Four");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void showWinningScreen(BorderPane parentPane) {
        //set up win screen pane
        HBox winningPane = new HBox();
        winningPane.setPrefHeight(parentPane.getHeight());
        winningPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8);");
        winningPane.setAlignment(Pos.CENTER);

        //load dog image
        ImageView mongsoonImage = new ImageView("resources/mongsoon win screen.jpg");
        mongsoonImage.setFitWidth(300);
        mongsoonImage.setPreserveRatio(true);
        winningPane.getChildren().add(mongsoonImage);

        parentPane.setCenter(winningPane);
        parentPane.setRight(null);
    }
}
