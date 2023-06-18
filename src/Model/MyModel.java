package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.search.BestFirstSearch;
import algorithms.search.ISearchingAlgorithm;
import algorithms.search.SearchableMaze;
import algorithms.search.Solution;
import javafx.scene.control.Alert;

import java.util.Observable;
import java.util.Observer;

public class MyModel extends Observable implements IModel{
    private Maze maze;
    private int PlayerRow;
    private int PlayerCol;
    private Solution solution;

    private MyMazeGenerator generator;


    public MyModel(){
        generator = new MyMazeGenerator();
        this.maze = null;
        PlayerRow = 0;
        PlayerCol = 0;
    }

    public int getPlayerRow() {
        return PlayerRow;
    }

    public int getPlayerCol() {
        return PlayerCol;
    }

    @Override
    public void generateMaze(int rows, int cols) {
        maze = generator.generate(rows,cols);
        setChanged();
        notifyObservers("maze generated");
    }

    @Override
    public Maze getMaze() {
        return maze;
    }

    @Override
    public void updatePlayerLocation(MovementDirection direction) {
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
                if((checkIfCanMoveDown(PlayerRow,PlayerCol)!=PlayerRow)&&(checkIfCanMoveLeft(PlayerRow,PlayerCol)!=PlayerCol)&&
                        (PlayerRow+1>=0)&&(PlayerCol-1<=maze.getCol()-1)&&(maze.getplacevalue(PlayerRow+1, PlayerCol-1)==0)){
                    PlayerRow = checkIfCanMoveDown(PlayerRow, PlayerCol);
                    PlayerCol = checkIfCanMoveLeft(PlayerRow, PlayerCol);
                    if(PlayerRow != row){
                        setChanged();
                        notifyObservers("player moved");
                    }
                }
            }
            case DIGIT3 -> {
                if((checkIfCanMoveDown(PlayerRow, PlayerCol)!=PlayerRow)&&(checkIfCanMoveRight(PlayerRow, PlayerCol)!=PlayerCol)&&
                        (PlayerRow+1>=0)&&(PlayerCol+1<=maze.getCol()-1)&&(maze.getplacevalue(PlayerRow+1, PlayerCol+1)==0)){
                    PlayerRow = checkIfCanMoveDown(PlayerRow, PlayerCol);
                    PlayerCol = checkIfCanMoveRight(PlayerRow, PlayerCol);
                    if(PlayerRow != row){
                        setChanged();
                        notifyObservers("player moved");
                    }
                }
            }
            case DIGIT7 -> {
                if((checkIfCanMoveUp(PlayerRow, PlayerCol)!=PlayerRow)&&(checkIfCanMoveLeft(PlayerRow, PlayerCol)!=PlayerCol)&&
                        (PlayerRow-1>=0)&&(PlayerCol-1<=maze.getCol()-1)&&(maze.getplacevalue(PlayerRow-1, PlayerCol-1)==0)){
                    PlayerRow = checkIfCanMoveUp(PlayerRow, PlayerCol);
                    PlayerCol = checkIfCanMoveLeft(PlayerRow, PlayerCol);
                    if(PlayerRow != row){
                        setChanged();
                        notifyObservers("player moved");
                    }
                }
            }
            case DIGIT9 -> {
                if((checkIfCanMoveUp(PlayerRow, PlayerCol)!=PlayerRow)&&(checkIfCanMoveRight(PlayerRow, PlayerCol)!=PlayerCol)&&
                        (PlayerRow-1>=0)&&(PlayerCol+1<=maze.getCol()-1)&&(maze.getplacevalue(PlayerRow-1, PlayerCol+1)==0)){
                    PlayerRow = checkIfCanMoveUp(PlayerRow, PlayerCol);
                    PlayerCol = checkIfCanMoveRight(PlayerRow, PlayerCol);
                    if(PlayerRow != row){
                        setChanged();
                        notifyObservers("player moved");
                    }
                }
            }
        }
        if(PlayerRow==maze.getRow()-1 && PlayerCol == maze.getCol()-1){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Game End!");
            alert.show();
        }
    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }

    @Override
    public void solveMaze() {
        SearchableMaze searchableMaze = new SearchableMaze(maze);
        ISearchingAlgorithm searcher = new BestFirstSearch();
        solution = searcher.solve(searchableMaze);
        setChanged();
        notifyObservers("maze solved");
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
}
