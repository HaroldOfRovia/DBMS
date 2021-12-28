package person;

import connect.ControlDatabase;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.MainWindowController;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Phone book");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/MainWindow.fxml"));
        Parent mainView = loader.load();

        MainWindowController mainController = loader.getController();
        ControlDatabase controlDatabase = new ControlDatabase();
        ObservableList<Person> people = controlDatabase.fillPeopleList();
        mainController.setListPersons(people);

        Scene scene = new Scene(mainView);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
