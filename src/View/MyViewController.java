package View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.search.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MyViewController implements IView {
    public MyMazeGenerator generator;
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public MazeDisplayer mazeDisplayer;
    public Label playerRow;
    public Label playerCol;
    public Maze maze;

    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();

    public String getUpdatePlayerRow() {
        return updatePlayerRow.get();
    }

    public void setUpdatePlayerRow(int updatePlayerRow) {
        this.updatePlayerRow.set(updatePlayerRow + "");
    }

    public String getUpdatePlayerCol() {
        return updatePlayerCol.get();
    }

    public void setUpdatePlayerCol(int updatePlayerCol) {
        this.updatePlayerCol.set(updatePlayerCol + "");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playerRow.textProperty().bind(updatePlayerRow);
        playerCol.textProperty().bind(updatePlayerCol);
    }

    public void generateMaze(ActionEvent actionEvent) {
        if(generator == null)
            generator = new MyMazeGenerator();

        int rows = Integer.valueOf(textField_mazeRows.getText());
        int cols = Integer.valueOf(textField_mazeColumns.getText());

        maze = generator.generate(rows, cols);

        mazeDisplayer.drawMaze(maze);
        setPlayerPosition(0, 0);
    }

    public void solveMaze(ActionEvent actionEvent) {
        /*Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Solving maze...");
        alert.show();*/
        SearchableMaze searchableMaze = new SearchableMaze(maze);
        ISearchingAlgorithm searcher = new BestFirstSearch();
        Solution solution = searcher.solve(searchableMaze);
        for(int i=0; i<solution.getSolutionPath().size();i++){
            int r = solution.getSolutionPath().get(i).getRow();
            int c = solution.getSolutionPath().get(i).getCol();
            if(maze.getplacevalue(r,c)==0){
                mazeDisplayer.paintPosition(r,c);
            }
        }
    }

    public void openFile(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open maze");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze"));
        fc.setInitialDirectory(new File("./resources"));
        File chosen = fc.showOpenDialog(null);
        //...
    }

    public void keyPressed(KeyEvent keyEvent) {
        int row = mazeDisplayer.getPlayerRow();
        int col = mazeDisplayer.getPlayerCol();
        switch (keyEvent.getCode()) {
                case UP -> row = checkIfCanMoveUp(row,col);
                case DOWN -> row = checkIfCanMoveDown(row,col);
                case RIGHT -> col = checkIfCanMoveRight(row,col);
                case LEFT -> col = checkIfCanMoveLeft(row, col);

        }
        if(row==maze.getRow()-1 && col == maze.getCol()-1){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Game End!");
            alert.show();
        }
        setPlayerPosition(row, col);

        keyEvent.consume();
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

    public void setPlayerPosition(int row, int col){
        mazeDisplayer.setPlayerPosition(row, col);
        setUpdatePlayerRow(row);
        setUpdatePlayerCol(col);
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }
}
