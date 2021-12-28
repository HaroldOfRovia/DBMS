package view;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class AddNewDataController {
    @FXML
    private TextField type;
    @FXML
    private TextField data;
    @FXML
    private CheckBox active;
    @FXML
    private Label error;
    private Stage stage;

    private final ArrayList<String> newData;


    public AddNewDataController(ArrayList<String> newData){
        this.newData = newData;
    }

    public void setDialogStage(Stage stage){
        this.stage = stage;
    }

    public void addData(){
        if (data.getText().equals("") || type.getText().equals(""))
            error.opacityProperty().setValue(1);
        else {
            newData.addAll(List.of(data.getText(), type.getText(), String.valueOf(active.isSelected()).toUpperCase()));
            stage.close();
        }
    }
}
