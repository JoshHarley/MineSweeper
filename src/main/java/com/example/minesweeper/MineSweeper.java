package com.example.minesweeper;

import javafx.scene.image.Image;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class MineSweeper {
    private Markers[][] mineSweeper;
    private int width;
    private int height;
    private int bombAmount;
    private HashMap<Integer[], Markers> previousStatus;


    public void gameSetup(String size, MineSweeperLogic logic){
        switch(size){
            case "Beginner": this.bombAmount = 10;
                        this.width = 8;
                        this.height = 8;
                        this.mineSweeper = new Markers[8][8];
                        this.previousStatus = new HashMap<>();
                        this.initCells();
                        this.setBomb();
                        this.setNumbers(logic);
                        break;
            case "Intermediate": this.bombAmount = 40;
                        this.width = 16;
                        this.height = 16;
                        this.mineSweeper = new Markers[16][16];
                        this.previousStatus = new HashMap<>();
                        this.initCells();
                        this.setBomb();
                        this.setNumbers(logic);
                        break;
            case "Expert": this.bombAmount = 99;
                        this.width = 30;
                        this.height = 16;
                        this.mineSweeper = new Markers[30][16];
                        this.previousStatus = new HashMap<>();
                        this.initCells();
                        this.setBomb();
                        this.setNumbers(logic);
                        break;
        }
    }

    public void initCells(){
        for(int i = 0; i < this.width; i++){
            for(int j = 0; j < this.height; j++){
                this.mineSweeper[i][j] = Markers.Empty;
            }
        }
    }

    public int getHeight(){
        return this.height;
    }

    public int getWidth(){
        return this.width;
    }
    public Markers getStatus(int x, int y){
        if(!(x >= this.width || y >= this.height || x < 0 || y < 0)){
            return this.mineSweeper[x][y];
        }
        return null;
    }

    public void setStatus(int x, int y, Markers value){
        if(x > this.width || y > this.height || x < 0 || y < 0){
            return;
        } else if (this.getStatus(x, y) == Markers.Bomb && value == Markers.Flag){
            this.mineSweeper[x][y] = value;
        } else if (this.getStatus(x, y) != Markers.Bomb) {
            this.mineSweeper[x][y] = value;
        }

    }

    public void setPreviousStatus(int x, int y, Markers status){
        this.previousStatus.put(new Integer[]{x, y}, status);
    }

    public Markers getPreviousStatus(int x, int y){
        Integer[] previous = new Integer[] {x, y};
        Markers resetMarker = Markers.Empty;
        for(Integer[] previousMarker: this.previousStatus.keySet()){
            if(Arrays.equals(previous, previousMarker)){
                resetMarker = this.previousStatus.get(previousMarker);
            }
        }
        return resetMarker;
    }

    private void setBomb(){
        Random rand = new Random();
        int bombsPlaced = 0;
        while(bombsPlaced < this.bombAmount){
            int row = rand.nextInt(this.width);
            int column = rand.nextInt(this.height);
            if(this.mineSweeper[row][column] == Markers.Bomb){
                continue;
            }
            this.mineSweeper[row][column] = Markers.Bomb;
            bombsPlaced++;
        }
    }

    public int bombsLeft(){
        int bombs = 0;
        for(int i = 0; i < this.width; i++){
            for(int j = 0; j < this.height; j++){
                if(this.mineSweeper[i][j].equals(Markers.Bomb)){
                    bombs += 1;
                }
            }
        }
        return bombs;
    }
    public boolean isBomb(int x, int y){
        Markers current = this.mineSweeper[x][y];
        if(current == (Markers.Bomb)){
            return true;
        }
        return false;
    }

    public int getBombAmount(){
        return this.bombAmount;
    }

    public void incrementBombAmount(){
        this.bombAmount++;
    }

    public void decrementBombAmount(){
        this.bombAmount--;
    }

    public void setNumbers(MineSweeperLogic logic){
        for (int i = 0; i < this.getWidth(); i++) {
            for (int j = 0; j < this.getHeight(); j++) {
                logic.surroundingCheck(i, j);
            }
        }
    }
}

enum Markers{
    Empty (" "),
    One ("1"),
    Two ("2"),
    Three ("3"),
    Four ("4"),
    Five ("5"),
    Six ("6"),
    Bomb ("B"),
    Flag ("X");

    private final String marker;
    private final Image bomb = new Image("images/bomb.png");
    Markers(String _marker){
        this.marker = _marker;
    }

    public String getMarker(){
        return this.marker;
    }
    public Image getImage(){
        return bomb;
    }

    private static final Markers[] values = values();

    public Markers next(){
        return values[this.ordinal() + 1 % values.length];
    }
}