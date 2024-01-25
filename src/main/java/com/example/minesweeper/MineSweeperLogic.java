package com.example.minesweeper;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MineSweeperLogic {
    private final MineSweeper game;
    Image blankCellImage = new Image("images/basicCell.png");
    BackgroundImage blankCellBackImage =new BackgroundImage(blankCellImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false, false));
    Background blankCell = new Background(blankCellBackImage);
    Image image = new Image("images/flag.png");
    BackgroundImage flagBackgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false, false));
    Background flagBackground = new Background(flagBackgroundImage);
    public MineSweeperLogic(MineSweeper _game){
        this.game = _game;
    }

    public void surroundingCheck(int x, int y) {
        Markers notBombs = Markers.Empty;
        if(game.getStatus(x + 1, y) == Markers.Bomb){
            notBombs = notBombs.next();
        }
        if(game.getStatus(x + 1, y - 1) == Markers.Bomb){
            notBombs = notBombs.next();
        }
        if(game.getStatus(x + 1, y + 1) == Markers.Bomb){
            notBombs = notBombs.next();
        }
        if(game.getStatus(x, y - 1) == Markers.Bomb){
            notBombs = notBombs.next();
        }
        if(game.getStatus(x, y + 1) == Markers.Bomb){
            notBombs = notBombs.next();
        }
        if(game.getStatus(x - 1, y) == Markers.Bomb){
            notBombs = notBombs.next();
        }
        if(game.getStatus(x - 1, y - 1) == Markers.Bomb){
            notBombs = notBombs.next();
        }
        if(game.getStatus(x -1, y + 1) == Markers.Bomb){
            notBombs = notBombs.next();
        }
        this.game.setStatus(x, y, notBombs);
    }

    public ArrayList<Button> calculateAllSurroundings(int x, int y, HashMap<Integer[], Button> map) {
        ArrayList<Button> buttons = new ArrayList<>();
        ArrayList<Integer[]> coords = new ArrayList<>();

        if (x >= 0 && y >= 0 && x < game.getWidth() && y < game.getHeight()) {

            ArrayList<Integer[]> newCoords = getSurroundingEmpties(x, y);
            coords.addAll(newCoords);
            do{
                newCoords.clear();
                newCoords.addAll(popList(coords));
                int size = newCoords.size() -  1;
                for(int i = size; i >= 0; i--){
                    Integer[] coord = newCoords.get(i);
                    if(!compare(coords,  coord)){
                        coords.add(coord);
                    } else {
                        newCoords.remove(coord);
                    }
                }
            }while(!newCoords.isEmpty());

            for(Integer[] coord: coords){
                for(Integer[] butLoc: map.keySet()){
                    if(Arrays.equals(butLoc, coord)){
                        buttons.add(map.get(butLoc));
                    }
                }

            }
        }
        return buttons;
    }

    public ArrayList<Integer[]> popList(ArrayList<Integer[]> coords){
        ArrayList<Integer[]> newCoords = new ArrayList<>();
        for(Integer[] coord: coords){
            int xx = coord[0];
            int yy = coord[1];
            if(game.getStatus(coord[0], coord[1]) == Markers.Empty) {
                newCoords.addAll(getSurroundingEmpties(xx, yy));
            }
        }
        return newCoords;
    }

    public boolean compare(ArrayList<Integer[]> list, Integer[] coord){
        for(Integer[] listCoord: list){
            if(Arrays.equals(listCoord, coord)){
                return true;
            }
        }
        return false;
    }

    public ArrayList<Integer[]> getSurroundingEmpties(int x, int y){
        ArrayList<Integer[]> coords = new ArrayList<>();
        if (x >= 0 && y >= 0 && x < game.getWidth() && y < game.getHeight()) {
            if (game.getStatus(x + 1, y) == Markers.Empty) {
                coords.add(new Integer[]{x + 1, y});
            }
            if (game.getStatus(x + 1, y + 1) == Markers.Empty) {
                coords.add(new Integer[]{x + 1, y + 1});
            }
            if (game.getStatus(x + 1, y - 1) == Markers.Empty) {
                coords.add(new Integer[]{x + 1, y - 1});
            }
            if (game.getStatus(x, y + 1) == Markers.Empty) {
                coords.add(new Integer[]{x, y + 1});
            }
            if (game.getStatus(x, y - 1) == Markers.Empty) {
                coords.add(new Integer[]{x, y - 1});
            }
            if (game.getStatus(x - 1, y + 1) == Markers.Empty) {
                coords.add(new Integer[]{x - 1, y - 1});
            }
            if (game.getStatus(x - 1, y) == Markers.Empty) {
                coords.add(new Integer[]{x - 1, y});
            }
            if (game.getStatus(x - 1, y - 1) == Markers.Empty) {
                coords.add(new Integer[]{x - 1, y - 1});
            }
        }
        coords.removeIf(coord -> game.getStatus(coord[0], coord[1]) != Markers.Empty);
        return coords;
    }

    public void goBoom(ArrayList<Button> bombs){
        Image image = new Image("images/bomb.png");
        BackgroundImage bombBackgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false, false));
        Background bombBackground = new Background(bombBackgroundImage);
        for(Button bomb: bombs){
            bomb.setBackground(bombBackground);
        }
    }

    public void setFlag(Button button, int row, int column, Map<Integer[], Button> buttonLocation){
        button.setBackground(flagBackground);
        Integer[] loc = new Integer[]{row, column};
        for(Integer[] key: buttonLocation.keySet()){
            if(Arrays.equals(key, loc)){
                game.setPreviousStatus(row, column, game.getStatus(row, column));
                break;
            }
        }
        this.game.decrementBombAmount();
        game.setStatus(row, column, Markers.Flag);
    }

    public void removeFlag(Button button, int row, int column){
        Markers beforeFlag = game.getPreviousStatus(row, column);
        button.setBackground(blankCell);
        this.game.incrementBombAmount();
        game.setStatus(row, column, beforeFlag);
    }

    public boolean win(Map<Integer[], Button> map){
        boolean win = true;
        for(Integer[] bomb: map.keySet()){
            if(game.getStatus(bomb[0], bomb[1]) == Markers.Bomb){
                win = false;
                break;
            }
        }
        return win;
    }
}
