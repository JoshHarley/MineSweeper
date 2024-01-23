package com.example.minesweeper;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
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




    public void startGame(Stage stage) throws Exception {
        //setup for the start screen
        VBox startLayout = new VBox();
        startLayout.setPrefSize(300, 400);
        startLayout.setAlignment(Pos.CENTER);
        startLayout.setSpacing(10);
        startLayout.setPadding(new Insets(20, 20, 20, 20));
        Label message = new Label("Choose your difficulty");
        Button confirm = new Button("Confirm");
        ObservableList<String> options = FXCollections.observableArrayList("Beginner", "Intermediate", "Expert");
        final ComboBox<String> difficultySelect = new ComboBox<>(options);
        difficultySelect.setPromptText("Select difficulty");
        difficultySelect.setEditable(true);
        difficultySelect.setOnAction(event -> {
                this.game.gameSetup(difficultySelect.getValue());
                for(int i = 0; i < this.game.getWidth(); i++) {
                    for (int j = 0; j < this.game.getHeight(); j++) {
                        this.logic.surroundingCheck(i, j);
                    }
                }
            }
        );

        //setup for the game grid screen
        BorderPane gameLayout = new BorderPane();
        gameLayout.setPrefSize(800, 400);
        GridPane gameGrid = new GridPane();
        HBox topBox = new HBox();
        Button restart = new Button("restart");

        Label bombsLeft = new Label();
        topBox.getChildren().addAll(restart, bombsLeft);
        topBox.setAlignment(Pos.CENTER_RIGHT);
        Scene gameScene = new Scene(gameLayout);

        gameLayout.setCenter(gameGrid);
        gameLayout.setTop(topBox);
        gameGrid.setAlignment(Pos.CENTER);

        bombsLeft.setFont(Font.font("Monospaced", 40));


        confirm.setOnMouseClicked((event) -> {
            bombsLeft.setText("" + this.game.bombsLeft());
            for(int i = 0; i < this.game.getWidth(); i++){
                for(int j = 0; j < this.game.getHeight(); j++){
                    Button btn = new Button();
                    btn.setPrefSize(40, 40);
                    btn.setFont(Font.font("Monospaced", 10));
                    btn.setBackground(blankCell);
                    btn.setVisible(true);
                    //btn.setText(game.getStatus(i, j).getMarker());
                    if(game.getStatus(i, j) == Markers.Bomb){
                        bombs.add(btn);
                    }
                    gameGrid.add(btn, i, j);
                    //this.buttons.add(btn);
                    Integer[] locBtn = new Integer[]{i, j};
                    this.buttonLocation.put(locBtn, btn);
                    final int row = i;
                    final int column = j;
                    btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            MouseButton mouseButton = mouseEvent.getButton();
                            if(mouseButton == MouseButton.PRIMARY) {
                                if(!(game.getStatus(row, column) == Markers.Flag)) {
                                    gameStatus = game.isBomb(row, column);
                                    if (gameStatus) {
                                        logic.goBoom(bombs);
                                    } else {
                                        ArrayList<Button> emptyButtons = logic.calculateAllSurroundings(row, column, buttonLocation);
                                        if (game.getStatus(row, column) == Markers.Empty) {
                                            for (Button emptyButton : emptyButtons) {
                                                emptyButton.setDisable(true);
                                            }
                                        }
                                        btn.setBackground(flatCell);
                                        btn.setText(game.getStatus(row, column).getMarker());
                                    }
                                }
                            }
                            if(mouseButton == MouseButton.SECONDARY){
                                if(!(game.getStatus(row, column) == Markers.Flag)) {
                                    logic.setFlag(btn, row, column, buttonLocation);
                                } else if (game.getStatus(row, column) == Markers.Flag){
                                    logic.removeFlag(btn, row, column);
                                }
                            }
                        }
                    });
                }
            }
            stage.setScene(gameScene);
        });
        startLayout.getChildren().addAll(message, difficultySelect, confirm);
        Scene startScreen = new Scene(startLayout);
        restart.setOnMouseClicked((event) -> {
            stage.close();
            try {
                start(stage);
            }catch(Exception e){
                System.out.println("Error: " + e.getMessage());
            };
        });
        stage.setTitle("MineSweeper");
        stage.setScene(startScreen);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(MineSweeperApplication.class);
    }

    @Override
    public void start(Stage mainStage) throws Exception{
        startGame(mainStage);
    }
}