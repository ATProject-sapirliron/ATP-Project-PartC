package View;

import Server.Configurations;
import ViewModel.MyViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
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
    private MyViewModel viewModel;

    private Clip theclip;


    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();

    public String getUpdatePlayerRow() {
        return updatePlayerRow.get();
    }

    public void setUpdatePlayerRow(int updatePlayerRow) {
        this.playerRow.textProperty().set(updatePlayerRow + "");
    }

    public String getUpdatePlayerCol() {
        return updatePlayerCol.get();
    }

    public void setUpdatePlayerCol(int updatePlayerCol) {
        this.playerCol.textProperty().set(updatePlayerCol + "");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Other initialization code
        playerRow.textProperty().bind(updatePlayerRow);
        playerCol.textProperty().bind(updatePlayerCol);
        String filepath = "resources/music/Untitled.wav";
        theclip = playMusic(filepath);
    }

    public void generateMaze(ActionEvent actionEvent) {
        int rows = Integer.valueOf(textField_mazeRows.getText());
        int cols = Integer.valueOf(textField_mazeColumns.getText());
        viewModel.generateMaze(rows, cols);
        setPlayerPosition(0, 0);
        theclip.stop();
        String filepath = "resources/music/WiliWonka.wav";
        theclip = playMusic(filepath);
    }

    public Clip playMusic(String filepath){
        try{
            File musicpath = new File(filepath);
            if(musicpath.exists()){
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicpath);
                theclip = AudioSystem.getClip();
                theclip.open(audioInput);
                theclip.start();
                return theclip;
            }
            else{
                System.out.println("cant find file");
            }
        }
        catch(Exception e){
            System.out.println(e);

        }
        return null;
    }

    public void stopMusic(Clip c){
        c.stop();
    }

    public void solveMaze(ActionEvent actionEvent) {
        /*Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Solving maze...");
        alert.show();
      */
        viewModel.solveMaze();

    }

    public void keyPressed(KeyEvent keyEvent) {
        viewModel.movePlayer(keyEvent);
        keyEvent.consume();
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
        this.viewModel = viewModel;
        this.viewModel.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        String change = (String) arg;
        switch (change){
            case "maze generated" -> mazeGenerated();
            case "player moved" -> playerMoved();
            case "maze solved" -> mazeSolved();
            default -> System.out.println("Not implemented change: " + change);
        }
    }

    private void playerMoved() {
        setPlayerPosition(viewModel.getRowChar(), viewModel.getColChar());
        if((viewModel.getColChar()==viewModel.getMaze().getCol()-1) &&(viewModel.getRowChar()==viewModel.getMaze().getRow()-1)){
            theclip.stop();
            playMusic("resources/music/clap.wav");
        }
    }

    private void mazeGenerated() {
        mazeDisplayer.setSolution(null);
        mazeDisplayer.drawMaze(viewModel.getMaze());
    }

    private void mazeSolved() {
        mazeDisplayer.setSolution(viewModel.getSolution());
        System.out.println("drawing solution...");
    }

    public void openFile(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open maze");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze"));
        fc.setInitialDirectory(new File("./resources"));
        File chosen = fc.showOpenDialog(null);
        //...
    }


    public void handleExit(ActionEvent actionEvent) {
        System.out.println("Exit Game");
        System.exit(0);
    }

    public void handleHelp(ActionEvent actionEvent){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText(null);
        alert.setContentText("1.Objective:\n" +
                "Your goal is to help the Carine navigate through the choclate maze and reach the cookie.\n" +
                "\n" +
                "2.Controls:\n" +
                "Use the numbers on your keyboard to move the character up, down, left, right, up-left, up-right, down-left or down-right.\n" +
                "Explore the maze by moving Carine one step at a time.\n" +
                "\n" +
                "3.Rules:\n" +
                "You cannot move through choclate walls. Choclate walls are obstacles that block your path.\n" +
                "You must find the correct path to the exit without getting trapped.\n" +
                "\n" +
                "4.Tips:\n" +
                "Pay attention to the maze layout and look for openings or pathways.\n" +
                "Plan your moves and think ahead before making each move.\n" +
                "Take your time and be patient. Maze-solving requires concentration and problem-solving skills.\n" +
                "Don't be afraid to backtrack if you reach a dead end. Sometimes you need to go back to find a different route.\n" +
                "\n" +
                "5.Game Over:\n" +
                "If Carine reached the cookie, you win! Congratulations!\n" +
                "If you get stuck and can't find a way to the exit, don't worry. You can try again or ask for help.\n" +
                "Have Fun!\n" +
                "\n" +
                "Don't forget to celebrate your achievements along the way!\n" +
                "Remember, maze games are meant to be fun and challenging. Enjoy the journey of exploring and solving the maze puzzles!\n");

        alert.showAndWait();
    }

    public void handleProperties(ActionEvent actionEvent){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Properties");
        alert.setHeaderText(null);
        String allProperties ="";
        allProperties += "Maze Generating Algorithm: "+ Configurations.getInstance().getMazeGeneratingAlgorithm()+"\n";
        allProperties += "Maze Searching Algorithm: "+ Configurations.getInstance().getMazeSearchingAlgorithm()+"\n";
        allProperties += "Thread Pool Size: "+ Configurations.getInstance().getThreadPoolSize()+"\n";
        alert.setContentText(allProperties);
        alert.showAndWait();
    }

    public void handleAbout(ActionEvent actionEvent){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(null);
        String allProperties ="Programmer Names: Liron Miriam Shemen, Sapir Tzaig\n";
        allProperties+= "We used this 3 algorithm in order to solve the maze. you can use each one of them:\n";
        allProperties+= "Best First Search, Breadth First Search, Depth First Search\n";
        allProperties+= "Try them out! \n";
        allProperties+= "Enjoy \n";
        alert.setContentText(allProperties);
        alert.showAndWait();
    }

    public void zoomIn() {
        mazeDisplayer.zoomIn();
    }

    public void zoomOut() {
        mazeDisplayer.zoomOut();
    }

    @FXML
    private void handleScroll(ScrollEvent event) {
        if (event.isControlDown()) {
            double delta = event.getDeltaY();
            if (delta > 0) {
                // Zoom in
                mazeDisplayer.zoomIn();
            } else if (delta < 0) {
                // Zoom out
                mazeDisplayer.zoomOut();
            }
            event.consume();
        }
    }


}
