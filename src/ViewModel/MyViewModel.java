package ViewModel;

import Model.IModel;
import View.MazeDisplayer;
import algorithms.mazeGenerators.Maze;
import javafx.scene.input.KeyEvent;

import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {
    private IModel model;
    private Maze maze;
    private int rowChar;
    private int colChar;

    public Maze getMaze() {
        return maze;
    }

    public int getRowChar() {
        return rowChar;
    }

    public int getColChar() {
        return colChar;
    }

    public MyViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof IModel){
            if(maze ==null){
                this.maze = model.getMaze();
            }
            else{
                Maze maze = model.getMaze();
                if(maze ==this.maze){
                    int rowChar = model.getPlayerRow();
                    int colChar = model.getPlayerCol();
                    if(this.colChar == colChar && this.rowChar == rowChar){
                        model.getSolution();
                    }
                    else{
                        this.rowChar = rowChar;
                        this.colChar = colChar;
                    }
                }
                else{
                    this.maze = maze;
                }
            }
        }
        setChanged();
        notifyObservers(arg);
    }

    public void generateMaze(int rows, int cols){
        model.generateMaze(rows, cols);
    }

    public void movePlayer(KeyEvent keyEvent){

    }

    public void solveMaze(Maze maze){
        model.solveMaze(maze);
    }

    public void getSolution() {
        model.getSolution();
    }
}
