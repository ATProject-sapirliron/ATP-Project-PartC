package Model;

import Client.*;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.*;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.search.*;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MyModel extends Observable implements IModel{
    private Maze maze;
    private int PlayerRow;
    private int PlayerCol;
    private Solution solution;

    private MyMazeGenerator generator;
    Server mazeGeneratingServer;
    Server solveSearchProblemServer;


    public MyModel(){
        generator = new MyMazeGenerator();
        this.maze = null;
        PlayerRow = 0;
        PlayerCol = 0;
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        solveSearchProblemServer.start();
        mazeGeneratingServer.start();
    }

    public int getPlayerRow() {
        return PlayerRow;
    }

    public int getPlayerCol() {
        return PlayerCol;
    }

    public void setPlayerRow(int playerRow) {
        PlayerRow = playerRow;
    }

    public void setPlayerCol(int playerCol) {
        PlayerCol = playerCol;
    }

    @Override
    public Maze getMaze() {
        return maze;
    }

    @Override
    public void updatePlayerLocation(MovementDirection direction) {
        if(maze==null) {
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
        int row = PlayerRow;
        int col = PlayerCol;
        switch (direction){
            case DIGIT8 -> {
                PlayerRow = checkIfCanMoveUp(PlayerRow,PlayerCol);
                if(PlayerRow != row){
                    setChanged();
                    notifyObservers("player moved");
                }
            }
            case DIGIT2 -> {
                PlayerRow = checkIfCanMoveDown(PlayerRow,PlayerCol);
                if(PlayerRow != row){
                    setChanged();
                    notifyObservers("player moved");
                }
            }
            case DIGIT6 -> {
                PlayerCol = checkIfCanMoveRight(PlayerRow,PlayerCol);
                if(PlayerCol != col){
                    setChanged();
                    notifyObservers("player moved");
                }
            }
            case DIGIT4 -> {
                PlayerCol = checkIfCanMoveLeft(PlayerRow,PlayerCol);
                if(PlayerCol != col){
                    setChanged();
                    notifyObservers("player moved");
                }
            }
            case DIGIT1 -> {
                if(checkIfCanMoveDownLeft()){
                    PlayerRow +=1;
                    PlayerCol -=1;
                    if(PlayerRow != row){
                        setChanged();
                        notifyObservers("player moved");
                    }
                }
            }
            case DIGIT3 -> {
                if(checkIfCanMoveDownRight()){
                    PlayerRow +=1;
                    PlayerCol +=1;
                    if(PlayerRow != row){
                        setChanged();
                        notifyObservers("player moved");
                    }
                }
            }
            case DIGIT7 -> {
                if(checkIfCanMoveUpLeft()){
                    PlayerRow -=1;
                    PlayerCol -=1;
                    if(PlayerRow != row){
                        setChanged();
                        notifyObservers("player moved");
                    }
                }
            }
            case DIGIT9 -> {
                if(checkIfCanMoveUpRight()){
                    PlayerRow -=1;
                    PlayerCol +=1;
                    if(PlayerRow != row){
                        setChanged();
                        notifyObservers("player moved");
                    }
                }
            }
        }
        if(PlayerRow==maze.getRow()-1 && PlayerCol == maze.getCol()-1){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game End");
            alert.setHeaderText(null);
            alert.setContentText("Congrats!");
            alert.getDialogPane().setGraphic(null);
            DialogPane dialogPane = alert.getDialogPane();
            BackgroundFill backgroundFill = new BackgroundFill(Color.rgb(222,119,224), CornerRadii.EMPTY, Insets.EMPTY);
            Background background = new Background(backgroundFill);
            dialogPane.setBackground(background);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/jelly.png")));
            alert.showAndWait();
            PlayerRow = 0;
            PlayerCol = 0;
        }
    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }

    @Override
    public Solution getSolution() {
        return solution;
    }

    public int checkIfCanMoveUp(int row, int col){
        if(row-1>=0){
            if(maze.getplacevalue(row-1,col)==0){
                return row-1;
            }
        }
        return row;
    }
    public int checkIfCanMoveDown(int row, int col){
        if(row+1<=maze.getRow()-1){
            if(maze.getplacevalue(row+1,col)==0){
                return row+1;
            }
        }
        return row;
    }
    public int checkIfCanMoveRight(int row, int col){
        if(col+1<=maze.getCol()-1){
            if(maze.getplacevalue(row,col+1)==0){
                return col+1;
            }
        }
        return col;
    }
    public int checkIfCanMoveLeft(int row, int col){
        if(col-1>=0){
            if(maze.getplacevalue(row,col-1)==0){
                return col-1;
            }
        }
        return col;
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

    @Override
    public void generateMaze(int rows, int cols) {
        if((rows<=0 || cols<=0)){
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
        try {
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
                        sum = 2+ ((rows+256)/256)+((cols+256)/256)+(rows*cols);
                        byte[] decompressedMaze = new byte[sum];
                        is.read(decompressedMaze);
                        maze = new Maze(decompressedMaze);
                        setChanged();
                        notifyObservers("maze generated");
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
                        notifyObservers("maze solved");

                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }
                }
            });
            if(maze!=null){
                client.communicateWithServer();
            }
            else{
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

    public void generateloadMaze(int row, int col, int playerrow, int playercol, int[] position){
        maze = generator.generate(row,col);
        PlayerCol = playercol;
        PlayerRow = playerrow;
        int counter = 4;
        for(int r = 0; r<row; r++){
            for(int c = 0; c<col; c++){
                maze.setPlace(r,c,position[counter]);
                counter++;
            }
        }
        setChanged();
        notifyObservers("maze generated");
        setChanged();
        notifyObservers("player moved");
    }

}
