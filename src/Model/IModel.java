package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

import java.util.Observer;

public interface IModel{
    public void generateMaze(int rows, int cols);
    public Maze getMaze();
    public int getPlayerRow();
    public int getPlayerCol();
    public void updatePlayerLocation(MovementDirection direction);
    public void assignObserver(Observer o);
    public void solveMaze();
    public Solution getSolution();

    public void setPlayerRow(int playerRow);

    public void setPlayerCol(int playerCol);
    public void generateloadMaze(int row, int col, int playerrow, int playercol, int[] position);

}
