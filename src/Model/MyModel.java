package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

import java.util.Observable;
import java.util.Observer;

public class MyModel extends Observable implements IModel{
    private Maze maze;
    private int PlayerRow;
    private int PlayerCol;
    private Solution solution;


    public MyModel(){
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

    }

    @Override
    public Maze getMaze() {
        return null;
    }

    @Override
    public void updatePlayerLocation(MovementDirection direction) {

    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }

    @Override
    public void solveMaze(Maze maze) {
        solution = new Solution();
        setChanged();
        notifyObservers("maze solved");
    }
    @Override
    public Solution getSolution() {
        return solution;
    }
}
