package view;

import connect.ControlDatabase;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import person.Data;
import person.Person;

import java.util.ArrayList;

public class EditDataController {
    @FXML
    private ListView<Data> listData;
    @FXML
    private TextField typeDataField;
    @FXML
    private TextField dataField;
    @FXML
    private CheckBox checkActive;
    private Stage stage;
    private Data curData;
    private Person curPerson;
    private ArrayList<Data> dataList = new ArrayList<Data>();

    public EditDataController(Person curPerson) {
        this.curPerson = curPerson;
    }

    public void setStage(Stage stage){
        listData.setItems(FXCollections.observableArrayList(curPerson.getData()));
        this.stage = stage;
    }

    public void setTextFieldData(){
        if(listData.getSelectionModel().getSelectedItem() != null) {
            curData = listData.getSelectionModel().getSelectedItem();
            typeDataField.setText(curData.getTypeData());
            dataField.setText(curData.getData());
            checkActive.setVisible(curData.isPhone());
            checkActive.setSelected(curData.getActive());
        }
    }

    public void save() {
        Data newData = new Data(typeDataField.getText(), dataField.getText(), checkActive.isSelected());
        if(newData.equals(curData) || curData == null)
            return;
        ControlDatabase controlDatabase = new ControlDatabase();
        try{
            controlDatabase.update(curPerson, curData, newData);
            curPerson.deleteData(curData);
            curPerson.addData(newData);
            curData = newData;
            listData.setItems(FXCollections.observableArrayList(curPerson.getData()));
        }catch (Exception ignored){
            checkActive.setSelected(curData.getActive());
        }
    }

    public void delete() throws Exception {
        if(curData == null)
            return;
        Stage stage = new Stage();
        stage.setTitle("Confirm");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Confirm.fxml"));
        Parent root = loader.load();

        ConfirmController controller = loader.getController();
        controller.setDialogStage(stage);
        controller.setText("Do you want to delete this object?");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();

        if(controller.getCheck()) {
            ControlDatabase controlDatabase = new ControlDatabase();
            controlDatabase.deleteInfo(curPerson, curData);
            curPerson.deleteData(curData);
            listData.setItems(FXCollections.observableArrayList(curPerson.getData()));
            typeDataField.setText("");
            dataField.setText("");
            checkActive.setVisible(false);
        }
    }

    public void quit(){
        stage.close();
    }
}
