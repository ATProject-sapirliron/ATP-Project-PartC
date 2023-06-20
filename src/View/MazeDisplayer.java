package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MazeDisplayer extends Canvas {
    private Maze maze;
    private Solution solution;

    // player position:
    private int playerRow = 0;
    private int playerCol = 0;
    // wall and player images:
    StringProperty imageFileNameWall = new SimpleStringProperty();
    StringProperty imageFileNameGoal = new SimpleStringProperty();
    StringProperty imageFileNamePlayer = new SimpleStringProperty();
    private static double zoomFactor = 1.0;

    public int getPlayerRow() {
        return playerRow;
    }

    public int getPlayerCol() {
        return playerCol;
    }

    public void setPlayerPosition(int row, int col) {
        if(playerCol!=col || playerRow!=row){
            System.out.println("player moved...");

        }
        this.playerRow = row;
        this.playerCol = col;
        draw();
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
        draw();
    }

    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }

    public String imageFileNameWallProperty() {
        return imageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNamePlayer() {

        return imageFileNamePlayer.get();
    }

    public String imageFileNamePlayerProperty() {
        return imageFileNamePlayer.get();
    }

    public String getImageFileNameGoal() {
        return imageFileNameGoal.get();
    }

    public void setImageFileNamePlayer(String imageFileNamePlayer) {
        this.imageFileNamePlayer.set(imageFileNamePlayer);
    }

    public void setImageFileNameGoal(String imageFileNameGoal) {
        this.imageFileNameGoal.set(imageFileNameGoal);
    }

    public void drawMaze(Maze maze) {
        this.maze = maze;
        System.out.println("maze generated...");
        draw();
    }

    private void draw() {
        if(maze != null){
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = maze.getRow();
            int cols = maze.getCol();

            double cellHeight = canvasHeight / rows* zoomFactor;
            double cellWidth = canvasWidth / cols* zoomFactor;

            GraphicsContext graphicsContext = getGraphicsContext2D();
            //clear the canvas:
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);
            drawMazeWalls(graphicsContext, cellHeight, cellWidth, rows, cols);
            if(solution==null){
                resetMazeColor(graphicsContext, cellHeight, cellWidth);
            }
            if(solution!=null){
                drawSolution(graphicsContext, cellHeight, cellWidth);
            }
            drawPlayer(graphicsContext, cellHeight, cellWidth);
            drawGoal(graphicsContext, cellHeight, cellWidth);
        }
    }



    private void drawSolution(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        // need to be implemented
        for(int i=0; i<solution.getSolutionPath().size();i++){
            int r = solution.getSolutionPath().get(i).getRow();
            int c = solution.getSolutionPath().get(i).getCol();
            if(maze.getplacevalue(r,c)==0){
                paintPosition(r,c, graphicsContext);
            }
        }
    }

    private void drawMazeWalls(GraphicsContext graphicsContext, double cellHeight, double cellWidth, int rows, int cols) {
        graphicsContext.setFill(Color.RED);

        Image wallImage = null;
        try{
            wallImage = new Image(new FileInputStream(getImageFileNameWall()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no wall image file");
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(maze.getplacevalue(i,j) == 1){
                    //if it is a wall:
                    double x = j * cellWidth;
                    double y = i * cellHeight;
                    if(wallImage == null)
                        graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                    else
                        graphicsContext.drawImage(wallImage, x, y, cellWidth, cellHeight);
                }
            }
        }
    }

    private void drawPlayer(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        double x = getPlayerCol() * cellWidth;
        double y = getPlayerRow() * cellHeight;
        graphicsContext.setFill(Color.GREEN);

        Image playerImage = null;
        try {
            playerImage = new Image(new FileInputStream(getImageFileNamePlayer()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no player image file");
        }
        if(playerImage == null)
            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
        else
            graphicsContext.drawImage(playerImage, x, y, cellWidth, cellHeight);
    }

    private void drawGoal(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        double x = (maze.getCol()-1) * cellWidth;
        double y = (maze.getRow()-1) * cellHeight;
        graphicsContext.setFill(Color.GREEN);

        Image GoalImage = null;
        try {
            GoalImage = new Image(new FileInputStream(getImageFileNameGoal()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no goal image file");
        }
        if(GoalImage == null)
            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
        else
            graphicsContext.drawImage(GoalImage, x, y, cellWidth, cellHeight);
    }

    public void paintPosition(int r,int c, GraphicsContext graphicsContext) {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = maze.getRow();
            int cols = maze.getCol();
            graphicsContext.setFill(Color.TRANSPARENT);

            double cellHeight = canvasHeight / rows;
            double cellWidth = canvasWidth / cols;

            double x = c * cellWidth;
            double y = r * cellHeight;

            // Clear the entire cell
            graphicsContext.clearRect(x, y, cellWidth, cellHeight);
            // Fill the cell with lavender color
            //graphicsContext.setFill(Color.LAVENDER);
            graphicsContext.fillRect(x, y, cellWidth, cellHeight);

            if(this.maze.getplacevalue(r,c)==0){
                Image image = new Image(getClass().getResourceAsStream("/images/jelly.png"));
                graphicsContext.setGlobalAlpha(0.5);
                graphicsContext.drawImage(image, x, y, cellWidth, cellHeight);
                graphicsContext.setGlobalAlpha(1.0);
            }
        }
    }

    public void resetMazeColor(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        //graphicsContext.setFill(Color.LAVENDER);
        if (maze != null) {
            for (int r = 0; r < maze.getRow(); r++) {
                for (int c = 0; c < maze.getCol(); c++) {
                    double x = c * cellWidth;
                    double y = r * cellHeight;
                    if (maze.getplacevalue(r, c) == 0) {
                        graphicsContext.clearRect(x, y, cellWidth, cellHeight);
                    }
                }
            }
        }
    }

    public void zoomIn() {
        zoomFactor *= 1.1; // Increase the zoom factor by 10%
        draw();
    }

    public void zoomOut() {
        zoomFactor /= 1.1; // Decrease the zoom factor by 10%
        draw();
    }


    public void updateCanvasWidth(double oldvalue, double newValue) {
        double val = newValue/oldvalue;
        setWidth(val*getWidth());
        drawMaze(this.maze);
    }

    public void updateCanvasHeight(double oldvalue, double newValue) {
        double val = newValue/oldvalue;
        setHeight(val*getHeight());
        drawMaze(this.maze);
    }
}
