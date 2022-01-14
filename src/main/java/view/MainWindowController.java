package view;

import connect.ControlDatabase;
import exceptions.AlertException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import person.Main;
import person.Person;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class MainWindowController {
    @FXML
    private ListView<Person> listPersons;
    @FXML
    private Text curPersonText;
    @FXML
    private TextField searchValue;
    private Boolean curDataOrOld = Boolean.TRUE;
    private final ArrayList<String> searchTag = new ArrayList<>();
    private Person curPerson;

    public void changeOnFalse(){
        curDataOrOld = Boolean.FALSE;
        curPersonText.setText(curPerson.toStringOldData());
    }

    public void changeOnTrue(){
        curDataOrOld = Boolean.TRUE;
        curPersonText.setText(curPerson.toStringPersonData());
    }

    public void setListPersons(ObservableList<Person> people) {
        listPersons.setItems(people);
        if (people.size() != 0) {
            listPersons.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null)
                    curPerson = newValue;
                if (curDataOrOld) {
                    curPersonText.setText(curPerson.toStringPersonData());
                }
                else
                    curPersonText.setText(curPerson.toStringOldData());
            });
            listPersons.getSelectionModel().selectFirst();
        }
        else
            curPersonText.setText("");
    }

    public void searchByName() throws IOException, SQLException {
        searchTag.clear();
        FXMLLoader loader = new
                FXMLLoader(Main.class.getResource("/view/SearchByNameWindow.fxml"));
        SearchByNameWindowController controller = new SearchByNameWindowController(searchTag);
        loader.setController(controller);
        Parent root = loader.load();

        Stage searchStage = new Stage();
        searchStage.setResizable(false);
        searchStage.setTitle("Search by name or surname");
        Scene scene = new Scene(root);
        searchStage.setScene(scene);
        controller.setDialogStage(searchStage);

        searchStage.showAndWait();
        try{
            if (!searchTag.isEmpty()) {
                ControlDatabase controlDatabase = new ControlDatabase();
                setListPersons(controlDatabase.searchByName(searchTag));
            }
        }
        catch(Exception ignored){}
    }

    public void searchByPhone() throws IOException {
        searchTag.clear();
        FXMLLoader loader = new
                FXMLLoader(Main.class.getResource("/view/SearchByPhoneWindow.fxml"));
        SearchByPhoneWindowController controller = new SearchByPhoneWindowController(searchTag);
        loader.setController(controller);
        Parent root = loader.load();

        Stage searchStage = new Stage();
        searchStage.setResizable(false);
        searchStage.setTitle("Search by phone");
        Scene scene = new Scene(root);
        searchStage.setScene(scene);
        controller.setDialogStage(searchStage);

        searchStage.showAndWait();
        try{
            if (!searchTag.isEmpty()) {
                ControlDatabase controlDatabase = new ControlDatabase();
                setListPersons(controlDatabase.searchByPhone(searchTag));
                if (searchTag.get(1).equals("Old phone"))
                    changeOnFalse();
                else
                    changeOnTrue();
            }
        }catch (Exception ignored){}
    }

    public void resetList() throws SQLException {
        ControlDatabase controlDatabase = new ControlDatabase();
        setListPersons(controlDatabase.fillPeopleList());
    }

    public void search(){
        try{
            ControlDatabase controlDatabase = new ControlDatabase();
            setListPersons(controlDatabase.search(searchValue.getText()));
        }
        catch (Exception ignored){}
    }

    public void addData() throws IOException {
            Stage searchStage = new Stage();
            searchStage.setResizable(false);
            searchStage.setTitle("Add new data");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddNewData.fxml"));
            Parent root = loader.load();

            AddNewDataController controller = loader.getController();
            controller.setDialogStage(searchStage);

            Scene scene = new Scene(root);
            searchStage.setScene(scene);
            searchStage.showAndWait();
        try {
            if(!controller.getError()) {//если ошибка не произошла, то добавляем
                ControlDatabase controlDatabase = new ControlDatabase();
                controlDatabase.addNewData(controller.getType(), controller.getData(), controller.getActive(), curPerson);
                resetList();
            }
        }
        catch (Exception ignored){}
    }

    public void editData() throws IOException, SQLException {
        FXMLLoader loader = new
                FXMLLoader(Main.class.getResource("/view/EditData.fxml"));
        EditDataController controller = new EditDataController(curPerson);
        loader.setController(controller);
        Parent root = loader.load();

        Stage editStage = new Stage();
        editStage.setResizable(false);
        editStage.setTitle("Edit data");
        Scene scene = new Scene(root);
        editStage.setScene(scene);
        controller.setStage(editStage);

        editStage.showAndWait();

        resetList();
        ControlDatabase controlDatabase = new ControlDatabase();
        controlDatabase.cleanType("type_of_info", "type_of_info_id");
    }

    public void addPerson() throws IOException {
        Stage stage = new Stage();
        stage.setTitle("New person");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddNewPerson.fxml"));
        Parent root = loader.load();

        AddNewPersonController controller = loader.getController();
        controller.setDialogStage(stage);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();
        try {
            if(!controller.getError()) {
                ControlDatabase controlDatabase = new ControlDatabase();
                controlDatabase.addNewPerson(controller.getSurname(), controller.getName(), controller.getType());
                resetList();
            }
        }catch (Exception ignored){}
    }

    public void deletePerson() throws IOException, AlertException {
        if(curPerson == null)
            return;
        Stage stage = new Stage();
        stage.setTitle("Confirm");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Confirm.fxml"));
        Parent root = loader.load();

        ConfirmController controller = loader.getController();
        controller.setDialogStage(stage);
        controller.setText("Do you want to delete this person?");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();

        if(controller.getCheck()){
            try {
                ControlDatabase controlDatabase = new ControlDatabase();
                controlDatabase.deletePerson(curPerson);
                controlDatabase.cleanType("type_of_person", "type_of_person_id");
                resetList();
            }catch (Exception ex){
                throw new AlertException(ex.getMessage());
            }
        }
    }
}