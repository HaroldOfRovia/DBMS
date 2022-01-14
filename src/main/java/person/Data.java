package person;

public class Data {
    private String typeData;
    private String data;
    private Boolean active;

    public Data(String typeData, String data, Boolean active){
        this.typeData = typeData;
        this.data = data;
        this.active = active;
    }

    public void setTypeData(String typeData){
        this.typeData = typeData;
    }
    public String getTypeData() {
        return typeData;
    }

    public void setData(String data){
        this.data = data;
    }
    public String getData() {
        return data;
    }

    public void setActive(Boolean active){
        this.active = active;
    }
    public Boolean getActive() {
        return active;
    }

    public boolean isPhone(){
        return typeData.matches(".*(phone).*");
    }

    @Override
    public String toString(){
        return typeData + ": " + data;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        final Data other = (Data) obj;
        return (this.typeData.equals(other.typeData)) && (this.data.equals(other.data)) && (this.active == other.active);
    }
}
