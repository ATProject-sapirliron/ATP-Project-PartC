package ViewModel;

import Model.IModel;
import Model.MovementDirection;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyEvent;

import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {
    private IModel model;
    public MyViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }

    public Maze getMaze() {
        return model.getMaze();
    }

    public int getRowChar() {
        return model.getPlayerRow();
    }

    public int getColChar() {
        return model.getPlayerCol();
    }
    public Solution getSolution() {
        return model.getSolution();
    }


    public void generateMaze(int rows, int cols){
        model.generateMaze(rows, cols);
    }

    public void movePlayer(KeyEvent keyEvent){
        MovementDirection direction = null;

        switch (keyEvent.getCode()) {

            case NUMPAD8 -> direction = MovementDirection.DIGIT8;
            case NUMPAD2 -> direction = MovementDirection.DIGIT2;
            case NUMPAD6 -> direction = MovementDirection.DIGIT6;
            case NUMPAD4 -> direction = MovementDirection.DIGIT4;
            case NUMPAD1 -> direction = MovementDirection.DIGIT1;
            case NUMPAD3 -> direction = MovementDirection.DIGIT3;
            case NUMPAD7 -> direction = MovementDirection.DIGIT7;
            case NUMPAD9 -> direction = MovementDirection.DIGIT9;


            case DIGIT8 -> direction = MovementDirection.DIGIT8;
            case DIGIT2 -> direction = MovementDirection.DIGIT2;
            case DIGIT6 -> direction = MovementDirection.DIGIT6;
            case DIGIT4 -> direction = MovementDirection.DIGIT4;
            case DIGIT1 -> direction = MovementDirection.DIGIT1;
            case DIGIT3 -> direction = MovementDirection.DIGIT3;
            case DIGIT7 -> direction = MovementDirection.DIGIT7;
            case DIGIT9 -> direction = MovementDirection.DIGIT9;
            default ->  {
                return;
            }
        }
        //keyEvent.consume();
        model.updatePlayerLocation(direction);
    }


    public void solveMaze(){
        model.solveMaze();
    }
}
