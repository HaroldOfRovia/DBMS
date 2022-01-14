package view;

import exceptions.AlertException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;



public class AddNewPersonController {
    @FXML
    private TextField surname;
    @FXML
    private TextField name;
    @FXML
    private TextField type;
    private Stage stage;
    private boolean error = true;

    public void setDialogStage(Stage stage) {
        this.stage = stage;
    }

    public void addPerson(){
        try {
            if (surname.getText().equals("") || name.getText().equals("") || type.getText().equals(""))
                throw new AlertException("All fields must be filled in.");
            else {
                error = false;
                stage.close();
            }
        } catch (Exception ignored){}
    }

    public String getSurname() {
        return surname.getText();
    }
    public String getName() {
        return name.getText();
    }
    public String getType() {
        return type.getText();
    }
    public boolean getError(){
        return error;
    }
}
