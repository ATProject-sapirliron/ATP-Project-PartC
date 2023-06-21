package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.search.Solution;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

//this class extends the observable class since the viewmodel watch her. it also implements the Imodel
public class MyModel extends Observable implements IModel{
    private Maze maze; //the maze
    private int PlayerRow; //players row location on maze
    private int PlayerCol; //players column location on maze
    private Solution solution; //the maze solution
    private MyMazeGenerator generator; //the maze generator
    Server mazeGeneratingServer; //the server that generates the maze
    Server solveSearchProblemServer; //the server that solves the maze
    private final Logger log = LogManager.getLogger(MyModel.class); //the logger

    //the constructor of the model
    public MyModel(){
        generator = new MyMazeGenerator(); //the generator if the maze
        this.maze = null; //the maze
        PlayerRow = 0; //player row
        PlayerCol = 0; //player column
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        solveSearchProblemServer.start();
        mazeGeneratingServer.start();

    }

    //this method returns the player row
    public int getPlayerRow() {
        return PlayerRow;
    }

    //this method returns the player column
    public int getPlayerCol() {
        return PlayerCol;
    }

    //this method sets players row
    public void setPlayerRow(int playerRow) {
        PlayerRow = playerRow;
    }

    //this method sets players column
    public void setPlayerCol(int playerCol) {
        PlayerCol = playerCol;
    }

    //this method returns the maze
    @Override
    public Maze getMaze() {
        return maze;
    }

    //this method updates player location
    @Override
    public void updatePlayerLocation(MovementDirection direction) {
        if(maze==null) { //checks if the maze exists, if not pops alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Cant move in ungenerated maze!");
            alert.getDialogPane().setGraphic(null);
            DialogPane dialogPane = alert.getDialogPane();
            BackgroundFill backgroundFill = new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY);
            Background background = new Background(backgroundFill);
            dialogPane.setBackground(background);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/jelly.png")));
            alert.showAndWait();
            return;
        }
        int row = PlayerRow; //set players row
        int col = PlayerCol; //sets players column
        switch (direction){ //check the direction
            case DIGIT8 -> {
                PlayerRow = checkIfCanMoveUp(PlayerRow,PlayerCol); //checks if player can go that direction
                if(PlayerRow != row){ //checks if row has changed
                    setChanged();
                    notifyObservers("player moved"); //update observers
                }
            }
            case DIGIT2 -> {
                PlayerRow = checkIfCanMoveDown(PlayerRow,PlayerCol); //checks if player can go that direction
                if(PlayerRow != row){ //checks if row has changed
                    setChanged();
                    notifyObservers("player moved"); //update observers
                }
            }
            case DIGIT6 -> {
                PlayerCol = checkIfCanMoveRight(PlayerRow,PlayerCol); //checks if player can go that direction
                if(PlayerCol != col){ //checks if row has changed
                    setChanged();
                    notifyObservers("player moved"); //update observers
                }
            }
            case DIGIT4 -> {
                PlayerCol = checkIfCanMoveLeft(PlayerRow,PlayerCol); //checks if player can go that direction
                if(PlayerCol != col){ //checks if row has changed
                    setChanged();
                    notifyObservers("player moved"); //update observers
                }
            }
            case DIGIT1 -> {
                if(checkIfCanMoveDownLeft()){ //checks if player can go that direction
                    PlayerRow +=1; //change player position
                    PlayerCol -=1; //change player position
                    if(PlayerRow != row){ //checks if row has changed
                        setChanged();
                        notifyObservers("player moved"); //update observers
                    }
                }
            }
            case DIGIT3 -> {
                if(checkIfCanMoveDownRight()){ //checks if player can go that direction
                    PlayerRow +=1; //change player position
                    PlayerCol +=1; //change player position
                    if(PlayerRow != row){ //checks if row has changed
                        setChanged();
                        notifyObservers("player moved"); //update observers
                    }
                }
            }
            case DIGIT7 -> {
                if(checkIfCanMoveUpLeft()){ //checks if player can go that direction
                    PlayerRow -=1; //change player position
                    PlayerCol -=1; //change player position
                    if(PlayerRow != row){ //checks if row has changed
                        setChanged();
                        notifyObservers("player moved"); //update observers
                    }
                }
            }
            case DIGIT9 -> {
                if(checkIfCanMoveUpRight()){ //checks if player can go that direction
                    PlayerRow -=1; //change player position
                    PlayerCol +=1; //change player position
                    if(PlayerRow != row){ //checks if row has changed
                        setChanged();
                        notifyObservers("player moved"); //update observers
                    }
                }
            }
        }
        if(PlayerRow==maze.getRow()-1 && PlayerCol == maze.getCol()-1){ //if we reach the goal position
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game End"); //show alert end game
            alert.setHeaderText(null);
            alert.setContentText("Congrats!");
            //alert design
            alert.getDialogPane().setGraphic(null);
            DialogPane dialogPane = alert.getDialogPane();
            BackgroundFill backgroundFill = new BackgroundFill(Color.rgb(222,119,224), CornerRadii.EMPTY, Insets.EMPTY);
            Background background = new Background(backgroundFill);
            dialogPane.setBackground(background);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/jelly.png")));
            alert.showAndWait();
            PlayerRow = 0; //reset player position
            PlayerCol = 0;
        }
    }

    //this method adds observer to the model
    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }

    //this method returns the solution of the maze
    @Override
    public Solution getSolution() {
        return solution;
    }

    //this method checks if the player can move up
    public int checkIfCanMoveUp(int row, int col){
        if(row-1>=0){ //if the location is valid
            if(maze.getplacevalue(row-1,col)==0){ //if the location is not a wall
                return row-1; //return new location
            }
        }
        return row; //return old location
    }

    //this method checks if the player can move down
    public int checkIfCanMoveDown(int row, int col){
        if(row+1<=maze.getRow()-1){ //if the location is valid
            if(maze.getplacevalue(row+1,col)==0){ //if the location is not a wall
                return row+1; //return new location
            }
        }
        return row; //return old location
    }

    //this method checks if the player can move right
    public int checkIfCanMoveRight(int row, int col){
        if(col+1<=maze.getCol()-1){ //if the location is valid
            if(maze.getplacevalue(row,col+1)==0){ //if the location is not a wall
                return col+1; //return new location
            }
        }
        return col; //return old location
    }

    //this method checks if the player can move left
    public int checkIfCanMoveLeft(int row, int col){
        if(col-1>=0){ //if the location is valid
            if(maze.getplacevalue(row,col-1)==0){ //if the location is not a wall
                return col-1;  //return new location
            }
        }
        return col; //return old location
    }

    //digit 7
    public boolean checkIfCanMoveUpLeft(){
        if(((checkIfCanMoveUp(PlayerRow, PlayerCol)!=PlayerRow)&&(checkIfCanMoveLeft(PlayerRow-1, PlayerCol)!=PlayerCol))||
                ((checkIfCanMoveLeft(PlayerRow, PlayerCol)!=PlayerCol)&&(checkIfCanMoveUp(PlayerRow, PlayerCol-1)!=PlayerRow))){
            return true;
        }
        return false;
    }

    //digit 9
    public boolean checkIfCanMoveUpRight(){
        if(((checkIfCanMoveUp(PlayerRow, PlayerCol)!=PlayerRow)&&(checkIfCanMoveRight(PlayerRow-1, PlayerCol)!=PlayerCol))||
                ((checkIfCanMoveRight(PlayerRow, PlayerCol)!=PlayerCol)&&(checkIfCanMoveUp(PlayerRow, PlayerCol+1)!=PlayerRow))){
            return true;
        }
        return false;
    }

    //digit 1
    public boolean checkIfCanMoveDownLeft(){
        if(((checkIfCanMoveDown(PlayerRow, PlayerCol)!=PlayerRow)&&(checkIfCanMoveLeft(PlayerRow+1, PlayerCol)!=PlayerCol))||
                ((checkIfCanMoveLeft(PlayerRow, PlayerCol)!=PlayerCol)&&(checkIfCanMoveDown(PlayerRow, PlayerCol-1)!=PlayerRow))){
            return true;
        }
        return false;
    }

    //digit 3
    public boolean checkIfCanMoveDownRight(){
        if(((checkIfCanMoveDown(PlayerRow, PlayerCol)!=PlayerRow)&&(checkIfCanMoveRight(PlayerRow+1, PlayerCol)!=PlayerCol))||
                ((checkIfCanMoveRight(PlayerRow, PlayerCol)!=PlayerCol)&&(checkIfCanMoveDown(PlayerRow, PlayerCol+1)!=PlayerRow))){
            return true;
        }
        return false;
    }

    //this method generates the maze by connecting to the server
    @Override
    public void generateMaze(int rows, int cols) {
        if((rows<=0 || cols<=0)){ //if the indexes are invalid show alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid Arguments!");
            alert.getDialogPane().setGraphic(null);
            DialogPane dialogPane = alert.getDialogPane();
            BackgroundFill backgroundFill = new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY);
            Background background = new Background(backgroundFill);
            dialogPane.setBackground(background);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/jelly.png")));
            alert.showAndWait();
            return;
        }
        try { //else connect with thw server and ask him to generate the maze
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{rows, cols};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = (byte[])fromServer.readObject();
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        int sum = 0;
                        sum = 2+ ((rows+256)/256)+((cols+256)/256)+(rows*cols); //calculate the length of the array
                        byte[] decompressedMaze = new byte[sum];
                        is.read(decompressedMaze);
                        maze = new Maze(decompressedMaze);
                        setChanged();
                        notifyObservers("maze generated");
                        log.info("maze generated");
                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
        }
    }

    //this method solves the maze
    @Override
    public void solveMaze() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(maze);
                        toServer.flush();
                        solution = (Solution)fromServer.readObject();
                        setChanged();
                        notifyObservers("maze solved"); //update all observers
                        log.info("maze solved"); //write to the log

                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }
                }
            });
            if(maze!=null){ //if maze is not null generate a solution
                client.communicateWithServer();
            }
            else{ //else pop an alert
                log.error("Can't solve ungenerated maze!");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Can't solve ungenerated maze!");
                alert.getDialogPane().setGraphic(null);
                DialogPane dialogPane = alert.getDialogPane();
                BackgroundFill backgroundFill = new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY);
                Background background = new Background(backgroundFill);
                dialogPane.setBackground(background);
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/jelly.png")));
                alert.showAndWait();
            }
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
        }

    }

    //this method generates the loaded maze
    public void generateloadMaze(int row, int col, int playerrow, int playercol, int[] position){
        maze = generator.generate(row,col); //generate the mazed as usual
        PlayerCol = playercol; //set latest player col
        PlayerRow = playerrow; //set latest player row
        int counter = 4;
        //run all over the maze and set the old values of the loaded maze
        for(int r = 0; r<row; r++){
            for(int c = 0; c<col; c++){
                maze.setPlace(r,c,position[counter]); //set old value
                counter++;
            }
        }
        setChanged();
        notifyObservers("maze generated"); //update all observers
        setChanged();
        notifyObservers("player moved"); //update all observers
    }

}
