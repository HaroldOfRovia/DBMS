package person;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.HashMap;

public class Person {
    private Integer personId;
    private StringProperty surname;
    private StringProperty name;
    private StringProperty typePerson;
    private HashMap<String, String> info = new HashMap<String, String>();
    private ArrayList<String> oldPhones = new ArrayList<String>();

    public Person(){
        personId = 0;
        setSurname("");
        setName("");
        setTypePerson("");
        setInfo(new HashMap<String, String>());
        setOldPhones(new ArrayList<>());
    }

    public Person(Integer personId, String surname, String name, String typePerson, HashMap<String, String> info, ArrayList<String> oldPhones){
        this.personId = personId;
        setSurname(surname);
        setName(name);
        setTypePerson(typePerson);
        setInfo(info);
        setOldPhones(oldPhones);
    }

    public void setPersonId(Integer value){
        personId = value;
    }
    public Integer getPersonId(){
        return personId;
    }

    public StringProperty surnameStringProperty(){
        if (surname == null) {
            surname = new SimpleStringProperty();
        }
        return surname;
    }
    public void setSurname(String value){
        surnameStringProperty().set(value);
    }
    public String getSurname(){
        return surnameStringProperty().get();
    }

    public StringProperty nameStringProperty(){
        if (name == null) {
            name = new SimpleStringProperty();
        }
        return name;
    }
    public void setName(String value){
        nameStringProperty().set(value);
    }
    public String getName(){
        return nameStringProperty().get();
    }

    public StringProperty typePersonStringProperty(){
        if (typePerson == null) {
            typePerson = new SimpleStringProperty();
        }
        return typePerson;
    }
    public void setTypePerson(String value){
        typePersonStringProperty().set(value);
    }
    public String getTypePerson(){
        return typePersonStringProperty().get();
    }

    public void setInfo(HashMap<String, String> values){
        info.putAll(values);
    }
    public HashMap<String, String> getInfo(){
        return info;
    }

    public void setOldPhones(ArrayList<String> oldPhones){
        this.oldPhones.addAll(oldPhones);
    }
    public ArrayList<String> getOldPhones() {
        return oldPhones;
    }

    public String toStringOldData(){
        StringBuilder str = new StringBuilder();
        oldPhones.forEach((value) -> {
            str.append(value + "\n");
        });
        return str.toString();
    }

    public String dataToString(){
        StringBuilder str = new StringBuilder();
        info.forEach((key, value) -> {
            str.append(key + ": " + value + ";\n");
        });
        return str.toString();
    }

    public String toStringData(){
        return "type person: " + typePerson.getValue() + ";\n" + dataToString();
    }

    @Override
    public String toString(){
        return surname.getValue() + " " + name.getValue();
    }

    public void addData(String key, String value){
        info.put(key, value);
    }

    public void addOldPhone(String oldPhone){
        oldPhones.add(oldPhone);
    }

}
