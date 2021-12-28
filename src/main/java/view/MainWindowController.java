package view;

import connect.ControlDatabase;
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
        curPersonText.setText(curPerson.toStringData());
    }

    public void setListPersons(ObservableList<Person> people) {
        listPersons.setItems(people);
        if (people.size() != 0) {
            listPersons.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null)
                    curPerson = newValue;
                if (curDataOrOld) {
                    curPersonText.setText(curPerson.toStringData());
                }
                else
                    curPersonText.setText(curPerson.toStringOldData());
            });
            listPersons.getSelectionModel().selectFirst();
        }
        else
            curPersonText.setText("");
    }

    public void searchByName() throws Exception {
        searchTag.clear();
        FXMLLoader loader = new
                FXMLLoader(Main.class.getResource("/view/SearchByNameWindow.fxml"));
        SearchByNameWindowController controller = new SearchByNameWindowController(searchTag);
        loader.setController(controller);
        Parent root = loader.load();

        Stage searchStage = new Stage();
        searchStage.setTitle("Search by name or surname");
        Scene scene = new Scene(root);
        searchStage.setScene(scene);
        controller.setDialogStage(searchStage);

        searchStage.showAndWait();
        if(!searchTag.isEmpty()) {
            ControlDatabase controlDatabase = new ControlDatabase();
            setListPersons(controlDatabase.searchByName(searchTag));
        }
    }

    public void searchByPhone() throws Exception {
        searchTag.clear();
        FXMLLoader loader = new
                FXMLLoader(Main.class.getResource("/view/SearchByPhoneWindow.fxml"));
        SearchByPhoneWindowController controller = new SearchByPhoneWindowController(searchTag);
        loader.setController(controller);
        Parent root = loader.load();

        Stage searchStage = new Stage();
        searchStage.setTitle("Search by phone");
        Scene scene = new Scene(root);
        searchStage.setScene(scene);
        controller.setDialogStage(searchStage);

        searchStage.showAndWait();
        if(!searchTag.isEmpty()) {
            ControlDatabase controlDatabase = new ControlDatabase();
            setListPersons(controlDatabase.searchByPhone(searchTag));
            if(searchTag.get(1).equals("Old phone"))
                changeOnFalse();
            else
                changeOnTrue();
        }
    }

    public void resetList() throws SQLException {
        ControlDatabase controlDatabase = new ControlDatabase();
        setListPersons(controlDatabase.fillPeopleList());
    }

    public void search() throws SQLException {
        ControlDatabase controlDatabase = new ControlDatabase();
        setListPersons(controlDatabase.search(searchValue.getText()));
    }

    public void addData() throws IOException, SQLException {
        searchTag.clear();
        FXMLLoader loader = new
                FXMLLoader(Main.class.getResource("/view/AddNewData.fxml"));
        AddNewDataController controller = new AddNewDataController(searchTag);
        loader.setController(controller);
        Parent root = loader.load();

        Stage searchStage = new Stage();
        searchStage.setTitle("Add new data");
        Scene scene = new Scene(root);
        searchStage.setScene(scene);
        controller.setDialogStage(searchStage);

        searchStage.showAndWait();

        searchTag.add(curPerson.getPersonId().toString());
        if(searchTag.size() != 1) {
            ControlDatabase controlDatabase = new ControlDatabase();
            controlDatabase.addNewData(searchTag);
            resetList();
        }
    }


}