package exceptions;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.AlertController;

import java.io.IOException;

public class AlertException extends Exception{
    public AlertException(String s) throws IOException {
        Stage stage = new Stage();
        stage.setTitle("Alert!");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Alert.fxml"));
        Parent root = loader.load();

        AlertController controller = loader.getController();
        controller.setAlert(s);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();
    }
}
