package ViewModel;

import Model.IModel;
import Model.MovementDirection;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

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
    public void setColChar(int col) {

        model.setPlayerCol(col);
    }

    public void setRowChar(int row) {
        model.setPlayerRow(row);
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

    public void moveCharacter(MouseEvent mouseEvent, double CellHeight, double CellWidth){
        double mousex = mouseEvent.getX();
        double mousey = mouseEvent.getY();

        int row = (int)(mousey/CellHeight);
        int col = (int)(mousex/CellWidth);
        MovementDirection direction = null;
        //1
        if(row==this.getRowChar()+1 && col==getColChar()-1){
            direction = MovementDirection.DIGIT1;
            model.updatePlayerLocation(direction);
        }
        //2
        else if(row==this.getRowChar()+1 && col==getColChar()){
            direction = MovementDirection.DIGIT2;
            model.updatePlayerLocation(direction);
        }
        //3
        else if(row==this.getRowChar()+1 && col==getColChar()+1){
            direction = MovementDirection.DIGIT3;
            model.updatePlayerLocation(direction);
        }
        //4
        else if(row==this.getRowChar() && col==getColChar()-1){
            direction = MovementDirection.DIGIT4;
            model.updatePlayerLocation(direction);
        }
        //6
        else if(row==this.getRowChar() && col==getColChar()+1){
            direction = MovementDirection.DIGIT6;
            model.updatePlayerLocation(direction);
        }
        //7
        else if(row==this.getRowChar()-1 && col==getColChar()-1){
            direction = MovementDirection.DIGIT7;
            model.updatePlayerLocation(direction);
        }
        //8
        else if(row==this.getRowChar()-1 && col==getColChar()){
            direction = MovementDirection.DIGIT8;
            model.updatePlayerLocation(direction);
        }
        //9
        else if(row==this.getRowChar()-1 && col==getColChar()+1){
            direction = MovementDirection.DIGIT9;
            model.updatePlayerLocation(direction);
        }
        else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid Step!\n Player cant move more than one step at a time!");
            alert.getDialogPane().setGraphic(null);
            DialogPane dialogPane = alert.getDialogPane();
            BackgroundFill backgroundFill = new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY);
            Background background = new Background(backgroundFill);
            dialogPane.setBackground(background);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/jelly.png")));
            alert.showAndWait();
        }
    }

    public void callgenerateloadMaze(int row, int col, int playerrow, int playercol, int[] position){
        model.generateloadMaze(row, col, playerrow, playercol, position);
    }
}
