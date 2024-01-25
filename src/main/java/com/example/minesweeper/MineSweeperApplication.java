package com.example.minesweeper;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

public class MineSweeperApplication extends Application {
    boolean gameStatus = false;
    MineSweeper game = new MineSweeper();
    ArrayList<Button> bombs = new ArrayList<>();
    HashMap<Integer[], Button> buttonLocation = new HashMap<>();
    MineSweeperLogic logic = new MineSweeperLogic(this.game);
    Background blankCell = new Background(new BackgroundImage(new Image("images/basicCell.png"), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false, false)));

    Background flatCell = new Background(new BackgroundImage(new Image("images/flatCell.png"), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false, false)));
    Background normalRestart = new Background(new BackgroundImage(new Image("images/smiley.png"), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false, false)));
    Background winRestart = new Background(new BackgroundImage(new Image("images/smiley_cool.png"), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false, false)));
    Background loseRestart = new Background(new BackgroundImage(new Image("images/smiley_rip.png"), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false, false)));





    public void startGame(Stage stage) {

        //region Timer
        //Declare variables for timer
        Label timeLabel = new Label("0");
        AnimationTimer timer = new AnimationTimer() {
            private long timeStamp;
            private long time = 0;

            @Override
            public void start(){
                long fraction = 0;
                timeStamp = System.currentTimeMillis() - fraction;
                super.start();
            }

            @Override
            public void stop(){
                super.stop();
            }
            @Override
            public void handle(long l) {
                long newTime = System.currentTimeMillis();
                if(timeStamp + 1000 <= newTime){
                    long deltaT = (newTime - timeStamp) /1000;
                    time += deltaT;
                    timeStamp += 1000 * deltaT;
                    timeLabel.setText(Long.toString(time));
                }
            }
        };
        //endregion

        //setup for the start screen
        VBox startLayout = new VBox();
        startLayout.setPrefSize(300, 400);
        startLayout.setAlignment(Pos.CENTER);
        startLayout.setSpacing(10);
        startLayout.setPadding(new Insets(20, 20, 20, 20));
        Label message = new Label("Choose your difficulty");
        Button confirm = new Button("Confirm");
        timeLabel.setPrefWidth(40);
        timeLabel.setFont(Font.font("Monospaced", 40));
        ObservableList<String> options = FXCollections.observableArrayList("Beginner", "Intermediate", "Expert");
        final ComboBox<String> difficultySelect = new ComboBox<>(options);
        difficultySelect.setPromptText("Select difficulty");
        difficultySelect.setEditable(false);
        difficultySelect.setOnAction(event -> {
            this.game.gameSetup(difficultySelect.getValue(), logic);
            timer.start();
        });

        //setup for the game grid screen
        BorderPane gameLayout = new BorderPane();
        GridPane gameGrid = new GridPane();
        gameGrid.setAlignment(Pos.CENTER);
        Label bombsLeft = new Label();
        HBox topBox = new HBox();
        topBox.setAlignment(Pos.CENTER_RIGHT);
        Button restart = new Button();
        restart.setPrefSize(40, 40);
        restart.setBackground(normalRestart);
        Region region1 = new Region();
        HBox.setHgrow(region1, Priority.ALWAYS);
        Region region2 = new Region();
        HBox.setHgrow(region2, Priority.ALWAYS);

        topBox.getChildren().addAll(bombsLeft, region1, restart, region2, timeLabel);



        gameLayout.setCenter(gameGrid);
        gameLayout.setTop(topBox);

        Scene gameScene = new Scene(gameLayout);
        bombsLeft.setFont(Font.font("Monospaced", 40));


        confirm.setOnMouseClicked((event) -> {
            if(difficultySelect.getValue() != null) {
                bombsLeft.setText("" + this.game.bombsLeft());
                for (int i = 0; i < this.game.getWidth(); i++) {
                    for (int j = 0; j < this.game.getHeight(); j++) {
                        Button btn = new Button();
                        btn.setPrefSize(40, 40);
                        btn.setFont(Font.font("Monospaced", FontWeight.BOLD, 20));
                        btn.setBackground(blankCell);
                        btn.setVisible(true);
                        if (game.getStatus(i, j) == Markers.Bomb) {
                            bombs.add(btn);
                        }
                        gameGrid.add(btn, i, j);
                        Integer[] locBtn = new Integer[]{i, j};
                        this.buttonLocation.put(locBtn, btn);
                        final int row = i;
                        final int column = j;

                        btn.setOnMouseClicked(mouseEvent -> {
                            MouseButton mouseButton = mouseEvent.getButton();
                            if (mouseButton == MouseButton.PRIMARY) {
                                if (!(game.getStatus(row, column) == Markers.Flag)) {
                                    gameStatus = game.isBomb(row, column);
                                    if (gameStatus) {
                                        logic.goBoom(bombs);
                                        timer.stop();
                                        restart.setBackground(loseRestart);
                                        gameGrid.setMouseTransparent(true);
                                    } else {
                                        ArrayList<Button> emptyButtons = logic.calculateAllSurroundings(row, column, buttonLocation);
                                        if (game.getStatus(row, column) == Markers.Empty) {
                                            for (Button emptyButton : emptyButtons) {
                                                emptyButton.setBackground(flatCell);
                                            }
                                        }
                                        btn.setBackground(flatCell);
                                        btn.setText(game.getStatus(row, column).getMarker());
                                    }
                                }
                            }
                            if (mouseButton == MouseButton.SECONDARY) {
                                if (!(game.getStatus(row, column) == Markers.Flag)) {
                                    logic.setFlag(btn, row, column, buttonLocation);
                                    bombsLeft.setText("" + game.getBombAmount());
                                } else if (game.getStatus(row, column) == Markers.Flag) {
                                    logic.removeFlag(btn, row, column);
                                    bombsLeft.setText("" + game.getBombAmount());
                                }
                            }
                            if (Integer.parseInt(bombsLeft.getText()) == 0 && Long.parseLong(timeLabel.getText()) > 1) {
                                boolean win = logic.win(buttonLocation);
                                if (win) {
                                    restart.setBackground(winRestart);
                                    gameGrid.setMouseTransparent(true);
                                    timer.stop();
                                }
                            }
                        });
                    }
                }
                stage.setScene(gameScene);

            }
        });

        startLayout.getChildren().addAll(message, difficultySelect, confirm);
        Scene startScreen = new Scene(startLayout);

        //Restart button on click event - button should close down the current window and load up the start screen.
        restart.setOnMouseClicked((event) -> {
            stage.close();
            try {
                start(stage);
            }catch(Exception e){
                System.out.println("Error: " + e.getMessage());
            }
        });


        //set title and start screen for game
        stage.setTitle("MineSweeper");
        stage.setScene(startScreen);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(MineSweeperApplication.class);
    }

    //Override Application start function for restart button to action
    @Override
    public void start(Stage mainStage) throws Exception{
        startGame(mainStage);
    }
}