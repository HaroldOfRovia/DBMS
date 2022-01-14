package view;

import exceptions.AlertException;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddNewDataController {
    @FXML
    private TextField type;
    @FXML
    private TextField data;
    @FXML
    private CheckBox active;
    private Stage stage;
    private boolean error = true;

    public void setDialogStage(Stage stage){
        this.stage = stage;
    }

    public void addData() {
        try {
            if (data.getText().equals("") || type.getText().equals(""))
                throw new AlertException("Both fields must be filled in.");
            else if (!active.isSelected() && !type.getText().matches(".*(phone).*"))
                throw new AlertException("Only phone numbers can be inactive.");
            else if (type.getText().matches(".*(phone).*") && !data.getText().matches("^[\\d\\-+()]*$"))
                throw new AlertException("Phone numbers can contain only (, ), +, - and digits.");
            else {
                error = false;
                stage.close();
            }
        }
        catch (Exception ignored){}
    }

    public String getType(){
        return type.getText();
    }

    public String getData(){
        return data.getText();
    }

    public boolean getActive(){
        return active.isSelected();
    }

    public boolean getError(){
        return error;
    }
}
