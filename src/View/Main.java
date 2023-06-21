package View;

import Model.IModel;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

//the main class that identify the stage and scene in the project
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyView.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Candy Maze"); //the title of the window that opened
        Scene scene = new Scene(root, 1000, 700);
        //defines the icon in the window
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/jelly.png")));
        primaryStage.setScene(scene);
        IModel model = new MyModel(); //create a model
        MyViewModel viewModel = new MyViewModel(model); //create view model that observes the model
        MyViewController view = fxmlLoader.getController();
        view.setResize(scene);
        primaryStage.show();
        String filepath = "resources/music/Untitled.wav";
        view.playMusic(filepath);//the music that plays when run that project
        view.setViewModel(viewModel);
        viewModel.addObserver(view); //add the view as observer to the viewModel

    }

    public static void main(String[] args) {
        launch(args);
    } //line that send the main to the fxml main.
}
