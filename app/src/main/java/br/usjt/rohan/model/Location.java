package br.usjt.rohan.model;
//Um lugar deve ter, no mínimo, data de cadastro, descrição e coordenadas latitude e
//        longitude.
public class Location {
    private String dt_created;
    private String description;

    public Location(String dt_created,String description,String coordinates,String location_name;){
        this.dt_created = dt_created;
        this.description = description;
        this.location_name = location_name;
        this.coordinates = coordinates;
    }

    public Location(String){
        
    }
    public String getDt_created() {
        return dt_created;
    }

    public void setDt_created(String dt_created) {
        this.dt_created = dt_created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    private String coordinates;
    private String location_name;
}
