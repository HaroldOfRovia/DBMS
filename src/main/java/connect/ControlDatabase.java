package connect;

import exceptions.AlertException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import person.Data;
import person.Person;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class ControlDatabase {
    static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/postgres";
    static final String USER = "postgres";
    static final String PASS = "postgres";
    private Connection connection = null;

    //init connection
    public ControlDatabase() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (
                SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
        }
    }

    public Person createPerson(Integer personId) throws SQLException {
        Person person = new Person();
        Statement statementPeople = connection.createStatement();
        ResultSet rowPerson = statementPeople.executeQuery("SELECT person.surname, person.name, type_of_person.brief " +
                "FROM person, type_of_person where person.person_id = " + personId +
                " and type_of_person.type_of_person_id = person.type_of_person_id ");
        rowPerson.next();
        person.setPersonId(personId);
        person.setTypePerson(rowPerson.getString(3));
        person.setName(rowPerson.getString(2));
        person.setSurname(rowPerson.getString(1));
        Statement statementPeopleInfo = connection.createStatement();
        ResultSet rowPersonInfo = statementPeopleInfo.executeQuery("SELECT type_of_info.brief, info.value, info.active " +
                "FROM info, person, type_of_person, type_of_info where person.person_id = " + personId +
                " and type_of_person.type_of_person_id = person.type_of_person_id and " +
                "person.person_id = info.person_id and info.type_of_info_id = type_of_info.type_of_info_id");
        while(rowPersonInfo.next()){
            person.addData(rowPersonInfo.getString(1), rowPersonInfo.getString(2), rowPersonInfo.getString(3));
        }
        return person;
    }

    public ObservableList<Person> fillPeopleList() throws SQLException {
        ObservableList<Person> people = FXCollections.observableArrayList();
        Statement statementPeople = connection.createStatement();
        ResultSet rowPerson = statementPeople.executeQuery("select person.person_id from person");
        while(rowPerson.next()){
            people.add(createPerson(rowPerson.getInt("person_id")));
        }
        return people;
    }

    public ArrayList<String> listTypePerson() throws SQLException {
        ArrayList<String> typePerson = new ArrayList<>();
        Statement statementTypePerson = connection.createStatement();
        ResultSet rowTypeData = statementTypePerson.executeQuery("SELECT * FROM type_of_person");
        while(rowTypeData.next()){
            typePerson.add(rowTypeData.getString("brief"));
        }
        return typePerson;
    }

    public ObservableList<Person> searchByName(ArrayList<String> searchTag) throws IOException, AlertException {//в массиве: имя, фамилия, тип
        try{
            ObservableList<Person> people = FXCollections.observableArrayList();
            Statement statementPeople = connection.createStatement();
            ResultSet rowPerson;
            if (searchTag.get(2).equals("All"))
                rowPerson = statementPeople.executeQuery("select person.person_id from person, type_of_person where " +
                        "LOWER (person.name) ~ '" + searchTag.get(0).toLowerCase() + "' and LOWER (person.surname) ~ '" + searchTag.get(1).toLowerCase() +
                        "' and person.type_of_person_id = type_of_person.type_of_person_id");
            else
                rowPerson = statementPeople.executeQuery("select person.person_id from person, type_of_person where " +
                        "LOWER (person.name) ~ '" + searchTag.get(0).toLowerCase() + "' and LOWER (person.surname) ~ '" + searchTag.get(1).toLowerCase() +
                        "' and type_of_person.brief = '" + searchTag.get(2) + "'and person.type_of_person_id = type_of_person.type_of_person_id");
            return getPeople(people, rowPerson);
        }
        catch (Exception ex){
            throw new AlertException(ex.getMessage());
        }
    }

    public ObservableList<Person> searchByPhone(ArrayList<String> searchTag) throws IOException, AlertException {//телефон, тип
        try{
            ObservableList<Person> people = FXCollections.observableArrayList();
            Statement statementPeople = connection.createStatement();
            ResultSet rowPerson;
            String phone = searchTag.get(0).replace("+", "\\+");
            switch (searchTag.get(1)) {
                case ("Current phone"):
                    rowPerson = statementPeople.executeQuery("select info.person_id " +
                            "from type_of_info, info where type_of_info.type_of_info_id = info.type_of_info_id and type_of_info.brief ~ 'phone' " +
                            "and info.value ~ '" + phone + "' and info.active = true");
                    break;
                case ("Old phone"):
                    rowPerson = statementPeople.executeQuery("select info.person_id " +
                            "from type_of_info, info where type_of_info.type_of_info_id = info.type_of_info_id and type_of_info.brief ~ 'phone' " +
                            "and info.value ~ '" + phone + "' and info.active = false");
                    break;
                default:
                    rowPerson = statementPeople.executeQuery("select info.person_id " +
                            "from type_of_info, info where type_of_info.type_of_info_id = info.type_of_info_id and type_of_info.brief ~ 'phone' " +
                            "and info.value ~ '" + phone + "'");
                    break;
            }
            return getPeople(people, rowPerson);
        }
        catch (Exception ex){
            throw new AlertException(ex.getMessage());
        }
    }

    public ObservableList<Person> search(String text) throws IOException, AlertException {
        try{
            text = text.toLowerCase();
            ObservableList<Person> people = FXCollections.observableArrayList();
            if (text.equals(""))
                return fillPeopleList();
            else {
                Statement statementPeople = connection.createStatement();
                ResultSet rowPerson = statementPeople.executeQuery("select person.person_id " +
                        "from person, type_of_person, info, type_of_info where (lower (person.name) ~ '" + text + "' or " +
                        "lower (person.surname) ~ '" + text + "' or lower (type_of_person.brief) ~ '" + text + "' or " +
                        "lower (info.value) ~ '" + text + "' or lower (type_of_info.brief) ~ '" + text + "') and " +
                        "person.type_of_person_id = type_of_person.type_of_person_id and info.type_of_info_id = type_of_info.type_of_info_id and info.person_id = person.person_id ");
                return getPeople(people, rowPerson);
            }
        }
        catch (Exception ex){
            throw new AlertException(ex.getMessage());
        }
    }

    private ObservableList<Person> getPeople(ObservableList<Person> people, ResultSet rowPerson) throws SQLException {
        ArrayList<Integer> idList = new ArrayList<>();
        while(rowPerson.next()){
            if (!idList.contains(rowPerson.getInt("person_id"))) {
                idList.add(rowPerson.getInt("person_id"));
                people.add(createPerson(rowPerson.getInt("person_id")));
            }
        }
        return people;
    }

    public void addNewData(String type, String data, boolean active, Person person) throws Exception {
        if (active && existActiveValue(person, type))
            throw new AlertException("An active object already exists in this type.");
        try {
            int newInfoId = findLessId("info", "info_id");
            Statement statement = connection.createStatement();
            ResultSet rowTypeInfo = statement.executeQuery("SELECT type_of_info_id, brief FROM type_of_info where brief = '" + type + "'");
            if (rowTypeInfo.next()) {//если тип существует
                statement.executeUpdate("INSERT INTO info " +
                        "VALUES (" + newInfoId + ", " + person.getPersonId() + ", " + rowTypeInfo.getInt(1) +
                        ", '" + data + "', " + active + ")");
            } else {//если тип не существует
                int newTypeInfoId = findLessId("type_of_info", "type_of_info_id");
                statement.executeUpdate("INSERT INTO type_of_info " +
                        "VALUES ( " + newTypeInfoId + ", '" + type + "')");
                statement.executeUpdate("INSERT INTO info " +
                        "VALUES (" + newInfoId + ", " + person.getPersonId() + ", " + newTypeInfoId +
                        ", '" + data + "', " + active + ")");
            }
        }
        catch (Exception ex){
            throw new AlertException(ex.getMessage());
        }
    }

    private Integer findLessId(String table, String columnId) throws SQLException {
        Statement statement = connection.createStatement();
        int freeId = 1;
        ResultSet row = statement.executeQuery("SELECT " + columnId + " FROM " + table);
        ArrayList<Integer> idList = new ArrayList<>();
        while (row.next())
            idList.add(row.getInt(1));
        while(idList.contains(freeId))
            freeId++;
        return freeId;
    }

    public void update(Person curPerson, Data curData, Data newData) throws Exception {
        if (newData.getActive() && existActiveValue(curPerson, newData.getTypeData()))
            throw new AlertException("An active object already exists in this type.");
        if (newData.getTypeData().matches(".*(phone).*") && !newData.getData().matches("^[\\d\\-+()]*$"))
            throw new AlertException("Phone numbers can contain only (, ), +, - and digits.");
        try {
            Statement statement = connection.createStatement();
            ResultSet rowNewTypeInfo = statement.executeQuery("SELECT type_of_info_id, brief FROM type_of_info where brief = '" +
                    newData.getTypeData() + "'");
            Statement statement1 = connection.createStatement();
            ResultSet rowOldTypeInfo = statement1.executeQuery("SELECT type_of_info_id, brief FROM type_of_info where brief = '" +
                    curData.getTypeData() + "'");
            rowOldTypeInfo.next();
            if (rowNewTypeInfo.next()) {
                statement.executeUpdate("update info set active = " + newData.getActive() + ", value = '" + newData.getData() +
                        "', type_of_info_id = " + rowNewTypeInfo.getInt(1) + " from type_of_info where info.type_of_info_id = " +
                        rowOldTypeInfo.getInt(1) + " and info.value = " +
                        "'" + curData.getData() + "'");
            } else {
                int newTypeInfoId = findLessId("type_of_info", "type_of_info_id");
                statement.executeUpdate("INSERT INTO type_of_info " +
                        "VALUES ( " + newTypeInfoId + ", '" + newData.getTypeData() + "')");
                statement.executeUpdate("update info set active = " + newData.getActive() + ", value = '" + newData.getData() +
                        "', type_of_info_id = " + newTypeInfoId + " from type_of_info where info.type_of_info_id = " +
                        rowOldTypeInfo.getInt(1) + " and info.value = '" + curData.getData() + "'");
            }
        }catch (Exception ex){
            throw new AlertException(ex.getMessage());
        }
    }

    public boolean existActiveValue(Person person, String typeData) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet row = statement.executeQuery("select active from info, type_of_info where " +
                "info.person_id = '" + person.getPersonId() + "' and type_of_info.brief = '" + typeData + "' and " +
                "type_of_info.type_of_info_id = info.type_of_info_id and active = true");
        return row.next();
    }

    public void deleteInfo(Person person, Data data) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("DELETE FROM info WHERE value = '" + data.getData() + "' and " +
                "info.person_id = '" + person.getPersonId() + "'");
    }

    public void cleanType(String tableName, String columnName) throws SQLException {//очищает пустые типы
        Statement statement = connection.createStatement();
        ResultSet row = statement.executeQuery("select max(" + columnName + ") from " + tableName);
        row.next();
        for(int i = row.getInt(1); i>0; i--){
            try {
                statement.executeUpdate("DELETE FROM " + tableName + " WHERE " + columnName + " = " + i );
            }
            catch (Exception ignored){}
        }
    }

    public void addNewPerson(String surname, String name, String type) throws IOException, AlertException {
        try {
            int newPersonId = findLessId("person", "person_id");
            Statement statement = connection.createStatement();
            ResultSet row = statement.executeQuery("select type_of_person_id from type_of_person where brief = '" + type + "'");
            if (row.next()) {
                statement.executeUpdate("insert into person values (" + newPersonId + ", '" + surname + "', '" +
                        name + "', " + row.getInt(1) + ")");
            } else {
                int newTypePersonId = findLessId("type_of_person", "type_of_person_id");
                statement.executeUpdate("INSERT INTO type_of_person " +
                        "VALUES ( " + newTypePersonId + ", '" + type + "')");
                statement.executeUpdate("insert into person values (" + newPersonId + ", '" + surname + "', '" +
                        name + "', " + newTypePersonId + ")");
            }
        } catch (Exception ex){
            throw new AlertException(ex.getMessage());
        }
    }

    public void deletePerson(Person curPerson) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("DELETE FROM person WHERE person_id = " + curPerson.getPersonId());
    }
}
