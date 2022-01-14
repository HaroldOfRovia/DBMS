package view;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AlertController {
    @FXML
    private Text alert;

    public void setAlert(String s){
        alert.setText(s);
    }

    public void ok(){
        Stage stage = (Stage) alert.getScene().getWindow();
        stage.close();
    }
}
