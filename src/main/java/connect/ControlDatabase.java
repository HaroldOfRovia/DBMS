package connect;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import person.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

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

    public HashMap<Integer, String> listTypeInfo() throws SQLException {
        HashMap<Integer, String> typeData = new HashMap<>();
        Statement statementTypeData = connection.createStatement();
        ResultSet rowTypeData = statementTypeData.executeQuery("SELECT type_of_info_id, lower (brief) FROM type_of_info ");
        while(rowTypeData.next()){
            typeData.put(rowTypeData.getInt("type_of_info_id"), rowTypeData.getString("brief").toLowerCase());
        }
        return typeData;
    }

    public Person createPerson(Integer personId) throws SQLException {
        Person person = new Person();
        Statement statementPeople = connection.createStatement();
        ResultSet rowPerson = statementPeople.executeQuery("SELECT person.surname, person.name, type_of_person.brief " +
                "FROM person, type_of_person where person.person_id = " + personId + " and type_of_person.type_of_person_id = person.type_of_person_id ");
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
            if (rowPersonInfo.getBoolean(3)) {
                person.addData(rowPersonInfo.getString(1), rowPersonInfo.getString(2));
            }
            else{
                if (rowPersonInfo.getString(1).matches(".*(phone).*")) {
                    person.addOldPhone(rowPersonInfo.getString(1) + ": " + rowPersonInfo.getString(2));
                }
            }
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

    public ObservableList<Person> searchByName(ArrayList<String> searchTag) throws SQLException {//в массиве: имя, фамилия, тип
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

    public ObservableList<Person> searchByPhone(ArrayList<String> searchTag) throws SQLException {//телефон, тип
        ObservableList<Person> people = FXCollections.observableArrayList();
        Statement statementPeople = connection.createStatement();
        ResultSet rowPerson;
        String phone = searchTag.get(0).replace("+", "\\+");
        switch (searchTag.get(1)){
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

    public ObservableList<Person> search(String text) throws SQLException {
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

    public void addNewData(ArrayList<String> searchTag) throws SQLException {// 0 - значение, 1 - тип, 2 - активно или нет (строкой), 3 - id человека
        Statement statementAdd = connection.createStatement();
        ResultSet rowTypeInfo = statementAdd.executeQuery("SELECT type_of_info_id, lower (brief) FROM type_of_info where brief = '" +
                searchTag.get(1).toLowerCase() + "'");
        int newInfoId = findLessId("info", "info_id", "value");
        if (rowTypeInfo.next()){//если такой тип существует
            Statement statementCheck = connection.createStatement();
            Statement statementUpdate = connection.createStatement();
            if (searchTag.get(2).equals("TRUE") && searchTag.get(1).matches(".*(phone).*")) {//если является телефоном и новый телефон активный
                statementUpdate.executeUpdate("update info set active = false where type_of_info_id = " + rowTypeInfo.getInt(1) +
                        " and person_id = " + searchTag.get(3));//все телефоны делает неактивынми
                int check = statementCheck.executeUpdate("update info set active = " + searchTag.get(2) + " from type_of_info " +
                        "where lower (type_of_info.brief) = '" + searchTag.get(1).toLowerCase() + "' and type_of_info.type_of_info_id = info.type_of_info_id " +
                        "and info.value = '" + searchTag.get(0) + "'");//пытается обновить значение телефона, если он уже существует
                if (check != 1) {//иначе создает новый телефон
                    statementUpdate.executeUpdate("INSERT INTO info " +
                            "VALUES (" + newInfoId + ", " + searchTag.get(3) + ", " + rowTypeInfo.getInt(1) + ", '" +
                            searchTag.get(0) + "', true)");
                }
            }
            else if (searchTag.get(2).equals("TRUE")) {//если не телефон, то просто обновляет значение
                int check = statementUpdate.executeUpdate("update info set value = '" + searchTag.get(0) + "' where type_of_info_id = " + rowTypeInfo.getInt(1) +
                        " and person_id = " + searchTag.get(3));
                if (check != 1) {
                    statementUpdate.executeUpdate("INSERT INTO info " +
                            "VALUES (" + newInfoId + ", " + searchTag.get(3) + ", " + rowTypeInfo.getInt(1) + ", '" +
                            searchTag.get(0) + "', true)");
                }
            }
            else if (searchTag.get(1).matches(".*(phone).*"))//если выбран неактивным, то просто меняет неактивный на активный (была универсальная замена)
                statementCheck.executeUpdate("update info set active = " + searchTag.get(2) + " from type_of_info " +
                    "where lower (type_of_info.brief) = '" + searchTag.get(1).toLowerCase() + "' and type_of_info.type_of_info_id = info.type_of_info_id " +
                    "and info.value = '" + searchTag.get(0) + "'");
        }
        else if (searchTag.get(2).equals("TRUE")){
            int newTypeInfoId = findLessId("type_of_info", "type_of_info_id", "brief");
            statementAdd.executeUpdate("INSERT INTO type_of_info " +
                    "VALUES ( " + newTypeInfoId + ", '" + searchTag.get(1) + "')");
            statementAdd.executeUpdate("INSERT INTO info " +
                    "VALUES (" + newInfoId + ", " + searchTag.get(3) + ", " + newTypeInfoId +
                    ", '" + searchTag.get(0) + "', " + searchTag.get(2) + ")");
        }
    }

    private Integer findLessId(String table, String columnId, String columnValue) throws SQLException {
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
}
