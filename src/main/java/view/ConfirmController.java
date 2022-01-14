package view;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ConfirmController {
    @FXML
    private Text warning;
    private Stage stage;
    private boolean check = false;

    public void setDialogStage(Stage stage) {
        this.stage = stage;
    }

    public void setText(String str) {
        warning.setText(str);
    }

    public void ok(){
        stage.close();
        check = true;
    }

    public void cancel(){
        stage.close();
        check = false;
    }

    public boolean getCheck(){
        return check;
    }
}
