package View;

import Model.IModel;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.search.BestFirstSearch;
import algorithms.search.ISearchingAlgorithm;
import algorithms.search.SearchableMaze;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;


public class MyViewController implements IView, Observer {
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public MazeDisplayer mazeDisplayer;
    public Label playerRow;
    public Label playerCol;
    public Maze maze;
    private MediaPlayer mediaPlayer;
    private MyViewModel viewModel;

    private MyMazeGenerator generator;

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
        // Initialize the MediaPlayer
        String musicFile = "resources/music/videoplayback.mp4"; // Replace with the actual path to your MP4 file
        Media media = new Media(new File(musicFile).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);

        // Other initialization code
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
        viewModel.movePlayer(keyEvent);
        int row = mazeDisplayer.getPlayerRow();
        int col = mazeDisplayer.getPlayerCol();
        switch (keyEvent.getCode()) {
            case DIGIT8 -> row = checkIfCanMoveUp(row,col);
            case DIGIT2 -> row = checkIfCanMoveDown(row,col);
            case DIGIT6 -> col = checkIfCanMoveRight(row,col);
            case DIGIT4 -> col = checkIfCanMoveLeft(row, col);
            case DIGIT1 -> {
                if((checkIfCanMoveDown(row, col)!=row)&&(checkIfCanMoveLeft(row, col)!=col)&&
                        (row+1>=0)&&(col-1<=maze.getCol()-1)&&(maze.getplacevalue(row+1, col-1)==0)){
                    row = checkIfCanMoveDown(row, col);
                    col = checkIfCanMoveLeft(row, col);
                }
            }
            case DIGIT3 -> {
                if((checkIfCanMoveDown(row, col)!=row)&&(checkIfCanMoveRight(row, col)!=col)&&
                        (row+1>=0)&&(col+1<=maze.getCol()-1)&&(maze.getplacevalue(row+1, col+1)==0)){
                    row = checkIfCanMoveDown(row, col);
                    col = checkIfCanMoveRight(row, col);
                }
            }
            case DIGIT7 -> {
                if((checkIfCanMoveUp(row, col)!=row)&&(checkIfCanMoveLeft(row, col)!=col)&&
                        (row-1>=0)&&(col-1<=maze.getCol()-1)&&(maze.getplacevalue(row-1, col-1)==0)){
                    row = checkIfCanMoveUp(row, col);
                    col = checkIfCanMoveLeft(row, col);
                }
            }
            case DIGIT9 -> {
                if((checkIfCanMoveUp(row, col)!=row)&&(checkIfCanMoveRight(row, col)!=col)&&
                        (row-1>=0)&&(col+1<=maze.getCol()-1)&&(maze.getplacevalue(row-1, col+1)==0)){
                    row = checkIfCanMoveUp(row, col);
                    col = checkIfCanMoveRight(row, col);
                }
            }
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

    public void setViewModel(MyViewModel viewModel) {

    }

    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof MyViewModel){
            if(maze ==null){
                this.maze = viewModel.getMaze();
                drawMaze();
            }
            else{
                Maze maze = viewModel.getMaze();
                if(maze ==this.maze){
                    int rowChar = mazeDisplayer.getPlayerRow();
                    int colChar = mazeDisplayer.getPlayerCol();
                    int rowFromViewModel = viewModel.getRowChar();
                    int colFromViewModel = viewModel.getColChar();
                    if(rowFromViewModel == colChar && colFromViewModel == rowChar){
                        viewModel.getSolution();
                    }
                    else{
                        setUpdatePlayerRow(rowFromViewModel);
                        setUpdatePlayerCol(colFromViewModel);
                        setPlayerPosition(rowFromViewModel,colFromViewModel);
                    }
                }
                else{
                    this.maze = maze;
                    drawMaze();
                }
            }
        }
    }

    public void drawMaze(){
        mazeDisplayer.drawMaze(maze);
    }
}
