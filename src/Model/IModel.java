package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

import java.util.Observer;

//this class represents the Model interface
public interface IModel{
    //this method generates the maze
    public void generateMaze(int rows, int cols);
    //this method returns the maze
    public Maze getMaze();
    //this method returns the player row
    public int getPlayerRow();
    //this method returns the player column
    public int getPlayerCol();
    //this method updates player location
    public void updatePlayerLocation(MovementDirection direction);
    //this method adds observer to the model
    public void assignObserver(Observer o);
    //this method solves the maze
    public void solveMaze();
    //this method returns the solution of the maze
    public Solution getSolution();
    //this method sets players row
    public void setPlayerRow(int playerRow);
    //this method sets players column
    public void setPlayerCol(int playerCol);
    //this method generates the loaded maze
    public void generateloadMaze(int row, int col, int playerrow, int playercol, int[] position);

}
