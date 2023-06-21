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

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyView.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Candy Maze");
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/jelly.png")));
        primaryStage.setScene(scene);
        IModel model = new MyModel();
        MyViewModel viewModel = new MyViewModel(model);
        MyViewController view = fxmlLoader.getController();
        view.setResize(scene);
        primaryStage.show();
        String filepath = "resources/music/Untitled.wav";
        view.playMusic(filepath);
        view.setViewModel(viewModel);
        viewModel.addObserver(view);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
