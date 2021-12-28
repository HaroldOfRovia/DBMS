package view;

import connect.ControlDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchByNameWindowController {
    @FXML
    private TextField searchName;
    @FXML
    private TextField searchSurname;
    @FXML
    private ChoiceBox<String> categoryList;
    private Stage stage;

    private final ArrayList<String> searchInfo;


    public SearchByNameWindowController(ArrayList<String> searchInfo){
        this.searchInfo = searchInfo;
    }

    public void setDialogStage(Stage stage) throws SQLException {
        ControlDatabase controlDatabase = new ControlDatabase();
        ObservableList<String> typePerson = FXCollections.observableArrayList(controlDatabase.listTypePerson());
        typePerson.setAll(typePerson.sorted());
        typePerson.add(0, "All");
        categoryList.setItems(typePerson);
        categoryList.setValue("All");
        this.stage = stage;
    }

    public void search(){
        searchInfo.addAll(List.of(searchName.getText(), searchSurname.getText(), categoryList.getValue()));
        stage.close();
    }
}
