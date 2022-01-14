package person;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class Person {
    private Integer personId;
    private StringProperty surname;
    private StringProperty name;
    private StringProperty typePerson;
    private ArrayList<Data> data = new ArrayList<Data>();

    public Person(){
        personId = 0;
        setSurname("");
        setName("");
        setTypePerson("");
        setData(new ArrayList<Data>());
    }

    public Person(Integer personId, String surname, String name, String typePerson, ArrayList<Data> data){
        this.personId = personId;
        setSurname(surname);
        setName(name);
        setTypePerson(typePerson);
        setData(data);
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

    public void setData(ArrayList<Data> data){
        this.data.clear();
        this.data.addAll(data);
    }
    public ArrayList<Data> getData() {
        return data;
    }

    public String toStringOldData(){
        StringBuilder str = new StringBuilder();
        data.forEach((value) -> {
            if(!value.getActive())
                str.append(value.toString()).append("\n");
        });
        return str.toString();
    }

    public String toStringActiveData(){
        StringBuilder str = new StringBuilder();
        data.forEach(value -> {
            if(value.getActive())
                str.append(value.toString()).append("\n");
        });
        return str.toString();
    }

    public String toStringPersonData(){
        return "type person: " + typePerson.getValue() + ";\n" + toStringActiveData();
    }

    @Override
    public String toString(){
        return surname.getValue() + " " + name.getValue();
    }

    public void addData(String type, String value, String active){
        Data data = new Data(type, value, active.equals("t"));
        this.data.add(data);
    }

    public void addData(Data data){
        this.data.add(data);
    }

    public void deleteData(Data curData) {
        data.remove(curData);
    }
}
