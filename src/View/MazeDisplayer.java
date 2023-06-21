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

//This class comatins the methods that controls the design of the maze and scene
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
    private static double zoomFactor = 1.0; //the default zoom

    public int getPlayerRow() {
        return playerRow;
    }

    public int getPlayerCol() {
        return playerCol;
    }

    public String imageFileNameWallProperty() {
        return imageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }public String imageFileNamePlayerProperty() {
        return imageFileNamePlayer.get();
    }
    public void setImageFileNamePlayer(String imageFileNamePlayer) {
        this.imageFileNamePlayer.set(imageFileNamePlayer);
    }

    public void setImageFileNameGoal(String imageFileNameGoal) {
        this.imageFileNameGoal.set(imageFileNameGoal);
    }


    //method that called from the controller to locate the character in the maze
    //when new maze is generated or when the player moves
    public void setPlayerPosition(int row, int col) {
        if(playerCol!=col || playerRow!=row){
            System.out.println("player moved...");

        }
        this.playerRow = row;
        this.playerCol = col;
        draw(); //call draw to update the screen shown
    }

    //method that called from the controller to draw a solution in the maze
    //when the player wants to now the solution of the maze
    public void setSolution(Solution solution) {
        this.solution = solution;
        draw();//call draw to update the screen shown
    }

    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }

    public String getImageFileNamePlayer() {

        return imageFileNamePlayer.get();
    }

    public String getImageFileNameGoal() {
        return imageFileNameGoal.get();
    }

    //function that set a new maze and draw it called when maze is generated
    public void drawMaze(Maze maze) {
        this.maze = maze;
        System.out.println("maze generated...");
        draw(); //call draw to update the screen shown
    }


    //the main methods that draw, all the other drawing methods call it
    //called in cases of- new maze was generated, player moved, player asked for solution,
    //player zoomed in or out
    private void draw() {
        if(maze != null){
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = maze.getRow();
            int cols = maze.getCol();


            //if the players zoom in or out
            double cellHeight = canvasHeight / rows* zoomFactor;
            double cellWidth = canvasWidth / cols* zoomFactor;

            GraphicsContext graphicsContext = getGraphicsContext2D();

            //clear the canvas:
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);
            drawMazeWalls(graphicsContext, cellHeight, cellWidth, rows, cols);

            if(solution==null){  //the player didn't ask for solution
                resetMazeColor(graphicsContext, cellHeight, cellWidth);
            }
            if(solution!=null){ //call the methods that draw the jelly beans solution
                drawSolution(graphicsContext, cellHeight, cellWidth);
            }
            drawPlayer(graphicsContext, cellHeight, cellWidth); //call the method that draw the character
            drawGoal(graphicsContext, cellHeight, cellWidth); //call the method that draw the goal
        }
    }



    //the methods that add jell beans in the solution path
    private void drawSolution(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        for(int i=0; i<solution.getSolutionPath().size();i++){
            //move on the solution path
            int r = solution.getSolutionPath().get(i).getRow();
            int c = solution.getSolutionPath().get(i).getCol();
            //if this point in the solution path is not a wall
            if(maze.getplacevalue(r,c)==0){
                //call the function that add jellybean
                paintPosition(r,c, graphicsContext, cellHeight, cellWidth);
            }
        }
    }

    //the function that draw the walls according to the generated maze
    private void drawMazeWalls(GraphicsContext graphicsContext, double cellHeight, double cellWidth, int rows, int cols) {
        graphicsContext.setFill(Color.RED); //in case that the image doesnt work

        Image wallImage = null;
        try{
            wallImage = new Image(new FileInputStream(getImageFileNameWall())); //get the image
        } catch (FileNotFoundException e) {
            System.out.println("There is no wall image file");
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(maze.getplacevalue(i,j) == 1){ //if the maze is 1 its a wall
                    //if it is a wall:
                    double x = j * cellWidth;
                    double y = i * cellHeight;
                    if(wallImage == null) //image wasnt found - paint in red
                        graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                    else //set the image of the chocolate wall
                        graphicsContext.drawImage(wallImage, x, y, cellWidth, cellHeight);
                }
            }
        }
    }

    //the function that draw the character inside the maze
    private void drawPlayer(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        // x, y are the coordinated that the character is located now
        double x = getPlayerCol() * cellWidth;
        double y = getPlayerRow() * cellHeight;
        graphicsContext.setFill(Color.GREEN); //if the image doesnt work the character will be green

        Image playerImage = null;
        try { //open the image of carine
            playerImage = new Image(new FileInputStream(getImageFileNamePlayer()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no player image file");
        }
        if(playerImage == null) //couldnt open the image
            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
        else //set carine's image to be the character and place it according to x, y
            graphicsContext.drawImage(playerImage, x, y, cellWidth, cellHeight);
    }

    //draw the cookie that needs to be in the end of the maze
    private void drawGoal(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
       //x , y are the end coordinates, they are the last row and column
        double x = (maze.getCol()-1) * cellWidth;
        double y = (maze.getRow()-1) * cellHeight;
        graphicsContext.setFill(Color.GREEN); //if the cookie image doesnt found

        Image GoalImage = null;
        try { //open the cookie image
            GoalImage = new Image(new FileInputStream(getImageFileNameGoal()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no goal image file");
        }
        if(GoalImage == null) //the end will be green
            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
        else //the end will be set as the cookie
            graphicsContext.drawImage(GoalImage, x, y, cellWidth, cellHeight);
    }

    //called from drawSolution to physically draw the solution path
    public void paintPosition(int r,int c, GraphicsContext graphicsContext, double Height, double Width) {
        if (maze != null) { //to avoid errors
            graphicsContext.setFill(Color.TRANSPARENT); //make the image TRANSPARENT

            double cellHeight = Height;
            double cellWidth = Width;

            // x, y are the location of the specific coordinate that is part of the solution
            double x = c * cellWidth;
            double y = r * cellHeight;

            // Clear the entire cell
            graphicsContext.clearRect(x, y, cellWidth, cellHeight);
            graphicsContext.fillRect(x, y, cellWidth, cellHeight);


            if(this.maze.getplacevalue(r,c)==0){ //make sure its not al wall
                //set the jelly bean image
                Image image = new Image(getClass().getResourceAsStream("/images/jelly.png"));
                graphicsContext.setGlobalAlpha(0.5);
                //draw it
                graphicsContext.drawImage(image, x, y, cellWidth, cellHeight);
                graphicsContext.setGlobalAlpha(1.0);
            }
        }
    }


    //this function is called from draw function in case the solution is null
    //it called to clear the maze in cases of new maze generated to prevent an old solution shown
    public void resetMazeColor(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        if (maze != null) {
            for (int r = 0; r < maze.getRow(); r++) {
                for (int c = 0; c < maze.getCol(); c++) {
                    double x = c * cellWidth;
                    double y = r * cellHeight;
                    if (maze.getplacevalue(r, c) == 0) { //if its not a wall
                        //clear it
                        graphicsContext.clearRect(x, y, cellWidth, cellHeight);
                    }
                }
            }
        }
    }


    //when the player zoom in with mouse and ctrl
    public void zoomIn() {
        zoomFactor *= 1.1; // Increase the zoom factor by 10%
        draw(); //call draw to update the screen shown
    }

    //when the player zoom out with mouse and ctrl
    public void zoomOut() {
        zoomFactor /= 1.1; // Decrease the zoom factor by 10%
        draw();//call draw to update the screen shown
    }


    //this function is called from controller, and it updates the size of the maze
    //when the player maximize the size of the window
    public void updateCanvasWidth(double oldvalue, double newValue) {
        double val = newValue/oldvalue;
        setWidth(val*getWidth());
        drawMaze(this.maze);
    }
    //this function is called from controller, and it updates the size of the maze
    //when the player maximize the size of the window
    public void updateCanvasHeight(double oldvalue, double newValue) {
        double val = newValue/oldvalue;
        setHeight(val*getHeight());
        drawMaze(this.maze);
    }
}
