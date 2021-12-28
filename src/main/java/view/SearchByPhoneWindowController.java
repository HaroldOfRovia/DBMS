package view;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class SearchByPhoneWindowController {
    @FXML
    private TextField searchPhone;
    @FXML
    private ChoiceBox<String> categoryList;
    private Stage stage;

    private final ArrayList<String> searchInfo;


    public SearchByPhoneWindowController(ArrayList<String> searchInfo){
        this.searchInfo = searchInfo;
    }

    public void setDialogStage(Stage stage){
        categoryList.setItems(FXCollections.observableArrayList(List.of("All", "Current phone", "Old phone")));
        categoryList.setValue("All");
        this.stage = stage;
    }

    public void search(){
        searchInfo.addAll(List.of(searchPhone.getText(), categoryList.getValue()));
        stage.close();
    }
}
