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

//this class represents the view model. the view observes it and the view model observes the model.
public class MyViewModel extends Observable implements Observer {
    private IModel model; //model instance in order to communicate with it

    //the constructor of the view model
    public MyViewModel(IModel model) {
        this.model = model; //set the model instance
        this.model.assignObserver(this); //add the view model as an observer of the model
    }

    //this method updates the observable (view)
    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }

    //this method returns the maze
    public Maze getMaze() {
        return model.getMaze();
    }

    //this method returns row player position
    public int getRowChar() {
        return model.getPlayerRow();
    }

    //this method returns column player position
    public void setColChar(int col) {
        model.setPlayerCol(col);
    }

    //this method sets row player position
    public void setRowChar(int row) {
        model.setPlayerRow(row);
    }

    //this method sets column player position
    public int getColChar() {
        return model.getPlayerCol();
    }

    //this method returns the maze solution
    public Solution getSolution() {
        return model.getSolution();
    }

    //this method calls the model to generate the maze
    public void generateMaze(int rows, int cols){
        model.generateMaze(rows, cols); //transform the model the maze sizes
    }

    //this method passes the user request to move the player
    public void movePlayer(KeyEvent keyEvent){
        MovementDirection direction = null; //direction from user

        switch (keyEvent.getCode()) {
            //check which key was pressed by keyboard or mouse
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
        model.updatePlayerLocation(direction); //updates player location on model
    }


    //this method solves the maze by passing the request to the model
    public void solveMaze(){
        model.solveMaze();
    }

    //this method moves the character according to the mouse event transformed from the controller
    public void moveCharacter(MouseEvent mouseEvent, double CellHeight, double CellWidth){
        double mousex = mouseEvent.getX(); //get mouse click x position
        double mousey = mouseEvent.getY(); //get mouse click y position

        int row = (int)(mousey/CellHeight); //get row calculation on maze
        int col = (int)(mousex/CellWidth); //get col calculation on maze
        MovementDirection direction = null;
        //1
        if(row==this.getRowChar()+1 && col==getColChar()-1){ //checks which movement is it
            direction = MovementDirection.DIGIT1; //set direction
            model.updatePlayerLocation(direction); //updates player location
        }
        //2
        else if(row==this.getRowChar()+1 && col==getColChar()){ //checks which movement is it
            direction = MovementDirection.DIGIT2; //set direction
            model.updatePlayerLocation(direction); //updates player location
        }
        //3
        else if(row==this.getRowChar()+1 && col==getColChar()+1){ //checks which movement is it
            direction = MovementDirection.DIGIT3; //set direction
            model.updatePlayerLocation(direction); //updates player location
        }
        //4
        else if(row==this.getRowChar() && col==getColChar()-1){ //checks which movement is it
            direction = MovementDirection.DIGIT4; //set direction
            model.updatePlayerLocation(direction); //updates player location
        }
        //6
        else if(row==this.getRowChar() && col==getColChar()+1){ //checks which movement is it
            direction = MovementDirection.DIGIT6; //set direction
            model.updatePlayerLocation(direction); //updates player location
        }
        //7
        else if(row==this.getRowChar()-1 && col==getColChar()-1){ //checks which movement is it
            direction = MovementDirection.DIGIT7; //set direction
            model.updatePlayerLocation(direction); //updates player location
        }
        //8
        else if(row==this.getRowChar()-1 && col==getColChar()){ //checks which movement is it
            direction = MovementDirection.DIGIT8; //set direction
            model.updatePlayerLocation(direction); //updates player location
        }
        //9
        else if(row==this.getRowChar()-1 && col==getColChar()+1){ //checks which movement is it
            direction = MovementDirection.DIGIT9; //set direction
            model.updatePlayerLocation(direction); //updates player location
        }
        else{ //if movment is not valid pop an alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid Step!\n Player cant move more than one step at a time!");
            //alert design
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

    //this method calls the model to generate the loaded maze sent from the view
    public void callgenerateloadMaze(int row, int col, int playerrow, int playercol, int[] position){
        model.generateloadMaze(row, col, playerrow, playercol, position);
    }
}