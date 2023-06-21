package View;

import Server.Configurations;
import ViewModel.MyViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.*;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;


public class MyViewController implements IView, Observer {
    public TextField textField_mazeRows; //user enter rows number
    public TextField textField_mazeColumns; //user enter columns number
    public MazeDisplayer mazeDisplayer; //mazedisplayer instance
    public Label playerRow; //label
    public Label playerCol; //label
    private MyViewModel viewModel; //mudelview instance
    private Clip theclip; //music clip
    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();

    //this method sets player row according to the updatePlayerRow
    public void setUpdatePlayerRow(int updatePlayerRow) {
        this.playerRow.textProperty().set(updatePlayerRow + "");
    }

    //this method sets player column according to the updatePlayerCol
    public void setUpdatePlayerCol(int updatePlayerCol) {
        this.playerCol.textProperty().set(updatePlayerCol + "");
    }

    //initialize scene
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Other initialization code
        playerRow.textProperty().bind(updatePlayerRow);
        playerCol.textProperty().bind(updatePlayerCol);
        String filepath = "resources/music/Untitled.wav";
        theclip = playMusic(filepath); //play music
    }

    //this method generates the maze by calling the viewModel. generateMaze function
    public void generateMaze(ActionEvent actionEvent) {
        //check if arguments are valid
        if(!checkMe(textField_mazeRows.getText())|| !checkMe(textField_mazeColumns.getText())){
            //if argements are not valid pop alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid Arguments!");
            //alert design
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
        else if(textField_mazeRows.getText().equals("")|| textField_mazeColumns.getText().equals("")){
            //if argements didn't entered pop alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Missing Arguments!");
            //alert design
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
        //else if arguments are valid
        int rows = Integer.valueOf(textField_mazeRows.getText()); //take given rows number
        int cols = Integer.valueOf(textField_mazeColumns.getText()); //take given cols number
        viewModel.generateMaze(rows, cols); //generate maze
        setPlayerPosition(0, 0); //set player position to start point
        viewModel.setColChar(0); //set charecter position
        viewModel.setRowChar(0); //set charecter position
        theclip.stop(); //stop music
        String filepath = "resources/music/WiliWonka.wav";
        theclip = playMusic(filepath); //play different music
    }

    //this method plays the given music according to a given path
    public Clip playMusic(String filepath){
        try{ //try upload music file
            File musicpath = new File(filepath);
            if(musicpath.exists()){
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicpath);
                theclip = AudioSystem.getClip();
                theclip.open(audioInput);
                theclip.start();
                return theclip;
            }
            else{ //print the file wasn't found
                System.out.println("cant find file");
            }
        }
        catch(Exception e){
            System.out.println(e);

        }
        return null;
    }

    //this method solves the maze by activating the viewmodel solvemaze method
    public void solveMaze(ActionEvent actionEvent) {
        viewModel.solveMaze();
    }

    //this method moves the player according to a mouse click
    public void keyPressed(KeyEvent keyEvent) {
        viewModel.movePlayer(keyEvent); //activated the move player method on view model
        keyEvent.consume();
    }


    //this method sets the given player position
    public void setPlayerPosition(int row, int col){
        mazeDisplayer.setPlayerPosition(row, col); //sets player position on maze
        setUpdatePlayerRow(row); //update player row
        setUpdatePlayerCol(col); //update player col
    }

    //this method activates according to mouse clicked
    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }

    //this method sets the viewmodel and also sets this class to be observer of the viewmodel
    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel; //sets the viewmodel
        this.viewModel.addObserver(this); //sets this class to be observer of the viewmodel
    }

    //this method updates the changes of maze/player/solution
    @Override
    public void update(Observable o, Object arg) {
        String change = (String) arg; //get string
        switch (change){
            case "maze generated" -> mazeGenerated(); //if maze was generated
            case "player moved" -> playerMoved(); //if player has moved
            case "maze solved" -> mazeSolved(); //is maze was solved
            default -> System.out.println("Not implemented change: " + change); //if no change was implemented
        }
    }

    //this method sets the new players position and also checks if he reaches the goal position
    private void playerMoved() {
        setPlayerPosition(viewModel.getRowChar(), viewModel.getColChar()); //sets plater position
        if((viewModel.getColChar()==viewModel.getMaze().getCol()-1) &&(viewModel.getRowChar()==viewModel.getMaze().getRow()-1)){
            theclip.stop(); //if player reached goal position stop current music
            playMusic("resources/music/clap.wav"); //play win music
        }
    }

    //this method calles the mazedisplayer draw maze function to draw the maze
    private void mazeGenerated() {
        mazeDisplayer.setSolution(null); //sets solution to null
        mazeDisplayer.drawMaze(viewModel.getMaze()); //draw maze
    }

    //this method calles the mazedisplayer setsolution in order to set the maze solution on the maze
    private void mazeSolved() {
        mazeDisplayer.setSolution(viewModel.getSolution()); //set solution
        System.out.println("drawing solution...");
    }

    //this method allows us to load an old maze saves to a file
    public void openFile(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open maze"); //window title
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files (*.txt)", "*.txt"));
        fc.setInitialDirectory(new File("./resources")); //search in defualt resource file
        File chosen = fc.showOpenDialog(null);
        if (chosen != null) {
            readMazeFromFile(chosen); //read from file
        }
    }

    //this method reads from guven file the data and call viewmodel to generate the maze
    private void readMazeFromFile(File file) {
        try {
            // Read the contents of the file
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String content = "";
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content+=line;
            }
            bufferedReader.close();
            String[] elements = content.split(","); //convert data to an array
            int[] newelements = new int[elements.length]; //genrate int array
            int counter = 0;
            for(int r=0; r<Integer.parseInt(elements[2]);r++){
                for(int c=0; c<Integer.parseInt(elements[3]);c++){
                    newelements[counter] = Integer.parseInt(elements[counter]); //copy the string array to the int array
                    counter++;
                }
            }
            //call generate function in view model
            viewModel.callgenerateloadMaze(Integer.parseInt(elements[2]), Integer.parseInt(elements[3]),
                    Integer.parseInt(elements[0]), Integer.parseInt(elements[1]), newelements);
        } catch (IOException e) {
            // Handle the exception if an error occurs while reading the file
            e.printStackTrace();
        }
    }

    //this function saves the maze to a file
    public void saveFile(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save"); //windows title
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Maze files (*.txt)", "*.txt"));
        fc.setInitialDirectory(new File("./resources")); //default file to open on

        // Set a default file name
        String defaultFileName = "myMaze.txt"; //file name
        fc.setInitialFileName(defaultFileName);

        // Show the save dialog
        File chosen = fc.showSaveDialog(null);
        if (chosen != null) {
            if(viewModel.getMaze()==null){ //if the maze is null show an alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Cant save ungenerated maze!");
                //alert design
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
            saveMazeToFile(chosen); //call save maze method
        }
    }

    //this method saves the maze data to a file
    private void saveMazeToFile(File file) {
        try {
            // Write the maze data to the file
            FileWriter fileWriter = new FileWriter(file);

            // Get the maze data as a string
            String mazeData = viewModel.getRowChar() + ","+viewModel.getColChar()+",";
            mazeData+= viewModel.getMaze().getRow() +","+ viewModel.getMaze().getCol()+",";
            for(int r=0; r<viewModel.getMaze().getRow();r++){
                for(int c=0; c<viewModel.getMaze().getCol();c++){
                    mazeData+=viewModel.getMaze().getplacevalue(r,c)+",";
                }
            }
            // Write the maze data to the file
            fileWriter.write(mazeData);
            fileWriter.close();
        } catch (IOException e) {
            // Handle the exception if an error occurs while saving the file
            e.printStackTrace();
        }
    }

    //this method creates a new default maze and activates play music
    public void CreateNew(ActionEvent actionEvent) {
        viewModel.generateMaze(10, 10); //generate default maze
        setPlayerPosition(0, 0); //set player position
        viewModel.setColChar(0); //set player row
        viewModel.setRowChar(0); //set player col
        theclip.stop(); //stop music
        String filepath = "resources/music/WiliWonka.wav";
        theclip = playMusic(filepath); //activate play music
    }


    //this method exits the game
    public void handleExit(ActionEvent actionEvent) {
        System.out.println("Exit Game");
        System.exit(0);
    }

    //this method pops alert of the help window
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
        DialogPane dialogPane = alert.getDialogPane();
        BackgroundFill backgroundFill = new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);
        dialogPane.setBackground(background);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/jelly.png")));
        alert.showAndWait();
    }

    //this method pops alert of the Properties window
    public void handleProperties(ActionEvent actionEvent){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Properties");
        alert.setHeaderText(null);
        String allProperties ="";
        allProperties += "Maze Generating Algorithm: "+ Configurations.getInstance().getMazeGeneratingAlgorithm()+"\n";
        allProperties += "Maze Searching Algorithm: "+ Configurations.getInstance().getMazeSearchingAlgorithm()+"\n";
        allProperties += "Thread Pool Size: "+ Configurations.getInstance().getThreadPoolSize()+"\n";
        alert.setContentText(allProperties);
        DialogPane dialogPane = alert.getDialogPane();
        BackgroundFill backgroundFill = new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);
        dialogPane.setBackground(background);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/jelly.png")));
        alert.showAndWait();
    }

    //this method pops alert of the About window
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
        DialogPane dialogPane = alert.getDialogPane();
        BackgroundFill backgroundFill = new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);
        dialogPane.setBackground(background);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/jelly.png")));
        alert.showAndWait();
    }

    //this method create zoomin and out of the maze
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

    //this method moves player according to the mouse click
    @FXML
    private void MovePlayerByMouse(MouseEvent event) {
        if (viewModel.getMaze() != null) {
            int row = viewModel.getRowChar();
            int col = viewModel.getColChar();
            viewModel.moveCharacter(event, mazeDisplayer.getHeight() / viewModel.getMaze().getRow(), mazeDisplayer.getWidth() / viewModel.getMaze().getCol());
        }
    }

    //this method checks if the string is an int
    public boolean checkMe(String s) {
        boolean amIValid = false;
        try {
            Integer.parseInt(s);
            amIValid = true;
        } catch (NumberFormatException e) {
        }
        return amIValid;
    }

    //this method sets the size of the maze according to the user zoom in and zoom out decision
    public void setResize(Scene scene){
        scene.widthProperty().addListener((observable,oldvalue, newValue)->{
            mazeDisplayer.updateCanvasWidth((double)oldvalue,(double)newValue);
        });
        scene.heightProperty().addListener((observable,oldvalue, newValue)->{
            mazeDisplayer.updateCanvasHeight((double)oldvalue,(double)newValue);
        });
    }
}